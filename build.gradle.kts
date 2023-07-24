import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.9.0"
    kotlin("plugin.serialization") version "1.9.0"
    id("org.teamvoided.iridium") version "2.2.3"
    id("iridium.mod.build-script") version "2.2.3"
}

group = project.properties["maven_group"]!!
version = project.properties["mod_version"]!!
base.archivesName.set(project.properties["archives_base_name"] as String)
description = "TeamVoided Template Mod"

repositories {
    mavenCentral()
}

modSettings {
    modId(base.archivesName.get())
    modName("Team Voided Template Mod")

    entrypoint("main", "org.teamvoided.templatemod.TemplateMod::commonInit")
    entrypoint("client", "org.teamvoided.templatemod.TemplateMod::clientInit")

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