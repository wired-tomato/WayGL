plugins {
    java
}

val lwjglVersion = "3.3.6"

repositories {
    mavenCentral()
}

dependencies {
    compileOnly(platform("org.lwjgl:lwjgl-bom:$lwjglVersion"))

    compileOnly("org.lwjgl", "lwjgl")
    compileOnly("org.lwjgl", "lwjgl-glfw")

    compileOnly("org.slf4j:slf4j-api:2.0.17")

    compileOnly("org.apache.commons:commons-lang3:3.20.0")

    compileOnly("com.github.oshi:oshi-core:6.6.5")
}

java {
    withSourcesJar()

    //for use in 1.20.x
    //targetCompatibility = JavaVersion.VERSION_17
}
