package com.example.clone_daum.ui.browser.favorite

import android.app.Application
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import com.example.clone_daum.model.local.MyFavorite
import com.example.clone_daum.model.local.MyFavoriteDao
import com.example.common.*
import com.example.common.arch.SingleLiveEvent
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.slf4j.LoggerFactory
import javax.inject.Inject
import com.example.clone_daum.R
import kotlinx.android.synthetic.main.folder_dialog.view.*

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2019. 3. 4. <p/>
 */

class FavoriteModifyViewModel @Inject constructor(application: Application
    , private val favoriteDao: MyFavoriteDao
) : RecyclerViewModel<MyFavorite>(application), ICommandEventAware, IFinishFragmentAware
    , IDialogAware, ISnackbarAware {
    companion object {
        private val mLog = LoggerFactory.getLogger(FavoriteModifyViewModel::class.java)

        const val CMD_SELECT_ALL    = "select-all"
        const val CMD_CHOOSE_DELETE = "choose-delete"
        const val CMD_POPUP_MENU    = "popup-menu"
    }

    override val commandEvent  = SingleLiveEvent<Pair<String, Any>>()
    override val finishEvent   = SingleLiveEvent<Void>()
    override val dialogEvent   = SingleLiveEvent<DialogParam>()
    override val snackbarEvent = SingleLiveEvent<String>()

    lateinit var dp: CompositeDisposable


    fun initShowAll(dp: CompositeDisposable) {
        this.dp = dp

        initAdapter(arrayOf("favorite_item_folder", "favorite_item"))
        dp.add(favoriteDao.selectShowAll().subscribe {
            if (mLog.isDebugEnabled) {
                mLog.debug("FAVORITE MODIFY COUNT : ${it.size}")
            }

            items.set(it)
        })
    }

    override fun commandEvent(cmd: String, data: Any) {
        when (cmd) {
            CMD_SELECT_ALL -> items.get()?.forEach {
                it.check = true
            }
            CMD_CHOOSE_DELETE -> {
                if (mLog.isDebugEnabled) {
                    mLog.debug("DELETE CHOOSE ITEMS")
                }

                val favorites = arrayListOf<MyFavorite>()
                items.get()?.forEach {
                    if (it.check) {
                        favorites.add(it)
                    }
                }

                favoriteDao.delete(favorites)
            }
            else -> super.commandEvent(cmd, data)
        }
    }
}