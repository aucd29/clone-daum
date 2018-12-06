package com.example.clone_daum

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.clone_daum.databinding.MainActivityBinding
import com.example.clone_daum.di.migration.AndroidXInjection
import com.example.clone_daum.di.migration.HasXFragmentInjector
import com.example.clone_daum.ui.main.MainFragment
import com.example.common.*
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import org.slf4j.LoggerFactory
import javax.inject.Inject

class MainActivity : BaseActivity<MainActivityBinding>(), HasXFragmentInjector {
    companion object {
        private val mLog = LoggerFactory.getLogger(MainActivity::class.java)
    }

    @Inject
    lateinit var xFragmentInjector: DispatchingAndroidInjector<Fragment>

    override fun layoutId() = R.layout.main_activity

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        AndroidXInjection.inject(this)

        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            supportFragmentManager.run {
                add(FragmentParams(R.id.container, MainFragment::class.java,
                    commit = FragmentCommit.NOW))
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // HasXFragmentInjector
    //
    ////////////////////////////////////////////////////////////////////////////////////

    override fun xFragmentInjector(): AndroidInjector<Fragment> = xFragmentInjector
}
