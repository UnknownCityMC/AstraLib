rootProject.name = "astralib"

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        maven("https://repo.papermc.io/repository/maven-public/")
        maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")

        maven("https://oss.sonatype.org/content/repositories/snapshots/") {
            name = "sonatype-snapshots"
            mavenContent {
                snapshotsOnly()
            }
        }
    }
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)


}

include(":astralib-paper-api")
include(":astralib-paper-plugin")
include(":astralib-velocity-api")
include(":astralib-velocity-plugin")
include(":astralib-common")
include("paper-plugin")
