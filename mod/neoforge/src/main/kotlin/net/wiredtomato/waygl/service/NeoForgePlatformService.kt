package net.wiredtomato.waygl.service

import deplatformed.ServiceImpl
import net.minecraft.client.Minecraft
import net.neoforged.fml.ModList
import net.neoforged.fml.loading.FMLEnvironment
import net.neoforged.fml.loading.FMLPaths
import net.neoforged.neoforge.common.NeoForgeConfig
import java.nio.file.Path

@ServiceImpl([PlatformService::class])
class NeoForgePlatformService : PlatformService {
    override fun platform(): String = "neoforge"
    override fun isModLoaded(modId: String): Boolean = ModList.get().isLoaded(modId)
    override fun isDevelopmentEnvironment(): Boolean = !FMLEnvironment.production
    override fun getMinecraftVersion(): String = Minecraft.getInstance().launchedVersion
    override fun getConfigDir(): Path = FMLPaths.CONFIGDIR.get()
}