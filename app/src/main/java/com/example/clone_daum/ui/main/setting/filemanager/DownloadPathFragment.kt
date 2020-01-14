package com.example.clone_daum.ui.main.setting.filemanager

import androidx.fragment.app.Fragment
import brigitte.BaseDaggerFragment
import brigitte.di.dagger.scope.FragmentScope
import com.example.clone_daum.R
import com.example.clone_daum.databinding.DownloadPathFragmentBinding
import dagger.Binds
import dagger.android.ContributesAndroidInjector
import javax.inject.Inject

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2019-12-18 <p/>
 */

class DownloadPathFragment @Inject constructor(
): BaseDaggerFragment<DownloadPathFragmentBinding, DownloadPathViewModel>() {
    override val layoutId = R.layout.download_path_fragment

    var closeCallback: ((String) -> Unit)? = null

    override fun initViewBinding() {
    }

    override fun initViewModelEvents() {
        viewModel.apply {
            initAdapter(
                R.layout.download_path_up_item,
                R.layout.download_path_file_item)

            observe(currentRoot) {
                loadFileList()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        closeCallback?.invoke(viewModel.currentRoot.value!!)
    }

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // MODULE
    //
    ////////////////////////////////////////////////////////////////////////////////////

    @dagger.Module
    abstract class Module {
        @FragmentScope
        @ContributesAndroidInjector(modules = [DownloadPathFragmentModule::class])
        abstract fun contributeDownloadPathFragmentInjector(): DownloadPathFragment
    }

    @dagger.Module
    abstract class DownloadPathFragmentModule {
        @Binds
        abstract fun bindDownloadPathFragment(fragment: DownloadPathFragment): Fragment
    }
}