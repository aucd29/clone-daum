package com.example.clone_daum.ui.main.mediasearch.barcode

import com.example.clone_daum.databinding.BarcodeInputFragmentBinding
import com.example.common.BaseDaggerFragment
import com.example.common.hideKeyboard
import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2019. 1. 23. <p/>
 */

class BarcodeInputFragment: BaseDaggerFragment<BarcodeInputFragmentBinding, BarcodeViewModel>() {
    override fun initViewBinding() {
    }

    override fun initViewModelEvents() {
    }

    override fun onDestroyView() {
        context?.hideKeyboard(mBinding.barcodeBack)

        super.onDestroyView()
    }

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // MODULE
    //
    ////////////////////////////////////////////////////////////////////////////////////

    @dagger.Module
    abstract class Module {
        @ContributesAndroidInjector
        abstract fun contributeInjector(): BarcodeInputFragment
    }
}