import java.io.File

plugins {
    id("build-logic.root-project.base")
    id("com.diffplug.spotless")
}

val ktlintVersion = "1.6.0"
val genCopyright = false

allprojects {
    configureSpotless {
        if (genCopyright) {
            copyrightForKts(
                excludeTargets = setOf("**/build/**/*.kts", "**/spotless/copyright.kts"),
                licenseHeaderFile = rootProject.file("spotless/copyright.kt").takeIf(File::exists),
                licenseHeaderConfig = {
                    updateYearWithLatest(true)
                    yearSeparator("-")
                },
            )

            copyrightForXml(
                excludeTargets =
                    setOf(
                        "**/build/**/*.xml",
                        "**/spotless/copyright.xml",
                        "**/.idea/**/*.xml",
                    ),
                licenseHeaderFile = rootProject.file("spotless/copyright.xml").takeIf(File::exists),
                licenseHeaderConfig = {
                    updateYearWithLatest(true)
                    yearSeparator("-")
                },
            )
        }

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
            licenseHeaderFile =
                if (genCopyright) {
                    rootProject.file("spotless/copyright.kt").takeIf(File::exists)
                } else {
                    null
                },
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
}
