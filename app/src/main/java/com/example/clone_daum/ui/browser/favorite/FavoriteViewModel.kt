package com.example.clone_daum.ui.browser.favorite

import android.app.Application
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.recyclerview.widget.RecyclerView
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

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2019. 3. 4. <p/>
 */

class FavoriteViewModel @Inject constructor(application: Application
    , private val favoriteDao: MyFavoriteDao
) : RecyclerViewModel<MyFavorite>(application), ICommandEventAware, IFinishFragmentAware
    , IDialogAware, ISnackbarAware {
    companion object {
        private val mLog = LoggerFactory.getLogger(FavoriteViewModel::class.java)

        const val CMD_BRS_OPEN           = "brs-open"

        const val CMD_FOLDER_CHOOSE      = "folder-choose"
        const val CMD_FAVORITE_MODIFY    = "favorite-modify"

        // FavoriteFragment + FolderFragment
        const val CMD_SHOW_FOLDER_DIALOG = "show-folder-dialog"

        // FolderFragment
        const val CMD_CHANGE_FOLDER      = "change-folder"
    }

    override val commandEvent  = SingleLiveEvent<Pair<String, Any>>()
    override val finishEvent   = SingleLiveEvent<Void>()
    override val dialogEvent   = SingleLiveEvent<DialogParam>()
    override val snackbarEvent = SingleLiveEvent<String>()

    private lateinit var mDisposable: CompositeDisposable

    val itemAnimator          = ObservableField<RecyclerView.ItemAnimator?>()

    // FolderFragment
    var selectedPosition: Int = 0
    var smoothToPosition = ObservableInt(0)

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // FAVORITE FRAGMENT
    //
    ////////////////////////////////////////////////////////////////////////////////////

    fun initShowAll(dp: CompositeDisposable) {
        this.mDisposable = dp

        initAdapter(arrayOf("favorite_item_folder", "favorite_item"))
        dp.add(favoriteDao.selectShowAllFlowable()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { it ->
                if (mLog.isDebugEnabled) {
                    mLog.debug("FAVORITE COUNT : ${it.size}")
                    it.forEach {
                        mLog.debug("DATE : ${it._id} : ${it.date}")
                    }
                }

                items.set(it)
            })
    }

    fun initShowFolder(dp: CompositeDisposable) {
        this.mDisposable = dp

        initAdapter(arrayOf("favorite_item_folder", "favorite_item"))
        dp.add(favoriteDao.selectShowFolder()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                if (mLog.isDebugEnabled) {
                    mLog.debug("FAVORITE COUNT : ${it.size}")
                    it.forEach {
                        mLog.debug("DATE : ${it._id} : ${it.date}")
                    }
                }

                items.set(it)
            })
    }

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // FAVORITE FRAGMENT, FOLDER FRAGMENT
    //
    ////////////////////////////////////////////////////////////////////////////////////

    fun insertFolder(folderName: String, fromFolderFragment: Boolean) {
        mDisposable.add(favoriteDao.insert(MyFavorite(folderName, favType = MyFavorite.T_FOLDER))
            .subscribeOn(Schedulers.io())
            .subscribe({
                if (fromFolderFragment) {
                    reloadFolderItems()
                } else {
                    initShowAll(mDisposable)
                }
            }, ::snackbar))
    }

    fun hasFolder(dp: CompositeDisposable, name: String, callback: (Boolean) -> Unit) {
        dp.add(favoriteDao.hasFolder(name)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (mLog.isDebugEnabled) {
                    mLog.debug("HAS FAVORITE FOLDER : $name ($it)")
                }

                callback(it > 0)
            }, {
                if (mLog.isDebugEnabled) {
                    it.printStackTrace()
                }

                mLog.error("ERROR: ${it.message}")

                callback(false)
            }))
    }

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // FOLDER FRAGMENT
    //
    ////////////////////////////////////////////////////////////////////////////////////

    fun initFolder(dp: CompositeDisposable, position: Int) {
        this.mDisposable = dp
        selectedPosition = position

        initAdapter("folder_item")
        reloadFolderItems()
    }

    fun reloadFolderItems() {
        if (mLog.isDebugEnabled) {
            mLog.debug("RELOAD FOLDER LIST")
        }

        mDisposable.add(favoriteDao.selectShowFolder()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                if (mLog.isDebugEnabled) {
                    mLog.debug("FOLDER COUNT : ${it.size}")
                }

                // 첫 번째 위치에 즐겨찾기를 추가
                val list = it.toMutableList()
                list.add(0, MyFavorite(string(R.string.favorite_title), favType = MyFavorite.T_FOLDER))

                items.set(list)
                smoothToPosition.set(selectedPosition)
            })
    }

    fun firstWord(name: String): String {
        val firstWord = name.substring(0, 1)

        if (mLog.isDebugEnabled) {
            mLog.debug("FIRST WORD : $firstWord")
        }

        return firstWord
    }

    fun currentFolder() = selectedPosition to items.get()!!.get(selectedPosition).name

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // ICommandEvent
    //
    ////////////////////////////////////////////////////////////////////////////////////

    override fun commandEvent(cmd: String, data: Any) {
        when (cmd) {
            CMD_CHANGE_FOLDER -> {
                // 선택 된 위치 값을 표현 하기 위해 화면 갱신을 줌
                val oldPos  = selectedPosition
                selectedPosition = data as Int

                adapter.get()?.let {
                    it.notifyItemChanged(oldPos)
                    it.notifyItemChanged(selectedPosition)
                }
            }
        }

        super.commandEvent(cmd, data)
    }
}