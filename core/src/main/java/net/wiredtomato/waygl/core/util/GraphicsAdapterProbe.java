package net.wiredtomato.waygl.core.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.util.ExecutingCommand;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/*
 * Implementation from Sodium (https://github.com/CaffeineMC/sodium/blob/dev/common/src/boot/java/net/caffeinemc/mods/sodium/client/compatibility/environment/probe/GraphicsAdapterProbe.java)
 */
public final class GraphicsAdapterProbe {
    private static final Logger LOGGER = LoggerFactory.getLogger("WayGL/NvidiaWorkaround");

    private static List<LinuxAdapterInfo> CACHE = null;

    private GraphicsAdapterProbe() {}

    public static List<LinuxAdapterInfo> findLinuxAdapters() {
        if (CACHE != null) {
            return CACHE;
        }

        var results = new ArrayList<LinuxAdapterInfo>();

        try {
            for (var devicePath : Files.list(Paths.get("/sys/bus/pci/devices/")).toList()) {
                // 0x030000 = VGA compatible controller
                // 0x030200 = 3D controller (GPUs with no inputs attached, e.g. hybrid graphics laptops)
                var deviceClass = Files.readString(devicePath.resolve("class")).trim();
                if (!deviceClass.equals("0x030000") && !deviceClass.equals("0x030200")) {
                    continue;
                }

                var pciVendorId = Files.readString(devicePath.resolve("vendor")).trim();
                var pciDeviceId = Files.readString(devicePath.resolve("device")).trim();

                // The Linux kernel doesn't provide a way to get the device name, so we need to use lspci,
                // since it comes with a list of known device names mapped to device IDs.
                var command = String.format("lspci -vmm -d %s:%s", pciVendorId.substring(2), pciDeviceId.substring(2));

                var name = ExecutingCommand
                        .runNative(command)
                        .stream()
                        .filter((it) -> it.startsWith("Device:"))
                        .map((it) -> it.substring("Device:".length()).trim())
                        .findFirst()
                        .orElse("unknown");

                var vendor = GraphicsAdapterVendor.fromPciVendorId(pciVendorId);
                var info = new LinuxAdapterInfo(vendor, name, pciVendorId, pciDeviceId);

                results.add(info);
            }
        } catch (IOException e) {
            LOGGER.error("Failed to probe system for graphics adapters.");
            LOGGER.error("Is \"/sys/bus/pci/devices/*\" accessible?");
        }

        CACHE = results;

        return CACHE;
    }

    public record LinuxAdapterInfo(
            GraphicsAdapterVendor vendor,
            String name,
            String pciVendorId,
            String pciDeviceId
    ) {}

    public enum GraphicsAdapterVendor {
        NVIDIA,
        AMD,
        INTEL,
        UNKNOWN;

        public static GraphicsAdapterVendor fromPciVendorId(String vendorId) {
            if (vendorId.contains("0x1002")) {
                return AMD;
            } else if (vendorId.contains("0x10de")) {
                return NVIDIA;
            } else if (vendorId.contains("0x8086")) {
                return INTEL;
            } else {
                return UNKNOWN;
            }
        }
    }
}
