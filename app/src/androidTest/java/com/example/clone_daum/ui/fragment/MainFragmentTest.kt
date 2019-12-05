package com.example.clone_daum.ui.fragment

import android.app.Application
import android.content.Context
import android.content.res.Resources
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.clone_daum.MainApp
import com.example.clone_daum.ui.main.MainFragment
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows
import org.robolectric.annotation.Config
import org.slf4j.LoggerFactory

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2019-08-05 <p/>
 *
 * https://github.com/jeppeman/android-jetpack-playground?source=post_page---------------------------
 *
 */
@RunWith(AndroidJUnit4::class)
class MainFragmentTest {
    @Before
    @Throws(Exception::class)
    fun setup() {
        initMock()

        fragmentScenario.onFragment {

        }
    }

    @Test
    //@Config(sdk=[24], manifest = "src/main/AndroidManifest.xml")
    fun test() {

    }

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // MOCK
    //
    ////////////////////////////////////////////////////////////////////////////////////

    companion object {
        private val mLog = LoggerFactory.getLogger(MainFragmentTest::class.java)
    }

    private fun initMock() {
        MockitoAnnotations.initMocks(this)

        // shadowApp.grantPermissions(android.Manifest.permission.ACCESS_FINE_LOCATION)
    }

    // https://medium.com/@daniel.nesfeder/android-fragment-testing-fragmentscenario-b8079b6e09fa
    private val fragmentScenario = launchFragmentInContainer<MainFragment>()
    // https://stackoverflow.com/questions/13684094/how-can-we-access-context-of-an-application-in-robolectric
    private val app = ApplicationProvider.getApplicationContext<MainApp>()

    // https://stackoverflow.com/questions/35031301/android-robolectric-unit-test-for-marshmallow-permissionhelper
    private val shadowApp = Shadows.shadowOf(app)
}
