plugins {
    `multiloader-mod-common-legacy`
}

val clothConfigVersion: String by project

repositories {
    maven("https://maven.shedaniel.me/")
}

dependencies {
    compileOnly("me.shedaniel.cloth:cloth-config-forge:${clothConfigVersion}")
    compileOnly("org.lwjgl:lwjgl-glfw:3.3.2")
}
