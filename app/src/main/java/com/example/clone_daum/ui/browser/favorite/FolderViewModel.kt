package com.example.clone_daum.ui.browser.favorite

import android.app.Application
import androidx.databinding.ObservableInt
import com.example.clone_daum.R
import com.example.clone_daum.model.local.MyFavorite
import com.example.clone_daum.model.local.MyFavoriteDao
import com.example.common.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.slf4j.LoggerFactory
import javax.inject.Inject

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2019. 4. 5. <p/>
 */

class FolderViewModel @Inject constructor(application: Application
    , private val mFavoriteDao: MyFavoriteDao
) : RecyclerViewModel<MyFavorite>(application), IFolder {
    companion object {
        private val mLog = LoggerFactory.getLogger(FolderViewModel::class.java)

        const val CMD_CHANGE_FOLDER      = "change-folder"
        const val CMD_SHOW_FOLDER_DIALOG = "show-folder-dialog"
    }

    private lateinit var mDisposable: CompositeDisposable

    private var mCurrentFolder: String? = null
    var selectedPosition: Int = 0
    var smoothToPosition = ObservableInt(0)

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // FOLDER FRAGMENT
    //
    ////////////////////////////////////////////////////////////////////////////////////

    fun initFolder(dp: CompositeDisposable, currentFolder: String?) {
        this.mDisposable    = dp
        this.mCurrentFolder = currentFolder

        initAdapter("folder_item")
        reloadFolderItems()
    }

    fun reloadFolderItems() {
        if (mLog.isDebugEnabled) {
            mLog.debug("RELOAD FOLDER LIST")
        }

        mDisposable.add(mFavoriteDao.selectShowFolderFlowable()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                if (mLog.isDebugEnabled) {
                    mLog.debug("FOLDER COUNT : ${it.size}")
                }

                // 첫 번째 위치에 즐겨찾기를 추가
                val list = it.toMutableList()
                val defaultFolder = string(R.string.favorite_title)
                var pos = 0

                list.add(0, MyFavorite(defaultFolder, favType = MyFavorite.T_FOLDER))
                if (mCurrentFolder != defaultFolder) {
                    for (it in list) {
                        if (it.name == mCurrentFolder) {
                            selectedPosition = pos
                            break;
                        }

                        ++pos
                    }
                }

                items.set(list)
                smoothToPosition.set(selectedPosition)
            })
    }

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // IFolder
    //
    ////////////////////////////////////////////////////////////////////////////////////

    override fun processFolder(folderName: Any) {
        mDisposable.add(mFavoriteDao.insert(MyFavorite(folderName.toString()
            , favType = MyFavorite.T_FOLDER))
            .subscribeOn(Schedulers.io())
            .subscribe({
                if (mLog.isDebugEnabled) {
                    mLog.debug("INSERTED FAVORITE FOLDER")
                }
            }, {
                if (mLog.isDebugEnabled) {
                    it.printStackTrace()
                }

                mLog.error("ERROR: ${it.message}")

                snackbar(it)
            }))
    }

    override fun hasFolder(name: String, callback: (Boolean) -> Unit, id: Int) {
        mDisposable.add(mFavoriteDao.hasFolder(name)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (mLog.isDebugEnabled) {
                    if (it > 0) {
                        mLog.debug("HAS FAVORITE FOLDER : $name ($it)")
                    }
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

    fun currentFolder() = selectedPosition to items.get()!!.get(selectedPosition).name

    fun firstWord(name: String): String {
        val firstWord = name.substring(0, 1)

        if (mLog.isTraceEnabled) {
            mLog.trace("FIRST WORD : $firstWord")
        }

        return firstWord
    }

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // ICommandEvent
    //
    ////////////////////////////////////////////////////////////////////////////////////

    override fun command(cmd: String, data: Any) {
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

        super.command(cmd, data)
    }
}