package net.wiredtomato.waygl.core.os;

import org.lwjgl.system.*;

public final class Libc {
    private static final SharedLibrary LIBRARY = APIUtil.apiCreateLibrary("libc.so.6");
    private static final long PFN_setenv = APIUtil.apiGetFunctionAddress(LIBRARY, "setenv");

    private Libc() {}

    public static void setenv(String name, String value) {
        try (var stack = MemoryStack.stackPush()) {
            var nameBuf = stack.UTF8(name);
            var valueBuf = stack.UTF8(value);

            JNI.callPPI(MemoryUtil.memAddress(nameBuf), MemoryUtil.memAddress(valueBuf), 1, PFN_setenv);
        }
    }
}
