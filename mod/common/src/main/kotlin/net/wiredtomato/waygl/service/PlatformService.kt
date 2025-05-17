package net.wiredtomato.waygl.service

import net.wiredtomato.waygl.util.Version
import net.wiredtomato.waygl.util.Version.Companion.toVersion
import java.nio.file.Path

interface PlatformService {
    fun platform(): String
    fun isModLoaded(modId: String): Boolean
    fun isDevelopmentEnvironment(): Boolean
    fun getEnvironmentName(): String = if (isDevelopmentEnvironment()) "development" else "production"
    fun getMinecraftVersionString(): String
    fun getMinecraftVersion(): Version = getMinecraftVersionString().toVersion()
    fun getConfigDir(): Path
}

val PlatformServiceImpl = Services.getService<PlatformService>()
