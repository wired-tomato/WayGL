plugins {
    `multiloader-mod-neoforge`
}

val clothConfigVersion: String by project

repositories {
    maven("https://maven.shedaniel.me/")
}

dependencies {
    implementation("me.shedaniel.cloth:cloth-config-neoforge:${clothConfigVersion}")
}
