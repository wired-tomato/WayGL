package net.wiredtomato.waygl

import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets

fun ByteBuffer.string(length: Int): String {
    val data = mutableListOf<Byte>()
    for (i in 0..<length) {
        data.add(get())
    }

    return data.toByteArray().toString(StandardCharsets.UTF_8)
}

fun ByteBuffer.int(): Long {
    return uInt().toLong()
}

fun ByteBuffer.uInt(): UInt {
    return bytes().uInt()
}

fun ByteBuffer.bytes(): List<Byte> {
    val data = mutableListOf<Byte>()
    for (i in 0..<4) {
        data.add(get())
    }
    return data.reversed()
}

fun List<Byte>.uInt() = toByteArray().uInt()
fun List<Byte>.uIntAt(idx: Int) = toByteArray().uintAt(idx)

fun ByteArray.uInt(): UInt {
    return uintAt(0)
}

fun ByteArray.uintAt(idx: Int): UInt {
    assert(this.lastIndex >= idx + 3)

    val b0 = this[idx].toUInt()
    val b1 = this[idx + 1].toUInt()
    val b2 = this[idx + 2].toUInt()
    val b3 = this[idx + 3].toUInt()

    return (b0 and 0xFFu shl 24) or
            (b1 and 0xFFu shl 16) or
            (b2 and 0xFFu shl 8) or
            (b3 and 0xFFu)
}
