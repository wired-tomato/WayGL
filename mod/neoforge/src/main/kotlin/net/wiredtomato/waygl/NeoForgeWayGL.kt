package net.wiredtomato.waygl

import net.minecraft.client.gui.screens.TitleScreen
import net.minecraft.network.chat.Component
import net.neoforged.api.distmarker.Dist
import net.neoforged.fml.common.Mod
import net.neoforged.fml.loading.FMLConfig
import net.neoforged.neoforge.client.event.ScreenEvent
import net.neoforged.neoforge.common.NeoForge.EVENT_BUS
import net.wiredtomato.waygl.client.screen.WarningScreen
import thedarkcolour.kotlinforforge.neoforge.forge.runWhenOn

@Mod(WayGL.MODID)
object NeoForgeWayGL {
    private var gaveWarning = false

    init {
        WayGL.tryUseWayland()

        runWhenOn(Dist.CLIENT) {
            EVENT_BUS.addListener(::onScreenOpen)
        }
    }

    private fun onScreenOpen(event: ScreenEvent.Opening) {
        if (gaveWarning || event.screen !is TitleScreen || WayGL.isWayland) return

        val earlyWindowControl = FMLConfig.getBoolConfigValue(FMLConfig.ConfigValue.EARLY_WINDOW_CONTROL)
        if (earlyWindowControl) {
            event.newScreen = (WarningScreen(Component.translatable("waygl.screen.warning"), event.screen))
        }

        gaveWarning = true
    }
}