plugins {
    kotlin("jvm")
}


group = "net.wiredtomato"
base.archivesName = "deplatformed-api"
version = project.property("version").toString()

repositories {
    mavenCentral()
}
