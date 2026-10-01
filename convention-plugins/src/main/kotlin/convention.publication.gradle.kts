import com.vanniktech.maven.publish.Checksum

plugins {
    id("com.vanniktech.maven.publish")
}

mavenPublishing {
    checksums(Checksum.MD5, Checksum.SHA1, Checksum.SHA256, Checksum.SHA512)
    excludeSignatureChecksums(false)
    publishToMavenCentral(automaticRelease = true)

    signAllPublications()

    pom {
        val githubUrl = "https://github.com/avan1235/compose-extensions"

        name.set("Compose Multiplatform Extensions")
        description.set("Helper functions and extensions when working with compose-multiplatform projects")
        inceptionYear.set("2023")
        url.set(githubUrl)

        licenses {
            license {
                name.set("MIT")
                url.set("https://opensource.org/licenses/MIT")
                distribution.set("https://opensource.org/licenses/MIT")
            }
        }
        developers {
            developer {
                id.set("avan1235")
                name.set("Maciej Procyk")
                email.set("maciej@procyk.in")
                url.set("https://procyk.in")
            }
        }
        issueManagement {
            system.set("GitHub")
            url.set("$githubUrl/issues")
        }
        scm {
            url.set(githubUrl)
            connection.set("scm:git:git://github.com/avan1235/compose-extensions.git")
            developerConnection.set("scm:git:ssh://git@github.com/avan1235/compose-extensions.git")
        }
    }
}
