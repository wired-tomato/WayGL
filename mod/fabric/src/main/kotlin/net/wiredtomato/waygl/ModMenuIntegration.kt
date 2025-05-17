package net.wiredtomato.waygl

import com.terraformersmc.modmenu.ModMenuModMenuCompat
import com.terraformersmc.modmenu.api.ConfigScreenFactory
import net.wiredtomato.waygl.config.Config

object ModMenuIntegration: ModMenuModMenuCompat() {
    override fun getModConfigScreenFactory(): ConfigScreenFactory<*> {
        return ConfigScreenFactory { parent -> Config.YACL.generateScreen(parent) }
    }
}