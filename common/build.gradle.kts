plugins {
    alias(libs.plugins.android.library)
}

android.namespace = "com.github.kr328.clash.common"

dependencies {
    compileOnly(project(":hideapi"))

    implementation(libs.kotlin.coroutine)
    implementation(libs.androidx.core)
}
