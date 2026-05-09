plugins {
    java
    id("org.spongepowered.gradle.vanilla")
}

val modId: String by project
val minecraftVersion: String by project

val core = project(":core")

minecraft {
    version(minecraftVersion)

    val accessWidener = file("src/main/resources/${modId}.accesswidener")
    if (accessWidener.exists()) {
        accessWideners(accessWidener)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    compileOnly("org.spongepowered:mixin:0.8.7")
    compileOnly("io.github.llamalad7:mixinextras-common:0.4.1")
    annotationProcessor("io.github.llamalad7:mixinextras-common:0.4.1")

    compileOnly(core)
}
