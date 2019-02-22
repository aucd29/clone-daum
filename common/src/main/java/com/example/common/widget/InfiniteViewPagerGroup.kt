package com.example.common.widget

import android.content.Context
import android.database.DataSetObserver
import android.os.Parcelable
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import org.slf4j.LoggerFactory

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2019. 2. 22. <p/>
 *
 * https://github.com/antonyt/InfiniteViewPager/blob/master/library/src/main/java/com/antonyt/infiniteviewpager/InfinitePagerAdapter.java
 * https://github.com/antonyt/InfiniteViewPager/blob/master/library/src/main/java/com/antonyt/infiniteviewpager/InfiniteViewPager.java
 */

class InfiniteViewPager : ViewPager {
    constructor(context: Context): super(context) {

    }

    constructor(context: Context, attr: AttributeSet): super(context, attr) {

    }

    override fun setAdapter(adapter: PagerAdapter?) {
        super.setAdapter(adapter)

        setCurrentItem(0)
    }

    override fun setCurrentItem(item: Int) {
        super.setCurrentItem(item, false)
    }

    override fun setCurrentItem(item: Int, smoothScroll: Boolean) {
        adapter?.let {
            if (it.count == 0) {
                super.setCurrentItem(item, smoothScroll)
                return
            }

            val newItem = offsetAmount() + (item % it.count)

            super.setCurrentItem(newItem, smoothScroll)
        } ?: super.setCurrentItem(item, smoothScroll)
    }

    private fun offsetAmount(): Int {
        return adapter?.let {
            if (it.count == 0) {
                0
            }

            if (it is InfinitePagerAdapter) {
                it.realCount * 100
            } else {
                0
            }
        }  ?: 0
    }
}

////////////////////////////////////////////////////////////////////////////////////
//
// InfinitePagerAdapter
//
////////////////////////////////////////////////////////////////////////////////////

class InfinitePagerAdapter(val adapter: PagerAdapter): PagerAdapter() {
    companion object {
        private val mLog = LoggerFactory.getLogger(InfinitePagerAdapter::class.java)
    }

    val realCount: Int
        get() = adapter.count

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val virtualPos = position % realCount

        if (mLog.isDebugEnabled) {
            mLog.debug("   REAL POS : $position")
            mLog.debug("VIRTUAL POS : $virtualPos")
        }

        return adapter.instantiateItem(container, virtualPos)
    }

    override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
        val virtualPos = position % realCount

        if (mLog.isDebugEnabled) {
            mLog.debug("   REAL POS : $position")
            mLog.debug("VIRTUAL POS : $virtualPos")
        }

        adapter.destroyItem(container, virtualPos, obj)
    }

    override fun finishUpdate(container: ViewGroup) =
        adapter.finishUpdate(container)

    override fun isViewFromObject(view: View, obj: Any) =
        adapter.isViewFromObject(view, obj)

    override fun restoreState(state: Parcelable?, loader: ClassLoader?) =
        adapter.restoreState(state, loader)

    override fun saveState() =
        adapter.saveState()

    override fun startUpdate(container: ViewGroup) =
        adapter.startUpdate(container)

    override fun getPageTitle(position: Int) =
        adapter.getPageTitle(position % realCount)

    override fun getPageWidth(position: Int) =
        adapter.getPageWidth(position)

    override fun setPrimaryItem(container: ViewGroup, position: Int, obj: Any) =
        adapter.setPrimaryItem(container, position, obj)

    override fun unregisterDataSetObserver(observer: DataSetObserver) =
        adapter.unregisterDataSetObserver(observer)

    override fun registerDataSetObserver(observer: DataSetObserver) =
        adapter.registerDataSetObserver(observer)

    override fun notifyDataSetChanged() =
        adapter.notifyDataSetChanged()

    override fun getItemPosition(obj: Any) =
        adapter.getItemPosition(obj)

    override fun getCount() =
        if (realCount == 0) 0 else Int.MAX_VALUE
}