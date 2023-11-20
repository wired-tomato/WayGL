package org.teamvoided.template

import net.minecraft.util.Identifier
import org.slf4j.LoggerFactory

@Suppress("unused")
object Template {
    const val MODID = "template"

    @JvmField
    val LOGGER = LoggerFactory.getLogger(Template::class.java)

    fun commonInit() {
        LOGGER.info("Hello from Common")
    }

    fun clientInit() {
        LOGGER.info("Hello from Client")
    }

    fun id(path: String) = Identifier(MODID, path)
}
