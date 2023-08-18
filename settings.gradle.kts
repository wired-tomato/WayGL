pluginManagement {
    repositories {
        maven("https://maven.fabricmc.net/") {
            name = "Fabric"
        }
        mavenCentral()
        gradlePluginPortal()
        maven("https://maven.teamvoided.org/releases")
    }
}

rootProject.name = "TeamVoidedTemplate"

