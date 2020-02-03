package com.example.clone_daum.ui.browser.favorite

import android.app.Application
import androidx.databinding.ObservableField
import androidx.recyclerview.widget.RecyclerView
import com.example.clone_daum.model.local.MyFavorite
import com.example.clone_daum.model.local.MyFavoriteDao
import brigitte.*
import brigitte.arch.SingleLiveEvent
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.slf4j.LoggerFactory
import javax.inject.Inject

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2019. 3. 4. <p/>
 */

class FavoriteViewModel @Inject constructor(
    app: Application,
    private val favoriteDao: MyFavoriteDao
) : RecyclerViewModel<MyFavorite>(app), IDialogAware, IFolder {
    companion object {
        private val logger = LoggerFactory.getLogger(FavoriteViewModel::class.java)

        const val CMD_BRS_OPEN           = "brs-open"
        const val CMD_FOLDER_CHOOSE      = "folder-choose"
        const val CMD_FAVORITE_MODIFY    = "favorite-modify"
        const val CMD_SHOW_FOLDER_DIALOG = "show-folder-dialog"
    }

    val itemAnimator = ObservableField<RecyclerView.ItemAnimator?>()
    override val dialogEvent = SingleLiveEvent<DialogParam>()
    private val dp = CompositeDisposable()

    fun initScrollToPosition() {
        adapter.get()?.run { isScrollToPosition = false }
    }

    fun initItems() {
        dp.clear()
        dp.add(favoriteDao.selectShowAllFlowable()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (logger.isDebugEnabled) {
                    logger.debug("FAVORITE COUNT : ${it.size} ${it.hashCode()}")
                }

                items.set(it)
            }, {
                errorLog(it)
                snackbar(it)
            }))
    }

    fun initItemsByFolder() {
        dp.clear()
        dp.add(favoriteDao.selectShowFolderFlowable()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (logger.isDebugEnabled) {
                    logger.debug("FAVORITE BY FOLDER NAME COUNT : ${it.size}")
                }

                items.set(it)
            }, {
                errorLog(it)
                snackbar(it)
            }))
    }

    fun insertFolder(folderName: String) {
        dp.add(favoriteDao.insert(MyFavorite(folderName, favType = MyFavorite.T_FOLDER))
            .subscribeOn(Schedulers.io())
            .subscribe({
                if (logger.isDebugEnabled) {
                    logger.debug("INSERTED FOLDER: $folderName")
                }
            }, {
                errorLog(it)
                snackbar(it)
            }))
    }

    fun firstWord(name: String): String {
        val firstWord = name.substring(0, 1)

        if (logger.isTraceEnabled) {
            logger.trace("FIRST WORD : $firstWord")
        }

        return firstWord
    }

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // IFolder
    //
    ////////////////////////////////////////////////////////////////////////////////////

    override fun processFolder(folderName: Any) {
        insertFolder(folderName.toString())
    }

    override fun hasFolder(name: String, callback: (Boolean) -> Unit, id: Int) {
        dp.add(favoriteDao.hasFolder(name)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (logger.isDebugEnabled) {
                    if (it > 0) {
                        logger.debug("HAS FAVORITE FOLDER : $name ($it)")
                    }
                }

                callback(it > 0)
            }, {
                errorLog(it)
                snackbar(it)
                callback(false)
            }))
    }

    override fun onCleared() {
        dp.dispose()
        super.onCleared()
    }
}