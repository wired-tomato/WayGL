import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.9.0"
    kotlin("plugin.serialization") version "1.9.0"
    id("org.teamvoided.iridium") version "2.4.0"
    id("iridium.mod.build-script") version "2.4.0"
}

group = project.properties["maven_group"]!!
version = project.properties["mod_version"]!!
base.archivesName.set(project.properties["archives_base_name"] as String)
description = "TeamVoided Template Description"

repositories {
    mavenCentral()
}

modSettings {
    modId(base.archivesName.get())
    modName("Team Voided Template")

    entrypoint("main", "org.teamvoided.template.Template::commonInit")
    entrypoint("client", "org.teamvoided.template.Template::clientInit")

    mixinFile("template.mixins.json")

    isModParent(true)
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