plugins {
    `kotlin-dsl`
}

dependencies {
    // DSL
    compileOnly(embeddedKotlin("gradle-plugin"))
    implementation(libs.gradle.plugin.android)

    implementation(libs.gradle.plugin.spotless)
}
