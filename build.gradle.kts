import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "2.1.0"
    kotlin("plugin.serialization") version "2.1.0"
    id("com.google.devtools.ksp") version "2.1.0-1.0.29" apply false
    id("fabric-loom") version "1.9-SNAPSHOT" apply false
    id("net.neoforged.moddev") version "2.0.66-beta" apply false
    id("com.github.johnrengelman.shadow") version "8.1.1" apply false
    `maven-publish`
}

repositories {
    mavenCentral()
}

val minecraft_version: String by rootProject.properties
val parchment_minecraft: String by rootProject.properties
val parchment_version: String by rootProject.properties

val mod_id: String by rootProject.properties
val mod_name: String by rootProject.properties
val mod_description: String by rootProject.properties
val mod_version: String by rootProject.properties
val mod_author: String by rootProject.properties

val fabric_loader_version: String by rootProject.properties
val fabric_version: String by rootProject.properties
val flk_version: String by rootProject.properties

val minecraft_version_range: String by rootProject.properties
val neoforge_version: String by rootProject.properties
val kff_version: String by rootProject.properties
val kff_loader_version_range: String by rootProject.properties
val credits: String by rootProject.properties

val yacl_version: String by rootProject.properties

val license: String by rootProject.properties
val java_version: String by rootProject.properties

subprojects {
    apply(plugin = "java")
    apply(plugin = "kotlin")
    apply(plugin = "org.jetbrains.kotlin.plugin.serialization")
    apply(plugin = "com.google.devtools.ksp")
    apply(plugin = "maven-publish")

    group = rootProject.property("group").toString()
    base.archivesName = "$mod_id-${path.replace(":", "-")}"
    version = mod_version

    java {
        withSourcesJar()
    }

    repositories {
        mavenCentral()
        maven("https://maven.wiredtomato.net/snapshots")
        maven("https://maven.parchmentmc.org")
        maven("https://maven.isxander.dev/releases") {
            name = "Xander Maven"
        }
    }

    dependencies {
        if (!projectDir.path.contains("deplatformed")) {
            implementation(project(":deplatformed-api"))
            "ksp"(project(":deplatformed-ksp"))
        }
    }

    tasks {
        withType<JavaCompile> {
            options.encoding = "UTF-8"
            options.release.set(java_version.toInt())
        }

        withType<KotlinCompile>().configureEach {
            compilerOptions {
                jvmTarget = JvmTarget.fromTarget(java_version)
            }
        }

        processResources {
            val propertyMap = mapOf(
                "version" to project.version,
                "mod_id" to mod_id,
                "mod_name" to mod_name,
                "mod_description" to mod_description,
                "mod_author" to mod_author,
                "license" to license,
                "fabric_loader_version" to fabric_loader_version,
                "flk_version" to flk_version,
                "minecraft_version" to minecraft_version,
                "java_version" to java_version,
                "kff_version" to kff_version,
                "kff_loader_version_range" to kff_loader_version_range,
                "neoforge_version" to neoforge_version,
                "minecraft_version_range" to minecraft_version_range,
                "credits" to credits,
                "yacl_version" to yacl_version
            )

            inputs.properties(propertyMap)
            filesMatching(listOf("${mod_id}.mixins.json", "fabric.mod.json", "${mod_id}.fabric.mixins.json", "META-INF/neoforge.mods.toml")) {
                expand(propertyMap)
            }
        }
    }

    publishing {
        publications {
            create<MavenPublication>(mod_id) {
                groupId = group.toString()
                artifactId = mod_id
                version = if (mod_version.contains("beta")) "$mod_version-SNAPSHOT" else mod_version

                from(components["java"])
            }

            repositories {
                maven("https://maven.wiredtomato.net/releases") {
                    credentials {
                        username = System.getenv("MAVEN_USERNAME")
                        password = System.getenv("MAVEN_PASSWORD")
                    }
                }
            }
        }
    }
}
