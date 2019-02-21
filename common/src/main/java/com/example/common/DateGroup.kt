@file:Suppress("NOTHING_TO_INLINE", "unused")
package com.example.common

import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2018. 12. 3. <p/>
 */


fun Long.toDate(format: String)
        = SimpleDateFormat(format, Locale.getDefault()).format(this)