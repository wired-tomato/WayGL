package net.wiredtomato.waygl.neoforge;

import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLConfig;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;
import net.wiredtomato.waygl.WayGL;
import net.wiredtomato.waygl.config.ConfigManager;
import net.wiredtomato.waygl.core.Loader;
import net.wiredtomato.waygl.core.os.OS;
import net.wiredtomato.waygl.neoforge.screen.WarningScreen;

@Mod(value = WayGL.MOD_ID, dist = Dist.CLIENT)
public class NeoForgeWayGL {
    private boolean gaveWarning = false;

    public NeoForgeWayGL(IEventBus modBus, ModContainer container) {
        WayGL.init();
        NeoForge.EVENT_BUS.addListener(this::onScreenOpen);

        container.registerExtensionPoint(IConfigScreenFactory.class, (configContainer, modListScreen) ->
                ConfigManager.createConfigScreen(modListScreen)
        );
    }

    private void onScreenOpen(ScreenEvent.Opening event) {
        if (gaveWarning || OS.current() != OS.LINUX || !(event.getScreen() instanceof TitleScreen) || Loader.isWayland()) return;

        var earlyWindowControl = FMLConfig.getBoolConfigValue(FMLConfig.ConfigValue.EARLY_WINDOW_CONTROL);
        if (earlyWindowControl) {
            event.setNewScreen(new WarningScreen(Component.translatable("waygl.warning.early_window_active"), event.getScreen()));
        }

        gaveWarning = true;
    }
}
