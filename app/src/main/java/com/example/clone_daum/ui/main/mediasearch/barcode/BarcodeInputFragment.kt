package com.example.clone_daum.ui.main.mediasearch.barcode

import com.example.clone_daum.databinding.BarcodeInputFragmentBinding
import brigitte.BaseDaggerFragment
import brigitte.hideKeyboard
import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2019. 1. 23. <p/>
 */

class BarcodeInputFragment: BaseDaggerFragment<BarcodeInputFragmentBinding, BarcodeInputViewModel>() {
    override fun initViewBinding() {
    }

    override fun initViewModelEvents() {
    }

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // ICommandEventAware
    //
    ////////////////////////////////////////////////////////////////////////////////////

    override fun onCommandEvent(cmd: String, data: Any) {
        BarcodeInputViewModel.apply {
            when (cmd) {
                CMD_HIDE_KEYBOARD -> hideKeyboard()
            }
        }
    }

    override fun onDestroyView() {
        hideKeyboard()

        super.onDestroyView()
    }

    private fun hideKeyboard() = context?.hideKeyboard(mBinding.barcodeEdit)

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