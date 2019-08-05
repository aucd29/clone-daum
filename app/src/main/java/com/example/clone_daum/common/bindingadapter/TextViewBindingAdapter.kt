package com.example.clone_daum.common.bindingadapter

import android.annotation.SuppressLint
import android.graphics.Typeface
import android.util.TypedValue
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.example.clone_daum.R
import brigitte.dpToPx
import brigitte.spToPx
import org.slf4j.LoggerFactory

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2019. 1. 8. <p/>
 */

object TextViewBindingAdapter {
    private val mLog = LoggerFactory.getLogger(TextViewBindingAdapter::class.java)

    // 이건 common 으로 이동 해야 할 듯
    @JvmStatic
    @BindingAdapter("bindUrlToChar")
    fun bindUrlToChar(view: TextView, url: String) {
        if (mLog.isDebugEnabled) {
            mLog.debug("URL : $url")
        }

        urlToCharAndBackground(view, url)
    }

    @SuppressLint("SetTextI18n")
    @JvmStatic
    @BindingAdapter("bindUrlToText", "bindIsMoveUrl")
    fun bindIsMoveUrl(view: TextView, url: String, data: String) {
        if (mLog.isDebugEnabled) {
            mLog.debug("URL : $url, DATA : $data")
        }

        if (data.isNotEmpty() && data != "사이트이동") {
            urlToCharAndBackground(view, url)
        } else {
            view.apply {
                text = "http"
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 11f)
                setTypeface(null, Typeface.BOLD_ITALIC)
                setTextColor(0xff000000.toInt())
                setBackgroundResource(R.drawable.shape_frequently_default_move_site_background)
            }
        }
    }

    private fun urlToCharAndBackground(view: TextView, url: String) {
        // 넘어오는 도메인에서 1차 도메인의 첫 글자만 얻은 뒤
        // 아스키 코드 값을 MOD 해서 배경 값을 정하도록 함
        var tmpUrl = url.replace("^(http|https)://".toRegex(), "")
        tmpUrl = tmpUrl.substringBefore("/")
        val splited = tmpUrl.split(".")
        val char = splited[splited.size - 2].substring(0, 1).toUpperCase()
        val domain = char.toCharArray()[0].toInt()

        if (mLog.isTraceEnabled) {
            mLog.trace("CHAR : $char, MOD : ${domain % 2}")
        }

        view.apply {
            // 배경이 늘어나서 when 이 귀찮으면 나중에 identifier 로~
            text = char
            setBackgroundResource(
                when (domain % 2) {
                    0    -> R.drawable.shape_frequently_0_background
                    else -> R.drawable.shape_frequently_1_background
                }
            )
        }
    }
}