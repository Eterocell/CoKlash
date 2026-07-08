import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.DynamicFeatureExtension
import com.android.build.api.dsl.LibraryExtension
import com.diffplug.gradle.spotless.FormatExtension
import com.diffplug.gradle.spotless.SpotlessExtension
import com.diffplug.spotless.kotlin.KtLintStep
import java.util.Properties

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.legacy.kapt) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.ksp) apply false

    alias(libs.plugins.spotless)
}

buildscript {
    dependencies {
        classpath(libs.gradle.plugin.kotlin)
        classpath(libs.gradle.plugin.ksp)
    }
}

val verCode = findProperty("VERSION_CODE") as? String ?: throw IllegalStateException("Should specify VERSION_CODE property in gradle.properties.")
val verName = findProperty("VERSION_NAME") as? String ?: throw IllegalStateException("Should specify VERSION_NAME property in gradle.properties.")

fun Project.withAndroidApplication(block: Plugin<in Any>.() -> Unit) = plugins.withId("com.android.application", block)

fun Project.withAndroidLibrary(block: Plugin<in Any>.() -> Unit) = plugins.withId("com.android.library", block)

fun Project.withAndroidDynamicFeature(block: Plugin<in Any>.() -> Unit) = plugins.withId("com.android.dynamic-feature", block)

fun Project.withAndroid(block: Plugin<in Any>.() -> Unit) {
    withAndroidApplication(block)
    withAndroidLibrary(block)
    withAndroidDynamicFeature(block)
}

fun Project.configureAndroidCommon(action: Action<CommonExtension>) {
    withAndroidApplication {
        action.execute(extensions.getByType<ApplicationExtension>())
    }
    withAndroidLibrary {
        action.execute(extensions.getByType<LibraryExtension>())
    }
    withAndroidDynamicFeature {
        action.execute(extensions.getByType<DynamicFeatureExtension>())
    }
}

fun Project.configureAndroidApplication(block: ApplicationExtension.() -> Unit) = withAndroidApplication { extensions.configure(block) }

fun Project.configureAndroidLibrary(block: LibraryExtension.() -> Unit) = withAndroidLibrary { extensions.configure(block) }

subprojects {

    val isApp = name == "app"

    configureAndroidCommon {

        ndkVersion = "29.0.14206865"
        buildToolsVersion = "37.0.0"
        compileSdk {
            version = release(37)
        }

        defaultConfig.apply {
            minSdk = 23

            resValue("string", "release_name", "v$verName")
            resValue("integer", "release_code", verCode)

            ndk {
                abiFilters += listOf("arm64-v8a", "armeabi-v7a", "x86", "x86_64")
            }

            externalNativeBuild {
                cmake {
                    abiFilters("arm64-v8a", "armeabi-v7a", "x86", "x86_64")
                }
            }
        }

        productFlavors {
            flavorDimensions += "feature"

            create("alpha") {
                dimension = flavorDimensions.first()

                buildConfigField("boolean", "PREMIUM", "Boolean.parseBoolean(\"false\")")

                if (project.name == "app" || project.name == "design") {
                    resValue("string", "launch_name", "@string/launch_name_meta")
                    resValue("string", "application_name", "@string/application_name_meta")
                }
            }
        }

        sourceSets {
            getByName("alpha") {
                java.directories += "src/foss/java"
            }
        }

        signingConfigs {
            val keystore = rootProject.file("signing.properties")
            if (keystore.exists()) {
                create("release") {
                    val prop =
                        Properties().apply {
                            keystore.inputStream().use(this::load)
                        }

                    storeFile = rootProject.file(prop.getProperty("keystore.path"))
                    storePassword = prop.getProperty("keystore.password")!!
                    keyAlias = prop.getProperty("key.alias")!!
                    keyPassword = prop.getProperty("key.password")!!
                }
            }
        }

        buildTypes {
            named("release") {
                isMinifyEnabled = isApp
                isShrinkResources = isApp
                proguardFiles(
                    getDefaultProguardFile("proguard-android-optimize.txt"),
                    "proguard-rules.pro",
                )
            }
        }

        buildFeatures.apply {
            resValues = true
            buildConfig = true
            viewBinding = name != "hideapi"
            dataBinding.apply {
                enable = name != "hideapi"
            }
        }

        compileOptions.apply {
            sourceCompatibility = JavaVersion.VERSION_11
            targetCompatibility = JavaVersion.VERSION_11
            isCoreLibraryDesugaringEnabled = true
        }

        val libs: VersionCatalog = rootProject.extensions.getByType<VersionCatalogsExtension>().named("libs")

        dependencies.add("coreLibraryDesugaring", libs.findLibrary("android-desugar-jdk-libs").get())
    }

    configureAndroidApplication {
        defaultConfig {
            applicationId = "com.eterocell.mihomoforandroid"

            targetSdk = 36

            versionName = verName
            versionCode = verCode.toInt()
        }

        packaging {
            resources {
                excludes.add("DebugProbesKt.bin")
            }
        }

        productFlavors.named("alpha") {
            isDefault = true
            versionNameSuffix = ".Meta.Alpha"
            applicationIdSuffix = ".meta"
        }

        buildTypes {
            named("release") {
                signingConfig = signingConfigs.findByName("release")
            }
            named("debug") {
                versionNameSuffix = ".debug"
            }
        }

        splits {
            abi {
                // Do not enable multiple APKs when building bundle
                val isBuildingBundle = gradle.startParameter.taskNames.any { it.lowercase().contains("bundle") }
                isEnable = !isBuildingBundle
                isUniversalApk = true
                reset()
                include("arm64-v8a", "armeabi-v7a", "x86", "x86_64")
            }
        }
    }

    configureAndroidLibrary {
        defaultConfig {
            consumerProguardFiles("consumer-rules.pro")
        }

        productFlavors.named("alpha") {
            isDefault = true
        }

        buildTypes {
            named("release") {
                signingConfig = signingConfigs.findByName("release")
            }
        }
    }

    val isHideApi = name == "hideapi"

    // Disable androidTest tasks if no androidTest source directory exists
    afterEvaluate {
        if (!isHideApi && !project.projectDir.resolve("src/androidTest").exists()) {
            tasks.matching { it.name.contains("AndroidTest") }.configureEach {
                enabled = false
            }
        }
    }
}

