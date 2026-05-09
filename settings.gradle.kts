pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        exclusiveContent {
            forRepository {
                maven("https://maven.fabricmc.net") {
                    name = "Fabric"
                }
            }

            filter {
                includeGroup("net.fabricmc")
                includeGroup("net.fabricmc.unpick")
                includeGroup("fabric-loom")
            }
        }

        exclusiveContent {
            forRepository {
                maven("https://repo.spongepowered.org/repository/maven-public/") {
                    name = "Sponge"
                }
            }

            filter {
                includeGroupAndSubgroups("org.spongepowered")
            }
        }

        maven("https://maven.minecraftforge.net/") {
            name = "MinecraftForge"
        }
    }
}

include(":core")

fun includeVersion(version: String, disableFabric: Boolean = false, disableNeoForge: Boolean = false) {
    include("mod:$version")
    include("mod:$version:common")
    if (!disableFabric) include("mod:$version:fabric")
    if (!disableNeoForge) include("mod:$version:neoforge")
}

//disable based on current version
//also change java target in :core
includeVersion("26.1.x")
//includeVersion("1.21.9-11")
//includeVersion("1.21.x-8")
//includeVersion("1.20.x", disableNeoForge = true)

rootProject.name = "waygl"
