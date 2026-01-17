package net.wiredtomato.waygl.core.service;

import java.nio.file.Path;
import java.util.ServiceLoader;

public interface PlatformService {
    PlatformService IMPL = ServiceLoader.load(PlatformService.class).findFirst().orElseThrow(() -> new IllegalStateException("Failed to load service: " + PlatformService.class));

    String platform();
    Boolean isModLoaded(String modId);
    Boolean isDevelopmentEnvironment();
    default String getEnvironmentName() {
        if (isDevelopmentEnvironment()) {
            return "development";
        } else return "production";
    }

    String getMinecraftVersionString();
    Path getConfigDirectory();
}
