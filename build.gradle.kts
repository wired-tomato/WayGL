import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("fabric-loom") version "1.6-SNAPSHOT"
    kotlin("jvm") version "2.0.0"
    kotlin("plugin.serialization") version "2.0.0"
    alias(libs.plugins.minotaur)
    `maven-publish`
}

group = project.properties["maven_group"]!!
version = project.properties["mod_version"]!!
base.archivesName.set(project.properties["archives_base_name"] as String)
description = "Make GLFW use wayland on supported systems"
val modid = project.properties["modid"]!! as String

repositories {
    mavenCentral()
    maven("https://maven.terraformersmc.com/releases")
    maven("https://api.modrinth.com/maven")
    maven("https://oss.sonatype.org/content/repositories/snapshots")
    maven("https://maven.isxander.dev/releases") {
        name = "Xander Maven"
    }
}

dependencies {
    minecraft(libs.minecraft)
    mappings(variantOf(libs.yarn.mappings) { classifier("v2") })
    modImplementation(libs.fabric.loader)
    modImplementation(libs.fabric.kt)

    modImplementation(libs.yacl)
    modImplementation(libs.mod.menu)
    implementation("org.lwjgl:lwjgl-glfw:3.3.3")
}

tasks {
    processResources {
        inputs.property("version", project.version)

        filesMatching("fabric.mod.json") {
            expand("version" to project.version)
        }
    }

    val targetJavaVersion = 17
    withType<JavaCompile>().configureEach {
        options.encoding = "UTF-8"
        options.release.set(targetJavaVersion)
    }

    withType<KotlinCompile>().all {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }

    java {
        toolchain.languageVersion.set(JavaLanguageVersion.of(JavaVersion.toVersion(targetJavaVersion).toString()))
        withSourcesJar()
    }

    jar {
        from("LICENSE") {
            rename { "${it}_${project.base.archivesName.get()}"}
        }
    }
}

modrinth {
    token.set(System.getenv("MODRINTH_TOKEN"))
    projectId.set("waygl")
    versionNumber.set(project.version.toString())
    versionType.set("release")
    uploadFile.set(tasks.remapJar)
    gameVersions.addAll("1.20", "1.20.1", "1.20.2", "1.20.3", "1.20.4")
    loaders.add("fabric")
    dependencies {
        required.project("fabric-language-kotlin")
        required.project("yacl")
    }
}

publishing {
    publications.create<MavenPublication>("waygl") {
        groupId = project.group.toString()
        artifactId = project.name.lowercase()
        version = project.version.toString()

        from(components["java"])
    }

    repositories {
        maven("https://maven.wiredtomato.net/releases") {
            name = "wtRepo"
            credentials {
                username = System.getenv("MAVEN_USERNAME")
                password = System.getenv("MAVEN_PASSWORD")
            }
        }
    }
}
