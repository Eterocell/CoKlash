import java.net.URI

plugins {
    id("build-logic.root-project.base")
    id("build-logic.spotless")
}

val wrapper: Wrapper by tasks.named<Wrapper>("wrapper") {
    gradleVersion = "9.0.0"
    distributionType = Wrapper.DistributionType.ALL
    val sha256 =
        URI("$distributionUrl.sha256")
            .toURL()
            .openStream()
            .use { it.reader().readText().trim() }
    distributionSha256Sum = sha256
}

tasks.clean {
    allprojects.forEach {
        delete(it.layout.buildDirectory)
        delete(files("${it.layout.projectDirectory.asFile.absolutePath}/.cxx"))
    }
}
