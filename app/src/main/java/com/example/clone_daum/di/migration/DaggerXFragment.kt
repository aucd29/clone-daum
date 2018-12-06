package com.example.clone_daum.di.migration

import android.content.Context
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.example.common.BaseRuleFragment
import dagger.android.DispatchingAndroidInjector
import javax.inject.Inject

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2018. 12. 6. <p/>
 */

abstract class DaggerXFragment<T: ViewDataBinding> : BaseRuleFragment<T>(), HasXFragmentInjector {
    @Inject
    lateinit var childFragmentInjector: DispatchingAndroidInjector<Fragment>

    override fun onAttach(context: Context?) {
        AndroidXInjection.inject(this)
        super.onAttach(context)
    }

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // HasXFragmentInjector
    //
    ////////////////////////////////////////////////////////////////////////////////////

    override fun xFragmentInjector() = childFragmentInjector
}