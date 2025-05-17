package net.wiredtomato.waygl

data class GLVersion(
    val major: Int,
    val minor: Int,
    val patch: Int? = null,
) {
    override fun toString(): String {
        return "$major.$minor${if (patch != null) "-$patch" else ""}"
    }
}
