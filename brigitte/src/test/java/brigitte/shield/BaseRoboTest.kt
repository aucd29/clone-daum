@file:Suppress("NOTHING_TO_INLINE", "unused")
package brigitte.shield

import android.annotation.SuppressLint
import android.app.Application
import android.net.ConnectivityManager
import android.net.NetworkInfo
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import androidx.test.core.app.ApplicationProvider
import brigitte.color
import brigitte.string
import brigitte.systemService
import org.mockito.MockitoAnnotations
import org.robolectric.Shadows
import org.robolectric.shadows.ShadowApplication
import org.robolectric.shadows.ShadowNetworkInfo

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2019-08-23 <p/>
 */

open class BaseRoboTest constructor()  {
    protected open fun initMock() {
        MockitoAnnotations.initMocks(this)
    }

    inline fun string(@StringRes resid: Int) = app.string(resid)
    inline fun color(@ColorRes resid: Int) = app.color(resid)

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // SHADOW
    //
    ////////////////////////////////////////////////////////////////////////////////////

    protected val app = ApplicationProvider.getApplicationContext<Application>()

    // https://stackoverflow.com/questions/35031301/android-robolectric-unit-test-for-marshmallow-permissionhelper
    protected var shadowApp: ShadowApplication? = null
    protected inline fun mockApp() {
        if (shadowApp == null) {
            shadowApp = Shadows.shadowOf(app)
        }
    }
    protected inline fun mockPermissions(vararg permissions: String) {
        mockApp()

        shadowApp?.grantPermissions(*permissions)
    }

    // https://github.com/robolectric/robolectric/blob/master/robolectric/src/test/java/org/robolectric/shadows/ShadowConnectivityManagerTest.java
    protected var shadowNetworkInfo: ShadowNetworkInfo? = null

    @SuppressLint("MissingPermission")
    protected inline fun mockNetwork() {
        app.systemService<ConnectivityManager>()?.let {
            shadowNetworkInfo = Shadows.shadowOf(it.activeNetworkInfo)
        }
    }
    protected inline fun mockEnableNetwork() {
        shadowNetworkInfo?.setConnectionStatus(NetworkInfo.State.CONNECTED)
    }
    protected inline  fun mockDisableNetwork() {
        shadowNetworkInfo?.setConnectionStatus(NetworkInfo.State.DISCONNECTED)
    }
}