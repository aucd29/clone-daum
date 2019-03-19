package com.example.clone_daum.ui.browser.favorite

import android.app.Application
import androidx.databinding.ObservableInt
import com.example.clone_daum.model.local.MyFavorite
import com.example.clone_daum.model.local.MyFavoriteDao
import com.example.common.*
import com.example.common.arch.SingleLiveEvent
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.slf4j.LoggerFactory
import javax.inject.Inject

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2019. 3. 4. <p/>
 */

class FavoriteViewModel @Inject constructor(application: Application
    , private val favoriteDao: MyFavoriteDao
) : RecyclerViewModel<MyFavorite>(application), ICommandEventAware, IFinishFragmentAware
    , IDialogAware, ISnackbarAware {
    companion object {
        private val mLog = LoggerFactory.getLogger(FavoriteViewModel::class.java)

        const val CMD_CHECKED_FOLDER = "checked-folder"
        const val CMD_ADDED_FOLDER   = "added-folder"
    }

    override val commandEvent  = SingleLiveEvent<Pair<String, Any>>()
    override val finishEvent   = SingleLiveEvent<Void>()
    override val dialogEvent   = SingleLiveEvent<DialogParam>()
    override val snackbarEvent = SingleLiveEvent<String>()

    lateinit var dp: CompositeDisposable
    val count = ObservableInt(0)
    var selectedPosition: Int = 0

    fun init(dp: CompositeDisposable) {
        this.dp = dp

        initAdapter(arrayOf("favorite_item", "favorite_folder_item"))
        dp.add(favoriteDao.selectMain().subscribe {
            if (mLog.isDebugEnabled) {
                mLog.debug("FAVORITE COUNT : ${it.size}")
            }

            count.set(it.size)
            items.set(it)
        })
    }

    fun addFolder(folderName: String) {
        dp.add(favoriteDao.insert(MyFavorite(folderName, favType = MyFavorite.T_FOLDER))
            .subscribeOn(Schedulers.io())
            .subscribe(::reloadFolderItems, ::snackbar))
    }

    fun initFolder(dp: CompositeDisposable) {
        this.dp = dp

        initAdapter("folder_item")
        reloadFolderItems()
    }

    fun reloadFolderItems() {
        if (mLog.isDebugEnabled) {
            mLog.debug("RELOAD FOLDER LIST")
        }

        dp.add(favoriteDao.selectFolder()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                if (mLog.isDebugEnabled) {
                    mLog.debug("FOLDER COUNT : ${it.size}")
                }

                items.set(it)
            })
    }

    override fun commandEvent(cmd: String, data: Any) {
        when (cmd) {
            CMD_CHECKED_FOLDER -> {
                val oldPos = selectedPosition
                selectedPosition = data as Int

                adapter.get()?.let {
                    it.notifyItemChanged(oldPos)
                    it.notifyItemChanged(selectedPosition)
                }
            }

            else -> super.commandEvent(cmd, data)
        }
    }
}