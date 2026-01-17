plugins {
    java
    id("net.minecraftforge.gradle")
    //id("org.spongepowered.mixin")
}

val modId: String by project
val minecraftVersion: String by project
val forgeVersion: String by project

/*
mixin {
    add(sourceSets.main.get(), "${modId}.refmap.json")

    config("${modId}.mixins.json")
    config("${modId}.forge.mixins.json")
}
 */

var core = project(":core")
val common = project.parent!!.project("common")

minecraft {
    mappings("official", minecraftVersion)

    val at = file("src/main/resources/META-INF/accesstransformer.cfg")
    if (at.exists()) {
        accessTransformer.from(at)
    }

    runs {
        configureEach {
            workingDir.set(file("run"))
        }

        create("client")
    }
}

repositories {
    mavenCentral()

    maven("https://maven.minecraftforge.net/")
    maven("https://libraries.minecraft.net/")
}

dependencies {
    //"minecraft"("net.minecraftforge:forge:${minecraftVersion}-${forgeVersion}")
    annotationProcessor("net.minecraftforge:eventbus-validator:7.0-beta.12")

    compileOnly(core)
    compileOnly(common)
}

tasks.compileJava {
    val coreJava = core.tasks.compileJava.get()
    val commonJava = common.tasks.compileJava.get()

    dependsOn(coreJava)
    dependsOn(commonJava)

    source(coreJava.source)
    source(commonJava.source)
}

tasks.processResources {
    val coreResources = core.tasks.processResources.get()
    val commonResources = common.tasks.processResources.get()

    dependsOn(coreResources)
    dependsOn(commonResources)

    from(coreResources)
    from(commonResources)
}

tasks.getByName<Jar>("sourcesJar") {
    val coreSources = core.tasks.getByName<Jar>("sourcesJar")
    val commonSources = common.tasks.getByName<Jar>("sourcesJar")

    dependsOn(coreSources)
    dependsOn(commonSources)

    from(coreSources.archiveFile.map { zipTree(it) })
    from(commonSources.archiveFile.map { zipTree(it) })
}
