import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("fabric-loom") version "1.3.8"
    kotlin("jvm") version "1.9.0"
    kotlin("plugin.serialization") version "1.9.0"
    id("org.teamvoided.iridium") version "3.1.9"
}

group = project.properties["maven_group"]!!
version = project.properties["mod_version"]!!
base.archivesName.set(project.properties["archives_base_name"] as String)
description = "Make GLFW use wayland on supported systems"
val modid = project.properties["modid"]!! as String

repositories {
    mavenCentral()
}

modSettings {
    modId(modid)
    modName("WayGL")

    entrypoint("client", "net.wiredtomato.waygl.WayGL::clientInit")
    mixinFile("waygl.mixins.json")

    mutation {
        this.depends["minecraft"] = ">=1.19.3"
        //FAPI is not required
        this.depends.remove("fabric-api")
    }
}

dependencies {
    implementation("org.lwjgl:lwjgl-glfw:3.3.2")
}

tasks {
    val targetJavaVersion = 17
    withType<JavaCompile> {
        options.encoding = "UTF-8"
        options.release.set(targetJavaVersion)
    }

    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = targetJavaVersion.toString()
    }

    java {
        toolchain.languageVersion.set(JavaLanguageVersion.of(JavaVersion.toVersion(targetJavaVersion).toString()))
        withSourcesJar()
    }
}