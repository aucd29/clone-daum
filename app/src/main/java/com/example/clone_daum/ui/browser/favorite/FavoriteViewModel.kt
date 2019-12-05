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
import com.example.clone_daum.R

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2019. 3. 4. <p/>
 */

class FavoriteViewModel @Inject constructor(
    app: Application,
    private val mFavoriteDao: MyFavoriteDao
) : RecyclerViewModel<MyFavorite>(app), IDialogAware, IFolder {
    companion object {
        private val mLog = LoggerFactory.getLogger(FavoriteViewModel::class.java)

        const val CMD_BRS_OPEN           = "brs-open"
        const val CMD_FOLDER_CHOOSE      = "folder-choose"
        const val CMD_FAVORITE_MODIFY    = "favorite-modify"
        const val CMD_SHOW_FOLDER_DIALOG = "show-folder-dialog"
    }

    override val dialogEvent = SingleLiveEvent<DialogParam>()

    private lateinit var mDisposable: CompositeDisposable
    val itemAnimator = ObservableField<RecyclerView.ItemAnimator?>()

    fun init(dp: CompositeDisposable) {
        this.mDisposable = dp

        initAdapter(R.layout.favorite_item_folder, R.layout.favorite_item)
        adapter.get()?.run { isScrollToPosition = false }
    }

    fun initItems() {
        mDisposable.clear()

        mDisposable.add(mFavoriteDao.selectShowAllFlowable()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (mLog.isDebugEnabled) {
                    mLog.debug("FAVORITE COUNT : ${it.size} ${it.hashCode()}")
                }

                items.set(it)
            }, {
                errorLog(it)
                snackbar(it)
            }))
    }

    fun initItemsByFolder() {
        mDisposable.clear()
        mDisposable.add(mFavoriteDao.selectShowFolderFlowable()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (mLog.isDebugEnabled) {
                    mLog.debug("FAVORITE BY FOLDER NAME COUNT : ${it.size}")
                }

                items.set(it)
            }, {
                errorLog(it)
                snackbar(it)
            }))
    }

    fun insertFolder(folderName: String) {
        mDisposable.add(mFavoriteDao.insert(MyFavorite(folderName, favType = MyFavorite.T_FOLDER))
            .subscribeOn(Schedulers.io())
            .subscribe({
                if (mLog.isDebugEnabled) {
                    mLog.debug("INSERTED FOLDER: $folderName")
                }
            }, {
                errorLog(it)
                snackbar(it)
            }))
    }

    fun firstWord(name: String): String {
        val firstWord = name.substring(0, 1)

        if (mLog.isTraceEnabled) {
            mLog.trace("FIRST WORD : $firstWord")
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
                snackbar(it)
                callback(false)
            }))
    }
}