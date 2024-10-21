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
include(":domain:login")
include(":core:designsystem")
include(":feature:board")
