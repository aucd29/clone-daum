package com.example.clone_daum.ui.browser

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.example.clone_daum.R
import com.example.clone_daum.databinding.BrowserSubmenuFragmentBinding
import com.example.common.*
import dagger.android.ContributesAndroidInjector

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2018. 12. 18. <p/>
 *
 * https://gist.github.com/orhanobut/8665372
 *
 * 만들려다가 왠지 있을거 같아서 검색보니 있더라.. (BottomSheetFragment) 구글아 고마워
 *
 * BottomSheetFragment
 *  - http://liveonthekeyboard.tistory.com/145
 *  - https://github.com/material-components/material-components-android/tree/master/lib/java/com/google/android/material
 */

@SuppressLint("ValidFragment")
class BrowserSubmenuFragment (val mUrl: String)
    : BaseDaggerBottomSheetDialogFragment<BrowserSubmenuFragmentBinding, BrowserSubmenuViewModel>() {
    override fun initViewBinding() {

    }

    override fun initViewModelEvents() {

    }

    override fun onCommandEvent(cmd: String, data: Any) {
        BrowserSubmenuViewModel.apply {
            when (cmd) {
                CMD_SUBMENU -> {
                    when (data.toString()) {
                        "URL 복사" -> {
                            context?.toast(R.string.brs_copied_url)
                            requireContext().clipboard(mUrl)

//                            dismiss()
                        }
                        "기타 브라우저" -> {
                            confirm(R.string.brs_using_base_brs, R.string.common_notify,
                                listener = { res, dlg ->
                                    if (res) {
                                        showBaseBrs()
                                    }

//                                    dismiss()
                                })
                        }
                    }
                }
            }
        }
    }

    fun showBaseBrs() {
        startActivity(Intent(Intent.ACTION_VIEW).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            setData(Uri.parse(mUrl))
        })
    }

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // Module
    //
    ////////////////////////////////////////////////////////////////////////////////////

    @dagger.Module
    abstract class Module {
        @ContributesAndroidInjector
        abstract fun contributeInjector(): BrowserSubmenuFragment
    }
}
