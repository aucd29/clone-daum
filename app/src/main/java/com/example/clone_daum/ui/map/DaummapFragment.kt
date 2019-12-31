package com.example.clone_daum.ui.map

import brigitte.*
import brigitte.di.dagger.scope.FragmentScope
import com.example.clone_daum.R
import com.example.clone_daum.common.widget.DaumMapView
import com.example.clone_daum.databinding.DaummapFragmentBinding
import dagger.android.ContributesAndroidInjector
import org.slf4j.LoggerFactory
import javax.inject.Inject

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2019-12-30 <p/>
 */

class DaummapFragment @Inject constructor(
): BaseDaggerFragment<DaummapFragmentBinding, DaummapViewModel>() {
    override val layoutId = R.layout.daummap_fragment

    override fun initViewBinding() {
        mBinding.daummapLayout.apply {
            initMapLayout(requireActivity())
            callback = { code, data ->
                if (mLog.isDebugEnabled) {
                    mLog.debug("CODE : $code, ")
                }

                when (code) {
                    DaumMapView.INIT                -> {}
                    DaumMapView.CENTER_POINT_MOVED  -> {}
                    DaumMapView.DOUBLE_TAPPED       -> {}
                    DaumMapView.LONG_PRESSED        -> {}
                    DaumMapView.SINGLE_TAPPED       -> {}
                    DaumMapView.DRAG_STARTED        -> {}
                    DaumMapView.DRAG_ENDED          -> {}
                    DaumMapView.MOVE_FINISHED       -> {}
                    DaumMapView.ZOOM_LEVEL_CHANGED  -> {}
                    DaumMapView.AUTH_RESULT         -> {}
                }
            }
        }
    }

    override fun initViewModelEvents() {
    }

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // MODULE
    //
    ////////////////////////////////////////////////////////////////////////////////////

    @dagger.Module
    abstract class Module {
        @FragmentScope
        @ContributesAndroidInjector(modules = [DaummapFragmentModule::class])
        abstract fun contributeMapFragmentInjector(): DaummapFragment
    }

    @dagger.Module
    abstract class DaummapFragmentModule {
//        @Binds
//        abstract fun bindMapFragment(fragment: DaummapFragment): Fragment
//
//        @dagger.Module
//        companion object {
//        }
    }

    companion object {
        private val mLog = LoggerFactory.getLogger(DaummapFragment::class.java)
    }
}