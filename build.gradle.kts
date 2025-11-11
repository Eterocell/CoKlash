import com.android.build.gradle.AppExtension
import com.android.build.gradle.BaseExtension
import java.util.*

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.kotlin.kapt) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.ksp) apply false

    id("build-logic.root-project")
}

subprojects {

    val isApp = name == "app"
    val isHideApi = name == "hideapi"

    plugins.apply(if (isApp) "com.android.application" else "com.android.library")
    extensions.configure<BaseExtension> {
        defaultConfig {
            if (isApp) {
                applicationId = "com.eterocell.mihomoforandroid"
            }

            minSdk = 23
            targetSdk = 36
            buildToolsVersion = "36.1.0"

            versionName = "3.0.0-beta05"
            versionCode = "03000050".toInt()

            if (!isHideApi) {
                vectorDrawables {
                    useSupportLibrary = true
                }
            }

            resValue("string", "release_name", "v$versionName")
            resValue("integer", "release_code", "$versionCode")

            ndk {
                abiFilters += listOf("arm64-v8a", "armeabi-v7a", "x86", "x86_64")
            }

            externalNativeBuild {
                cmake {
                    abiFilters("arm64-v8a", "armeabi-v7a", "x86", "x86_64")
                }
            }

            if (!isApp) {
                consumerProguardFiles("consumer-rules.pro")
            }
        }

        ndkVersion = "29.0.14206865"

        compileSdkVersion(36)

        if (isApp) {
            packagingOptions {
                resources {
                    excludes.add("DebugProbesKt.bin")
                }
            }
        }

        productFlavors {
            flavorDimensions("feature")

            create("alpha") {

                isDefault = true
                dimension = flavorDimensionList[0]
                versionNameSuffix = ".Meta.Alpha"

                buildConfigField("boolean", "PREMIUM", "Boolean.parseBoolean(\"false\")")

                if (project.name == "app" || project.name == "design") {
                    resValue("string", "launch_name", "@string/launch_name_meta")
                    resValue("string", "application_name", "@string/application_name_meta")
                }

                if (isApp) {
                    applicationIdSuffix = ".meta"
                }
            }
        }

        sourceSets {
            getByName("alpha") {
                java.srcDirs("src/foss/java")
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
                signingConfig = signingConfigs.findByName("release")
                proguardFiles(
                    getDefaultProguardFile("proguard-android-optimize.txt"),
                    "proguard-rules.pro",
                )
            }
            named("debug") {
                versionNameSuffix = ".debug"
            }
        }

        buildFeatures.apply {
            buildConfig = true
            compose = !isHideApi
            viewBinding = !isHideApi
            dataBinding {
                isEnabled = !isHideApi
            }
        }

        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_11
            targetCompatibility = JavaVersion.VERSION_11
            isCoreLibraryDesugaringEnabled = true
        }

        if (isApp) {
            this as AppExtension

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

        val libs: VersionCatalog = rootProject.extensions.getByType<VersionCatalogsExtension>().named("libs")

        dependencies {
            add("coreLibraryDesugaring", libs.findLibrary("android-desugar-jdk-libs").get())
        }

        if (!isHideApi) {
            plugins.apply("org.jetbrains.kotlin.plugin.compose")

            dependencies {
                val composeBom = libs.findLibrary("androidx.compose.bom").get()
                add("implementation", platform(composeBom))
                add("testImplementation", platform(composeBom))
                add("androidTestImplementation", platform(composeBom))
                add("implementation", libs.findLibrary("androidx.compose.ui.tooling.preview").get())
                add("debugImplementation", libs.findLibrary("androidx.compose.ui.tooling").get())
            }
        }
    }

    // Disable androidTest tasks if no androidTest source directory exists
    afterEvaluate {
        if (!isHideApi && !project.projectDir.resolve("src/androidTest").exists()) {
            tasks.matching { it.name.contains("AndroidTest") }.configureEach {
                enabled = false
            }
        }
    }

    // Disable androidTest tasks if no androidTest source directory exists
    afterEvaluate {
        if (!isHideApi && !project.projectDir.resolve("src/androidTest").exists()) {
            tasks.matching { it.name.contains("AndroidTest") }.configureEach {
                enabled = false
            }
        }
    }
}
