package com.example.clone_daum.ui.main

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.clone_daum.di.module.ViewModelAssistedFactory
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import org.slf4j.LoggerFactory

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2019-09-10 <p/>
 */

class AssignedInjectTestViewModel @AssistedInject constructor(
    @Assisted private val stateHandle: SavedStateHandle
) : ViewModel() {
    companion object {
        private val mLog = LoggerFactory.getLogger(AssignedInjectTestViewModel::class.java)

        private const val K_HELLO = "hello"
    }

    val testLive = stateHandle.getLiveData<String>(K_HELLO, "world")

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // FACTORY
    //
    ////////////////////////////////////////////////////////////////////////////////////

    @AssistedInject.Factory
    interface Factory : ViewModelAssistedFactory<AssignedInjectTestViewModel>
}