val ktlintVersion = "1.8.0"

fun SpotlessExtension.androidXml(
    block: FormatExtension.() -> Unit = {
        target(
            "**/AndroidManifest.xml",
            "**/src/**/*.xml",
        )
        leadingTabsToSpaces()
        trimTrailingWhitespace()
        endWithNewline()
    },
) = format("androidXml", block)

fun SpotlessExtension.gradleVersionCatalogs(
    block: FormatExtension.() -> Unit = {
        target(
            "**/*.versions.toml",
        )
        leadingTabsToSpaces()
        trimTrailingWhitespace()
        endWithNewline()
    },
) = format("gradleVersionCatalogs", block)

fun SpotlessExtension.intelliJIDEARunConfiguration(
    block: FormatExtension.() -> Unit = {
        target(
            "**/.run/*.xml",
            "**/.idea/runConfigurations/*.xml",
        )
        leadingTabsToSpaces()
        trimTrailingWhitespace()
        endWithNewline()
    },
) = format("intelliJIDEARunConfiguration", block)

fun SpotlessExtension.kotlin(
    targets: List<String> = listOf("**/src/**/*.kt"),
    excludeTargets: List<String> = listOf(),
    ktlintVersion: String = KtLintStep.defaultVersion(),
    licenseHeaderFile: java.io.File? = null,
    licenseHeaderConfig: FormatExtension.LicenseHeaderConfig.() -> Unit = {},
    editorConfigPath: String,
    editorConfigOverride: Map<String, String> = mapOf(),
    customKtlintRuleSets: List<String> = listOf(),
) = kotlin {
    target(targets)
    targetExclude(excludeTargets)
    leadingTabsToSpaces()
    trimTrailingWhitespace()
    endWithNewline()
    ktlint(ktlintVersion)
        .customRuleSets(customKtlintRuleSets)
        .setEditorConfigPath(editorConfigPath.takeIf { java.io.File(it).exists() })
        .editorConfigOverride(editorConfigOverride)
    licenseHeaderFile?.let(::licenseHeaderFile)?.apply(licenseHeaderConfig)
}

private val defaultExcludeTargetsForKotlinGradle: Set<String> =
    setOf(
        "**/build/kotlin-dsl/**/*.gradle.kts",
    )

