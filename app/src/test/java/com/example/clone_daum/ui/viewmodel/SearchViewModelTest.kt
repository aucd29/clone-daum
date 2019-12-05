package com.example.clone_daum.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import com.example.clone_daum.common.Config
import com.example.clone_daum.ui.search.SearchViewModel
import brigitte.shield.BaseRoboViewModelTest
import com.example.clone_daum.model.local.SearchHistoryDao
import com.example.clone_daum.model.remote.DaumSuggestService
import io.reactivex.disposables.CompositeDisposable
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.robolectric.RobolectricTestRunner

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2019-08-05 <p/>
 */
@RunWith(RobolectricTestRunner::class)
class SearchViewModelTest: BaseRoboViewModelTest<SearchViewModel>() {
    @Before
    @Throws(Exception::class)
    fun setup() {
        initMock()

        val dp = CompositeDisposable()
        viewmodel = SearchViewModel(stateHandle, config, daum, searchDao, dp, app)
    }

    @Test
    //@Config(sdk=[24], manifest = "src/main/AndroidManifest.xml")
    fun initTest() {

    }

    @Test
    fun reloadTest() {

    }

    @Test
    fun eventSearchTest() {

    }

    @Test
    fun eventToggleRecentSearchTest() {
    }

    @Test
    fun eventDeleteHistoryTest() {
    }

    @Test
    fun dateConvertTest() {
    }

    @Test
    fun suggestTest() {
    }

    @Test
    fun onTextChangedTest() {
    }

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // MOCK
    //
    ////////////////////////////////////////////////////////////////////////////////////

    @Mock lateinit var stateHandle: SavedStateHandle
    @Mock lateinit var config: Config
    @Mock lateinit var daum: DaumSuggestService
    @Mock lateinit var searchDao: SearchHistoryDao

}