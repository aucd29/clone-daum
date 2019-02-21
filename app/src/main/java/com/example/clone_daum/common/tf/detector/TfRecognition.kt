package com.example.smartlenskotlin.tf.detector

import android.graphics.RectF
import com.example.common.IRecyclerDiff

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2019. 2. 12. <p/>
 *
 * https://github.com/wadehuang36/tensorflow_mobilenet_android_example
 */

data class TfRecognition(
    /**
     * A unique identifier for what has been recognized. Specific to the class, not the instance of
     * the object.
     */
    val id: String?,
    /**
     * Display name for the recognition.
     */
    val title: String?,
    /**
     * A sortable score for how good the recognition is relative to others. Higher should be better.
     */
    val confidence: Float?,
    /**
     * Optional location within the source image for the location of the recognized object.
     */
    var location: RectF?
) : IRecyclerDiff {
    override fun compare(item: IRecyclerDiff): Boolean {
        val newItem = item as TfRecognition
        return id == newItem.id && title == newItem.title
    }

    fun string() =
        "$title${confidence?.run { String.format(" (%.1f%%)", this * 100.0f) }}"
}