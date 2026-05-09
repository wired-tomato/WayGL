import org.slf4j.event.Level

plugins {
    id("net.neoforged.moddev")
    id("com.modrinth.minotaur")
}


val modId: String by properties
val minecraftVersion: String by properties
val neoforgeVersion: String by properties
val parchmentVersion: String = (findProperty("parchmentVersion") as? String) ?: "unspecified"
val parchmentMinecraft = findProperty("parchmentMinecraft").let {
    if (it !is String || it.isBlank()) minecraftVersion else it
}

var core = project(":core")
val common = project.parent!!.project("common")

neoForge {
    version = neoforgeVersion

    val at = file("src/main/resources/accesstransformer.cfg")
    if (at.exists()) {
        accessTransformers.from(at)
    }

    if (parchmentVersion != "unspecified") {
        parchment {
            minecraftVersion = parchmentMinecraft
            mappingsVersion = parchmentVersion
        }
    }

    runs {
        val client by creating {
            client()

            systemProperty("neoforge.enableGameTestNamespaces", modId)
        }

        val server by creating {
            server()
            programArgument("--nogui")

            systemProperty("neoforge.enableGameTestNamespaces", modId)
        }

        configureEach {
            logLevel = Level.DEBUG
        }
    }

    mods {
        create(modId) {
            sourceSet(sourceSets.main.get())
        }
    }
}

dependencies {
    compileOnly(core)
    compileOnly(common)
}

val minecraftVersions = property("minecraftVersions").toString().split(',')
val modDependencies = property("modDependencies").toString().split(',')

modrinth {
    token = System.getenv("MODRINTH_TOKEN")
    projectId = "waygl"
    versionNumber = version.toString()
    versionType = "release"
    gameVersions = minecraftVersions
    loaders.set(listOf("neoforge"))
    uploadFile = tasks.jar.get()
    dependencies {
        modDependencies.forEach {
            required.project(it)
        }
    }
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
