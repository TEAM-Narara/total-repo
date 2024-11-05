pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://www.jitpack.io") }
    }
}

rootProject.name = "SuperBoard"
include(":app")
include(":core")
include(":feature")
include(":domain")
include(":core:database")
include(":core:network")
include(":core:data")
include(":core:datastore")
include(":feature:login")
include(":core:model")
include(":core:designsystem")
include(":feature:home")
include(":feature:board")
include(":core:ui")
include(":feature:card")
include(":feature:notification")
include(":domain:login")
include(":domain:logout")
include(":domain:workspace")
include(":domain:board")
include(":domain:member")
include(":domain:comment")
include(":domain:card")
include(":domain:list")
include(":domain:home")
