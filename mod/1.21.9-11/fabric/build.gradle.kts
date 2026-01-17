plugins {
    `multiloader-mod-fabric`
}

val clothConfigVersion: String by project
val modMenuVersion: String by project

repositories {
    maven("https://maven.shedaniel.me/")
    maven("https://maven.terraformersmc.com/")
}

dependencies {
    modImplementation("me.shedaniel.cloth:cloth-config-fabric:${clothConfigVersion}")
    modImplementation("com.terraformersmc:modmenu:${modMenuVersion}")
}
