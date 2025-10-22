plugins {
    id("com.vanniktech.maven.publish")
}

afterEvaluate {
    mavenPublishing {
        configureBasedOnAppliedPlugins(sourcesJar = true, javadocJar = false)

        publishToMavenCentral()

        signAllPublications()

        pom {
            val githubUrl = "https://github.com/avan1235/compose-extensions"

            name = "Compose Multiplatform Extensions"
            description = "Helper functions and extensions when working with compose-multiplatform projects"
            url = githubUrl

            licenses {
                license {
                    name = "MIT"
                    url = "https://opensource.org/licenses/MIT"
                }
            }
            developers {
                developer {
                    id = "avan1235"
                    name = "Maciej Procyk"
                    email = "maciej@procyk.in"
                    url = "https://procyk.in"
                }
            }
            issueManagement {
                system = "GitHub"
                url = "$githubUrl/issues"
            }
            scm {
                url = githubUrl
            }
        }
    }
}
