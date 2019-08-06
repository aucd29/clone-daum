package com.example.clone_daum.config

import android.os.Build
import androidx.test.core.app.ApplicationProvider
import brigitte.actionBarSize
import com.example.clone_daum.BuildConfig
import com.example.clone_daum.MainApp
import com.example.clone_daum.common.Config
import com.example.clone_daum.util.BaseRoboTest
import com.example.clone_daum.util.eq
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

        config.USER_AGENT eq USER_AGENT
    }

    @Test
    fun actionBarHeightTest() {
        config.ACTION_BAR_HEIGHT eq app.actionBarSize()
    }

    @Test
    fun gpsPermissionTest() {
        config.HAS_PERMISSION_GPS eq true
    }

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // MOCK
    //
    ////////////////////////////////////////////////////////////////////////////////////

    override fun initMock() {
        super.initMock()

        initShadow()
        shadowApp?.grantPermissions(android.Manifest.permission.ACCESS_FINE_LOCATION)
    }
}