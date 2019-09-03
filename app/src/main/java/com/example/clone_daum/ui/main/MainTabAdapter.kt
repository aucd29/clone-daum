@file:Suppress("NOTHING_TO_INLINE", "unused")
package com.example.clone_daum.ui.main

import android.annotation.SuppressLint
import android.util.SparseArray
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import brigitte.di.dagger.module.ChildFragmentManager
import brigitte.widget.pageradapter.FragmentStatePagerAdapter
import com.example.clone_daum.common.PreloadConfig
import com.example.clone_daum.model.local.TabData
import org.slf4j.LoggerFactory
import java.lang.ref.WeakReference
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Provider

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2018. 12. 10. <p/>
 *
 * infinite
 * - https://github.com/sanyuzhang/CircularViewPager
 */

// https://medium.com/@naturalwarren/dagger-kotlin-3b03c8dd6e9b
// https://stackoverflow.com/questions/48442623/dagger-2-constructor-injection-in-kotlin-with-named-arguments

@SuppressLint("WrongConstant")
class MainTabAdapter @Inject constructor(
    fm: FragmentManager,
    private val mPreConfig: PreloadConfig
) : FragmentStatePagerAdapter(fm) {
    companion object {
        private val mLog = LoggerFactory.getLogger(MainTabAdapter::class.java)

        const val K_POSITION = "position"
    }

    // https://dagger.dev/users-guide
//    @Inject lateinit var webViewFragment: Provider<MainWebviewFragment>

    private val mFragments = SparseArray<WeakReference<MainWebviewFragment>>()
    private val items: List<TabData>
        get() = mPreConfig.tabLabelList

    override fun getItem(position: Int): Fragment {
        if (mLog.isDebugEnabled) {
            mLog.debug("CREATE TAB ($position) ${url(position)}")
        }

//        val fragment = webViewFragment.get()
        val fragment = MainWebviewFragment.create(position, url(position))
        mFragments.put(position, WeakReference(fragment))

        return fragment
    }

    override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
        mFragments.remove(position)

        super.destroyItem(container, position, obj)
    }

    override fun getPageTitle(position: Int) = title(position)
    override fun getCount() = items.size // Integer.MAX_VALUE

    private fun url(pos: Int)   = data(pos).url
    private fun title(pos: Int) = data(pos).name
    private inline fun data(pos: Int) = items[pos]
}

//class MainTabWebViewAdapter @Inject constructor(
//    private val mPreConfig: PreloadConfig
//): PagerAdapter() {
//    private val tabList: List<TabData>
//        get() = mPreConfig.tabLabelList
//
//    override fun getCount() = tabList.size
//
//}