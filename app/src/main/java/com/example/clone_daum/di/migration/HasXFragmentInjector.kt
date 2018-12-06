package com.example.clone_daum.di.migration

import androidx.fragment.app.Fragment
import dagger.android.AndroidInjector
import dagger.internal.Beta

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2018. 12. 6. <p/>
 */

@Beta
interface HasXFragmentInjector {
    /** Returns an [AndroidInjector] of [Fragment]s.  */
    fun xFragmentInjector(): AndroidInjector<Fragment>
}
