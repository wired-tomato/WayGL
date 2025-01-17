package net.wiredtomato.waygl.workaround.env

import org.slf4j.LoggerFactory
import oshi.util.ExecutingCommand
import java.nio.file.Files
import kotlin.io.path.Path

/*
* Implementation from Sodium Fabric (https://github.com/CaffeineMC/sodium-fabric/blob/dev/src/main/java/net/caffeinemc/mods/sodium/client/compatibility/environment/probe/GraphicsAdapterProbe.java)
*/
object GraphicsAdapterProbe {
    private val LOGGER = LoggerFactory.getLogger("WayGL::GraphicsAdapterProbe")
    private lateinit var CACHE: List<LinuxAdapterInfo>

    fun findLinuxAdapters(): List<LinuxAdapterInfo> {
        if (::CACHE.isInitialized) return CACHE

        val results = mutableListOf<LinuxAdapterInfo>()

        runCatching {
            val devices = Files.list(Path("/sys/bus/pci/devices/")).iterator()

            while (devices.hasNext()) {
                val devicePath = devices.next()
                // 0x030000 = VGA compatible controller
                // 0x030200 = 3D controller (GPUs with no inputs attached, e.g. hybrid graphics laptops)
                val deviceClass = Files.readString(devicePath.resolve("class")).trim()
                if (deviceClass != "0x030000" && deviceClass != "0x030200") {
                    continue
                }

                val pciVendorId = Files.readString(devicePath.resolve("vendor")).trim()
                val pciDeviceId = Files.readString(devicePath.resolve("device")).trim()

                // The Linux kernel doesn't provide a way to get the device name, so we need to use lspci,
                // since it comes with a list of known device names mapped to device IDs.
                val name = ExecutingCommand // See `man lspci` for more information
                    .runNative("lspci -vmm -d ${pciVendorId.substring(2)}:${pciDeviceId.substring(2)}")
                    .stream()
                    .filter { it.startsWith("Device:") }
                    .map { it.substring("Device:".length).trim() }
                    .findFirst()
                    .orElse("unknown")

                val vender = GraphicsAdapterVendor.fromPciVendorId(pciVendorId)
                val info = LinuxAdapterInfo(vender, name, pciVendorId, pciDeviceId)

                results.add(info)
            }
        }.onFailure {
            LOGGER.error("Failed to probe system for graphics adapters.")
            LOGGER.error("Is \"/sys/bus/pci/devices/*\" accessible?")
        }

        CACHE = results
        return CACHE
    }
}

/*
* Implementation from Sodium Fabric (https://github.com/CaffeineMC/sodium-fabric/blob/dev/src/main/java/net/caffeinemc/mods/sodium/client/compatibility/environment/probe/GraphicsAdapterInfo.java)
*/
data class LinuxAdapterInfo(
    val vendor: GraphicsAdapterVendor,
    val name: String,
    val pciVendorId: String,
    val pciDeviceId: String
)

/*
* Implementation from Sodium Fabric (https://github.com/CaffeineMC/sodium-fabric/blob/dev/src/main/java/net/caffeinemc/mods/sodium/client/compatibility/environment/probe/GraphicsAdapterVendor.java)
*/
enum class GraphicsAdapterVendor {
    NVIDIA,
    AMD,
    INTEL,
    UNKNOWN;

    companion object {
        fun fromPciVendorId(vendor: String): GraphicsAdapterVendor {
            if (vendor.contains("0x1002")) {
                return AMD
            } else if (vendor.contains("0x10de")) {
                return NVIDIA
            } else if (vendor.contains("0x8086")) {
                return INTEL
            }

            return UNKNOWN
        }

        fun fromIcdName(name: String): GraphicsAdapterVendor {
            // Intel Gen 4, 5, 6    - ig4icd
            // Intel Gen 7          - ig7icd
            // Intel Gen 7.5        - ig75icd
            // Intel Gen 8          - ig8icd
            // Intel Gen 9, 9.5     - ig9icd
            // Intel Gen 11         - ig11icd
            // Intel Gen 12         - igxelpicd (Xe-LP; integrated) and igxehpicd (Xe-LP; dedicated)
            if (name.matches("ig(4|7|75|8|9|11|xelp|xehp)icd(32|64)".toRegex())) {
                return INTEL
            }

            if (name.matches("nvoglv(32|64)".toRegex())) {
                return NVIDIA
            }

            if (name.matches("atiglpxx|atig6pxx".toRegex())) {
                return AMD
            }

            return UNKNOWN
        }
    }
}