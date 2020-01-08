package com.example.clone_daum.ui.browser.favorite

import android.app.Application
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import com.example.clone_daum.R
import com.example.clone_daum.model.local.MyFavorite
import com.example.clone_daum.model.local.MyFavoriteDao
import brigitte.*
import brigitte.viewmodel.string
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.slf4j.LoggerFactory
import javax.inject.Inject

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2019. 4. 5. <p/>
 */

class FolderViewModel @Inject constructor(
    private val mFavoriteDao: MyFavoriteDao,
    app: Application
) : RecyclerViewModel<MyFavorite>(app), IFolder {
    companion object {
        private val mLog = LoggerFactory.getLogger(FolderViewModel::class.java)

        const val CMD_SHOW_FOLDER_DIALOG = "show-folder-dialog"
        const val CMD_CHANGE_FOLDER      = "change-folder"
    }

    private val mDisposable = CompositeDisposable()
    private var mCurrentFolderId: Int = 0

    var selectedPosition: Int = 0
    var smoothToPosition = ObservableInt(0)
    val empty = ObservableField("")

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // FOLDER FRAGMENT
    //
    ////////////////////////////////////////////////////////////////////////////////////

    fun initFolder(currentFolderId: Int) {
        this.mCurrentFolderId = currentFolderId

        initAdapter(R.layout.folder_item)
        reloadFolderItems()
    }

    fun reloadFolderItems() {
        if (mLog.isDebugEnabled) {
            mLog.debug("RELOAD FOLDER LIST")
        }

        mDisposable.add(mFavoriteDao.selectShowFolderFlowable()
            .subscribeOn(Schedulers.io())
            .filter { it.isNotEmpty() }
            .map {
                if (mLog.isDebugEnabled) {
                    mLog.debug("FOLDER COUNT : ${it.size}")
                }

                // 첫 번째 항목에 기본 위치인 '즐겨찾기' 를 추가 (0)
                val list = it as ArrayList
                val defaultFolder = string(R.string.favorite_title)
                list.add(0, MyFavorite(defaultFolder, favType = MyFavorite.T_FOLDER))

                var pos = 0
                if (mCurrentFolderId != 0) {
                    for (item in it) {
                        if (item._id == mCurrentFolderId) {
                            selectedPosition = pos
                            break
                        }

                        ++pos
                    }
                }

                list
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe ({
                items.set(it)
                smoothToPosition.set(selectedPosition)
            }, ::errorLog))
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
                errorLog(it)
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
                errorLog(it)
                callback(false)
            }))
    }

    fun currentFolder() = selectedPosition to items.get()!![selectedPosition]

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

    override fun onCleared() {
        mDisposable.dispose()
        super.onCleared()
    }
}