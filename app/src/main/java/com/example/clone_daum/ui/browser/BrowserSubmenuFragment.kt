package com.example.clone_daum.ui.browser

import androidx.recyclerview.widget.GridLayoutManager
import com.example.clone_daum.databinding.BrowserSubmenuFragmentBinding
import com.example.clone_daum.di.module.Config
import com.example.common.BaseDaggerBottomSheetDialogFragment
import com.example.common.layoutHeight
import com.example.common.layoutWidth
import dagger.android.ContributesAndroidInjector
import javax.inject.Inject

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2018. 12. 18. <p/>
 *
 * https://gist.github.com/orhanobut/8665372
 *
 * 만들려다가 왠지 있을거 같아서 검색보니 있더라.. (BottomSheetFragment) 구글아 고마워
 *
 * BottomSheetFragment
 * http://liveonthekeyboard.tistory.com/145
 */

class BrowserSubmenuFragment : BaseDaggerBottomSheetDialogFragment<BrowserSubmenuFragmentBinding, BrowserSubmenuViewModel>() {
    @Inject lateinit var config: Config

    override fun settingEvents() {
    }

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
