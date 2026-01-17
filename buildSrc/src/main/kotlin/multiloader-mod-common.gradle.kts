import gradle.kotlin.dsl.accessors._65a83017089d65cb5bffe24cf4d9e02a.compileJava
import gradle.kotlin.dsl.accessors._65a83017089d65cb5bffe24cf4d9e02a.compileOnly
import gradle.kotlin.dsl.accessors._65a83017089d65cb5bffe24cf4d9e02a.processResources
import org.gradle.util.VersionNumber

plugins {
    id("net.neoforged.moddev")
}

val mcVersion = property("minecraftVersion") as String
val neoformVersion: String by properties
val parchmentVersion: String by properties
val parchmentMinecraft = property("parchmentMinecraft").let {
    if (it !is String || it.isBlank()) mcVersion else it
}

val core = project(":core")

repositories {
    mavenCentral()
}

neoForge {
    neoFormVersion = "$mcVersion-$neoformVersion"

    val at = file("src/main/resources/accesstransformer.cfg")
    if (at.exists()) {
        accessTransformers.from(at)
    }

    parchment {
        minecraftVersion = parchmentMinecraft
        mappingsVersion = parchmentVersion
    }
}

dependencies {
    compileOnly("org.spongepowered:mixin:0.8.7")
    compileOnly("io.github.llamalad7:mixinextras-common:0.4.1")
    annotationProcessor("io.github.llamalad7:mixinextras-common:0.4.1")

    compileOnly(core)
}

java {
    withSourcesJar()
}

sourceSets.main {
    resources {
        srcDirs(
            "src/main/generated/resources/client",
            "src/main/generated/resources/server",
        )
    }
}
