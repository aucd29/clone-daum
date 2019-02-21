package com.example.clone_daum.common.camera

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2019. 2. 21. <p/>
 */

class Size constructor(val width: Int, val height: Int) : Comparable<Size> {
    /**
     * swap w & h
     */
    fun rotate() = Size(height, width)

    /**
     * scale by n / d
     */
    fun scale(n: Int, d: Int) = Size(width * n / d, height * n / d)

    /**
     * Scales the dimensions so that it fits entirely inside the parent.One of width or height will
     * fit exactly. Aspect ratio is preserved.
     *
     * @param into the parent to fit into
     * @return the scaled size
     */
    fun scaleFit(into: Size) = if (width * into.height >= into.width * height) {
        Size(into.width, height * into.width / width)
    } else {
        Size(width * into.height / height, into.height)
    }

    /**
     * Scales the size so that both dimensions will be greater than or equal to the corresponding
     * dimension of the parent. One of width or height will fit exactly. Aspect ratio is preserved.
     *
     * @param into the parent to fit into
     * @return the scaled size
     */
    fun scaleCrop(into: Size) = if (width * into.height <= into.width * height) {
        Size(into.width, height * into.width / width)
    } else {
        Size(width * into.height / height, into.height)
    }

    /**
     * Checks if both dimensions of the other size are at least as large as this size.
     *
     * @param other the size to compare with
     * @return true if this size fits into the other size
     */
    fun fitIn(other: Size): Boolean = width <= other.width && height <= other.height

    /**
     * Default sort order is ascending by size.
     */
    override fun compareTo(other: Size): Int {
        val aPixels = this.height * this.width
        val bPixels = other.height * other.width

        if (bPixels < aPixels) {
            return 1
        }

        return if (bPixels > aPixels) {
            -1
        } else 0
    }

    override fun toString(): String {
        return "${width}x${height}"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false

        val size = other as Size

        return width == size.width && height == size.height
    }

    override fun hashCode(): Int {
        var result = width
        result = 31 * result + height
        return result
    }
}