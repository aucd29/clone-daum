package com.example.clone_daum.ui.main

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.viewpager.widget.ViewPager
import com.example.clone_daum.R
import com.example.clone_daum.databinding.MainFragmentBinding
import com.example.clone_daum.ui.ViewController
import com.example.common.*
import com.google.android.material.appbar.AppBarLayout
import kotlinx.android.synthetic.main.tab_main_custom.view.*
import org.slf4j.LoggerFactory
import javax.inject.Inject
import kotlin.math.log

class MainFragment : BaseDaggerFragment<MainFragmentBinding, MainViewModel>() {
    companion object {
        private val mLog = LoggerFactory.getLogger(MainFragment::class.java)
    }

    @Inject lateinit var viewController: ViewController

//    // 테스트 코드
//    override fun onActivityCreated(savedInstanceState: Bundle?) {
//        super.onActivityCreated(savedInstanceState)
//
//        fragmentManager?.showDialog(BrowserSubmenuFragment(), "submenu")
//    }

    override fun settingEvents() = mViewModel.run {
        observe(gotoSearchEvent) { viewController.searchFragment() }
        observe(navEvent)        { viewController.navigationFragment() }

        mBinding.viewpager.offscreenPageLimit = 1

        // fixme main tab adapter 이건 고민 해봐야 될 듯 -_-;
        tabAdapter.set(MainTabAdapter(childFragmentManager, preConfig.tabLabelList))
        viewpager.set(mBinding.viewpager)
        viewpagerLoadedEvent.set {
            if (mLog.isDebugEnabled) {
                mLog.debug("TAB LOADED (COUNT :${mBinding.tab.tabCount})")
            }

            // https://stackoverflow.com/questions/40896907/can-a-custom-view-be-used-as-a-tabitem
            mBinding.tab.tabs.forEach {
                val view = layoutInflater.inflate(R.layout.tab_main_custom, null)
                view.tab_label.text = it?.text

                it?.customView = view
            }
        }

        appbarOffsetChangedEvent.set { appbar, offset ->
            val maxScroll = appbar.getTotalScrollRange()
            val percentage = Math.abs(offset).toFloat() / maxScroll.toFloat()

            if (mLog.isTraceEnabled) {
                mLog.trace("APP BAR (ALPHA) : $percentage")
            }

            mBinding.searchArea.alpha = 1.0f - percentage
        }
    }

    override fun onDestroy() {
        mBinding.viewpager.adapter = null

        super.onDestroy()
    }
    
    ////////////////////////////////////////////////////////////////////////////////////
    //
    //
    //
    ////////////////////////////////////////////////////////////////////////////////////

    @dagger.Module
    abstract class Module {
        @dagger.android.ContributesAndroidInjector
        abstract fun contributeInjector(): MainFragment
    }
}

// n 사도 그렇지만 k 사도 search 쪽을 view 로 가려서 하는 데
// 앱 개발자들 파워가 약한건지 -_ - 이러한 구조를 가져가는게
// 딱히 득이 될건 없어 보이는데 흠 ;
class MainScrollingViewBehavior(context: Context?, attrs: AttributeSet?) :
    AppBarLayout.ScrollingViewBehavior(context, attrs) {

    companion object {
        private val mLog = LoggerFactory.getLogger(MainScrollingViewBehavior::class.java)
    }

    override fun layoutChild(parent: CoordinatorLayout, child: View, layoutDirection: Int) {
        when (child) {
            is ViewPager -> {
                if (mLog.isDebugEnabled) {
                    mLog.debug("1 : VIEWPAGER")
                }
            }
            is AppBarLayout -> {
                mLog.error("2 : APP BAR LAYOUT")
            }
        }

        parent.onLayoutChild(child, layoutDirection)
    }

    override fun onLayoutChild(parent: CoordinatorLayout, child: View, layoutDirection: Int): Boolean {
        val res = super.onLayoutChild(parent, child, layoutDirection)

        when (child) {
            is ViewPager -> {
                if (mLog.isDebugEnabled) {
                    mLog.debug("2 : VIEWPAGER")
                }
            }
            is AppBarLayout -> {
                mLog.error("2 : APP BAR LAYOUT")
            }
        }

        return res
    }

    override fun onDependentViewChanged(parent: CoordinatorLayout, child: View, dependency: View): Boolean {
        return false; //super.onDependentViewChanged(parent, child, dependency)
    }

//    override fun onInterceptTouchEvent(parent: CoordinatorLayout, child: View, ev: MotionEvent): Boolean {
//        when (child) {
//            is ViewPager -> {
//                if (mLog.isDebugEnabled) {
//                    mLog.debug("INTERCEPTOR ${ev.x}, ${ev.y}")
//                }
//            }
//            is AppBarLayout -> {
//                mLog.error("ERROR: APP VAR LAYOUT!!!")
//            }
//        }
//
//        return super.onInterceptTouchEvent(parent, child, ev)
//    }
//
//    override fun onTouchEvent(parent: CoordinatorLayout, child: View, ev: MotionEvent): Boolean {
//        if (mLog.isDebugEnabled) {
//            mLog.debug("child  : $child")
//            mLog.debug("offset : ${child.translationY}")
//        }
//
//        return super.onTouchEvent(parent, child, ev)
//    }

    @SuppressLint("WrongConstant")
    override fun onMeasureChild(
        parent: CoordinatorLayout,
        child: View,
        parentWidthMeasureSpec: Int,
        widthUsed: Int,
        parentHeightMeasureSpec: Int,
        heightUsed: Int
    ): Boolean {
        val childLpHeight = child.layoutParams.height
        if (childLpHeight == -1 || childLpHeight == -2) {
            val dependencies = parent.getDependencies(child)
            val header = this.findFirstDependency(dependencies)

            if (header != null) {
//                if (ViewCompat.getFitsSystemWindows(header) &&
//                        !ViewCompat.getFitsSystemWindows(child)) {
//                    ViewCompat.setFitsSystemWindows(child, true)
//                    if (ViewCompat.getFitsSystemWindows(child)) {
//                        child.requestLayout()
//                        return true
//                    }
//                }
//
//                var availableHeight = View.MeasureSpec.getSize(parentHeightMeasureSpec)
//                if (availableHeight == 0) {
//                    availableHeight = parent.height
//                }
//
////                val height = availableHeight - header.measuredHeight + this.getScrollRange(header)
//                val height = availableHeight - this.getScrollRange(header)
//                val measureSpec = if (childLpHeight == -1) View.MeasureSpec.EXACTLY else View.MeasureSpec.AT_MOST
//                val heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(height, measureSpec)
//                parent.onMeasureChild(child, parentWidthMeasureSpec, widthUsed, heightMeasureSpec, heightUsed)

                return false
            }
        }

        return false
    }

    fun findFirstDependency(views: List<View>): AppBarLayout? {
        var i = 0

        val z = views.size
        while (i < z) {
            val view = views[i]
            if (view is AppBarLayout) {
                return view
            }
            ++i
        }

        return null
    }

    fun getScrollRange(v: View): Int {
        return v.measuredHeight
    }
}
