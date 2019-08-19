@file:Suppress("NOTHING_TO_INLINE", "unused")
package brigitte.shield

import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.app.Application
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.test.core.app.ApplicationProvider
import brigitte.*
import junit.framework.TestCase.*
import org.junit.Rule
import org.mockito.Mockito
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.mockito.verification.VerificationMode
import org.robolectric.Shadows
import org.robolectric.shadows.ShadowApplication
import org.robolectric.shadows.ShadowNetworkInfo

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2019-08-06 <p/>
 *
 * https://github.com/mockito/mockito/wiki/Mockito-features-in-Korean
 *
 * bdd - http://en.wikipedia.org/wiki/Behavior_Driven_Development
 *  * give, when, then
 *
 * 애를 lib 으로 어케 만들까나?
 */

open class BaseJUnitViewModelTest<T: ViewModel> @JvmOverloads constructor() {
    lateinit var viewmodel: T

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    protected fun initMock() {
        MockitoAnnotations.initMocks(this)
    }
}

open class BaseRoboViewModelTest<T: ViewModel>: BaseRoboTest() {
    lateinit var viewmodel: T
}

open class BaseRoboTest @JvmOverloads constructor()  {
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

inline fun <reified T> mockObserver(event: LiveData<T>): Observer<T> {
    val mockObserver = mock(Observer::class.java) as Observer<T>
    event.observeForever(mockObserver)

    return mockObserver
}

inline fun <VM: CommandEventViewModel> Observer<Pair<String, Any>>.verifyCommandChanged(viewmodel: VM, vararg cmds: Pair<String, Any>) {
    cmds.forEach {
        viewmodel.command(it.first, it.second)
        verifyChanged(it)
    }
}

inline fun <VM: CommandEventViewModel> Observer<Pair<String, Any>>.verifyCommandChanged(viewmodel: VM, vararg cmds: String) {
    cmds.forEach {
        viewmodel.command(it)
        verifyChanged(it to -1)
    }
}

inline fun <T> Observer<T>.verifyChanged(vararg cmd: T, mode: VerificationMode = atLeastOnce()) {
    // atLeastOnce()는 기본적으로 메소드 호출이 한 번 되는 것을 검증할 수 있다.
    cmd.forEach {
        verify(this, mode).onChanged(it)
    }
}

inline fun Observer<Pair<String, Any>>.verifyChanged(vararg cmd: String, mode: VerificationMode = atLeastOnce()) {
    cmd.forEach {
        verify(this, mode).onChanged(it to -1)
    }
}

inline fun <T> Observer<T>.verifyNeverChanged(vararg cmd: T) {
    verifyChanged(*cmd, mode = never())
}

inline fun Observer<Pair<String, Any>>.verifyNeverChanged(vararg cmd: String) {
    verifyChanged(*cmd, mode = never())
}

inline fun <T> T.mockReturn(value: T) {
    `when`(this).thenReturn(value)
}

inline fun <T> T.mockThrow(e: Throwable) {
    `when`(this).thenThrow(e)
}

inline fun <T> List<T>.mockReturn(value: List<T>) {
    `when`(this).thenReturn(value)
}

