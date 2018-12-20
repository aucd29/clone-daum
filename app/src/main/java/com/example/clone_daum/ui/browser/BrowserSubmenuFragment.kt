package com.example.clone_daum.ui.browser

import com.example.clone_daum.databinding.BrowserSubmenuFragmentBinding
import com.example.common.BaseDaggerBottomSheetDialogFragment
import dagger.android.ContributesAndroidInjector

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2018. 12. 18. <p/>
 *
 * https://gist.github.com/orhanobut/8665372
 *
 * 만들려다가 왠지 있을거 같아서 검색보니 있더라.. (BottomSheetFragment) 구글아 고마워
 *
 * BottomSheetFragment
 *  - http://liveonthekeyboard.tistory.com/145
 */

class BrowserSubmenuFragment
    : BaseDaggerBottomSheetDialogFragment<BrowserSubmenuFragmentBinding, BrowserSubmenuViewModel>() {

    override fun settingEvents() { }

    ////////////////////////////////////////////////////////////////////////////////////
    //
    //
    //
    ////////////////////////////////////////////////////////////////////////////////////

    @dagger.Module
    abstract class Module {
        @ContributesAndroidInjector
        abstract fun contributeInjector(): BrowserSubmenuFragment
    }
}
