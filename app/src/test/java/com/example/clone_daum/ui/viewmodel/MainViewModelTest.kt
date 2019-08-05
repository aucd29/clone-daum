package com.example.clone_daum.ui.viewmodel

import android.view.MotionEvent
import androidx.lifecycle.Observer
import androidx.test.core.app.ApplicationProvider
import com.example.clone_daum.MainApp
import com.example.clone_daum.common.Config
import com.example.clone_daum.ui.main.MainViewModel
import com.google.android.material.tabs.TabLayout
import junit.framework.TestCase.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows
import org.slf4j.LoggerFactory

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2019-08-01 <p/>
 */
@RunWith(RobolectricTestRunner::class)
class MainViewModelTest {
    lateinit var viewmodel: MainViewModel

    @Before
    @Throws(Exception::class)
    fun setup() {
        initMock()

        viewmodel = MainViewModel(app, config)
    }

    @Test
    fun testTabChanged() {
        val tab = mock(TabLayout.Tab::class.java)

        repeat(2) {
            `when`(tab.position).thenReturn(it)

            viewmodel.apply {
                tabChangedCallback.get()?.onTabSelected(tab)
                assertEquals(tabChangedLive.value?.position, it)
            }
        }
    }

    @Test
    fun testAppbarHeight() {
        viewmodel.apply {
            val mockAppbarHeight    = 20
            val mockContainerHeight = 10

            appbarHeight(mockAppbarHeight, mockContainerHeight)
            assertEquals(progressViewOffsetLive.value, mockAppbarHeight)
            assertEquals(appbarHeight, mockAppbarHeight - mockContainerHeight)
        }
    }

    @Test
    fun testAppbarAlpha() {
        viewmodel.apply {
            appbarAlpha()

            appbarChangedListener.get()?.invoke(100, 10)
            assertEquals(appbarAlpha.get(), 1f-10f/100f)
            assertEquals(appbarOffsetLive.value, 10)

            appbarChangedListener.get()?.invoke(100, 80)
            assertEquals(appbarAlpha.get(), 1f-80f/100f)
            assertEquals(appbarOffsetLive.value, 80)

            appbarChangedListener.get()?.invoke(100, 100)
            assertEquals(appbarAlpha.get(), 1f-100f/100f)
            assertEquals(appbarOffsetLive.value, 100)
        }
    }

    @Test
    fun testGoToNews() {
        viewmodel.apply {
            command(MainViewModel.GOTO_NEWS)

            assertEquals(tabSelector.get(), MainViewModel.INDEX_NEWS)
        }
    }

    @Test
    fun testMagneticEffect() {
        viewmodel.apply {
            mainContainerDispatchTouchEvent()
            appbarOffsetLive.value = -49
            appbarHeight = 100

            val motionEvent = mock(MotionEvent::class.java)
            `when`(motionEvent.action).thenReturn(MotionEvent.ACTION_UP)
            mainContainerTouchEvent.get()?.invoke(motionEvent)
            assertTrue(appbarMagneticEffectLive.value!!)

            appbarOffsetLive.value = -51
            mainContainerTouchEvent.get()?.invoke(motionEvent)
            assertFalse(appbarMagneticEffectLive.value!!)

            // 비정상 값
            appbarOffsetLive.value = 50
            mainContainerTouchEvent.get()?.invoke(motionEvent)
            assertFalse(appbarMagneticEffectLive.value!!)
        }
    }

    @Test
    fun testCommands() {
        viewmodel.apply {
            val observer = mock(Observer::class.java) as Observer<Pair<String, Any>>
            commandEvent.observeForever(observer)

            command(MainViewModel.CMD_SEARCH_FRAGMENT)
            verify(observer).onChanged(MainViewModel.CMD_SEARCH_FRAGMENT to -1)

            command(MainViewModel.CMD_NAVIGATION_FRAGMENT)
            verify(observer).onChanged(MainViewModel.CMD_NAVIGATION_FRAGMENT to -1)

            command(MainViewModel.CMD_MEDIA_SEARCH_FRAGMENT)
            verify(observer).onChanged(MainViewModel.CMD_MEDIA_SEARCH_FRAGMENT to -1)

            command(MainViewModel.CMD_REALTIME_ISSUE)
            verify(observer).onChanged(MainViewModel.CMD_REALTIME_ISSUE to -1)

            command(MainViewModel.CMD_BRS_OPEN, "http://test.net")
            verify(observer).onChanged(MainViewModel.CMD_BRS_OPEN to "http://test.net")

            verifyNoMoreInteractions(observer)
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

    private fun initMock() {
        MockitoAnnotations.initMocks(this)

        // shadowApp.grantPermissions(android.Manifest.permission.ACCESS_FINE_LOCATION)
    }


    // https://stackoverflow.com/questions/13684094/how-can-we-access-context-of-an-application-in-robolectric
    private val app = ApplicationProvider.getApplicationContext<MainApp>()

    // https://stackoverflow.com/questions/35031301/android-robolectric-unit-test-for-marshmallow-permissionhelper
    private val shadowApp = Shadows.shadowOf(app)
}