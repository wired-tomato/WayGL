package net.wiredtomato.waygl.os

import org.apache.commons.lang3.SystemUtils
import org.lwjgl.system.APIUtil
import org.lwjgl.system.JNI
import org.lwjgl.system.MemoryStack
import org.lwjgl.system.MemoryUtil


object OSUtils {
    fun os(): OS {
        return if (SystemUtils.IS_OS_WINDOWS) {
            OS.WIN
        } else if (SystemUtils.IS_OS_MAC) {
            OS.MAC
        } else if (SystemUtils.IS_OS_LINUX) {
            OS.LINUX
        } else {
            OS.UNKNOWN
        }
    }
}

enum class OS {
    WIN,
    MAC,
    LINUX,
    UNKNOWN
}


object Libc {
    private val LIBRARY = APIUtil.apiCreateLibrary("libc.so.6")
    private val PFN_setenv = APIUtil.apiGetFunctionAddress(LIBRARY, "setenv")

    fun setEnvironmentVariable(name: String, value: String) {
        MemoryStack.stackPush().use { stack ->
            val nameBuffer = stack.UTF8(name)
            val valueBuffer = stack.UTF8(value)

            JNI.callPPI(MemoryUtil.memAddress(nameBuffer), MemoryUtil.memAddress(valueBuffer), 1, PFN_setenv)
        }
    }
}
