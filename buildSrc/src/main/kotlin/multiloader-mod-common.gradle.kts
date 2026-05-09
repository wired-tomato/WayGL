plugins {
    id("net.neoforged.moddev")
}

val mcVersion = property("minecraftVersion") as String
val neoformVersion: String by properties
val parchmentVersion: String = (findProperty("parchmentVersion") as? String) ?: "unspecified"
val parchmentMinecraft = findProperty("parchmentMinecraft").let {
    if (it !is String || it.isBlank()) mcVersion else it
}

val disableParchment = (findProperty("disableParchment") as String?).toBoolean()

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

    if (!disableParchment && parchmentVersion != "unspecified") {
        parchment {
            minecraftVersion = parchmentMinecraft
            mappingsVersion = parchmentVersion
        }
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
