package net.wiredtomato.waygl.neoforge.service;

import com.google.auto.service.AutoService;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.fml.loading.FMLPaths;
import net.wiredtomato.waygl.core.service.PlatformService;

import java.nio.file.Path;

@AutoService(PlatformService.class)
public class NeoForgePlatformService implements PlatformService {
    @Override
    public String platform() {
        return "neoforge";
    }

    @Override
    public Boolean isModLoaded(String modId) {
        return ModList.get().isLoaded(modId);
    }

    @Override
    public Boolean isDevelopmentEnvironment() {
        return !FMLEnvironment.production;
    }

    @Override
    public String getMinecraftVersionString() {
        return FMLLoader.versionInfo().mcVersion();
    }

    @Override
    public Path getConfigDirectory() {
        return FMLPaths.CONFIGDIR.get();
    }
}
