val modId: String by project.properties
val modName: String by project.properties
val modDescription: String by project.properties
val modVersion: String by project.properties
val license: String by project.properties
val minecraftVersion: String by project.properties
val minecraftVersionRange: String by project.properties
val author: String by project.properties
val credits: String by project.properties
val loaderVersionRange: String by project.properties
val neoforgeVersion: String by project.properties
val fabricLoaderVersion: String by project.properties
val fabricVersion: String by project.properties
val clothConfigVersion: String by project.properties
val minecraftVersionMajor: String by project.properties
val javaVersion: String by project.properties
val neoforgeVersionRange: String by project.properties

val props = mapOf(
    "modId" to modId,
    "modName" to modName,
    "modDescription" to modDescription,
    "license" to license,
    "minecraftVersion" to minecraftVersion,
    "minecraftVersionRange" to minecraftVersionRange,
    "author" to author,
    "credits" to credits,
    "loaderVersionRange" to loaderVersionRange,
    "neoforgeVersion" to neoforgeVersion,
    "fabricLoaderVersion" to fabricLoaderVersion,
    "fabricVersion" to fabricVersion,
    "version" to modVersion,
    "clothConfigVersion" to clothConfigVersion,
    "minecraftVersionMajor" to minecraftVersionMajor,
    "javaVersion" to javaVersion,
    "neoforgeVersionRange" to neoforgeVersionRange
)

subprojects {
    apply(plugin = "java")

    group = "net.wiredtomato"
    version = modVersion

    extensions.getByType<BasePluginExtension>().apply {
        archivesName.set("${modId}-${name}")
    }

    repositories {
        mavenCentral()
        maven("https://maven.parchmentmc.org") {
            name = "ParchmentMC"
        }
    }

    dependencies {
        "compileOnly"("com.google.auto.service:auto-service-annotations:1.1.1")
        "annotationProcessor"("com.google.auto.service:auto-service:1.1.1")
    }


    extensions.getByType<JavaPluginExtension>().apply {
        withSourcesJar()

        toolchain {
            languageVersion.set(JavaLanguageVersion.of(javaVersion))
        }
    }

    tasks.withType<ProcessResources> {
        inputs.properties(props)

        filesMatching(listOf("fabric.mod.json", "META-INF/neoforge.mods.toml")) {
            expand(props)
        }
    }

    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
        options.release.set(javaVersion.toInt())
    }
}
