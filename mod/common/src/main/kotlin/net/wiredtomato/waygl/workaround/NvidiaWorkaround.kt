package net.wiredtomato.waygl.workaround

import net.wiredtomato.waygl.os.Libc
import net.wiredtomato.waygl.os.OS
import net.wiredtomato.waygl.os.OSUtils
import org.slf4j.LoggerFactory

/*
* Implementation from Sodium Fabric (https://github.com/CaffeineMC/sodium-fabric/blob/dev/src/main/java/net/caffeinemc/mods/sodium/client/compatibility/workarounds/nvidia/NvidiaWorkarounds.java)
*/
object NvidiaWorkaround {
    private val LOGGER = LoggerFactory.getLogger("WayGL::NvidiaWorkaround")

    fun apply() {
        LOGGER.warn("Applying workaround: Prevent NVIDIA OpenGL driver from using broken optimization (NVIDIA_THREADED_OPTIMIZATIONS)")

        try {
            if (OSUtils.os() == OS.LINUX) {
                Libc.setEnvironmentVariable("__GL_THREADED_OPTIMIZATIONS", "0")
            }
        } catch (t: Throwable) {
            LOGGER.error("Failed to apply NVIDIA workaround", t)
            LOGGER.error("READ ME! The workaround for the NVIDIA Graphics Driver did not apply correctly!")
            LOGGER.error("READ ME! Your game is highly likely to crash at startup")
            LOGGER.error("READ ME! For more information see: https://github.com/wired-tomato/WayGL/issues/1")
        }
    }
}