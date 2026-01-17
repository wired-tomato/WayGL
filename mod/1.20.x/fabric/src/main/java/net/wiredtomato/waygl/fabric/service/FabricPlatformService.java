package net.wiredtomato.waygl.fabric.service;

import com.google.auto.service.AutoService;
import net.fabricmc.loader.api.FabricLoader;
import net.wiredtomato.waygl.core.service.PlatformService;

import java.nio.file.Path;

@AutoService(PlatformService.class)
public class FabricPlatformService implements PlatformService {
    @Override
    public String platform() {
        return "net/wiredtomato/waygl/fabric";
    }

    @Override
    public Boolean isModLoaded(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }

    @Override
    public Boolean isDevelopmentEnvironment() {
        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }

    @Override
    public String getMinecraftVersionString() {
        return FabricLoader.getInstance().getModContainer("minecraft").orElseThrow().getMetadata().getVersion().getFriendlyString();
    }

    @Override
    public Path getConfigDirectory() {
        return FabricLoader.getInstance().getConfigDir();
    }
}
