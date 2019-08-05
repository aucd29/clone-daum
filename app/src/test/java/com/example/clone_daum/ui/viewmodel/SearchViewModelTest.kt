package com.example.clone_daum.ui.viewmodel

import android.app.Application
import android.content.Context
import android.content.res.Resources
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import com.example.clone_daum.MainApp
import com.example.clone_daum.common.Config
import com.example.clone_daum.ui.search.SearchViewModel
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
import org.slf4j.LoggerFactory

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2019-08-05 <p/>
 */
@RunWith(RobolectricTestRunner::class)
class SearchViewModelTest {
    lateinit var viewmodel: SearchViewModel

    @Before
    @Throws(Exception::class)
    fun setup() {
        initMock()

        viewmodel = SearchViewModel(app, config)
    }

    @Test
    //@Config(sdk=[24], manifest = "src/main/AndroidManifest.xml")
    fun testInit() {

    }

    @Test
    fun testReloadtest() {

    }

    @Test
    fun testEventSearch() {

    }

    @Test
    fun testEventToggleRecentSearch() {
    }

    @Test
    fun testEventDeleteHistory() {
    }

    @Test
    fun testDateConvert() {
    }

    @Test
    fun testSuggest() {
    }

    @Test
    fun testOnTextChanged() {
    }

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // MOCK
    //
    ////////////////////////////////////////////////////////////////////////////////////

    companion object {
        private val mLog = LoggerFactory.getLogger(SearchViewModelTest::class.java)
    }

    @Mock lateinit var config: Config

    private fun initMock() {
        MockitoAnnotations.initMocks(this)
    }

    // https://stackoverflow.com/questions/13684094/how-can-we-access-context-of-an-application-in-robolectric
    private val app = ApplicationProvider.getApplicationContext<MainApp>()
}