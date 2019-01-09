//package com.example.clone_daum.common.widget
//
//import android.animation.Animator
//import androidx.annotation.RestrictTo.Scope.LIBRARY_GROUP
//import androidx.viewpager.widget.ViewPager.SCROLL_STATE_DRAGGING
//import androidx.viewpager.widget.ViewPager.SCROLL_STATE_IDLE
//import androidx.viewpager.widget.ViewPager.SCROLL_STATE_SETTLING
//
//
//import android.content.Context
//import android.util.AttributeSet
//import androidx.viewpager.widget.PagerAdapter
//import androidx.viewpager.widget.ViewPager
//import android.database.DataSetObserver
//import android.text.TextUtils
//import android.content.res.ColorStateList
//import androidx.core.graphics.drawable.DrawableCompat
//import android.os.Build.VERSION_CODES
//import android.os.Build.VERSION
//import android.graphics.drawable.Drawable
//import android.animation.AnimatorListenerAdapter
//import android.animation.ValueAnimator
//import android.annotation.SuppressLint
//import androidx.core.view.ViewCompat
//import android.os.Build
//import android.graphics.drawable.GradientDrawable
//import androidx.appcompat.widget.TooltipCompat
//import androidx.core.view.MarginLayoutParamsCompat
//import androidx.core.widget.TextViewCompat
//import android.util.TypedValue
//import android.view.accessibility.AccessibilityNodeInfo
//import android.annotation.TargetApi
//import android.graphics.*
//import android.view.accessibility.AccessibilityEvent
//import android.graphics.drawable.LayerDrawable
//import android.graphics.drawable.RippleDrawable
//import android.text.Layout
//import android.view.*
//import android.widget.*
//import com.google.android.material.ripple.RippleUtils
//import androidx.appcompat.content.res.AppCompatResources
//import androidx.core.view.PointerIconCompat
//import androidx.core.view.GravityCompat
//import com.google.android.material.tabs.TabItem
//import com.google.android.material.resources.MaterialResources
//import com.google.android.material.internal.ThemeEnforcement
//import androidx.annotation.*
//import androidx.appcompat.widget.ViewUtils
//import androidx.core.util.Pools
//import com.example.clone_daum.R
//import com.google.android.material.animation.AnimationUtils
//import com.google.android.material.tabs.TabLayout
//import java.lang.ref.WeakReference
//
//
///**
// * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2018. 12. 27. <p/>
// */
//
//class InfiniteTabLayout: HorizontalScrollView {
//    @Dimension(unit = Dimension.DP)
//    private val DEFAULT_HEIGHT_WITH_TEXT_ICON = 72
//
//    @Dimension(unit = Dimension.DP)
//    val DEFAULT_GAP_TEXT_ICON = 8
//
//    @Dimension(unit = Dimension.DP)
//    private val DEFAULT_HEIGHT = 48
//
//    @Dimension(unit = Dimension.DP)
//    private val TAB_MIN_WIDTH_MARGIN = 56
//
//    @Dimension(unit = Dimension.DP)
//    private val MIN_INDICATOR_WIDTH = 24
//
//    @Dimension(unit = Dimension.DP)
//    val FIXED_WRAP_GUTTER_MIN = 16
//
//    private val INVALID_WIDTH = -1
//
//    private val ANIMATION_DURATION = 300
//
//    private val tabPool = Pools.SynchronizedPool<Tab>(16)
//
//    /**
//     * Scrollable tabs display a subset of tabs at any given moment, and can contain longer tab labels
//     * and a larger number of tabs. They are best used for browsing contexts in touch interfaces when
//     * users don’t need to directly compare the tab labels.
//     *
//     * @see .setTabMode
//     * @see .getTabMode
//     */
//    val MODE_SCROLLABLE = 0
//
//    /**
//     * Fixed tabs display all tabs concurrently and are best used with content that benefits from
//     * quick pivots between tabs. The maximum number of tabs is limited by the view’s width. Fixed
//     * tabs have equal width, based on the widest tab label.
//     *
//     * @see .setTabMode
//     * @see .getTabMode
//     */
//    val MODE_FIXED = 1
//
////    /** @hide
////     */
////    @IntDef(value = *intArrayOf(MODE_SCROLLABLE, MODE_FIXED))
////    annotation class Mode
//
//    /**
//     * If a tab is instantiated with [TabLayout.setText], and this mode is set,
//     * the text will be saved and utilized for the content description, but no visible labels will be
//     * created.
//     *
//     * @see .setTabLabelVisibility
//     */
//    val TAB_LABEL_VISIBILITY_UNLABELED = 0
//
//    /**
//     * This mode is set by default. If a tab is instantiated with [ ][TabLayout.setText], a visible label will be created.
//     *
//     * @see .setTabLabelVisibility
//     */
//    val TAB_LABEL_VISIBILITY_LABELED = 1
//
////    /** @hide
////     */
////    @IntDef(value = *intArrayOf(TAB_LABEL_VISIBILITY_UNLABELED, TAB_LABEL_VISIBILITY_LABELED))
////    annotation class LabelVisibility
//
//    /**
//     * Gravity used to fill the [TabLayout] as much as possible. This option only takes effect
//     * when used with [.MODE_FIXED] on non-landscape screens less than 600dp wide.
//     *
//     * @see .setTabGravity
//     * @see .getTabGravity
//     */
//    val GRAVITY_FILL = 0
//
//    /**
//     * Gravity used to lay out the tabs in the center of the [TabLayout].
//     *
//     * @see .setTabGravity
//     * @see .getTabGravity
//     */
//    val GRAVITY_CENTER = 1
//
////    /** @hide
////     */
////    @RestrictTo(LIBRARY_GROUP)
////    @IntDef(flag = true, value = *intArrayOf(GRAVITY_FILL, GRAVITY_CENTER))
////    @Retention(RetentionPolicy.SOURCE)
////    annotation class TabGravity
//
//    /**
//     * Indicator gravity used to align the tab selection indicator to the bottom of the [ ]. This will only take effect if the indicator height is set via the custom indicator
//     * drawable's intrinsic height (preferred), via the `tabIndicatorHeight` attribute
//     * (deprecated), or via [.setSelectedTabIndicatorHeight] (deprecated). Otherwise, the
//     * indicator will not be shown. This is the default value.
//     *
//     * @see .setSelectedTabIndicatorGravity
//     * @see .getTabIndicatorGravity
//     * @attr ref com.google.android.material.R.styleable#TabLayout_tabIndicatorGravity
//     */
//    val INDICATOR_GRAVITY_BOTTOM = 0
//
//    /**
//     * Indicator gravity used to align the tab selection indicator to the center of the [ ]. This will only take effect if the indicator height is set via the custom indicator
//     * drawable's intrinsic height (preferred), via the `tabIndicatorHeight` attribute
//     * (deprecated), or via [.setSelectedTabIndicatorHeight] (deprecated). Otherwise, the
//     * indicator will not be shown.
//     *
//     * @see .setSelectedTabIndicatorGravity
//     * @see .getTabIndicatorGravity
//     * @attr ref com.google.android.material.R.styleable#TabLayout_tabIndicatorGravity
//     */
//    val INDICATOR_GRAVITY_CENTER = 1
//
//    /**
//     * Indicator gravity used to align the tab selection indicator to the top of the [ ]. This will only take effect if the indicator height is set via the custom indicator
//     * drawable's intrinsic height (preferred), via the `tabIndicatorHeight` attribute
//     * (deprecated), or via [.setSelectedTabIndicatorHeight] (deprecated). Otherwise, the
//     * indicator will not be shown.
//     *
//     * @see .setSelectedTabIndicatorGravity
//     * @see .getTabIndicatorGravity
//     * @attr ref com.google.android.material.R.styleable#TabLayout_tabIndicatorGravity
//     */
//    val INDICATOR_GRAVITY_TOP = 2
//
//    /**
//     * Indicator gravity used to stretch the tab selection indicator across the entire height and
//     * width of the [TabLayout]. This will disregard `tabIndicatorHeight` and the
//     * indicator drawable's intrinsic height, if set.
//     *
//     * @see .setSelectedTabIndicatorGravity
//     * @see .getTabIndicatorGravity
//     * @attr ref com.google.android.material.R.styleable#TabLayout_tabIndicatorGravity
//     */
//    val INDICATOR_GRAVITY_STRETCH = 3
//
////    /** @hide
////     */
////    @RestrictTo(LIBRARY_GROUP)
////    @IntDef(
////        value = *intArrayOf(
////            INDICATOR_GRAVITY_BOTTOM,
////            INDICATOR_GRAVITY_CENTER,
////            INDICATOR_GRAVITY_TOP,
////            INDICATOR_GRAVITY_STRETCH
////        )
////    )
////    @Retention(RetentionPolicy.SOURCE)
////    annotation class TabIndicatorGravity
//
//    /** Callback interface invoked when a tab's selection state changes.  */
//    interface OnTabSelectedListener {
//        /**
//         * Called when a tab enters the selected state.
//         *
//         * @param tab The tab that was selected
//         */
//        fun onTabSelected(tab: Tab)
//
//        /**
//         * Called when a tab exits the selected state.
//         *
//         * @param tab The tab that was unselected
//         */
//        fun onTabUnselected(tab: Tab)
//
//        /**
//         * Called when a tab that is already selected is chosen again by the user. Some applications may
//         * use this action to return to the top level of a category.
//         *
//         * @param tab The tab that was reselected.
//         */
//        fun onTabReselected(tab: Tab)
//    }
//
//    /** Callback interface invoked when a tab's selection state changes.  */
//    @Deprecated("")
//    interface BaseOnTabSelectedListener<T : Tab> {
//        /**
//         * Called when a tab enters the selected state.
//         *
//         * @param tab The tab that was selected
//         */
//        fun onTabSelected(tab: T)
//
//        /**
//         * Called when a tab exits the selected state.
//         *
//         * @param tab The tab that was unselected
//         */
//        fun onTabUnselected(tab: T)
//
//        /**
//         * Called when a tab that is already selected is chosen again by the user. Some applications may
//         * use this action to return to the top level of a category.
//         *
//         * @param tab The tab that was reselected.
//         */
//        fun onTabReselected(tab: T)
//    }
//
//    private val tabs = arrayListOf<Tab>()
//    private var selectedTab: Tab? = null
//
//    private val tabViewContentBounds = RectF()
//
//    private lateinit var slidingTabIndicator: SlidingTabIndicator
//
//    var tabPaddingStart: Int = 0
//    var tabPaddingTop: Int = 0
//    var tabPaddingEnd: Int = 0
//    var tabPaddingBottom: Int = 0
//
//    var tabTextAppearance: Int = 0
//    var tabTextColors: ColorStateList? = null
//    var tabIconTint: ColorStateList? = null
//    var tabRippleColorStateList: ColorStateList? = null
//
//    var tabSelectedIndicator: Drawable? = null
//        set(value) { setSelectedTabIndicator(value) }
//
//
//    var tabIconTintMode: android.graphics.PorterDuff.Mode? = null
//    var tabTextSize: Float = 0.toFloat()
//    lateinit var tabTextMultiLineSize: Float
//
//    var tabBackgroundResId: Int
//
//    var tabMaxWidth = Integer.MAX_VALUE
//
//    private var requestedTabMinWidth: Int
//    private var requestedTabMaxWidth: Int
//    private var scrollableTabMinWidth: Int
//
//    private var contentInsetStart: Int
//
//    var tabGravity: Int = 0
//    var tabIndicatorAnimationDuration: Int = 0
////    @TabLayout.TabIndicatorGravity
//    var tabIndicatorGravity: Int = 0
////    @TabLayout.Mode
//    var mode: Int = 0
//    var inlineLabel: Boolean = false
//    var tabIndicatorFullWidth: Boolean = false
//    var unboundedRipple: Boolean = false
//
//    private var selectedListener: InfiniteTabLayout.OnTabSelectedListener? = null
//    private val selectedListeners = arrayListOf<InfiniteTabLayout.OnTabSelectedListener>()
//    private var currentVpSelectedListener: InfiniteTabLayout.OnTabSelectedListener? = null
//    private val selectedListenerMap = hashMapOf<InfiniteTabLayout.BaseOnTabSelectedListener<in Tab>,
//            InfiniteTabLayout.OnTabSelectedListener>()
//
//    private var scrollAnimator: ValueAnimator? = null
//
//    var viewPager: ViewPager? = null
//    private var pagerAdapter: PagerAdapter? = null
//    private var pagerAdapterObserver: DataSetObserver? = null
//    private var pageChangeListener: TabLayoutOnPageChangeListener? = null
//    private var adapterChangeListener: AdapterChangeListener? = null
//    private var setupViewPagerImplicitly: Boolean = false
//
//    // Pool we use as a simple RecyclerBin
//    private val tabViewPool = Pools.SimplePool<TabView>(12)
//
//    constructor(context: Context) : this(context, null) {
//    }
//
//    constructor(context: Context, attrs: AttributeSet?)
//            : this(context, attrs, R.attr.tabStyle) {
//    }
//
//    @SuppressLint("RestrictedApi")
//    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int)
//            : super(context, attrs, defStyleAttr) {
//        // Disable the Scroll Bar
//        isHorizontalScrollBarEnabled = false
//
//        // Add the TabStrip
//        slidingTabIndicator = SlidingTabIndicator(context)
//        super.addView(
//            slidingTabIndicator,
//            0,
//            ViewGroup.LayoutParams(
//                FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.MATCH_PARENT
//            )
//        )
//
//        val a = ThemeEnforcement.obtainStyledAttributes(
//            context,
//            attrs,
//            R.styleable.TabLayout,
//            defStyleAttr,
//            R.style.Widget_Design_TabLayout,
//            R.styleable.TabLayout_tabTextAppearance
//        )
//
//        slidingTabIndicator.setSelectedIndicatorHeight(
//            a.getDimensionPixelSize(R.styleable.TabLayout_tabIndicatorHeight, -1)
//        )
//        slidingTabIndicator.setSelectedIndicatorColor(
//            a.getColor(R.styleable.TabLayout_tabIndicatorColor, 0)
//        )
//        setSelectedTabIndicator(
//            MaterialResources.getDrawable(context, a, R.styleable.TabLayout_tabIndicator)
//        )
//        setSelectedTabIndicatorGravity(
//            a.getInt(R.styleable.TabLayout_tabIndicatorGravity, INDICATOR_GRAVITY_BOTTOM)
//        )
//        setTabIndicatorFullWidth(a.getBoolean(R.styleable.TabLayout_tabIndicatorFullWidth, true))
//
//        tabPaddingBottom = a.getDimensionPixelSize(R.styleable.TabLayout_tabPadding, 0)
//        tabPaddingEnd    = tabPaddingBottom
//        tabPaddingTop    = tabPaddingEnd
//        tabPaddingStart  = tabPaddingTop
//        tabPaddingStart  = a.getDimensionPixelSize(R.styleable.TabLayout_tabPaddingStart, tabPaddingStart)
//        tabPaddingTop    = a.getDimensionPixelSize(R.styleable.TabLayout_tabPaddingTop, tabPaddingTop)
//        tabPaddingEnd    = a.getDimensionPixelSize(R.styleable.TabLayout_tabPaddingEnd, tabPaddingEnd)
//        tabPaddingBottom = a.getDimensionPixelSize(R.styleable.TabLayout_tabPaddingBottom, tabPaddingBottom)
//        tabTextAppearance = a.getResourceId(R.styleable.TabLayout_tabTextAppearance, R.style.TextAppearance_Design_Tab)
//
//        // Text colors/sizes come from the text appearance first
//        val ta = context.obtainStyledAttributes(
//            tabTextAppearance, androidx.appcompat.R.styleable.TextAppearance)
//
//        try {
//            tabTextSize = ta.getDimensionPixelSize(
//                androidx.appcompat.R.styleable.TextAppearance_android_textSize, 0
//            ).toFloat()
//
//            tabTextColors = MaterialResources.getColorStateList(
//                context,
//                ta,
//                androidx.appcompat.R.styleable.TextAppearance_android_textColor
//            )
//        } finally {
//            ta.recycle()
//        }
//
//        if (a.hasValue(R.styleable.TabLayout_tabTextColor)) {
//            // If we have an explicit text color set, use it instead
//            tabTextColors = MaterialResources.getColorStateList(context, a, R.styleable.TabLayout_tabTextColor)
//        }
//
//        if (a.hasValue(R.styleable.TabLayout_tabSelectedTextColor)) {
//            // We have an explicit selected text color set, so we need to make merge it with the
//            // current colors. This is exposed so that developers can use theme attributes to set
//            // this (theme attrs in ColorStateLists are Lollipop+)
//            val selected = a.getColor(R.styleable.TabLayout_tabSelectedTextColor, 0)
//            tabTextColors = createColorStateList(tabTextColors!!.defaultColor, selected)
//        }
//
//        tabIconTint = MaterialResources.getColorStateList(context, a, R.styleable.TabLayout_tabIconTint)
//        tabIconTintMode = ViewUtils.parseTintMode(a.getInt(R.styleable.TabLayout_tabIconTintMode, -1), null)
//
//        tabRippleColorStateList = MaterialResources.getColorStateList(context, a, R.styleable.TabLayout_tabRippleColor)
//
//        tabIndicatorAnimationDuration =
//                a.getInt(R.styleable.TabLayout_tabIndicatorAnimationDuration, ANIMATION_DURATION)
//
//        requestedTabMinWidth = a.getDimensionPixelSize(R.styleable.TabLayout_tabMinWidth, INVALID_WIDTH)
//        requestedTabMaxWidth = a.getDimensionPixelSize(R.styleable.TabLayout_tabMaxWidth, INVALID_WIDTH)
//        tabBackgroundResId = a.getResourceId(R.styleable.TabLayout_tabBackground, 0)
//        contentInsetStart = a.getDimensionPixelSize(R.styleable.TabLayout_tabContentStart, 0)
//
//        mode = a.getInt(R.styleable.TabLayout_tabMode, MODE_FIXED)
//        tabGravity = a.getInt(R.styleable.TabLayout_tabGravity, GRAVITY_FILL)
//        inlineLabel = a.getBoolean(R.styleable.TabLayout_tabInlineLabel, false)
//        unboundedRipple = a.getBoolean(R.styleable.TabLayout_tabUnboundedRipple, false)
//        a.recycle()
//
//        // TODO add attr for these
//        val res    = resources
//        tabTextMultiLineSize  = res.getDimensionPixelSize(R.dimen.design_tab_text_size_2line).toFloat()
//        scrollableTabMinWidth = res.getDimensionPixelSize(R.dimen.design_tab_scrollable_min_width)
//
//        // Now apply the tab mode and gravity
//        applyModeAndGravity()
//    }
//
//    /**
//     * Sets the tab indicator's color for the currently selected tab.
//     *
//     * @param color color to use for the indicator
//     * @attr ref com.google.android.material.R.styleable#TabLayout_tabIndicatorColor
//     */
//    fun setSelectedTabIndicatorColor(@ColorInt color: Int) {
//        slidingTabIndicator.setSelectedIndicatorColor(color)
//    }
//
//    /**
//     * Sets the tab indicator's height for the currently selected tab. This method is deprecated. If
//     * possible, set the intrinsic height directly on a custom indicator drawable passed to [ ][.setSelectedTabIndicator].
//     *
//     * @param height height to use for the indicator in pixels
//     * @attr ref com.google.android.material.R.styleable#TabLayout_tabIndicatorHeight
//     */
//    @Deprecated("")
//    fun setSelectedTabIndicatorHeight(height: Int) {
//        slidingTabIndicator.setSelectedIndicatorHeight(height)
//    }
//
//    /**
//     * Set the scroll position of the tabs. This is useful for when the tabs are being displayed as
//     * part of a scrolling container such as [androidx.viewpager.widget.ViewPager].
//     *
//     *
//     * Calling this method does not update the selected tab, it is only used for drawing purposes.
//     *
//     * @param position current scroll position
//     * @param positionOffset Value from [0, 1) indicating the offset from `position`.
//     * @param updateSelectedText Whether to update the text's selected state.
//     */
//    fun setScrollPosition(position: Int, positionOffset: Float, updateSelectedText: Boolean) {
//        setScrollPosition(position, positionOffset, updateSelectedText, true)
//    }
//
//    fun setScrollPosition(
//        position: Int,
//        positionOffset: Float,
//        updateSelectedText: Boolean,
//        updateIndicatorPosition: Boolean
//    ) {
//        val roundedPosition = Math.round(position + positionOffset)
//        if (roundedPosition < 0 || roundedPosition >= slidingTabIndicator.childCount) {
//            return
//        }
//
//        // Set the indicator position, if enabled
//        if (updateIndicatorPosition) {
//            slidingTabIndicator.setIndicatorPositionFromTabPosition(position, positionOffset)
//        }
//
//        // Now update the scroll position, canceling any running animation
//        if (scrollAnimator != null && scrollAnimator!!.isRunning) {
//            scrollAnimator!!.cancel()
//        }
//        scrollTo(calculateScrollXForTab(position, positionOffset), 0)
//
//        // Update the 'selected state' view as we scroll, if enabled
//        if (updateSelectedText) {
//            setSelectedTabView(roundedPosition)
//        }
//    }
//
//    /**
//     * Add a tab to this layout. The tab will be added at the end of the list. If this is the first
//     * tab to be added it will become the selected tab.
//     *
//     * @param tab Tab to add
//     */
//    fun addTab(tab: Tab) {
//        addTab(tab, tabs.isEmpty())
//    }
//
//    /**
//     * Add a tab to this layout. The tab will be inserted at `position`. If this is the
//     * first tab to be added it will become the selected tab.
//     *
//     * @param tab The tab to add
//     * @param position The new position of the tab
//     */
//    fun addTab(tab: Tab, position: Int) {
//        addTab(tab, position, tabs.isEmpty())
//    }
//
//    /**
//     * Add a tab to this layout. The tab will be added at the end of the list.
//     *
//     * @param tab Tab to add
//     * @param setSelected True if the added tab should become the selected tab.
//     */
//    fun addTab(tab: Tab, setSelected: Boolean) {
//        addTab(tab, tabs.size, setSelected)
//    }
//
//    /**
//     * Add a tab to this layout. The tab will be inserted at `position`.
//     *
//     * @param tab The tab to add
//     * @param position The new position of the tab
//     * @param setSelected True if the added tab should become the selected tab.
//     */
//    fun addTab(tab: Tab, position: Int, setSelected: Boolean) {
//        if (tab.parent !== this) {
//            throw IllegalArgumentException("Tab belongs to a different TabLayout.")
//        }
//        configureTab(tab, position)
//        addTabView(tab)
//
//        if (setSelected) {
//            tab.select()
//        }
//    }
//
//    private fun addTabFromItemView(item: TabItem) {
//        val tab = newTab()
//        if (item.text != null) {
//            tab.setText(item.text)
//        }
//        if (item.icon != null) {
//            tab.setIcon(item.icon)
//        }
//        if (item.customLayout != 0) {
//            tab.setCustomView(item.customLayout)
//        }
//        if (!TextUtils.isEmpty(item.contentDescription)) {
//            tab.setContentDescription(item.contentDescription)
//        }
//        addTab(tab)
//    }
//
////
////    @Deprecated(
////        "Use {@url #addOnTabSelectedListener(OnTabSelectedListener)} and {@url\n" +
////                "   *     #removeOnTabSelectedListener(OnTabSelectedListener)}."
////    )
////    fun setOnTabSelectedListener(listener: OnTabSelectedListener?) {
////        // The logic in this method emulates what we had before support for multiple
////        // registered listeners.
////        if (selectedListener != null) {
////            removeOnTabSelectedListener(selectedListener)
////        }
////        // Update the deprecated field so that we can remove the passed listener the next
////        // time we're called
////        selectedListener = listener
////        if (listener != null) {
////            addOnTabSelectedListener(listener)
////        }
////    }
//
//
////    @Deprecated(
////        "Use {@url #addOnTabSelectedListener(OnTabSelectedListener)} and {@url\n" +
////                "   *     #removeOnTabSelectedListener(OnTabSelectedListener)}."
////    )
////    fun setOnTabSelectedListener(@Nullable listener: BaseOnTabSelectedListener<*>) {
////        setOnTabSelectedListener(wrapOnTabSelectedListener(listener))
////    }
//
//    /**
//     * Add a [TabLayout.OnTabSelectedListener] that will be invoked when tab selection changes.
//     *
//     *
//     * Components that add a listener should take care to remove it when finished via [ ][.removeOnTabSelectedListener].
//     *
//     * @param listener listener to add
//     */
//    fun addOnTabSelectedListener(listener: InfiniteTabLayout.OnTabSelectedListener) {
//        if (!selectedListeners.contains(listener)) {
//            selectedListeners.add(listener)
//        }
//    }
//
////    /**
////     * Add a [InfiniteTabLayout.BaseOnTabSelectedListener] that will be invoked when tab selection
////     * changes.
////     *
////     *
////     * Components that add a listener should take care to remove it when finished via [ ][.removeOnTabSelectedListener].
////     *
////     * @param listener listener to add
////     */
////    @Deprecated("use {@url #addOnTabSelectedListener(OnTabSelectedListener)}")
////    fun addOnTabSelectedListener(@Nullable listener: BaseOnTabSelectedListener<*>) {
////        addOnTabSelectedListener(wrapOnTabSelectedListener(listener))
////    }
//
//    /**
//     * Remove the given [InfiniteTabLayout.OnTabSelectedListener] that was previously added via [ ][.addOnTabSelectedListener].
//     *
//     * @param listener listener to remove
//     */
//    fun removeOnTabSelectedListener(listener: OnTabSelectedListener) {
//        selectedListeners.remove(listener)
//    }
//
////    /**
////     * Remove the given [InfiniteTabLayout.BaseOnTabSelectedListener] that was previously added via
////     * [.addOnTabSelectedListener].
////     *
////     * @param listener listener to remove
////     */
////    @Deprecated("use {@url #removeOnTabSelectedListener(OnTabSelectedListener)}")
////    fun removeOnTabSelectedListener(@Nullable listener: BaseOnTabSelectedListener<*>) {
////        removeOnTabSelectedListener(wrapOnTabSelectedListener(listener))
////    }
////
////    /** @hide
////     */
////    @RestrictTo(LIBRARY_GROUP)
////    protected fun wrapOnTabSelectedListener(
////        @Nullable baseListener: BaseOnTabSelectedListener<*>?
////    ): OnTabSelectedListener? {
////        if (baseListener == null) {
////            return null
////        }
////
////        if (selectedListenerMap.containsKey(baseListener)) {
////            return selectedListenerMap.get(baseListener)
////        }
////
////        val listener = object : OnTabSelectedListener {
////            override fun onTabSelected(tab: Tab) {
////                baseListener.onTabSelected(tab)
////            }
////
////            override fun onTabUnselected(tab: Tab) {
////                baseListener.onTabUnselected(tab)
////            }
////
////            override fun onTabReselected(tab: Tab) {
////                baseListener.onTabReselected(tab)
////            }
////        }
////
////        selectedListenerMap.put(baseListener, listener)
////        return listener
////    }
//
//    /** Remove all previously added [InfiniteTabLayout.OnTabSelectedListener]s.  */
//    fun clearOnTabSelectedListeners() {
//        selectedListeners.clear()
//        selectedListenerMap.clear()
//    }
//
//    /**
//     * Create and return a new [Tab]. You need to manually add this using [.addTab]
//     * or a related method.
//     *
//     * @return A new Tab
//     * @see .addTab
//     */
//    fun newTab(): Tab {
//        val tab = createTabFromPool()
//        tab.parent = this
//        tab.view = createTabView(tab)
//        return tab
//    }
//
//    // TODO: remove this method and just create the final field after the widget migration
//    fun createTabFromPool(): Tab {
//        var tab = tabPool.acquire()
//        if (tab == null) {
//            tab = Tab()
//        }
//        return tab
//    }
//
//    // TODO: remove this method and just create the final field after the widget migration
//    protected fun releaseFromTabPool(tab: Tab): Boolean {
//        return tabPool.release(tab)
//    }
//
//    /**
//     * Returns the number of tabs currently registered with the action bar.
//     *
//     * @return Tab count
//     */
//    fun getTabCount(): Int {
//        return tabs.size
//    }
//
//    /** Returns the tab at the specified index.  */
//    fun getTabAt(index: Int): Tab? {
//        return if (index < 0 || index >= getTabCount()) null else tabs.get(index)
//    }
//
//    /**
//     * Returns the position of the current selected tab.
//     *
//     * @return selected tab position, or `-1` if there isn't a selected tab.
//     */
//    fun getSelectedTabPosition(): Int {
//        return if (selectedTab != null) selectedTab!!.position else -1
//    }
//
//    /**
//     * Remove a tab from the layout. If the removed tab was selected it will be deselected and another
//     * tab will be selected if present.
//     *
//     * @param tab The tab to remove
//     */
//    fun removeTab(tab: Tab) {
//        if (tab.parent !== this) {
//            throw IllegalArgumentException("Tab does not belong to this TabLayout.")
//        }
//
//        removeTabAt(tab.position)
//    }
//
//    /**
//     * Remove a tab from the layout. If the removed tab was selected it will be deselected and another
//     * tab will be selected if present.
//     *
//     * @param position Position of the tab to remove
//     */
//    fun removeTabAt(position: Int) {
//        val selectedTabPosition = if (selectedTab != null) selectedTab!!.position else 0
//        removeTabViewAt(position)
//
//        val removedTab = tabs.remove(position)
//        if (removedTab != null) {
//            removedTab!!.reset()
//            releaseFromTabPool(removedTab)
//        }
//
//        val newTabCount = tabs.size
//        for (i in position until newTabCount) {
//            tabs.get(i).setPosition(i)
//        }
//
//        if (selectedTabPosition == position) {
//            selectTab(if (tabs.isEmpty()) null else tabs.get(Math.max(0, position - 1)))
//        }
//    }
//
//    /** Remove all tabs from the action bar and deselect the current tab.  */
//    fun removeAllTabs() {
//        // Remove all the views
//        for (i in slidingTabIndicator.childCount - 1 downTo 0) {
//            removeTabViewAt(i)
//        }
//
//        val i = tabs.iterator()
//        while (i.hasNext()) {
//            val tab = i.next()
//            i.remove()
//            tab.reset()
//            releaseFromTabPool(tab)
//        }
//
//        selectedTab = null
//    }
//
//    /**
//     * Set the behavior mode for the Tabs in this layout. The valid input options are:
//     *
//     *
//     *  * [.MODE_FIXED]: Fixed tabs display all tabs concurrently and are best used with
//     * content that benefits from quick pivots between tabs.
//     *  * [.MODE_SCROLLABLE]: Scrollable tabs display a subset of tabs at any given moment,
//     * and can contain longer tab labels and a larger number of tabs. They are best used for
//     * browsing contexts in touch interfaces when users don’t need to directly compare the tab
//     * labels. This mode is commonly used with a [androidx.viewpager.widget.ViewPager].
//     *
//     *
//     * @param mode one of [.MODE_FIXED] or [.MODE_SCROLLABLE].
//     * @attr ref com.google.android.material.R.styleable#TabLayout_tabMode
//     */
//    fun setTabMode(@Mode mode: Int) {
//        if (mode != this.mode) {
//            this.mode = mode
//            applyModeAndGravity()
//        }
//    }
//
//    /**
//     * Returns the current mode used by this [TabLayout].
//     *
//     * @see .setTabMode
//     */
//    @Mode
//    fun getTabMode(): Int {
//        return mode
//    }
//
//    /**
//     * Set the gravity to use when laying out the tabs.
//     *
//     * @param gravity one of [.GRAVITY_CENTER] or [.GRAVITY_FILL].
//     * @attr ref com.google.android.material.R.styleable#TabLayout_tabGravity
//     */
//    fun setTabGravity(@TabLayout.TabGravity gravity: Int) {
//        if (tabGravity != gravity) {
//            tabGravity = gravity
//            applyModeAndGravity()
//        }
//    }
//
//    /**
//     * The current gravity used for laying out tabs.
//     *
//     * @return one of [.GRAVITY_CENTER] or [.GRAVITY_FILL].
//     */
//    @TabGravity
//    fun getTabGravity(): Int {
//        return tabGravity
//    }
//
//    /**
//     * Set the indicator gravity used to align the tab selection indicator in the [TabLayout].
//     * You must set the indicator height via the custom indicator drawable's intrinsic height
//     * (preferred), via the `tabIndicatorHeight` attribute (deprecated), or via [ ][.setSelectedTabIndicatorHeight] (deprecated). Otherwise, the indicator will not be shown
//     * unless gravity is set to [.INDICATOR_GRAVITY_STRETCH], in which case it will ignore
//     * indicator height and stretch across the entire height and width of the [TabLayout]. This
//     * defaults to [.INDICATOR_GRAVITY_BOTTOM] if not set.
//     *
//     * @param indicatorGravity one of [.INDICATOR_GRAVITY_BOTTOM], [     ][.INDICATOR_GRAVITY_CENTER], [.INDICATOR_GRAVITY_TOP], or [     ][.INDICATOR_GRAVITY_STRETCH]
//     * @attr ref com.google.android.material.R.styleable#TabLayout_tabIndicatorGravity
//     */
//    fun setSelectedTabIndicatorGravity(@TabIndicatorGravity indicatorGravity: Int) {
//        if (tabIndicatorGravity != indicatorGravity) {
//            tabIndicatorGravity = indicatorGravity
//            ViewCompat.postInvalidateOnAnimation(slidingTabIndicator)
//        }
//    }
//
//    /**
//     * Get the current indicator gravity used to align the tab selection indicator in the [ ].
//     *
//     * @return one of [.INDICATOR_GRAVITY_BOTTOM], [.INDICATOR_GRAVITY_CENTER], [     ][.INDICATOR_GRAVITY_TOP], or [.INDICATOR_GRAVITY_STRETCH]
//     */
//    @TabIndicatorGravity
//    fun getTabIndicatorGravity(): Int {
//        return tabIndicatorGravity
//    }
//
//    /**
//     * Enable or disable option to fit the tab selection indicator to the full width of the tab item
//     * rather than to the tab item's content.
//     *
//     *
//     * Defaults to true. If set to false and the tab item has a text label, the selection indicator
//     * width will be set to the width of the text label. If the tab item has no text label, but does
//     * have an icon, the selection indicator width will be set to the icon. If the tab item has
//     * neither of these, or if the calculated width is less than a minimum width value, the selection
//     * indicator width will be set to the minimum width value.
//     *
//     * @param tabIndicatorFullWidth Whether or not to fit selection indicator width to full width of
//     * the tab item
//     * @attr ref com.google.android.material.R.styleable#TabLayout_tabIndicatorFullWidth
//     * @see .isTabIndicatorFullWidth
//     */
//    fun setTabIndicatorFullWidth(tabIndicatorFullWidth: Boolean) {
//        this.tabIndicatorFullWidth = tabIndicatorFullWidth
//        ViewCompat.postInvalidateOnAnimation(slidingTabIndicator)
//    }
//
//    /**
//     * Get whether or not selection indicator width is fit to full width of the tab item, or fit to
//     * the tab item's content.
//     *
//     * @return whether or not selection indicator width is fit to the full width of the tab item
//     * @attr ref com.google.android.material.R.styleable#TabLayout_tabIndicatorFullWidth
//     * @see .setTabIndicatorFullWidth
//     */
//    fun isTabIndicatorFullWidth(): Boolean {
//        return tabIndicatorFullWidth
//    }
//
//    /**
//     * Set whether tab labels will be displayed inline with tab icons, or if they will be displayed
//     * underneath tab icons.
//     *
//     * @see .isInlineLabel
//     * @attr ref com.google.android.material.R.styleable#TabLayout_tabInlineLabel
//     */
//    fun setInlineLabel(inline: Boolean) {
//        if (inlineLabel != inline) {
//            inlineLabel = inline
//            for (i in 0 until slidingTabIndicator.childCount) {
//                val child = slidingTabIndicator.getChildAt(i)
//                if (child is TabView) {
//                    child.updateOrientation()
//                }
//            }
//            applyModeAndGravity()
//        }
//    }
//
//    /**
//     * Set whether tab labels will be displayed inline with tab icons, or if they will be displayed
//     * underneath tab icons.
//     *
//     * @param inlineResourceId Resource ID for boolean inline flag
//     * @see .isInlineLabel
//     * @attr ref com.google.android.material.R.styleable#TabLayout_tabInlineLabel
//     */
//    fun setInlineLabelResource(@BoolRes inlineResourceId: Int) {
//        isInlineLabel = resources.getBoolean(inlineResourceId)
//    }
//
//    /**
//     * Returns whether tab labels will be displayed inline with tab icons, or if they will be
//     * displayed underneath tab icons.
//     *
//     * @see .setInlineLabel
//     * @attr ref com.google.android.material.R.styleable#TabLayout_tabInlineLabel
//     */
//    fun isInlineLabel(): Boolean {
//        return inlineLabel
//    }
//
//    /**
//     * Set whether this [TabLayout] will have an unbounded ripple effect or if ripple will be
//     * bound to the tab item size.
//     *
//     *
//     * Defaults to false.
//     *
//     * @see .hasUnboundedRipple
//     * @attr ref com.google.android.material.R.styleable#TabLayout_tabUnboundedRipple
//     */
//    fun setUnboundedRipple(unboundedRipple: Boolean) {
//        if (this.unboundedRipple != unboundedRipple) {
//            this.unboundedRipple = unboundedRipple
//            for (i in 0 until slidingTabIndicator.childCount) {
//                val child = slidingTabIndicator.getChildAt(i)
//                if (child is TabView) {
//                    child.updateBackgroundDrawable(context)
//                }
//            }
//        }
//    }
//
//    /**
//     * Set whether this [TabLayout] will have an unbounded ripple effect or if ripple will be
//     * bound to the tab item size. Defaults to false.
//     *
//     * @param unboundedRippleResourceId Resource ID for boolean unbounded ripple value
//     * @see .hasUnboundedRipple
//     * @attr ref com.google.android.material.R.styleable#TabLayout_tabUnboundedRipple
//     */
//    fun setUnboundedRippleResource(@BoolRes unboundedRippleResourceId: Int) {
//        setUnboundedRipple(resources.getBoolean(unboundedRippleResourceId))
//    }
//
//    /**
//     * Returns whether this [TabLayout] has an unbounded ripple effect, or if ripple is bound to
//     * the tab item size.
//     *
//     * @see .setUnboundedRipple
//     * @attr ref com.google.android.material.R.styleable#TabLayout_tabUnboundedRipple
//     */
//    fun hasUnboundedRipple(): Boolean {
//        return unboundedRipple
//    }
//
//    /**
//     * Sets the text colors for the different states (normal, selected) used for the tabs.
//     *
//     * @see .getTabTextColors
//     */
//    fun setTabTextColors(@Nullable textColor: ColorStateList?) {
//        if (tabTextColors !== textColor) {
//            tabTextColors = textColor
//            updateAllTabs()
//        }
//    }
//
//    /** Gets the text colors for the different states (normal, selected) used for the tabs.  */
//    @Nullable
//    fun getTabTextColors(): ColorStateList? {
//        return tabTextColors
//    }
//
//    /**
//     * Sets the text colors for the different states (normal, selected) used for the tabs.
//     *
//     * @attr ref com.google.android.material.R.styleable#TabLayout_tabTextColor
//     * @attr ref com.google.android.material.R.styleable#TabLayout_tabSelectedTextColor
//     */
//    fun setTabTextColors(normalColor: Int, selectedColor: Int) {
//        setTabTextColors(createColorStateList(normalColor, selectedColor))
//    }
//
//    /**
//     * Sets the icon tint for the different states (normal, selected) used for the tabs.
//     *
//     * @see .getTabIconTint
//     */
//    fun setTabIconTint(@Nullable iconTint: ColorStateList?) {
//        if (tabIconTint !== iconTint) {
//            tabIconTint = iconTint
//            updateAllTabs()
//        }
//    }
//
//    /**
//     * Sets the icon tint resource for the different states (normal, selected) used for the tabs.
//     *
//     * @param iconTintResourceId A color resource to use as icon tint.
//     * @see .getTabIconTint
//     */
//    fun setTabIconTintResource(@ColorRes iconTintResourceId: Int) {
//        setTabIconTint(AppCompatResources.getColorStateList(context, iconTintResourceId))
//    }
//
//    /** Gets the icon tint for the different states (normal, selected) used for the tabs.  */
//    @Nullable
//    fun getTabIconTint(): ColorStateList? {
//        return tabIconTint
//    }
//
//    /**
//     * Returns the ripple color for this TabLayout.
//     *
//     * @return the color (or ColorStateList) used for the ripple
//     * @see .setTabRippleColor
//     */
//    @Nullable
//    fun getTabRippleColor(): ColorStateList? {
//        return tabRippleColorStateList
//    }
//
//    /**
//     * Sets the ripple color for this TabLayout.
//     *
//     *
//     * When running on devices with KitKat or below, we draw this color as a filled overlay rather
//     * than a ripple.
//     *
//     * @param color color (or ColorStateList) to use for the ripple
//     * @attr ref com.google.android.material.R.styleable#TabLayout_tabRippleColor
//     * @see .getTabRippleColor
//     */
//    fun setTabRippleColor(@Nullable color: ColorStateList?) {
//        if (tabRippleColorStateList !== color) {
//            tabRippleColorStateList = color
//            for (i in 0 until slidingTabIndicator.childCount) {
//                val child = slidingTabIndicator.getChildAt(i)
//                if (child is TabView) {
//                    child.updateBackgroundDrawable(context)
//                }
//            }
//        }
//    }
//
//    /**
//     * Sets the ripple color resource for this TabLayout.
//     *
//     *
//     * When running on devices with KitKat or below, we draw this color as a filled overlay rather
//     * than a ripple.
//     *
//     * @param tabRippleColorResourceId A color resource to use as ripple color.
//     * @see .getTabRippleColor
//     */
//    fun setTabRippleColorResource(@ColorRes tabRippleColorResourceId: Int) {
//        tabRippleColor = AppCompatResources.getColorStateList(context, tabRippleColorResourceId)
//    }
//
////    /**
////     * Returns the selection indicator drawable for this TabLayout.
////     *
////     * @return The drawable used as the tab selection indicator, if set.
////     * @see .setSelectedTabIndicator
////     * @see .setSelectedTabIndicator
////     */
////    @Nullable
////    fun getTabSelectedIndicator(): Drawable? {
////        return tabSelectedIndicator
////    }
//
//    /**
//     * Sets the selection indicator for this TabLayout. By default, this is a line along the bottom of
//     * the tab. If `tabIndicatorColor` is specified via the TabLayout's style or via [ ][.setSelectedTabIndicatorColor] the selection indicator will be tinted that color.
//     * Otherwise, it will use the colors specified in the drawable.
//     *
//     * @param tabSelectedIndicator A drawable to use as the selected tab indicator.
//     * @see .setSelectedTabIndicatorColor
//     * @see .setSelectedTabIndicator
//     */
//    fun setSelectedTabIndicator(@Nullable tabSelectedIndicator: Drawable) {
//        if (this.tabSelectedIndicator !== tabSelectedIndicator) {
//            this.tabSelectedIndicator = tabSelectedIndicator
//            ViewCompat.postInvalidateOnAnimation(slidingTabIndicator)
//        }
//    }
//
//    /**
//     * Sets the drawable resource to use as the selection indicator for this TabLayout. By default,
//     * this is a line along the bottom of the tab. If `tabIndicatorColor` is specified via the
//     * TabLayout's style or via [.setSelectedTabIndicatorColor] the selection indicator
//     * will be tinted that color. Otherwise, it will use the colors specified in the drawable.
//     *
//     * @param tabSelectedIndicatorResourceId A drawable resource to use as the selected tab indicator.
//     * @see .setSelectedTabIndicatorColor
//     * @see .setSelectedTabIndicator
//     */
//    fun setSelectedTabIndicator(@DrawableRes tabSelectedIndicatorResourceId: Int) {
//        if (tabSelectedIndicatorResourceId != 0) {
//            setSelectedTabIndicator(
//                AppCompatResources.getDrawable(context, tabSelectedIndicatorResourceId)
//            )
//        } else {
//            setSelectedTabIndicator(null)
//        }
//    }
//
//    /**
//     * The one-stop shop for setting up this [TabLayout] with a [ViewPager].
//     *
//     *
//     * This is the same as calling [.setupWithViewPager] with
//     * auto-refresh enabled.
//     *
//     * @param viewPager the ViewPager to url to, or `null` to clear any previous url
//     */
//    fun setupWithViewPager(@Nullable viewPager: ViewPager?) {
//        setupWithViewPager(viewPager, true)
//    }
//
//    /**
//     * The one-stop shop for setting up this [TabLayout] with a [ViewPager].
//     *
//     *
//     * This method will url the given ViewPager and this TabLayout together so that changes in one
//     * are automatically reflected in the other. This includes scroll state changes and clicks. The
//     * tabs displayed in this layout will be populated from the ViewPager adapter's page titles.
//     *
//     *
//     * If `autoRefresh` is `true`, any changes in the [PagerAdapter] will trigger
//     * this layout to re-populate itself from the adapter's titles.
//     *
//     *
//     * If the given ViewPager is non-null, it needs to already have a [PagerAdapter] set.
//     *
//     * @param viewPager the ViewPager to url to, or `null` to clear any previous url
//     * @param autoRefresh whether this layout should refresh its contents if the given ViewPager's
//     * content changes
//     */
//    fun setupWithViewPager(@Nullable viewPager: ViewPager?, autoRefresh: Boolean) {
//        setupWithViewPager(viewPager, autoRefresh, false)
//    }
//
//    private fun setupWithViewPager(
//        @Nullable viewPager: ViewPager?, autoRefresh: Boolean, implicitSetup: Boolean
//    ) {
//        if (this.viewPager != null) {
//            // If we've already been setup with a ViewPager, remove us from it
//            if (pageChangeListener != null) {
//                this.viewPager!!.removeOnPageChangeListener(pageChangeListener!!)
//            }
//            if (adapterChangeListener != null) {
//                this.viewPager!!.removeOnAdapterChangeListener(adapterChangeListener!!)
//            }
//        }
//
//        if (currentVpSelectedListener != null) {
//            // If we already have a tab selected listener for the ViewPager, remove it
//            removeOnTabSelectedListener(currentVpSelectedListener)
//            currentVpSelectedListener = null
//        }
//
//        if (viewPager != null) {
//            this.viewPager = viewPager
//
//            // Add our custom OnPageChangeListener to the ViewPager
//            if (pageChangeListener == null) {
//                pageChangeListener = TabLayoutOnPageChangeListener(this)
//            }
//            pageChangeListener!!.reset()
//            viewPager.addOnPageChangeListener(pageChangeListener!!)
//
//            // Now we'll add a tab selected listener to set ViewPager's current item
//            currentVpSelectedListener = ViewPagerOnTabSelectedListener(viewPager)
//            addOnTabSelectedListener(currentVpSelectedListener)
//
//            val adapter = viewPager.adapter
//            if (adapter != null) {
//                // Now we'll populate ourselves from the pager adapter, adding an observer if
//                // autoRefresh is enabled
//                setPagerAdapter(adapter, autoRefresh)
//            }
//
//            // Add a listener so that we're notified of any adapter changes
//            if (adapterChangeListener == null) {
//                adapterChangeListener = AdapterChangeListener()
//            }
//            adapterChangeListener!!.setAutoRefresh(autoRefresh)
//            viewPager.addOnAdapterChangeListener(adapterChangeListener!!)
//
//            // Now update the scroll position to match the ViewPager's current item
//            setScrollPosition(viewPager.currentItem, 0f, true)
//        } else {
//            // We've been given a null ViewPager so we need to clear out the internal state,
//            // listeners and observers
//            this.viewPager = null
//            setPagerAdapter(null, false)
//        }
//
//        setupViewPagerImplicitly = implicitSetup
//    }
//
//
////    @Deprecated(
////        "Use {@url #setupWithViewPager(ViewPager)} to url a TabLayout with a ViewPager\n" +
////                "        together. When that method is used, the TabLayout will be automatically updated when the\n" +
////                "        {@url PagerAdapter} is changed."
////    )
////    fun setTabsFromPagerAdapter(@Nullable adapter: PagerAdapter?) {
////        setPagerAdapter(adapter, false)
////    }
//
//    override fun shouldDelayChildPressedState(): Boolean {
//        // Only delay the pressed state if the tabs can scroll
//        return getTabScrollRange() > 0
//    }
//
//    override fun onAttachedToWindow() {
//        super.onAttachedToWindow()
//
//        if (viewPager == null) {
//            // If we don't have a ViewPager already, check if our parent is a ViewPager to
//            // setup with it automatically
//            val vp = parent
//            if (vp is ViewPager) {
//                // If we have a ViewPager parent and we've been added as part of its decor, let's
//                // assume that we should automatically setup to display any titles
//                setupWithViewPager(vp, true, true)
//            }
//        }
//    }
//
//    override fun onDetachedFromWindow() {
//        super.onDetachedFromWindow()
//
//        if (setupViewPagerImplicitly) {
//            // If we've been setup with a ViewPager implicitly, let's clear out any listeners, etc
//            setupWithViewPager(null)
//            setupViewPagerImplicitly = false
//        }
//    }
//
//    private fun getTabScrollRange(): Int {
//        return Math.max(
//            0, slidingTabIndicator.width - width - paddingLeft - paddingRight
//        )
//    }
//
//    fun setPagerAdapter(@Nullable adapter: PagerAdapter?, addObserver: Boolean) {
//        if (pagerAdapter != null && pagerAdapterObserver != null) {
//            // If we already have a PagerAdapter, unregister our observer
//            pagerAdapter!!.unregisterDataSetObserver(pagerAdapterObserver!!)
//        }
//
//        pagerAdapter = adapter
//
//        if (addObserver && adapter != null) {
//            // Register our observer on the new adapter
//            if (pagerAdapterObserver == null) {
//                pagerAdapterObserver = PagerAdapterObserver()
//            }
//            adapter.registerDataSetObserver(pagerAdapterObserver!!)
//        }
//
//        // Finally make sure we reflect the new adapter
//        populateFromPagerAdapter()
//    }
//
//    fun populateFromPagerAdapter() {
//        removeAllTabs()
//
//        if (pagerAdapter != null) {
//            val adapterCount = pagerAdapter!!.count
//            for (i in 0 until adapterCount) {
//                addTab(newTab().setText(pagerAdapter!!.getPageTitle(i)), false)
//            }
//
//            // Make sure we reflect the currently set ViewPager item
//            if (viewPager != null && adapterCount > 0) {
//                val curItem = viewPager!!.currentItem
//                if (curItem != selectedTabPosition && curItem < tabCount) {
//                    selectTab(getTabAt(curItem))
//                }
//            }
//        }
//    }
//
//    private fun updateAllTabs() {
//        var i = 0
//        val z = tabs.size
//        while (i < z) {
//            tabs.get(i).updateView()
//            i++
//        }
//    }
//
//    private fun createTabView(tab: Tab): TabView {
//        var tabView: TabView? = tabViewPool?.acquire()
//        if (tabView == null) {
//            tabView = TabView(context)
//        }
//        tabView.tab = tab
//        tabView.isFocusable = true
//        tabView.minimumWidth = getTabMinWidth()
//        if (TextUtils.isEmpty(tab.contentDesc)) {
//            tabView.contentDescription = tab.text
//        } else {
//            tabView.contentDescription = tab.contentDesc
//        }
//        return tabView
//    }
//
//    private fun configureTab(tab: Tab, position: Int) {
//        tab.position = position
//        tabs.add(position, tab)
//
//        val count = tabs.size
//        for (i in position + 1 until count) {
//            tabs.get(i).setPosition(i)
//        }
//    }
//
//    private fun addTabView(tab: Tab) {
//        val tabView = tab.view
//        tabView!!.isSelected = false
//        tabView.isActivated = false
//        slidingTabIndicator.addView(tabView, tab.position, createLayoutParamsForTabs())
//    }
//
//    fun addView(child: View) {
//        addViewInternal(child)
//    }
//
//    fun addView(child: View, index: Int) {
//        addViewInternal(child)
//    }
//
//    fun addView(child: View, params: ViewGroup.LayoutParams) {
//        addViewInternal(child)
//    }
//
//    fun addView(child: View, index: Int, params: ViewGroup.LayoutParams) {
//        addViewInternal(child)
//    }
//
//    private fun addViewInternal(child: View) {
//        if (child is TabItem) {
//            addTabFromItemView(child as TabItem)
//        } else {
//            throw IllegalArgumentException("Only TabItem instances can be added to TabLayout")
//        }
//    }
//
//    private fun createLayoutParamsForTabs(): LinearLayout.LayoutParams {
//        val lp = LinearLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.MATCH_PARENT)
//        updateTabViewLayoutParams(lp)
//        return lp
//    }
//
//    private fun updateTabViewLayoutParams(lp: LinearLayout.LayoutParams) {
//        if (mode == MODE_FIXED && tabGravity == GRAVITY_FILL) {
//            lp.width = 0
//            lp.weight = 1f
//        } else {
//            lp.width = LinearLayout.LayoutParams.WRAP_CONTENT
//            lp.weight = 0f
//        }
//    }
//
//    @Dimension(unit = Dimension.PX)
//    fun dpToPx(@Dimension(unit = Dimension.DP) dps: Int): Int {
//        return Math.round(resources.displayMetrics.density * dps)
//    }
//
//    protected fun onDraw(canvas: Canvas) {
//        // Draw tab background layer for each tab item
//        for (i in 0 until slidingTabIndicator.childCount) {
//            val tabView = slidingTabIndicator.getChildAt(i)
//            if (tabView is TabView) {
//                tabView.drawBackground(canvas)
//            }
//        }
//
//        super.onDraw(canvas)
//    }
//
//    fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
//        var heightMeasureSpec = heightMeasureSpec
//        // If we have a MeasureSpec which allows us to decide our height, try and use the default
//        // height
//        val idealHeight = dpToPx(getDefaultHeight()) + paddingTop + paddingBottom
//        when (View.MeasureSpec.getMode(heightMeasureSpec)) {
//            View.MeasureSpec.AT_MOST -> heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(
//                Math.min(idealHeight, View.MeasureSpec.getSize(heightMeasureSpec)), View.MeasureSpec.EXACTLY
//            )
//            View.MeasureSpec.UNSPECIFIED -> heightMeasureSpec =
//                    View.MeasureSpec.makeMeasureSpec(idealHeight, View.MeasureSpec.EXACTLY)
//            else -> {
//            }
//        }
//
//        val specWidth = View.MeasureSpec.getSize(widthMeasureSpec)
//        if (View.MeasureSpec.getMode(widthMeasureSpec) != View.MeasureSpec.UNSPECIFIED) {
//            // If we don't have an unspecified width spec, use the given size to calculate
//            // the max tab width
//            tabMaxWidth = if (requestedTabMaxWidth > 0)
//                requestedTabMaxWidth
//            else
//                specWidth - dpToPx(TAB_MIN_WIDTH_MARGIN)
//        }
//
//        // Now super measure itself using the (possibly) modified height spec
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
//
//        if (childCount == 1) {
//            // If we're in fixed mode then we need to make the tab strip is the same width as us
//            // so we don't scroll
//            val child = getChildAt(0)
//            var remeasure = false
//
//            when (mode) {
//                MODE_SCROLLABLE ->
//                    // We only need to resize the child if it's smaller than us. This is similar
//                    // to fillViewport
//                    remeasure = child.measuredWidth < measuredWidth
//                MODE_FIXED ->
//                    // Resize the child so that it doesn't scroll
//                    remeasure = child.measuredWidth !== measuredWidth
//            }
//
//            if (remeasure) {
//                // Re-measure the child with a widthSpec set to be exactly our measure width
//                val childHeightMeasureSpec = ViewGroup.getChildMeasureSpec(
//                    heightMeasureSpec,
//                    paddingTop + paddingBottom,
//                    child.layoutParams.height
//                )
//                val childWidthMeasureSpec = View.MeasureSpec.makeMeasureSpec(measuredWidth, View.MeasureSpec.EXACTLY)
//                child.measure(childWidthMeasureSpec, childHeightMeasureSpec)
//            }
//        }
//    }
//
//    private fun removeTabViewAt(position: Int) {
//        val view = slidingTabIndicator.getChildAt(position) as TabView
//        slidingTabIndicator.removeViewAt(position)
//        if (view != null) {
//            view.reset()
//            tabViewPool.release(view)
//        }
//        requestLayout()
//    }
//
//    private fun animateToTab(newPosition: Int) {
//        if (newPosition == Tab.INVALID_POSITION) {
//            return
//        }
//
//        if (windowToken == null
//            || !ViewCompat.isLaidOut(this)
//            || slidingTabIndicator.childrenNeedLayout()
//        ) {
//            // If we don't have a window token, or we haven't been laid out yet just draw the new
//            // position now
//            setScrollPosition(newPosition, 0f, true)
//            return
//        }
//
//        val startScrollX = scrollX
//        val targetScrollX = calculateScrollXForTab(newPosition, 0f)
//
//        if (startScrollX != targetScrollX) {
//            ensureScrollAnimator()
//
//            scrollAnimator!!.setIntValues(startScrollX, targetScrollX)
//            scrollAnimator!!.start()
//        }
//
//        // Now animate the indicator
//        slidingTabIndicator.animateIndicatorToPosition(newPosition, tabIndicatorAnimationDuration)
//    }
//
//    private fun ensureScrollAnimator() {
//        if (scrollAnimator == null) {
//            scrollAnimator = ValueAnimator()
//            scrollAnimator!!.interpolator = AnimationUtils.FAST_OUT_SLOW_IN_INTERPOLATOR
//            scrollAnimator!!.duration = tabIndicatorAnimationDuration.toLong()
//            scrollAnimator!!.addUpdateListener { animator -> scrollTo(animator.animatedValue as Int, 0) }
//        }
//    }
//
//    fun setScrollAnimatorListener(listener: ValueAnimator.AnimatorListener) {
//        ensureScrollAnimator()
//        scrollAnimator!!.addListener(listener)
//    }
//
//    /**
//     * Called when a selected tab is added. Unselects all other tabs in the TabLayout.
//     *
//     * @param position Position of the selected tab.
//     */
//    private fun setSelectedTabView(position: Int) {
//        val tabCount = slidingTabIndicator.childCount
//        if (position < tabCount) {
//            for (i in 0 until tabCount) {
//                val child = slidingTabIndicator.getChildAt(i)
//                child.isSelected = i == position
//                child.isActivated = i == position
//            }
//        }
//    }
//
//    fun selectTab(tab: Tab?) {
//        selectTab(tab, true)
//    }
//
//    fun selectTab(tab: Tab?, updateIndicator: Boolean) {
//        val currentTab = selectedTab
//
//        if (currentTab === tab) {
//            if (currentTab != null) {
//                dispatchTabReselected(tab)
//                animateToTab(tab!!.position)
//            }
//        } else {
//            val newPosition = tab?.position ?: Tab.INVALID_POSITION
//            if (updateIndicator) {
//                if ((currentTab == null || currentTab.position == Tab.INVALID_POSITION) && newPosition != Tab.INVALID_POSITION) {
//                    // If we don't currently have a tab, just draw the indicator
//                    setScrollPosition(newPosition, 0f, true)
//                } else {
//                    animateToTab(newPosition)
//                }
//                if (newPosition != Tab.INVALID_POSITION) {
//                    setSelectedTabView(newPosition)
//                }
//            }
//            // Setting selectedTab before dispatching 'tab unselected' events, so that currentTab's state
//            // will be interpreted as unselected
//            selectedTab = tab
//            if (currentTab != null) {
//                dispatchTabUnselected(currentTab)
//            }
//            if (tab != null) {
//                dispatchTabSelected(tab)
//            }
//        }
//    }
//
//    private fun dispatchTabSelected(tab: Tab) {
//        for (i in selectedListeners.size - 1 downTo 0) {
//            selectedListeners.get(i).onTabSelected(tab)
//        }
//    }
//
//    private fun dispatchTabUnselected(tab: Tab) {
//        for (i in selectedListeners.size - 1 downTo 0) {
//            selectedListeners.get(i).onTabUnselected(tab)
//        }
//    }
//
//    private fun dispatchTabReselected(tab: Tab) {
//        for (i in selectedListeners.size - 1 downTo 0) {
//            selectedListeners.get(i).onTabReselected(tab)
//        }
//    }
//
//    private fun calculateScrollXForTab(position: Int, positionOffset: Float): Int {
//        if (mode == MODE_SCROLLABLE) {
//            val selectedChild = slidingTabIndicator.getChildAt(position)
//            val nextChild = if (position + 1 < slidingTabIndicator.childCount)
//                slidingTabIndicator.getChildAt(position + 1)
//            else
//                null
//            val selectedWidth = selectedChild?.width ?: 0
//            val nextWidth = nextChild?.width ?: 0
//
//            // base scroll amount: places center of tab in center of parent
//            val scrollBase = selectedChild!!.left + selectedWidth / 2 - width / 2
//            // offset amount: fraction of the distance between centers of tabs
//            val scrollOffset = ((selectedWidth + nextWidth).toFloat() * 0.5f * positionOffset).toInt()
//
//            return if (ViewCompat.getLayoutDirection(this) == ViewCompat.LAYOUT_DIRECTION_LTR)
//                scrollBase + scrollOffset
//            else
//                scrollBase - scrollOffset
//        }
//        return 0
//    }
//
//    private fun applyModeAndGravity() {
//        var paddingStart = 0
//        if (mode == MODE_SCROLLABLE) {
//            // If we're scrollable, or fixed at start, inset using padding
//            paddingStart = Math.max(0, contentInsetStart - tabPaddingStart)
//        }
//        ViewCompat.setPaddingRelative(slidingTabIndicator, paddingStart, 0, 0, 0)
//
//        when (mode) {
//            MODE_FIXED -> slidingTabIndicator.gravity = Gravity.CENTER_HORIZONTAL
//            MODE_SCROLLABLE -> slidingTabIndicator.gravity = GravityCompat.START
//        }
//
//        updateTabViews(true)
//    }
//
//    fun updateTabViews(requestLayout: Boolean) {
//        for (i in 0 until slidingTabIndicator.childCount) {
//            val child = slidingTabIndicator.getChildAt(i)
//            child.minimumWidth = getTabMinWidth()
//            updateTabViewLayoutParams(child.layoutParams as LinearLayout.LayoutParams)
//            if (requestLayout) {
//                child.requestLayout()
//            }
//        }
//    }
//
//
//    /** A tab in this layout. Instances can be created via [.newTab].  */
//    // TODO: make class final after the widget migration is finished
//    // TODO: make package private constructor after the widget migration is finished
//    class Tab {
//        private var tag: Any? = null
//        private var icon: Drawable? = null
//        private var text: CharSequence? = null
//        // This represents the content description that has been explicitly set on the Tab or TabItem
//        // in XML or through #setContentDescription. If the content description is empty, text should
//        // be used as the content description instead, but contentDesc should remain empty.
//        private var contentDesc: CharSequence? = null
//        /**
//         * Return the current position of this tab in the action bar.
//         *
//         * @return Current position, or [.INVALID_POSITION] if this tab is not currently in the
//         * action bar.
//         */
//        var position = INVALID_POSITION
//            internal set
//        private var customView: View? = null
////        @LabelVisibility
////        private var labelVisibilityMode = TAB_LABEL_VISIBILITY_LABELED
//
//        // TODO: make package private after the widget migration is finished
//        var parent: InfiniteTabLayout? = null
//        // TODO: make package private after the widget migration is finished
//        lateinit var view: TabView
//
//        /** Returns true if this tab is currently selected.  */
//        val isSelected: Boolean
//            get() {
//                if (parent == null) {
//                    throw IllegalArgumentException("Tab not attached to a TabLayout")
//                }
//                return parent?.getSelectedTabPosition() == position
//            }
//
//        /**
//         * Gets a brief description of this tab's content for use in accessibility support.
//         *
//         * @return Description of this tab's content
//         * @see .setContentDescription
//         * @see .setContentDescription
//         */
//        // This returns the view's content description instead of contentDesc because if the title
//        // is used as a replacement for the content description, contentDesc will be empty.
//        val contentDescription: CharSequence?
//            @Nullable
//            get() = if (view == null) null else view!!.contentDescription
//
//        /** @return This Tab's tag object.
//         */
//        @Nullable
//        fun getTag(): Any? {
//            return tag
//        }
//
//        /**
//         * Give this Tab an arbitrary object to hold for later use.
//         *
//         * @param tag Object to store
//         * @return The current instance for call chaining
//         */
//        @NonNull
//        fun setTag(@Nullable tag: Any): Tab {
//            this.tag = tag
//            return this
//        }
//
//        /**
//         * Returns the custom view used for this tab.
//         *
//         * @see .setCustomView
//         * @see .setCustomView
//         */
//        @Nullable
//        fun getCustomView(): View? {
//            return customView
//        }
//
//        /**
//         * Set a custom view to be used for this tab.
//         *
//         *
//         * If the provided view contains a [TextView] with an ID of [android.R.id.text1]
//         * then that will be updated with the value given to [.setText]. Similarly,
//         * if this layout contains an [ImageView] with ID [android.R.id.icon] then it will
//         * be updated with the value given to [.setIcon].
//         *
//         * @param view Custom view to be used as a tab.
//         * @return The current instance for call chaining
//         */
//        @NonNull
//        fun setCustomView(@Nullable view: View): Tab {
//            customView = view
//            updateView()
//            return this
//        }
//
//        /**
//         * Set a custom view to be used for this tab.
//         *
//         *
//         * If the inflated layout contains a [TextView] with an ID of [ ][android.R.id.text1] then that will be updated with the value given to [ ][.setText]. Similarly, if this layout contains an [ImageView] with ID
//         * [android.R.id.icon] then it will be updated with the value given to [ ][.setIcon].
//         *
//         * @param resId A layout resource to inflate and use as a custom tab view
//         * @return The current instance for call chaining
//         */
//        @NonNull
//        fun setCustomView(@LayoutRes resId: Int): Tab {
//            val inflater = LayoutInflater.from(view!!.context)
//            return setCustomView(inflater.inflate(resId, view, false))
//        }
//
//        /**
//         * Return the icon associated with this tab.
//         *
//         * @return The tab's icon
//         */
//        @Nullable
//        fun getIcon(): Drawable? {
//            return icon
//        }
//
//        /**
//         * Return the text of this tab.
//         *
//         * @return The tab's text
//         */
//        @Nullable
//        fun getText(): CharSequence? {
//            return text
//        }
//
//        /**
//         * Set the icon displayed on this tab.
//         *
//         * @param icon The drawable to use as an icon
//         * @return The current instance for call chaining
//         */
//        @NonNull
//        fun setIcon(@Nullable icon: Drawable?): Tab {
//            this.icon = icon
//            updateView()
//            return this
//        }
//
//        /**
//         * Set the icon displayed on this tab.
//         *
//         * @param resId A resource ID referring to the icon that should be displayed
//         * @return The current instance for call chaining
//         */
//        @NonNull
//        fun setIcon(@DrawableRes resId: Int): Tab {
//            if (parent == null) {
//                throw IllegalArgumentException("Tab not attached to a TabLayout")
//            }
//            return setIcon(AppCompatResources.getDrawable(parent!!.context, resId))
//        }
//
//        /**
//         * Set the text displayed on this tab. Text may be truncated if there is not room to display the
//         * entire string.
//         *
//         * @param text The text to display
//         * @return The current instance for call chaining
//         */
//        @NonNull
//        fun setText(@Nullable text: CharSequence?): Tab {
//            if (TextUtils.isEmpty(contentDesc) && !TextUtils.isEmpty(text)) {
//                // If no content description has been set, use the text as the content description of the
//                // TabView. If the text is null, don't update the content description.
//                view!!.contentDescription = text
//            }
//
//            this.text = text
//            updateView()
//            return this
//        }
//
//        /**
//         * Set the text displayed on this tab. Text may be truncated if there is not room to display the
//         * entire string.
//         *
//         * @param resId A resource ID referring to the text that should be displayed
//         * @return The current instance for call chaining
//         */
//        @NonNull
//        fun setText(@StringRes resId: Int): Tab {
//            if (parent == null) {
//                throw IllegalArgumentException("Tab not attached to a TabLayout")
//            }
//            return setText(parent!!.resources.getText(resId))
//        }
//
//        /**
//         * Sets the visibility mode for the Labels in this Tab. The valid input options are:
//         *
//         *
//         *  * [.TAB_LABEL_VISIBILITY_UNLABELED]: Tabs will appear without labels regardless of
//         * whether text is set.
//         *  * [.TAB_LABEL_VISIBILITY_LABELED]: Tabs will appear labeled if text is set.
//         *
//         *
//         * @param mode one of [.TAB_LABEL_VISIBILITY_UNLABELED]
//         * or [.TAB_LABEL_VISIBILITY_LABELED].
//         * @return The current instance for call chaining.
//         *
//         * @LabelVisibility
//         */
//        fun setTabLabelVisibility(mode: Int): Tab {
//            this.labelVisibilityMode = mode
//            this.updateView()
//            return this
//        }
//
//        /**
//         * Gets the visibility mode for the Labels in this Tab.
//         *
//         * @return the label visibility mode, one of [.TAB_LABEL_VISIBILITY_UNLABELED] or
//         * [.TAB_LABEL_VISIBILITY_LABELED].
//         * @see .setTabLabelVisibility
//         */
////        @LabelVisibility
//        fun getTabLabelVisibility(): Int {
//            return this.labelVisibilityMode
//        }
//
//        /** Select this tab. Only valid if the tab has been added to the action bar.  */
//        fun select() {
//            if (parent == null) {
//                throw IllegalArgumentException("Tab not attached to a TabLayout")
//            }
//            parent!!.selectTab(this)
//        }
//
//        /**
//         * Set a description of this tab's content for use in accessibility support. If no content
//         * description is provided the title will be used.
//         *
//         * @param resId A resource ID referring to the description text
//         * @return The current instance for call chaining
//         * @see .setContentDescription
//         * @see .getContentDescription
//         */
//        @NonNull
//        fun setContentDescription(@StringRes resId: Int): Tab {
//            if (parent == null) {
//                throw IllegalArgumentException("Tab not attached to a TabLayout")
//            }
//            return setContentDescription(parent!!.resources.getText(resId))
//        }
//
//        /**
//         * Set a description of this tab's content for use in accessibility support. If no content
//         * description is provided the title will be used.
//         *
//         * @param contentDesc Description of this tab's content
//         * @return The current instance for call chaining
//         * @see .setContentDescription
//         * @see .getContentDescription
//         */
//        @NonNull
//        fun setContentDescription(@Nullable contentDesc: CharSequence): Tab {
//            this.contentDesc = contentDesc
//            updateView()
//            return this
//        }
//
//        internal fun updateView() {
//            if (view != null) {
//                view!!.update()
//            }
//        }
//
//        internal fun reset() {
//            parent = null
//            view = null
//            tag = null
//            icon = null
//            text = null
//            contentDesc = null
//            position = INVALID_POSITION
//            customView = null
//        }
//
//        companion object {
//
//            /**
//             * An invalid position for a tab.
//             *
//             * @see .getPosition
//             */
//            val INVALID_POSITION = -1
//        }
//    }// Private constructor
//
//    internal inner class TabView(context: Context) : LinearLayout(context) {
//        var tab: Tab? = null
//            set(@Nullable tab) {
//                if (tab !== this.tab) {
//                    field = tab
//                    update()
//                }
//            }
//        private var textView: TextView? = null
//        private var iconView: ImageView? = null
//
//        private var customView: View? = null
//        private var customTextView: TextView? = null
//        private var customIconView: ImageView? = null
//        @Nullable
//        private var baseBackgroundDrawable: Drawable? = null
//
//        private var defaultMaxLines = 2
//
//        /**
//         * Calculates the width of the TabView's content.
//         *
//         * @return Width of the tab label, if present, or the width of the tab icon, if present. If tabs
//         * is in inline mode, returns the sum of both the icon and tab label widths.
//         */
//        private val contentWidth: Int
//            get() {
//                var initialized = false
//                var left = 0
//                var right = 0
//
//                for (view in arrayOf<View>(textView, iconView, customView)) {
//                    if (view != null && view!!.getVisibility() === View.VISIBLE) {
//                        left = if (initialized) Math.min(left, view!!.getLeft()) else view!!.getLeft()
//                        right = if (initialized) Math.max(right, view!!.getRight()) else view!!.getRight()
//                        initialized = true
//                    }
//                }
//
//                return right - left
//            }
//
//        init {
//            updateBackgroundDrawable(context)
//            ViewCompat.setPaddingRelative(
//                this, tabPaddingStart, tabPaddingTop, tabPaddingEnd, tabPaddingBottom
//            )
//            gravity = Gravity.CENTER
//            orientation = if (inlineLabel) LinearLayout.HORIZONTAL else LinearLayout.VERTICAL
//            isClickable = true
//            ViewCompat.setPointerIcon(
//                this, PointerIconCompat.getSystemIcon(getContext(), PointerIconCompat.TYPE_HAND)
//            )
//        }
//
//        @SuppressLint("RestrictedApi")
//        private fun updateBackgroundDrawable(context: Context) {
//            if (tabBackgroundResId != 0) {
//                baseBackgroundDrawable = AppCompatResources.getDrawable(context, tabBackgroundResId)
//                if (baseBackgroundDrawable != null && baseBackgroundDrawable!!.isStateful) {
//                    baseBackgroundDrawable!!.state = drawableState
//                }
//            } else {
//                baseBackgroundDrawable = null
//            }
//
//            val background: Drawable
//            val contentDrawable = GradientDrawable()
//            contentDrawable.setColor(Color.TRANSPARENT)
//
//            if (tabRippleColorStateList != null) {
//                val maskDrawable = GradientDrawable()
//                // TODO: Find a workaround for this. Currently on certain devices/versions,
//                // LayerDrawable will draw a black background underneath any layer with a non-opaque color,
//                // (e.g. ripple) unless we set the shape to be something that's not a perfect rectangle.
//                maskDrawable.cornerRadius = 0.00001f
//                maskDrawable.setColor(Color.WHITE)
//
//                val rippleColor = RippleUtils.convertToRippleDrawableColor(tabRippleColorStateList)
//
//                // TODO: Add support to RippleUtils.compositeRippleColorStateList for different ripple color
//                // for selected items vs non-selected items
//                if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
//                    background = RippleDrawable(
//                        rippleColor,
//                        if (unboundedRipple) null else contentDrawable,
//                        if (unboundedRipple) null else maskDrawable
//                    )
//                } else {
//                    val rippleDrawable = DrawableCompat.wrap(maskDrawable)
//                    DrawableCompat.setTintList(rippleDrawable, rippleColor)
//                    background = LayerDrawable(arrayOf(contentDrawable, rippleDrawable))
//                }
//            } else {
//                background = contentDrawable
//            }
//            ViewCompat.setBackground(this, background)
//            this@InfiniteTabLayout.invalidate()
//        }
//
//        /**
//         * Draw the background drawable specified by tabBackground attribute onto the canvas provided.
//         * This method will draw the background to the full bounds of this TabView. We provide a
//         * separate method for drawing this background rather than just setting this background on the
//         * TabView so that we can control when this background gets drawn. This allows us to draw the
//         * tab background underneath the TabLayout selection indicator, and then draw the TabLayout
//         * content (icons + labels) on top of the selection indicator.
//         *
//         * @param canvas canvas to draw the background on
//         */
//        private fun drawBackground(canvas: Canvas) {
//            if (baseBackgroundDrawable != null) {
//                baseBackgroundDrawable!!.setBounds(left, top, right, bottom)
//                baseBackgroundDrawable!!.draw(canvas)
//            }
//        }
//
//        fun drawableStateChanged() {
//            super.drawableStateChanged()
//            var changed = false
//            val state = drawableState
//            if (baseBackgroundDrawable != null && baseBackgroundDrawable!!.isStateful) {
//                changed = changed or baseBackgroundDrawable!!.setState(state)
//            }
//
//            if (changed) {
//                invalidate()
//                this@TabLayout.invalidate() // Invalidate TabLayout, which draws mBaseBackgroundDrawable
//            }
//        }
//
//        fun performClick(): Boolean {
//            val handled = super.performClick()
//
//            if (this.tab != null) {
//                if (!handled) {
//                    playSoundEffect(SoundEffectConstants.CLICK)
//                }
//                this.tab!!.select()
//                return true
//            } else {
//                return handled
//            }
//        }
//
//        fun setSelected(selected: Boolean) {
//            val changed = isSelected != selected
//
//            super.setSelected(selected)
//
//            if (changed && selected && Build.VERSION.SDK_INT < 16) {
//                // Pre-JB we need to manually send the TYPE_VIEW_SELECTED commandEvent
//                sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_SELECTED)
//            }
//
//            // Always dispatch this to the child views, regardless of whether the value has
//            // changed
//            if (textView != null) {
//                textView!!.isSelected = selected
//            }
//            if (iconView != null) {
//                iconView!!.setSelected(selected)
//            }
//            if (customView != null) {
//                customView!!.setSelected(selected)
//            }
//        }
//
//        fun onInitializeAccessibilityEvent(commandEvent: AccessibilityEvent) {
//            super.onInitializeAccessibilityEvent(commandEvent)
//            // This view masquerades as an action bar tab.
//            commandEvent.className = ActionBar.Tab::class.java!!.getName()
//        }
//
//        @TargetApi(14)
//        fun onInitializeAccessibilityNodeInfo(info: AccessibilityNodeInfo) {
//            super.onInitializeAccessibilityNodeInfo(info)
//            // This view masquerades as an action bar tab.
//            info.className = ActionBar.Tab::class.java!!.getName()
//        }
//
//        public override fun onMeasure(origWidthMeasureSpec: Int, origHeightMeasureSpec: Int) {
//            val specWidthSize = View.MeasureSpec.getSize(origWidthMeasureSpec)
//            val specWidthMode = View.MeasureSpec.getMode(origWidthMeasureSpec)
//            val maxWidth = getTabMaxWidth()
//
//            val widthMeasureSpec: Int
//
//            if (maxWidth > 0 && (specWidthMode == View.MeasureSpec.UNSPECIFIED || specWidthSize > maxWidth)) {
//                // If we have a max width and a given spec which is either unspecified or
//                // larger than the max width, update the width spec using the same mode
//                widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(tabMaxWidth, View.MeasureSpec.AT_MOST)
//            } else {
//                // Else, use the original width spec
//                widthMeasureSpec = origWidthMeasureSpec
//            }
//
//            // Now lets measure
//            super.onMeasure(widthMeasureSpec, origHeightMeasureSpec)
//
//            // We need to switch the text size based on whether the text is spanning 2 lines or not
//            if (textView != null) {
//                var textSize = tabTextSize
//                var maxLines = defaultMaxLines
//
//                if (iconView != null && iconView!!.getVisibility() === View.VISIBLE) {
//                    // If the icon view is being displayed, we limit the text to 1 line
//                    maxLines = 1
//                } else if (textView != null && textView!!.lineCount > 1) {
//                    // Otherwise when we have text which wraps we reduce the text size
//                    textSize = tabTextMultiLineSize
//                }
//
//                val curTextSize = textView!!.textSize
//                val curLineCount = textView!!.lineCount
//                val curMaxLines = TextViewCompat.getMaxLines(textView!!)
//
//                if (textSize != curTextSize || curMaxLines >= 0 && maxLines != curMaxLines) {
//                    // We've got a new text size and/or max lines...
//                    var updateTextView = true
//
//                    if (mode == MODE_FIXED && textSize > curTextSize && curLineCount == 1) {
//                        // If we're in fixed mode, going up in text size and currently have 1 line
//                        // then it's very easy to get into an infinite recursion.
//                        // To combat that we check to see if the change in text size
//                        // will cause a line count change. If so, abort the size change and stick
//                        // to the smaller size.
//                        val layout = textView!!.layout
//                        if (layout == null || approximateLineWidth(
//                                layout,
//                                0,
//                                textSize
//                            ) > measuredWidth - paddingLeft - paddingRight
//                        ) {
//                            updateTextView = false
//                        }
//                    }
//
//                    if (updateTextView) {
//                        textView!!.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize)
//                        textView!!.maxLines = maxLines
//                        super.onMeasure(widthMeasureSpec, origHeightMeasureSpec)
//                    }
//                }
//            }
//        }
//
//        fun reset() {
//            tab = null
//            isSelected = false
//        }
//
//        fun update() {
//            val tab = this.tab
//            val custom = tab?.getCustomView()
//            if (custom != null) {
//                val customParent = custom!!.getParent()
//                if (customParent !== this) {
//                    if (customParent != null) {
//                        (customParent as ViewGroup).removeView(custom)
//                    }
//                    addView(custom)
//                }
//                customView = custom
//                if (this.textView != null) {
//                    this.textView!!.visibility = View.GONE
//                }
//                if (this.iconView != null) {
//                    this.iconView!!.setVisibility(View.GONE)
//                    this.iconView!!.setImageDrawable(null)
//                }
//
//                customTextView = custom!!.findViewById(android.R.id.text1)
//                if (customTextView != null) {
//                    defaultMaxLines = TextViewCompat.getMaxLines(customTextView!!)
//                }
//                customIconView = custom!!.findViewById(android.R.id.icon)
//            } else {
//                // We do not have a custom view. Remove one if it already exists
//                if (customView != null) {
//                    removeView(customView)
//                    customView = null
//                }
//                customTextView = null
//                customIconView = null
//            }
//
//            if (customView == null) {
//                // If there isn't a custom view, we'll us our own in-built layouts
//                if (this.iconView == null) {
//                    val iconView = LayoutInflater.from(context)
//                        .inflate(R.layout.design_layout_tab_icon, this, false) as ImageView
//                    addView(iconView, 0)
//                    this.iconView = iconView
//                }
//                val icon = if (tab != null && tab.getIcon() != null)
//                    DrawableCompat.wrap(tab.getIcon()!!).mutate()
//                else
//                    null
//                if (icon != null) {
//                    DrawableCompat.setTintList(icon, tabIconTint)
//                    if (tabIconTintMode != null) {
//                        DrawableCompat.setTintMode(icon, tabIconTintMode!!)
//                    }
//                }
//
//                if (this.textView == null) {
//                    val textView = LayoutInflater.from(context)
//                        .inflate(R.layout.design_layout_tab_text, this, false) as TextView
//                    addView(textView)
//                    this.textView = textView
//                    defaultMaxLines = TextViewCompat.getMaxLines(this.textView!!)
//                }
//                TextViewCompat.setTextAppearance(this.textView!!, tabTextAppearance)
//                if (tabTextColors != null) {
//                    this.textView!!.setTextColor(tabTextColors)
//                }
//                updateTextAndIcon(this.textView, this.iconView)
//            } else {
//                // Else, we'll see if there is a TextView or ImageView present and update them
//                if (customTextView != null || customIconView != null) {
//                    updateTextAndIcon(customTextView, customIconView)
//                }
//            }
//
//            if (tab != null && !TextUtils.isEmpty(tab.contentDesc)) {
//                // Only update the TabView's content description from Tab if the Tab's content description
//                // has been explicitly set.
//                contentDescription = tab.contentDesc
//            }
//            // Finally update our selected state
//            isSelected = tab != null && tab.isSelected
//        }
//
//        fun updateOrientation() {
//            orientation = if (inlineLabel) LinearLayout.HORIZONTAL else LinearLayout.VERTICAL
//            if (customTextView != null || customIconView != null) {
//                updateTextAndIcon(customTextView, customIconView)
//            } else {
//                updateTextAndIcon(textView, iconView)
//            }
//        }
//
//        private fun updateTextAndIcon(
//            @Nullable textView: TextView?, @Nullable iconView: ImageView?
//        ) {
//            val icon = if (this.tab != null && this.tab!!.getIcon() != null)
//                DrawableCompat.wrap(this.tab!!.getIcon()!!).mutate()
//            else
//                null
//            val text = if (this.tab != null) this.tab!!.getText() else null
//
//            if (iconView != null) {
//                if (icon != null) {
//                    iconView!!.setImageDrawable(icon)
//                    iconView!!.setVisibility(View.VISIBLE)
//                    visibility = View.VISIBLE
//                } else {
//                    iconView!!.setVisibility(View.GONE)
//                    iconView!!.setImageDrawable(null)
//                }
//            }
//
//            val hasText = !TextUtils.isEmpty(text)
//            if (textView != null) {
//                if (hasText) {
//                    textView.text = text
//                    if (this.tab!!.labelVisibilityMode == TAB_LABEL_VISIBILITY_LABELED) {
//                        textView.visibility = View.VISIBLE
//                    } else {
//                        textView.visibility = View.GONE
//                    }
//                    visibility = View.VISIBLE
//                } else {
//                    textView.visibility = View.GONE
//                    textView.text = null
//                }
//            }
//
//            if (iconView != null) {
//                val lp = iconView!!.getLayoutParams() as ViewGroup.MarginLayoutParams
//                var iconMargin = 0
//                if (hasText && iconView!!.getVisibility() === View.VISIBLE) {
//                    // If we're showing both text and icon, add some margin bottom to the icon
//                    iconMargin = dpToPx(DEFAULT_GAP_TEXT_ICON)
//                }
//                if (inlineLabel) {
//                    if (iconMargin != MarginLayoutParamsCompat.getMarginEnd(lp)) {
//                        MarginLayoutParamsCompat.setMarginEnd(lp, iconMargin)
//                        lp.bottomMargin = 0
//                        // Calls resolveLayoutParams(), necessary for layout direction
//                        iconView!!.setLayoutParams(lp)
//                        iconView!!.requestLayout()
//                    }
//                } else {
//                    if (iconMargin != lp.bottomMargin) {
//                        lp.bottomMargin = iconMargin
//                        MarginLayoutParamsCompat.setMarginEnd(lp, 0)
//                        // Calls resolveLayoutParams(), necessary for layout direction
//                        iconView!!.setLayoutParams(lp)
//                        iconView!!.requestLayout()
//                    }
//                }
//            }
//
//            val contentDesc = if (this.tab != null) this.tab!!.contentDesc else null
//            TooltipCompat.setTooltipText(this, if (hasText) null else contentDesc)
//        }
//
//        /** Approximates a given lines width with the new provided text size.  */
//        private fun approximateLineWidth(layout: Layout, line: Int, textSize: Float): Float {
//            return layout.getLineWidth(line) * (textSize / layout.getPaint().getTextSize())
//        }
//    }
//
//    private inner class SlidingTabIndicator internal constructor(context: Context) : LinearLayout(context) {
//        private var selectedIndicatorHeight: Int = 0
//        private var selectedIndicatorPaint: Paint?
//        private val defaultSelectionIndicator: GradientDrawable
//
//        internal var selectedPosition = -1
//        internal var selectionOffset: Float = 0.toFloat()
//
//        private var layoutDirection = -1
//
//        private var indicatorLeft = -1
//        private var indicatorRight = -1
//
//        private var indicatorAnimator: ValueAnimator? = null
//
//        internal val indicatorPosition: Float
//            get() = selectedPosition + selectionOffset
//
//        init {
//            setWillNotDraw(false)
//            selectedIndicatorPaint = Paint()
//            defaultSelectionIndicator = GradientDrawable()
//        }
//
//        internal fun setSelectedIndicatorColor(color: Int) {
//            if (selectedIndicatorPaint!!.getColor() !== color) {
//                selectedIndicatorPaint!!.setColor(color)
//                ViewCompat.postInvalidateOnAnimation(this)
//            }
//        }
//
//        internal fun setSelectedIndicatorHeight(height: Int) {
//            if (selectedIndicatorHeight != height) {
//                selectedIndicatorHeight = height
//                ViewCompat.postInvalidateOnAnimation(this)
//            }
//        }
//
//        internal fun childrenNeedLayout(): Boolean {
//            var i = 0
//            val z = childCount
//            while (i < z) {
//                val child = getChildAt(i)
//                if (child.width <= 0) {
//                    return true
//                }
//                i++
//            }
//            return false
//        }
//
//        internal fun setIndicatorPositionFromTabPosition(position: Int, positionOffset: Float) {
//            if (indicatorAnimator != null && indicatorAnimator!!.isRunning) {
//                indicatorAnimator!!.cancel()
//            }
//
//            selectedPosition = position
//            selectionOffset = positionOffset
//            updateIndicatorPosition()
//        }
//
//        override fun onRtlPropertiesChanged(layoutDirection: Int) {
//            super.onRtlPropertiesChanged(layoutDirection)
//
//            // Workaround for a bug before Android M where LinearLayout did not re-layout itself when
//            // layout direction changed
//            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
//                if (this.layoutDirection != layoutDirection) {
//                    requestLayout()
//                    this.layoutDirection = layoutDirection
//                }
//            }
//        }
//
//        override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
//            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
//
//            if (View.MeasureSpec.getMode(widthMeasureSpec) != View.MeasureSpec.EXACTLY) {
//                // HorizontalScrollView will first measure use with UNSPECIFIED, and then with
//                // EXACTLY. Ignore the first call since anything we do will be overwritten anyway
//                return
//            }
//
//            if (mode == MODE_FIXED && tabGravity == GRAVITY_CENTER) {
//                val count = childCount
//
//                // First we'll find the widest tab
//                var largestTabWidth = 0
//                run {
//                    var i = 0
//                    while (i < count) {
//                        val child = getChildAt(i)
//                        if (child.visibility === View.VISIBLE) {
//                            largestTabWidth = Math.max(largestTabWidth, child.measuredWidth)
//                        }
//                        i++
//                    }
//                }
//
//                if (largestTabWidth <= 0) {
//                    // If we don't have a largest child yet, skip until the next measure pass
//                    return
//                }
//
//                val gutter = dpToPx(FIXED_WRAP_GUTTER_MIN)
//                var remeasure = false
//
//                if (largestTabWidth * count <= measuredWidth - gutter * 2) {
//                    // If the tabs fit within our width minus gutters, we will set all tabs to have
//                    // the same width
//                    for (i in 0 until count) {
//                        val lp = getChildAt(i).layoutParams as LinearLayout.LayoutParams
//                        if (lp.width != largestTabWidth || lp.weight != 0f) {
//                            lp.width = largestTabWidth
//                            lp.weight = 0f
//                            remeasure = true
//                        }
//                    }
//                } else {
//                    // If the tabs will wrap to be larger than the width minus gutters, we need
//                    // to switch to GRAVITY_FILL
//                    tabGravity = GRAVITY_FILL
//                    updateTabViews(false)
//                    remeasure = true
//                }
//
//                if (remeasure) {
//                    // Now re-measure after our changes
//                    super.onMeasure(widthMeasureSpec, heightMeasureSpec)
//                }
//            }
//        }
//
//        override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
//            super.onLayout(changed, l, t, r, b)
//
//            if (indicatorAnimator != null && indicatorAnimator!!.isRunning) {
//                // If we're currently running an animation, lets cancel it and start a
//                // new animation with the remaining duration
//                indicatorAnimator!!.cancel()
//                val duration = indicatorAnimator!!.duration
//                animateIndicatorToPosition(
//                    selectedPosition,
//                    Math.round((1f - indicatorAnimator!!.animatedFraction) * duration)
//                )
//            } else {
//                // If we've been layed out, update the indicator position
//                updateIndicatorPosition()
//            }
//        }
//
//        private fun updateIndicatorPosition() {
//            val selectedTitle = getChildAt(selectedPosition)
//            var left: Int
//            var right: Int
//
//            if (selectedTitle != null && selectedTitle.width > 0) {
//                left = selectedTitle.left
//                right = selectedTitle.right
//
//                if (!tabIndicatorFullWidth && selectedTitle is TabView) {
//                    calculateTabViewContentBounds(selectedTitle, tabViewContentBounds)
//                    left = tabViewContentBounds.left.toInt()
//                    right = tabViewContentBounds.right.toInt()
//                }
//
//                if (selectionOffset > 0f && selectedPosition < childCount - 1) {
//                    // Draw the selection partway between the tabs
//                    val nextTitle = getChildAt(selectedPosition + 1)
//                    var nextTitleLeft = nextTitle.left
//                    var nextTitleRight = nextTitle.right
//
//                    if (!tabIndicatorFullWidth && nextTitle is TabView) {
//                        calculateTabViewContentBounds(nextTitle, tabViewContentBounds)
//                        nextTitleLeft = tabViewContentBounds.left.toInt()
//                        nextTitleRight = tabViewContentBounds.right.toInt()
//                    }
//
//                    left = (selectionOffset * nextTitleLeft + (1.0f - selectionOffset) * left).toInt()
//                    right = (selectionOffset * nextTitleRight + (1.0f - selectionOffset) * right).toInt()
//                }
//
//            } else {
//                right = -1
//                left = right
//            }
//
//            setIndicatorPosition(left, right)
//        }
//
//        internal fun setIndicatorPosition(left: Int, right: Int) {
//            if (left != indicatorLeft || right != indicatorRight) {
//                // If the indicator's left/right has changed, invalidate
//                indicatorLeft = left
//                indicatorRight = right
//                ViewCompat.postInvalidateOnAnimation(this)
//            }
//        }
//
//        internal fun animateIndicatorToPosition(position: Int, duration: Int) {
//            if (indicatorAnimator != null && indicatorAnimator!!.isRunning) {
//                indicatorAnimator!!.cancel()
//            }
//
//            val targetView = getChildAt(position)
//            if (targetView == null) {
//                // If we don't have a view, just update the position now and return
//                updateIndicatorPosition()
//                return
//            }
//
//            var targetLeft = targetView.left
//            var targetRight = targetView.right
//
//            if (!tabIndicatorFullWidth && targetView is TabView) {
//                calculateTabViewContentBounds(targetView, tabViewContentBounds)
//                targetLeft = tabViewContentBounds.left.toInt()
//                targetRight = tabViewContentBounds.right.toInt()
//            }
//
//            val finalTargetLeft = targetLeft
//            val finalTargetRight = targetRight
//
//            val startLeft = indicatorLeft
//            val startRight = indicatorRight
//
//            if (startLeft != finalTargetLeft || startRight != finalTargetRight) {
//                indicatorAnimator = ValueAnimator()
//                val animator = indicatorAnimator
//                animator.setInterpolator(AnimationUtils.FAST_OUT_SLOW_IN_INTERPOLATOR)
//                animator.setDuration(duration.toLong())
//                animator.setFloatValues(0, 1)
//                animator.addUpdateListener(
//                    ValueAnimator.AnimatorUpdateListener { animator ->
//                        val fraction = animator.animatedFraction
//                        setIndicatorPosition(
//                            AnimationUtils.lerp(startLeft, finalTargetLeft, fraction),
//                            AnimationUtils.lerp(startRight, finalTargetRight, fraction)
//                        )
//                    })
//                animator.addListener(
//                    object : AnimatorListenerAdapter() {
//                        fun onAnimationEnd(animator: Animator) {
//                            selectedPosition = position
//                            selectionOffset = 0f
//                        }
//                    })
//                animator.start()
//            }
//        }
//
//        /**
//         * Given a [TabView], calculate the left and right bounds of its content.
//         *
//         *
//         * If only text label is present, calculates the width of the text label. If only icon is
//         * present, calculates the width of the icon. If both are present, the text label bounds take
//         * precedence. If both are present and inline mode is enabled, the sum of the bounds of the both
//         * the text label and icon are calculated. If neither are present or if the calculated
//         * difference between the left and right bounds is less than 24dp, then left and right bounds
//         * are adjusted such that the difference between them is equal to 24dp.
//         *
//         * @param tabView [TabView] for which to calculate left and right content bounds.
//         */
//        private fun calculateTabViewContentBounds(tabView: TabView, contentBounds: RectF) {
//            var tabViewContentWidth = tabView.contentWidth
//
//            if (tabViewContentWidth < dpToPx(MIN_INDICATOR_WIDTH)) {
//                tabViewContentWidth = dpToPx(MIN_INDICATOR_WIDTH)
//            }
//
//            val tabViewCenter = (tabView.left + tabView.right) / 2
//            val contentLeftBounds = tabViewCenter - tabViewContentWidth / 2
//            val contentRightBounds = tabViewCenter + tabViewContentWidth / 2
//
//            contentBounds.set(contentLeftBounds.toFloat(), 0f, contentRightBounds.toFloat(), 0f)
//        }
//
//        override fun draw(canvas: Canvas) {
//            var indicatorHeight = 0
//            if (tabSelectedIndicator != null) {
//                indicatorHeight = tabSelectedIndicator!!.intrinsicHeight
//            }
//            if (selectedIndicatorHeight >= 0) {
//                indicatorHeight = selectedIndicatorHeight
//            }
//
//            var indicatorTop = 0
//            var indicatorBottom = 0
//
//            when (tabIndicatorGravity) {
//                INDICATOR_GRAVITY_BOTTOM -> {
//                    indicatorTop = height - indicatorHeight
//                    indicatorBottom = height
//                }
//                INDICATOR_GRAVITY_CENTER -> {
//                    indicatorTop = (height - indicatorHeight) / 2
//                    indicatorBottom = (height + indicatorHeight) / 2
//                }
//                INDICATOR_GRAVITY_TOP -> {
//                    indicatorTop = 0
//                    indicatorBottom = indicatorHeight
//                }
//                INDICATOR_GRAVITY_STRETCH -> {
//                    indicatorTop = 0
//                    indicatorBottom = height
//                }
//                else -> {
//                }
//            }
//
//            // Draw the selection indicator on top of tab item backgrounds
//            if (indicatorLeft >= 0 && indicatorRight > indicatorLeft) {
//                val selectedIndicator: Drawable
//                selectedIndicator = DrawableCompat.wrap(
//                    if (tabSelectedIndicator != null) tabSelectedIndicator else defaultSelectionIndicator
//                )
//                selectedIndicator.setBounds(indicatorLeft, indicatorTop, indicatorRight, indicatorBottom)
//                if (selectedIndicatorPaint != null) {
//                    if (VERSION.SDK_INT == VERSION_CODES.LOLLIPOP) {
//                        // Drawable doesn't implement setTint in API 21
//                        selectedIndicator.setColorFilter(
//                            selectedIndicatorPaint!!.getColor(), PorterDuff.Mode.SRC_IN
//                        )
//                    } else {
//                        DrawableCompat.setTint(selectedIndicator, selectedIndicatorPaint!!.getColor())
//                    }
//                }
//                selectedIndicator.draw(canvas)
//            }
//
//            // Draw the tab item contents (icon and label) on top of the background + indicator layers
//            super.draw(canvas)
//        }
//    }
//
//    private fun createColorStateList(defaultColor: Int, selectedColor: Int): ColorStateList {
//        val states = arrayOfNulls<IntArray>(2)
//        val colors = IntArray(2)
//        var i = 0
//
//        states[i] = View.SELECTED_STATE_SET
//        colors[i] = selectedColor
//        i++
//
//        // Default enabled state
//        states[i] = View.EMPTY_STATE_SET
//        colors[i] = defaultColor
//        i++
//
//        return ColorStateList(states, colors)
//    }
//
//    @Dimension(unit = Dimension.DP)
//    private fun getDefaultHeight(): Int {
//        var hasIconAndText = false
//        var i = 0
//        val count = tabs.size
//        while (i < count) {
//            val tab = tabs.get(i)
//            if (tab.getIcon() != null && !TextUtils.isEmpty(tab.getText())) {
//                hasIconAndText = true
//                break
//            }
//            i++
//        }
//        return if (hasIconAndText && !inlineLabel) DEFAULT_HEIGHT_WITH_TEXT_ICON else DEFAULT_HEIGHT
//    }
//
//    private fun getTabMinWidth(): Int {
//        if (requestedTabMinWidth != INVALID_WIDTH) {
//            // If we have been given a min width, use it
//            return requestedTabMinWidth
//        }
//        // Else, we'll use the default value
//        return if (mode == MODE_SCROLLABLE) scrollableTabMinWidth else 0
//    }
//
//    override fun generateLayoutParams(attrs: AttributeSet?): FrameLayout.LayoutParams {
//        // We don't care about the layout params of any views added to us, since we don't actually
//        // add them. The only view we add is the SlidingTabStrip, which is done manually.
//        // We return the default layout params so that we don't blow up if we're given a TabItem
//        // without android:layout_* values.
//        return generateDefaultLayoutParams()
//    }
//
////    fun getTabMaxWidth(): Int {
////        return tabMaxWidth
////    }
//
//    /**
//     * A [ViewPager.OnPageChangeListener] class which contains the necessary calls back to the
//     * provided [TabLayout] so that the tab position is kept in sync.
//     *
//     *
//     * This class stores the provided TabLayout weakly, meaning that you can use [ ][ViewPager.addOnPageChangeListener] without removing the listener and not cause a
//     * leak.
//     */
//    class TabLayoutOnPageChangeListener(tabLayout: InfiniteTabLayout) : ViewPager.OnPageChangeListener {
//        private val tabLayoutRef: WeakReference<InfiniteTabLayout>
//        private var previousScrollState: Int = 0
//        private var scrollState: Int = 0
//
//        init {
//            tabLayoutRef = WeakReference(tabLayout)
//        }
//
//        override fun onPageScrollStateChanged(state: Int) {
//            previousScrollState = scrollState
//            scrollState = state
//        }
//
//        override fun onPageScrolled(
//            position: Int, positionOffset: Float, positionOffsetPixels: Int
//        ) {
//            val tabLayout = tabLayoutRef.get()
//            if (tabLayout != null) {
//                // Only update the text selection if we're not settling, or we are settling after
//                // being dragged
//                val updateText = scrollState != SCROLL_STATE_SETTLING || previousScrollState == SCROLL_STATE_DRAGGING
//                // Update the indicator if we're not settling after being idle. This is caused
//                // from a setCurrentItem() call and will be handled by an animation from
//                // onPageSelected() instead.
//                val updateIndicator =
//                    !(scrollState == SCROLL_STATE_SETTLING && previousScrollState == SCROLL_STATE_IDLE)
//                tabLayout.setScrollPosition(position, positionOffset, updateText, updateIndicator)
//            }
//        }
//
//        override fun onPageSelected(position: Int) {
//            val tabLayout = tabLayoutRef.get()
//            if (tabLayout != null
//                && tabLayout.getSelectedTabPosition() != position
//                && position < tabLayout.getTabCount()
//            ) {
//                // Select the tab, only updating the indicator if we're not being dragged/settled
//                // (since onPageScrolled will handle that).
//                val updateIndicator =
//                    scrollState == SCROLL_STATE_IDLE || scrollState == SCROLL_STATE_SETTLING && previousScrollState == SCROLL_STATE_IDLE
//                tabLayout.selectTab(tabLayout.getTabAt(position), updateIndicator)
//            }
//        }
//
//        internal fun reset() {
//            scrollState = SCROLL_STATE_IDLE
//            previousScrollState = scrollState
//        }
//    }
//
//    /**
//     * A [TabLayout.OnTabSelectedListener] class which contains the necessary calls back to the
//     * provided [ViewPager] so that the tab position is kept in sync.
//     */
//    class ViewPagerOnTabSelectedListener(private val viewPager: ViewPager) : OnTabSelectedListener {
//        override fun onTabSelected(tab: Tab) {
//            viewPager.currentItem = tab.position
//        }
//
//        override fun onTabUnselected(tab: Tab) {
//            // No-op
//        }
//
//        override fun onTabReselected(tab: Tab) {
//            // No-op
//        }
//    }
//
//    private inner class PagerAdapterObserver internal constructor() : DataSetObserver() {
//        override fun onChanged() {
//            populateFromPagerAdapter()
//        }
//
//        override fun onInvalidated() {
//            populateFromPagerAdapter()
//        }
//    }
//
//    private inner class AdapterChangeListener internal constructor() : ViewPager.OnAdapterChangeListener {
//        private var autoRefresh: Boolean = false
//
//        override fun onAdapterChanged(
//            viewPager: ViewPager,
//            @Nullable oldAdapter: PagerAdapter?,
//            @Nullable newAdapter: PagerAdapter?
//        ) {
//            if (this@InfiniteTabLayout.viewPager === viewPager) {
//                setPagerAdapter(newAdapter, autoRefresh)
//            }
//        }
//
//        internal fun setAutoRefresh(autoRefresh: Boolean) {
//            this.autoRefresh = autoRefresh
//        }
//    }
//}
//
//interface IMaxCount {
//    fun maxCount()
//}