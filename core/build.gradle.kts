plugins {
    java
}

val lwjglVersion = "3.3.6"

repositories {
    mavenCentral()
}

dependencies {
    compileOnly(platform("org.lwjgl:lwjgl-bom:$lwjglVersion"))

    compileOnly("org.lwjgl:lwjgl")
    compileOnly("org.lwjgl:lwjgl-glfw")

    compileOnly("org.slf4j:slf4j-api:2.0.17")

    compileOnly("org.apache.commons:commons-lang3:3.20.0")

    compileOnly("com.github.oshi:oshi-core:6.6.5")
}

java {
    withSourcesJar()

    targetCompatibility = if (findProject(":mod:1.20.x") != null) {
        JavaVersion.VERSION_17
    } else if (findProject(":mod:1.21.9-11") != null || findProject(":mod:1.21.x-8") != null) {
        JavaVersion.VERSION_21
    } else JavaVersion.VERSION_25
    
}
