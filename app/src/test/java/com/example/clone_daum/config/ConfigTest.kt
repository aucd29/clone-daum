package com.example.clone_daum.config

import android.os.Build
import briggite.shield.BaseRoboTest
import briggite.shield.assertEquals
import briggite.shield.assertTrue
import brigitte.actionBarSize
import com.example.clone_daum.BuildConfig
import com.example.clone_daum.common.Config
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.util.*


/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2019-07-31 <p/>
 */
@RunWith(RobolectricTestRunner::class)
class ConfigTest: BaseRoboTest() {
    lateinit var config: Config

    @Before
    @Throws(Exception::class)
    fun setup() {
        initMock()

        config = Config(app)
    }

    @Test
    fun userAgentTest() {
        val release = Build.VERSION.RELEASE
        val country = Locale.getDefault().country
        val language= Locale.getDefault().language
        val param          = "service"   // LoginActorDeleteToken
        val version = BuildConfig.VERSION_NAME

        val USER_AGENT = "DaumMobileApp (Linux; U; Android $release; $country-$language) $param/$version"

        config.USER_AGENT.assertEquals(USER_AGENT)
    }

    @Test
    fun actionBarHeightTest() {
        config.ACTION_BAR_HEIGHT.assertEquals(app.actionBarSize())
    }

    @Test
    fun gpsPermissionTest() {
        config.HAS_PERMISSION_GPS.assertTrue()
    }

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // MOCK
    //
    ////////////////////////////////////////////////////////////////////////////////////

    override fun initMock() {
        super.initMock()

        mockPermissions(android.Manifest.permission.ACCESS_FINE_LOCATION)
    }
}