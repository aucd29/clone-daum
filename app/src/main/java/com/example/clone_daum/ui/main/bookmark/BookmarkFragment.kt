package com.example.clone_daum.ui.main.bookmark

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import brigitte.BaseDaggerFragment
import brigitte.di.dagger.module.ViewModelKey
import brigitte.di.dagger.scope.FragmentScope
import com.example.clone_daum.R
import com.example.clone_daum.databinding.BookmarkFragmentBinding
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap
import javax.inject.Inject

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2019-12-05 <p/>
 */

class BookmarkFragment @Inject constructor(
): BaseDaggerFragment<BookmarkFragmentBinding, BookmarkViewModel>() {
    override val layoutId = R.layout.bookmark_fragment

    override fun initViewBinding() {
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
        @ContributesAndroidInjector(modules = [BookmarkFragmentModule::class])
        abstract fun contributeBookmarkFragmentInjector(): BookmarkFragment
    }

    @dagger.Module
    abstract class BookmarkFragmentModule {
        @Binds
        abstract fun bindBookmarkFragment(fragment: BookmarkFragment): Fragment

        @dagger.Module
        companion object {
        }
    }
}