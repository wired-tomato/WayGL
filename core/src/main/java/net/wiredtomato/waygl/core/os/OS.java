package net.wiredtomato.waygl.core.os;

import org.apache.commons.lang3.SystemUtils;

public enum OS {
    WIN,
    MAC,
    LINUX,
    UNKNOWN;
    public static OS current() {
        if (SystemUtils.IS_OS_WINDOWS) {
            return OS.WIN;
        } else if (SystemUtils.IS_OS_MAC) {
            return OS.MAC;
        } else if (SystemUtils.IS_OS_LINUX) {
            return OS.LINUX;
        } else  {
            return OS.UNKNOWN;
        }
    }
}
