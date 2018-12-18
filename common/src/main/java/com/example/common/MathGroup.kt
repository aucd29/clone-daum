@file:Suppress("NOTHING_TO_INLINE", "unused")
package com.example.common

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2018. 11. 27. <p/>
 */


const val UNIT_STRING: String = " KMGTPE"

inline fun Long.toFileSizeString(): String {
    var u = 0
    var size = this

    while (size > 1024 * 1024) {
        u++
        size = size shr 10
    }

    if (size > 1024) {
        u++
    }

    return String.format("%.1f %cB", size / 1024f, UNIT_STRING[u])
}

inline fun Double.toFileSizeString(unit: Int): String {
    val size = this.toLong() shr (10 * unit)

    return String.format("%.1f %cB", size / 1024f, UNIT_STRING[unit])
}

inline fun Double.toFileSizeString(): String {
    var u = 0
    var size = this.toLong()

    while (size > 1024 * 1024) {
        u++
        size = size shr 10
    }

    if (size > 1024) {
        u++
    }

    return String.format("%.1f %cB", size / 1024f, UNIT_STRING[u])
}

class NumberUnit(private var maxSize: Long, convertMaxSize: (Int) -> Unit) {
    private var mUnitCount: Int = 0
    private val mUnitChar: Char
    private val mMaxString: String

    init {
        val tempSize = maxSize

        while (maxSize > 1024 * 1024) {
            mUnitCount++
            maxSize = maxSize shr 10
        }

        var count = mUnitCount
        if (maxSize > 1024) {
            ++count
        }

        val newSize = convertProgress(tempSize)
        mUnitChar   = UNIT_STRING[count]
        mMaxString  = String.format("%.1f%cB", newSize / 1024f, mUnitChar)

        convertMaxSize(newSize)
    }

    fun progress(value: Long) = convertProgress(value).let { it to formatProgress(it) }

    private fun convertProgress(value: Long) = (value shr (10 * mUnitCount)).toInt()
    private fun formatProgress(value : Int) =
        String.format("%.1f%cB/%s", value / 1024f, mUnitChar, mMaxString)
}