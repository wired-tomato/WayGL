plugins {
    id("net.fabricmc.fabric-loom")
    id("com.modrinth.minotaur")
}

val minecraftVersion by properties
val fabricLoaderVersion by properties
val fabricVersion by properties
val parchmentVersion: String = (findProperty("parchmentVersion") as? String) ?: "unspecified"
val parchmentMinecraft = findProperty("parchmentMinecraft").let {
    if (it !is String || it.isBlank()) minecraftVersion else it
}

val disableParchment = findProperty("disableParchment").let {
    if (it !is String || it.isBlank()) "false" else it
}.toBoolean()

var core = project(":core")
val common = project.parent!!.project("common")

dependencies {
    minecraft("com.mojang:minecraft:$minecraftVersion")

    implementation("net.fabricmc:fabric-loader:$fabricLoaderVersion")
    implementation("net.fabricmc.fabric-api:fabric-api:$fabricVersion")

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
