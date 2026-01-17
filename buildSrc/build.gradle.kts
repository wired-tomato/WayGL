plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()

    maven("https://maven.fabricmc.net") {
        name = "FabricMC"
    }

    maven("https://maven.teamvoided.org/etc") {
        name = "TeamVoidedEtc"
    }

    maven("https://maven.minecraftforge.net/") {
        name = "MinecraftForge"
    }

    maven("https://repo.spongepowered.org/repository/maven-public/") {
        name = "SpongeMC"
    }
}

dependencies {
    fun pluginDep(id: String, version: String) = "${id}:${id}.gradle.plugin:${version}"

    implementation(pluginDep("fabric-loom", "1.14-SNAPSHOT"))
    implementation(pluginDep("net.neoforged.moddev", "2.0.116"))
    implementation(pluginDep("com.modrinth.minotaur", "2.+"))
    implementation(pluginDep("org.spongepowered.gradle.vanilla", "0.2.2"))
    implementation(pluginDep("net.minecraftforge.gradle", "7.0.0-rc.2"))
    implementation(pluginDep("org.spongepowered.mixin", "0.7-SNAPSHOT"))
}
