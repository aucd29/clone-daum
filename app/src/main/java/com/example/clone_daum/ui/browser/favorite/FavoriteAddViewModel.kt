package com.example.clone_daum.ui.browser.favorite

import android.app.Application
import android.view.View
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import com.example.clone_daum.R
import com.example.clone_daum.model.local.MyFavorite
import com.example.clone_daum.model.local.MyFavoriteDao
import com.example.clone_daum.ui.search.SearchViewModel
import com.example.common.*
import com.example.common.arch.SingleLiveEvent
import org.slf4j.LoggerFactory
import javax.inject.Inject

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2019. 3. 4. <p/>
 */

class FavoriteAddViewModel @Inject constructor(app: Application
    , private val mFavoriteDao: MyFavoriteDao
) : CommandEventViewModel(app), IDialogAware, ISnackbarAware {
    companion object {
        private val mLog = LoggerFactory.getLogger(FavoriteAddViewModel::class.java)

        const val CMD_NAME_RESET    = "name-reset"
        const val CMD_ADDRESS_RESET = "address-reset"
        const val CMD_FOLDER_DETAIL = "folder-detail"
        const val CMD_SAVE          = "save"
    }

    override val commandEvent  = SingleLiveEvent<Pair<String, Any>>()
    override val finishEvent   = SingleLiveEvent<Void>()
    override val dialogEvent   = SingleLiveEvent<DialogParam>()
    override val snackbarEvent = SingleLiveEvent<String>()

    val name      = ObservableField<String>()
    val url       = ObservableField<String>()
    val folder    = ObservableField<String>(string(R.string.folder_favorite))
    val enabledOk = ObservableBoolean(true)

    override fun commandEvent(cmd: String, data: Any) {
        when (cmd) {
            CMD_NAME_RESET    -> name.set("")
            CMD_ADDRESS_RESET -> url.set("")
            CMD_SAVE          -> {
                val name   = name.get()!!
                val url    = url.get()!!
                val folder = folder.get()!!

                mFavoriteDao.insert(MyFavorite(
                    name, url, folder
                )).subscribe(::finishEvent, ::snackbar)
            }
            else -> super.commandEvent(cmd, data)
        }
    }

    fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        if (mLog.isDebugEnabled) {
            mLog.debug("TEXT CHANGED")
        }

        // 데이터 변화 시 ok 버튼을 활성화 또는 비활화 시켜야 함
        enabledOk.set(!name.get().isNullOrEmpty() && !url.get().isNullOrEmpty())
    }
}