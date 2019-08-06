@file:Suppress("NOTHING_TO_INLINE", "unused")
package com.example.clone_daum.util

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.test.core.app.ApplicationProvider
import brigitte.CommandEventViewModel
import com.example.clone_daum.MainApp
import junit.framework.TestCase.assertEquals
import org.junit.Rule
import org.mockito.Mockito
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.robolectric.Shadows
import org.robolectric.shadows.ShadowApplication
import java.lang.reflect.Field
import java.lang.reflect.Modifier

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2019-08-06 <p/>
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

    protected open fun initShadow() {
        shadowApp = Shadows.shadowOf(app)
    }

    protected val app = ApplicationProvider.getApplicationContext<MainApp>()

    // https://stackoverflow.com/questions/35031301/android-robolectric-unit-test-for-marshmallow-permissionhelper
    protected var shadowApp: ShadowApplication? = null
}

inline fun <reified T> mockObserver(event: LiveData<T>): Observer<T> {
    val mockObserver = mock(Observer::class.java) as Observer<T>
    event.observeForever(mockObserver)

    return mockObserver
}

inline fun <VM: CommandEventViewModel> Observer<Pair<String, Any>>.verifyChanged(viewmodel: VM, vararg cmds: Pair<String, Any>) {
    cmds.forEach {
        viewmodel.command(it.first, it.second)
        verifyChanged(it)
    }
}

inline fun <VM: CommandEventViewModel> Observer<Pair<String, Any>>.verifyChanged(viewmodel: VM, vararg cmds: String) {
    cmds.forEach {
        viewmodel.command(it)
        verifyChanged(it to -1)
    }
}

inline fun <T> Observer<T>.verifyChanged(cmd: T) {
    verify(this).onChanged(cmd)
}

inline fun <T> Any.mockReturn(value: T) {
    `when`(this).thenReturn(value)
}

inline fun <T> List<T>.mockReturn(value: List<T>) {
    `when`(this).thenReturn(value)
}

infix fun <A, B> A.eq(that: B) = assertEquals(this, that)