fun SpotlessExtension.kotlinGradle(
    targets: List<String> = listOf("**/*.gradle.kts"),
    overrideExcludeTargets: Set<String> = setOf(),
    additionalExcludeTargets: Set<String> = setOf(),
    ktlintVersion: String = KtLintStep.defaultVersion(),
    editorConfigPath: String,
    editorConfigOverride: Map<String, String> = mapOf(),
    customKtlintRuleSets: List<String> = listOf(),
) = kotlinGradle {
    target(targets)
    targetExclude(
        overrideExcludeTargets.ifEmpty {
            defaultExcludeTargetsForKotlinGradle + additionalExcludeTargets
        },
    )
    leadingTabsToSpaces()
    trimTrailingWhitespace()
    endWithNewline()
    ktlint(ktlintVersion)
        .customRuleSets(customKtlintRuleSets)
        .setEditorConfigPath(editorConfigPath.takeIf { java.io.File(it).exists() })
        .editorConfigOverride(editorConfigOverride)
}

fun SpotlessExtension.protobuf(
    clangFormatVersion: String = "13.0.0",
    style: String = "Google",
    block: FormatExtension.() -> Unit = {
        target("**/src/**/*.proto")
        clangFormat(clangFormatVersion).style(style)
    },
) = format("protobuf", block)

fun SpotlessExtension.copyrightForKts(
    targets: List<String> = listOf("**/*.kts"),
    excludeTargets: Set<String> = setOf(),
    licenseHeaderFile: java.io.File? = null,
    licenseHeaderDelimiter: String = "(^(?![\\/ ]\\*).*\$)",
    licenseHeaderConfig: FormatExtension.LicenseHeaderConfig.() -> Unit = {},
) {
    format("kts") {
        target(targets)
        targetExclude(excludeTargets)
        licenseHeaderFile
            ?.let { licenseHeaderFile(it, licenseHeaderDelimiter) }
            ?.apply(licenseHeaderConfig)
    }
}

fun SpotlessExtension.copyrightForXml(
    targets: List<String> = listOf("**/*.xml"),
    excludeTargets: Set<String> = setOf(),
    licenseHeaderFile: java.io.File? = null,
    licenseHeaderDelimiter: String = "(<[^!?])",
    licenseHeaderConfig: FormatExtension.LicenseHeaderConfig.() -> Unit = {},
) {
    format("xml") {
        target(targets)
        targetExclude(excludeTargets)
        licenseHeaderFile
            ?.let { licenseHeaderFile(it, licenseHeaderDelimiter) }
            ?.apply(licenseHeaderConfig)
    }
}

configure<SpotlessExtension> {
    androidXml()
    intelliJIDEARunConfiguration()
    gradleVersionCatalogs()

    kotlin(
        editorConfigPath = "${rootProject.rootDir}/.editorconfig",
        editorConfigOverride =
            mapOf(
                "ktlint_standard_argument-list-wrapping" to "disabled",
                "ktlint_standard_filename" to "disabled",
                "ktlint_standard_no-wildcard-imports" to "disabled",
                "ktlint_function_naming_ignore_when_annotated_with" to "Composable",
                "ij_kotlin_allow_trailing_comma" to "true",
                "ij_kotlin_allow_trailing_comma_on_call_site" to "true",
                "ktlint_standard_argument-list-wrapping" to "disabled",
                "ktlint_standard_filename" to "disabled",
            ),
        licenseHeaderFile = null,
        excludeTargets = listOf("**/spotless/copyright.kt", "*.kts"),
        licenseHeaderConfig = {
            updateYearWithLatest(true)
            yearSeparator("-")
        },
        ktlintVersion = ktlintVersion,
    )
    kotlinGradle(
        editorConfigPath = "${rootProject.rootDir}/.editorconfig",
        editorConfigOverride =
            mapOf(
                "ktlint_standard_argument-list-wrapping" to "disabled",
                "ktlint_standard_filename" to "disabled",
                "ktlint_standard_no-wildcard-imports" to "disabled",
                "ij_kotlin_allow_trailing_comma" to "true",
                "ij_kotlin_allow_trailing_comma_on_call_site" to "true",
                "ktlint_standard_argument-list-wrapping" to "disabled",
                "ktlint_standard_filename" to "disabled",
            ),
        ktlintVersion = ktlintVersion,
    )
}
