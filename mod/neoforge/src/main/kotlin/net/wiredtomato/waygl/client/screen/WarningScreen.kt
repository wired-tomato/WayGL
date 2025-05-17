package net.wiredtomato.waygl.client.screen

import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.components.MultiLineTextWidget
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component
import net.neoforged.fml.loading.FMLConfig

class WarningScreen(title: Component, val oldScreen: Screen?) : Screen(title) {
    companion object {
        private val WARNING = Component.translatable("waygl.warning.early_window_active")
        private val WARNING_DESC = Component.translatable("waygl.warning.early_window_active.desc")
        private val CONTINUE = Component.translatable("waygl.warning.early_window_active.continue")
        private val DISABLE = Component.translatable("waygl.warning.early_window_active.disable")
    }

    override fun init() {
        val warning = MultiLineTextWidget(
            width / 2 - font.width(WARNING) / 2,
            height / 2 - 100,
            Component.translatable("waygl.warning.early_window_active"),
            font
        )

        val longestLine = font.split(WARNING_DESC, width / 2).maxOf { font.width(it) }

        val desc = MultiLineTextWidget(
            width / 2 - longestLine / 2,
            height / 2 - 80,
            WARNING_DESC,
            font
        ).setCentered(true).setMaxWidth(width / 2)

        val continueAnyways = Button.builder(CONTINUE) {
            minecraft?.setScreen(oldScreen)
        }.pos(width / 2 - 75, height / 2 + 12).size(150, 20).build()

        val disableAndCloseGame = Button.builder(DISABLE) {
            FMLConfig.updateConfig(FMLConfig.ConfigValue.EARLY_WINDOW_CONTROL, false)
            minecraft!!.stop()
        }.pos(width / 2 - 75, height / 2 - 12).size(150, 20).build()

        addRenderableWidget(warning)
        addRenderableWidget(desc)
        addRenderableWidget(continueAnyways)
        addRenderableWidget(disableAndCloseGame)
    }
}