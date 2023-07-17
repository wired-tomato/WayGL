package org.teamvoided.templatemod

import org.slf4j.LoggerFactory

object TemplateMod {
    val LOGGER = LoggerFactory.getLogger(TemplateMod::class.java)

    @Suppress("unused")
    fun commonInit() {
        LOGGER.info("Hello from commonInit")
    }

    @Suppress("unused")
    fun clientInit() {
        LOGGER.info("Hello from clientInit")
    }
}