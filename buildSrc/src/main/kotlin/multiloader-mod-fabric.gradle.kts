plugins {
    id("fabric-loom")
    id("com.modrinth.minotaur")
}

val minecraftVersion by properties
val fabricLoaderVersion by properties
val fabricVersion by properties
val parchmentVersion: String by properties
val parchmentMinecraft = property("parchmentMinecraft").let {
    if (it !is String || it.isBlank()) minecraftVersion else it
}

val disableParchment = property("disableParchment").let {
    if (it !is String || it.isBlank()) "false" else it
}.toBoolean()

var core = project(":core")
val common = project.parent!!.project("common")

dependencies {
    minecraft("com.mojang:minecraft:$minecraftVersion")
    mappings(loom.layered {
        officialMojangMappings()
        if (!disableParchment) parchment("org.parchmentmc.data:parchment-$parchmentMinecraft:$parchmentVersion@zip")
    })

    modImplementation("net.fabricmc:fabric-loader:$fabricLoaderVersion")
    modImplementation("net.fabricmc.fabric-api:fabric-api:$fabricVersion")

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
    loaders.set(listOf("fabric"))
    uploadFile = tasks.remapJar.get()
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
