@file:Suppress("NOTHING_TO_INLINE", "unused")
package com.example.clone_daum.ui.main

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import brigitte.BaseDaggerWebViewFragment
import brigitte.widget.pageradapter.FragmentPagerAdapter
import com.example.clone_daum.common.PreloadConfig
import com.example.clone_daum.model.local.TabData
import org.slf4j.LoggerFactory
import javax.inject.Inject
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
    private val preConfig: PreloadConfig
) : FragmentPagerAdapter(fm) {
    companion object {
        private val logger = LoggerFactory.getLogger(MainTabAdapter::class.java)

        const val K_POSITION = "position"
    }

    // https://dagger.dev/users-guide
    @Inject lateinit var webViewFragment: Provider<MainWebviewFragment>

    private val items: List<TabData>
        get() = preConfig.tabLabelList

    override fun getItem(position: Int): Fragment {
        if (logger.isDebugEnabled) {
            logger.debug("CREATE TAB ($position) ${url(position)}")
        }

        return webViewFragment.get().apply {
            arguments = Bundle().apply {
                putInt(K_POSITION, position)
                putString(BaseDaggerWebViewFragment.K_URL, url(position))
            }
        }
    }

    override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
        super.destroyItem(container, position, obj)

        if (obj is MainWebviewFragment) {
            if (logger.isDebugEnabled) {
                logger.debug("DESTROY ITEM")
            }
            obj.disableSwipeRefresh()
            obj.fragmentManager?.beginTransaction()?.apply {
                remove(obj)
                commit()
            }
        }
    }

    override fun getPageTitle(position: Int) = title(position)
    override fun getCount() = items.size // Integer.MAX_VALUE

    private inline fun url(pos: Int)   = data(pos).url
    private inline fun title(pos: Int) = data(pos).name
    private inline fun data(pos: Int) = items[pos]
}
