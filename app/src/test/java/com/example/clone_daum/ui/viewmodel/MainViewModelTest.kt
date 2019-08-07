package com.example.clone_daum.ui.viewmodel

import android.view.MotionEvent
import com.example.clone_daum.common.Config
import com.example.clone_daum.ui.main.MainViewModel
import com.example.clone_daum.util.*
import com.google.android.material.tabs.TabLayout
import junit.framework.TestCase.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.robolectric.RobolectricTestRunner

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2019-08-01 <p/>
 */
@RunWith(RobolectricTestRunner::class)
class MainViewModelTest: BaseRoboViewModelTest<MainViewModel>() {
    @Before
    @Throws(Exception::class)
    fun setup() {
        initMock()

        viewmodel = MainViewModel(app, config)
    }

    @Test
    fun tabChangedCallbackTest() {
        val tab = mock(TabLayout.Tab::class.java)

        repeat(2) {
            tab.position.mockReturn(it)

            viewmodel.apply {
                tabChangedCallback.get()?.onTabSelected(tab)

                tabChangedLive.value?.position eq it
            }
        }
    }

    @Test
    fun appbarHeightTest() {
        viewmodel.apply {
            val mockAppbarHeight    = 20
            val mockContainerHeight = 10

            appbarHeight(mockAppbarHeight, mockContainerHeight)

           progressViewOffsetLive.value eq mockAppbarHeight
           appbarHeight eq mockAppbarHeight - mockContainerHeight
        }
    }

    @Test
    fun appbarAlphaTest() {
        viewmodel.apply {
            appbarAlpha()

            appbarChangedListener.get()?.invoke(100, 10)
            appbarAlpha.get() eq 1f-10f/100f
            appbarOffsetLive.value eq 10

            appbarChangedListener.get()?.invoke(100, 80)
            appbarAlpha.get() eq 1f-80f/100f
            appbarOffsetLive.value eq 80

            appbarChangedListener.get()?.invoke(100, 100)
            appbarAlpha.get() eq 1f-100f/100f
            appbarOffsetLive.value eq 100
        }
    }

    @Test
    fun gotoNewsTest() {
        viewmodel.apply {
            command(MainViewModel.GOTO_NEWS)

            tabSelector.get() eq MainViewModel.INDEX_NEWS
        }
    }

    @Test
    fun mainContainerDispatchTouchEventTest() {
        viewmodel.apply {
            mainContainerDispatchTouchEvent()

            val motionEvent = mock(MotionEvent::class.java)
            motionEvent.action.mockReturn(MotionEvent.ACTION_UP)
            appbarHeight = 100

            appbarOffsetLive.value = -49
            mainContainerTouchEvent.get()?.invoke(motionEvent)
            appbarMagneticEffectLive.value!! eq true

            appbarOffsetLive.value = -51
            mainContainerTouchEvent.get()?.invoke(motionEvent)
            appbarMagneticEffectLive.value!! eq false

            // 비정상 값
            appbarOffsetLive.value = 50
            mainContainerTouchEvent.get()?.invoke(motionEvent)
            appbarMagneticEffectLive.value!! eq false
        }
    }

    @Test
    fun commandTest() {
        viewmodel.apply {
            mockObserver<Pair<String, Any>>(commandEvent).apply {
                with (MainViewModel) {
                    verifyChanged(viewmodel,
                        CMD_SEARCH_FRAGMENT,
                        CMD_NAVIGATION_FRAGMENT,
                        CMD_MEDIA_SEARCH_FRAGMENT,
                        CMD_REALTIME_ISSUE)
                }

                verifyChanged(viewmodel, MainViewModel.CMD_BRS_OPEN to "http://test.net")

                verifyNoMoreInteractions(this)
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // MOCK
    //
    ////////////////////////////////////////////////////////////////////////////////////

    @Mock lateinit var config: Config

    override fun initMock() {
        super.initMock()

        initShadow()
        shadowApp?.grantPermissions(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION)
    }
}