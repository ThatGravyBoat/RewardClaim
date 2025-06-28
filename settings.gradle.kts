enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "RewardClaim"

pluginManagement {
    repositories {
        maven("https://maven.fabricmc.net/")
        maven("https://maven.teamresourceful.com/repository/maven-public/")
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    versionCatalogs {
        create("libs")
    }
}