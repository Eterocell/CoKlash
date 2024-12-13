import com.github.kr328.golang.GolangBuildTask
import com.github.kr328.golang.GolangPlugin
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.serialization)
    id("golang-android")
}

val golangSource = file("src/main/golang/native")

golang {
    sourceSets {
        create("alpha") {
            tags.set(listOf("foss", "with_gvisor", "cmfa"))
            srcDir.set(file("src/foss/golang"))
            fileName.set("libclash.so")
            packageName.set("cfa/native")
        }
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_11)
    }
}

android {
    namespace = "com.github.kr328.clash.core"
    productFlavors {
        all {
            externalNativeBuild {
                cmake {
                    arguments += listOf(
                        "-DGO_SOURCE:STRING=$golangSource",
                        "-DGO_OUTPUT:STRING=${GolangPlugin.outputDirOf(project, null, null)}",
                        "-DFLAVOR_NAME:STRING=$name",
                        "-DANDROID_SUPPORT_FLEXIBLE_PAGE_SIZES=ON",
                    )
                }
            }
        }
    }

    externalNativeBuild {
        cmake {
            version = "3.31.1"
            path = file("src/main/cpp/CMakeLists.txt")
        }
    }
}

dependencies {
    implementation(project(":common"))

    implementation(libs.androidx.core)
    implementation(libs.kotlin.coroutine)
    implementation(libs.kotlin.serialization.json)
}

afterEvaluate {
    tasks.withType(GolangBuildTask::class.java).forEach {
        it.inputs.dir(golangSource)
    }
}
