plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    id("fabric-loom")
}

val minecraft_version: String by project.properties
val minecraft_versions = project.properties["minecraft_versions"].toString().split(',')
val parchment_minecraft: String by rootProject.properties
val parchment_version: String by rootProject.properties
val fabric_loader_version: String by rootProject.properties
val fabric_version: String by rootProject.properties
val flk_version: String by rootProject.properties
val mod_id: String by rootProject.properties
val mod_name: String by rootProject.properties
val mod_description: String by rootProject.properties
val mod_author: String by rootProject.properties
val license: String by rootProject.properties
val java_version: String by rootProject.properties
val yacl_version: String by rootProject.properties
val modmenu_version: String by rootProject.properties

val common = project(":common")

repositories {
    maven("https://maven.terraformersmc.com/")
}

dependencies {
    minecraft("com.mojang:minecraft:$minecraft_version")
    mappings(loom.layered {
        officialMojangMappings()
        parchment("org.parchmentmc.data:parchment-$parchment_minecraft:$parchment_version@zip")
    })

    modImplementation("net.fabricmc:fabric-loader:$fabric_loader_version")
    modImplementation("net.fabricmc.fabric-api:fabric-api:$fabric_version")
    modImplementation("net.fabricmc:fabric-language-kotlin:$flk_version")

    modImplementation("dev.isxander:yet-another-config-lib:${yacl_version}-fabric")
    modImplementation("com.terraformersmc:modmenu:${modmenu_version}")

    compileOnly(common)
}

loom {
    val aw = common.file("src/main/resources/$mod_id.accesswidener")
    if (aw.exists()) {
        accessWidenerPath.set(aw)
    }

    mixin {
        defaultRefmapName.set("$mod_id.refmap.json")
    }

    runs {
        val client by getting {
            client()
            configName = "Fabric Client"
            ideConfigGenerated(true)
            runDir("runs/client")
        }

        val server by getting {
            server()
            configName = "Fabric Server"
            ideConfigGenerated(true)
            runDir("runs/server")
        }
    }
}

modrinth {
    token = System.getenv("MODRINTH_TOKEN")
    projectId = "waygl"
    versionNumber = version.toString()
    versionType = "release"
    gameVersions = minecraft_versions
    loaders.set(listOf("fabric"))
    // uploadFile = tasks.jar.get()
    uploadFile = tasks.remapJar.get()
    dependencies {
        required.project("fabric-language-kotlin")
        required.project("fabric-api")
        required.project("yacl")
    }
}

tasks.jar {
    archiveClassifier.set("dev")
}

tasks.compileJava {
    val commonJava = common.tasks.compileJava.get()
    dependsOn(commonJava)
    source(commonJava.source)
}

tasks.compileKotlin {
    val commonKotlin = common.tasks.compileKotlin.get()
    dependsOn(commonKotlin)
    source(commonKotlin.sources)
}

tasks.processResources {
    val commonResources = common.tasks.processResources.get()
    dependsOn(commonResources)
    from(commonResources)
}

tasks.sourcesJar {
    val commonSources = common.tasks.getByName<Jar>("sourcesJar")
    dependsOn(commonSources)
    from(commonSources.archiveFile.map { zipTree(it) })
}
