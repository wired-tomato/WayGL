package net.wiredtomato.waygl.util

import java.util.regex.Pattern

data class Version(
    val major: Int,
    val minor: Int,
    val patch: Int,
    val additionalInfo: String
) {
    companion object {
        val VERSION_PATTERN = Pattern.compile("([0-9]+)\\.([0-9]+)(\\.([0-9]*)(.*))?")

        fun String.toVersion(): Version {
            val matcher = VERSION_PATTERN.matcher(this)
            matcher.find()
            val major = matcher.group(1).toInt()
            val minor = matcher.group(2).toInt()
            val patch: Int = matcher.group(4)?.toInt() ?: 0
            val additionalInfo: String = matcher.group(5) ?: ""
            return Version(major, minor, patch, additionalInfo)
        }
    }
}