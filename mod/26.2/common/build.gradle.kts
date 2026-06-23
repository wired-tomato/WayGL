plugins {
    `multiloader-mod-common`
}

val clothConfigVersion: String by project

repositories {
    maven("https://maven.shedaniel.me/")
}

dependencies {
    compileOnly("me.shedaniel.cloth:cloth-config-neoforge:${clothConfigVersion}")
}


