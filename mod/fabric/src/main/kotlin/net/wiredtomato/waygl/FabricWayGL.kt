package net.wiredtomato.waygl

import net.fabricmc.api.ClientModInitializer

object FabricWayGL : ClientModInitializer {
    override fun onInitializeClient() {
        WayGL.clientInit()
    }
}