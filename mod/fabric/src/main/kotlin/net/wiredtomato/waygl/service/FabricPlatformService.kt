package net.wiredtomato.waygl.service

import deplatformed.ServiceImpl
import net.fabricmc.loader.api.FabricLoader
import java.nio.file.Path

@ServiceImpl([PlatformService::class])
class FabricPlatformService : PlatformService {
    override fun platform(): String = "fabric"
    override fun isModLoaded(modId: String): Boolean = FabricLoader.getInstance().isModLoaded(modId)
    override fun isDevelopmentEnvironment(): Boolean = FabricLoader.getInstance().isDevelopmentEnvironment
    override fun getMinecraftVersion(): String = FabricLoader.getInstance().getModContainer("minecraft").orElseThrow().metadata.version.friendlyString
    override fun getConfigDir(): Path = FabricLoader.getInstance().configDir
}