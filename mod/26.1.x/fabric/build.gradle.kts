plugins {
    `multiloader-mod-fabric-deobf`
}

val clothConfigVersion: String by project
val modMenuVersion: String by project

repositories {
    maven("https://maven.shedaniel.me/")
    maven("https://maven.terraformersmc.com/")
}

dependencies {
    implementation("me.shedaniel.cloth:cloth-config-fabric:${clothConfigVersion}")
    implementation("com.terraformersmc:modmenu:${modMenuVersion}")
}
