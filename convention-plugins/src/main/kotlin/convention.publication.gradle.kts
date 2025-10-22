import java.util.*

plugins {
    id("maven-publish") apply true
    id("signing") apply true
}

ext["signing.keyId"] = null
ext["signing.password"] = null
ext["signing.key"] = null
ext["ossrhUsername"] = null
ext["ossrhPassword"] = null

val secretPropsFile = project.rootProject.file("local.properties")
if (secretPropsFile.exists()) {
    secretPropsFile.reader().use {
        Properties().apply { load(it) }
    }.onEach { (name, value) ->
        ext[name.toString()] = value
    }
} else {
    ext["signing.keyId"] = System.getenv("SIGNING_KEY_ID")
    ext["signing.password"] = System.getenv("SIGNING_PASSWORD")
    ext["signing.key"] = System.getenv("SIGNING_KEY")
    ext["ossrhUsername"] = System.getenv("OSSRH_USERNAME")
    ext["ossrhPassword"] = System.getenv("OSSRH_PASSWORD")
}

operator fun ExtraPropertiesExtension.invoke(name: String) = ext[name]?.toString()

afterEvaluate {
    publishing {
        repositories {
            maven {
                name = "sonatype"
                setUrl("https://ossrh-staging-api.central.sonatype.com/service/local/staging/deploy/maven2/")
                credentials {
                    username = ext("ossrhUsername")
                    password = ext("ossrhPassword")
                }
            }
            mavenLocal()
        }

        publications.withType<MavenPublication> {
            val publication = this
            val javadocJar = tasks.register("${publication.name}JavadocJar", Jar::class) {
                archiveClassifier = "javadoc"
                archiveBaseName = "${archiveBaseName.get()}-${publication.name}"
            }
            artifact(javadocJar)
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

    signing {
        useInMemoryPgpKeys(
            ext("signing.keyId"),
            ext("signing.key"),
            ext("signing.password"),
        )
        sign(publishing.publications)
    }
}
