pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "recipes"
include(":app")
include(":ui:recipe")
include(":ui:settings")
include(":designsystem")
include(":data")
include(":domain")
include(":model")
