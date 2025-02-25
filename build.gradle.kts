import com.android.build.gradle.AppExtension
import com.android.build.gradle.BaseExtension
import java.util.*

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.kapt) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.ksp) apply false

    id("build-logic.root-project")
}

subprojects {

    val isApp = name == "app"

    apply(plugin = if (isApp) "com.android.application" else "com.android.library")

    extensions.configure<BaseExtension> {
        defaultConfig {
            if (isApp) {
                applicationId = "com.eterocell.mihomoforandroid"
            }

            minSdk = 21
            targetSdk = 35
            buildToolsVersion = "35.0.1"

            versionName = "3.0.0-alpha04"
            versionCode = "03000004".toInt()

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
            } else {
                setProperty("archivesBaseName", "cmfa-$versionName")
            }
        }

        splits {
            abi {
                isEnable = true
                reset()
                include("arm64-v8a", "x86_64", "armeabi-v7a", "x86")
                isUniversalApk = true
            }
        }

        ndkVersion = "28.0.13004108"

        compileSdkVersion(35)

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
            viewBinding {
                isEnabled = name != "hideapi"
            }
            dataBinding {
                isEnabled = name != "hideapi"
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
                    isEnable = true
                    isUniversalApk = true
                    reset()
                    include("arm64-v8a", "armeabi-v7a", "x86", "x86_64")
                }
            }
        }

        dependencies.add("coreLibraryDesugaring", "com.android.tools:desugar_jdk_libs:2.1.5")
    }
}
