package com.example.clone_daum.common.bindingadapter

import android.graphics.Typeface
import android.util.TypedValue
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.example.clone_daum.R
import com.example.common.dpToPx
import com.example.common.spToPx
import org.slf4j.LoggerFactory

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2019. 1. 8. <p/>
 */

object TextViewBindingAdapter {
    private val mLog = LoggerFactory.getLogger(TextViewBindingAdapter::class.java)

    @JvmStatic
    @BindingAdapter(*["bindFrequentlySiteUrl", "bindFrequentlySiteTitle"])
    fun bindFrequentlySiteBackground(view: TextView, url: String, title: String) {
        if (mLog.isDebugEnabled) {
            mLog.debug("URL : $url")
        }

        // 넘어오는 도메인에서 1차 도메인의 첫 글자만 얻은 뒤
        // 아스키 코드 값을 MOD 해서 배경 값을 정하도록 함
        var tmpUrl = url.replace("^(http|https)://".toRegex(), "")
        tmpUrl = tmpUrl.substringBefore("/")
        val splited = tmpUrl.split(".")
        val char = splited.get(splited.size - 2).substring(0, 1)
        val domain = char.toCharArray().get(0).toInt()

        if (mLog.isDebugEnabled) {
            mLog.debug("CHAR : $char, MOD : ${domain % 2}")
        }

        view.apply {
            if (title.equals("사이트이동")) {
                setText("http")
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 11f)
                setTypeface(null, Typeface.BOLD_ITALIC)
                setTextColor(0xff000000.toInt())
                setBackgroundResource(R.drawable.shape_frequently_default_move_site_background)
            } else {
                // 배경이 늘어나서 when 이 귀찮으면 나중에 identifier 로~
                setText(char)
                setBackgroundResource(
                    when (domain % 2) {
                        0    -> R.drawable.shape_frequently_0_background
                        else -> R.drawable.shape_frequently_1_background
                    }
                )
            }
        }
    }
}