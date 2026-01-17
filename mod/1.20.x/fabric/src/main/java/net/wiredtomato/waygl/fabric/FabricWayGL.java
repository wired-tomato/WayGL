package net.wiredtomato.waygl.fabric;

import net.fabricmc.api.ClientModInitializer;
import net.wiredtomato.waygl.WayGL;

public class FabricWayGL implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        WayGL.init();
    }
}
