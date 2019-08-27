package com.example.clone_daum.ui.viewmodel

import android.view.MotionEvent
import com.example.clone_daum.common.Config
import com.example.clone_daum.ui.main.MainViewModel
import brigitte.shield.*
import com.google.android.material.tabs.TabLayout
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.robolectric.RobolectricTestRunner
import org.slf4j.LoggerFactory

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2019-08-01 <p/>
 */
@RunWith(RobolectricTestRunner::class)
class MainViewModelTest: BaseRoboViewModelTest<MainViewModel>() {
    @Before
    @Throws(Exception::class)
    fun setup() {
        initMock()

        viewmodel = MainViewModel(config, app)
    }

    @Test
    fun tabChangedCallbackTest() {
        val tab = mock(TabLayout.Tab::class.java)

        repeat(2) {
            tab.position.mockReturn(it)

            if (mLog.isDebugEnabled) {
                mLog.debug("TEST CHANGE TAB $it")
            }

            viewmodel.apply {
                mockObserver(tabChangedLive).apply {
                    tabChangedCallback.get()?.onTabSelected(tab)
                    verifyChanged(tab)

                    tabChangedLive.value?.position.assertEquals(it)
                }
            }
        }
    }

    @Test
    fun appbarHeightTest() {
        viewmodel.apply {
            val mockAppbarHeight    = 20
            val mockContainerHeight = 10

            appbarHeight(mockAppbarHeight, mockContainerHeight)

//            progressViewOffsetLive.value.assertEquals(mockAppbarHeight)
            appbarHeight.assertEquals(mockAppbarHeight - mockContainerHeight)
        }
    }

    @Test
    fun appbarAlphaTest() {
        viewmodel.apply {
            appbarAlpha()

            appbarChangedListener.get()?.invoke(100, 10)
            appbarAlpha.get().assertEquals(1f-10f/100f)
            appbarOffsetLive.value.assertEquals(10)

            appbarChangedListener.get()?.invoke(100, 80)
            appbarAlpha.get().assertEquals(1f-80f/100f)
            appbarOffsetLive.value.assertEquals(80)

            appbarChangedListener.get()?.invoke(100, 100)
            appbarAlpha.get().assertEquals(1f-100f/100f)
            appbarOffsetLive.value.assertEquals(100)
        }
    }

    @Test
    fun gotoNewsTest() {
        viewmodel.apply {
            command(MainViewModel.ITN_GOTO_NEWS)

            tabSelector.get().assertEquals(MainViewModel.INDEX_NEWS)
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
            appbarMagneticEffectLive.value?.assertEquals(true) ?: assert(false)

            appbarOffsetLive.value = -51
            mainContainerTouchEvent.get()?.invoke(motionEvent)
            appbarMagneticEffectLive.value?.assertEquals(false) ?: assert(false)

            // 비정상 값
            appbarOffsetLive.value = 50
            mainContainerTouchEvent.get()?.invoke(motionEvent)
            appbarMagneticEffectLive.value?.assertEquals(false) ?: assert(false)
        }
    }

    @Test
    fun commandTest() {
        viewmodel.apply {
            mockObserver<Pair<String, Any>>(commandEvent).apply {
                with (MainViewModel) {
                    verifyCommandChanged(viewmodel,
                        CMD_SEARCH_FRAGMENT,
                        CMD_NAVIGATION_FRAGMENT,
                        CMD_MEDIA_SEARCH_FRAGMENT,
                        CMD_REALTIME_ISSUE)
                }

                verifyCommandChanged(viewmodel, MainViewModel.CMD_BRS_OPEN to "http://test.net")
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // MOCK
    //
    ////////////////////////////////////////////////////////////////////////////////////

    companion object {
        private val mLog = LoggerFactory.getLogger(MainViewModelTest::class.java)
    }

    @Mock lateinit var config: Config
}