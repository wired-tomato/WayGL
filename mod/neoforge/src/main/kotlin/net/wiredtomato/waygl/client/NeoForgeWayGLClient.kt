package net.wiredtomato.waygl.client

import net.neoforged.api.distmarker.Dist
import net.neoforged.fml.ModContainer
import net.neoforged.fml.common.Mod
import net.neoforged.neoforge.client.gui.IConfigScreenFactory
import net.wiredtomato.waygl.WayGL
import net.wiredtomato.waygl.config.Config

@Mod(value = WayGL.MODID, dist = [Dist.CLIENT])
class NeoForgeWayGLClient(container: ModContainer) {
    init {
        WayGL.clientInit()
        container.registerExtensionPoint(IConfigScreenFactory::class.java, IConfigScreenFactory { configContainer, screen ->
            Config.generateYacl().generateScreen(screen)
        })
    }
}