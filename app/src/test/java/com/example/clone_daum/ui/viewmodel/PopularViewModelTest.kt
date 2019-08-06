package com.example.clone_daum.ui.viewmodel

import com.beloo.widget.chipslayoutmanager.ChipsLayoutManager
import com.example.clone_daum.ui.search.PopularViewModel
import com.example.clone_daum.util.BaseRoboViewModelTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.robolectric.RobolectricTestRunner

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2019-08-05 <p/>
 */
@RunWith(RobolectricTestRunner::class)
class PopularViewModelTest: BaseRoboViewModelTest<PopularViewModel>() {
    @Before
    @Throws(Exception::class)
    fun setup() {
        initMock()

        viewmodel = PopularViewModel(app, layoutmanager)
    }

    @Test
    fun loadTest() {

    }

    @Test
    fun initTest() {

    }

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // MOCK
    //
    ////////////////////////////////////////////////////////////////////////////////////

    @Mock lateinit var layoutmanager: ChipsLayoutManager
}