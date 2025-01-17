plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    id("com.google.devtools.ksp")
    id("net.neoforged.moddev")
}

val neo_form_version: String by rootProject.properties
val parchment_minecraft: String by rootProject.properties
val parchment_version: String by rootProject.properties
val yacl_version: String by rootProject.properties

neoForge {
    setNeoFormVersion(neo_form_version)

    val at = file("src/main/resources/META-INF/accesstransformer.cfg")
    if (at.exists()) {
        accessTransformers.from(at.absolutePath)
    }

    parchment {
        minecraftVersion = parchment_minecraft
        mappingsVersion = parchment_version
    }
}

dependencies {
    compileOnly("org.spongepowered:mixin:0.8.7")
    compileOnly("io.github.llamalad7:mixinextras-common:0.4.1")
    annotationProcessor("io.github.llamalad7:mixinextras-common:0.4.1")

    compileOnly("org.ow2.asm:asm-tree:9.6")

    implementation("dev.isxander:yet-another-config-lib:${yacl_version}-neoforge")
}

sourceSets.main {
    resources {
        srcDir("src/main/generated/resources")
    }
}
