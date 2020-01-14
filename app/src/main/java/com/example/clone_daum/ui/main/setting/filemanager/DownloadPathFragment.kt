package com.example.clone_daum.ui.main.setting.filemanager

import androidx.fragment.app.Fragment
import brigitte.BaseDaggerFragment
import brigitte.RecyclerAdapter
import brigitte.di.dagger.scope.FragmentScope
import com.example.clone_daum.R
import com.example.clone_daum.databinding.DownloadPathFragmentBinding
import com.example.clone_daum.model.local.FileInfo
import dagger.Binds
import dagger.Provides
import dagger.android.ContributesAndroidInjector
import javax.inject.Inject

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2019-12-18 <p/>
 */

class DownloadPathFragment @Inject constructor(
): BaseDaggerFragment<DownloadPathFragmentBinding, DownloadPathViewModel>() {
    override val layoutId = R.layout.download_path_fragment

    var closeCallback: ((String) -> Unit)? = null

    @Inject lateinit var adapter: RecyclerAdapter<FileInfo>

    override fun initViewBinding() {
        adapter.viewModel = viewModel
        binding.downloadPathRecycler.adapter = adapter
    }

    override fun initViewModelEvents() {
        viewModel.apply {
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

        @dagger.Module
        companion object {
            @JvmStatic
            @Provides
            fun provideFileInfoAdapter(): RecyclerAdapter<FileInfo> =
                RecyclerAdapter(arrayOf(R.layout.download_path_up_item,
                    R.layout.download_path_file_item))
        }
    }
}