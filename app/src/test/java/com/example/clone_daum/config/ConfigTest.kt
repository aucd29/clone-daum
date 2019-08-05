package com.example.clone_daum.config

import android.os.Build
import androidx.test.core.app.ApplicationProvider
import brigitte.actionBarSize
import com.example.clone_daum.BuildConfig
import com.example.clone_daum.MainApp
import com.example.clone_daum.common.Config
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows
import org.slf4j.LoggerFactory
import java.util.*


/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2019-07-31 <p/>
 */
@RunWith(RobolectricTestRunner::class)
class ConfigTest {
    lateinit var config: Config

    @Before
    @Throws(Exception::class)
    fun setup() {
        initMock()

        config = Config(app)
    }

    @Test
    fun testUserAgent() {
        val release = Build.VERSION.RELEASE
        val country = Locale.getDefault().country
        val language= Locale.getDefault().language
        val param          = "service"   // LoginActorDeleteToken
        val version = BuildConfig.VERSION_NAME

        val USER_AGENT = "DaumMobileApp (Linux; U; Android $release; $country-$language) $param/$version"

        assertEquals(config.USER_AGENT, USER_AGENT)
    }

    @Test
    fun testActionBarHeight() {
        assertEquals(config.ACTION_BAR_HEIGHT, app.actionBarSize())
    }

    @Test
    fun testGpsPermission() {
        assertTrue(config.HAS_PERMISSION_GPS)
    }

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // MOCK
    //
    ////////////////////////////////////////////////////////////////////////////////////

    companion object {
        private val mLog = LoggerFactory.getLogger(ConfigTest::class.java)
    }

    private fun initMock() {
        MockitoAnnotations.initMocks(this)

        shadowApp.grantPermissions(android.Manifest.permission.ACCESS_FINE_LOCATION)
    }

    // https://stackoverflow.com/questions/13684094/how-can-we-access-context-of-an-application-in-robolectric
    private val app = ApplicationProvider.getApplicationContext<MainApp>()

    // https://stackoverflow.com/questions/35031301/android-robolectric-unit-test-for-marshmallow-permissionhelper
    private val shadowApp = Shadows.shadowOf(app)
}