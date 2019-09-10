package com.example.clone_daum.ui.main.mediasearch.barcode

import androidx.savedstate.SavedStateRegistryOwner
import com.example.clone_daum.databinding.BarcodeInputFragmentBinding
import brigitte.BaseDaggerFragment
import brigitte.di.dagger.scope.FragmentScope
import brigitte.hideKeyboard
import com.example.clone_daum.R
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import javax.inject.Inject

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2019. 1. 23. <p/>
 */

class BarcodeInputFragment @Inject constructor() : BaseDaggerFragment<BarcodeInputFragmentBinding, BarcodeInputViewModel>() {

    override val layoutId = R.layout.barcode_input_fragment

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
        @FragmentScope
        @ContributesAndroidInjector(modules = [BarcodeInputFragmentModule::class])
        abstract fun contributeBarcodeInputFragmentInjector(): BarcodeInputFragment
    }

    @dagger.Module
    abstract class BarcodeInputFragmentModule {
        @Binds
        abstract fun bindSavedStateRegistryOwner(activity: BarcodeInputFragment): SavedStateRegistryOwner
    }
}