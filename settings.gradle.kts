pluginManagement {
    includeBuild("build-logic")
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven("https://androidx.dev/storage/compose-compiler/repository/") {
            content {
                includeGroup("androidx.compose.compiler")
            }
        }
        maven("https://raw.githubusercontent.com/MetaCubeX/maven-backup/main/releases") {
            content {
                includeGroupAndSubgroups("com.github.kr328")
            }
        }
    }
}

includeBuild("golang-android-plugin")

rootProject.name = "MihomoForAndroid"

include(":app")
include(":core")
include(":service")
include(":design")
include(":common")
include(":hideapi")
