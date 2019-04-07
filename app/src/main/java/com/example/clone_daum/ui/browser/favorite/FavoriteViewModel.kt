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
) : RecyclerViewModel<MyFavorite>(application), IDialogAware, IFolder {
    companion object {
        private val mLog = LoggerFactory.getLogger(FavoriteViewModel::class.java)

        // FavoriteFragment
        const val CMD_BRS_OPEN           = "brs-open"
        const val CMD_FOLDER_CHOOSE      = "folder-choose"
        const val CMD_FAVORITE_MODIFY    = "favorite-modify"

        // FolderFragment
        const val CMD_CHANGE_FOLDER      = "change-folder"

        // FavoriteFragment + FolderFragment
        const val CMD_SHOW_FOLDER_DIALOG = "show-folder-dialog"
    }

    override val dialogEvent   = SingleLiveEvent<DialogParam>()

    private lateinit var mDisposable: CompositeDisposable

    val itemAnimator = ObservableField<RecyclerView.ItemAnimator?>()

    // FolderFragment
    private var mCurrentFolder: String? = null
    var selectedPosition: Int = 0
    var smoothToPosition = ObservableInt(0)

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // FAVORITE FRAGMENT
    //
    ////////////////////////////////////////////////////////////////////////////////////

    fun init(dp: CompositeDisposable) {
        this.mDisposable = dp

        initAdapter(arrayOf("favorite_item_folder", "favorite_item"))
    }

    fun initItems() {
        mDisposable.clear()
        mDisposable.add(favoriteDao.selectShowAllFlowable()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (mLog.isDebugEnabled) {
                    mLog.debug("FAVORITE COUNT : ${it.size} ${it.hashCode()}")
                }

                items.set(it)
            }, {
                if (mLog.isDebugEnabled) {
                    it.printStackTrace()
                }

                mLog.error("ERROR: ${it.message}")
            }))
    }

    fun initItemsByFolder() {
        mDisposable.clear()
        mDisposable.add(favoriteDao.selectShowFolderFlowable()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (mLog.isDebugEnabled) {
                    mLog.debug("FAVORITE BY FOLDER NAME COUNT : ${it.size}")
                }

                items.set(it)
            }, {
                if (mLog.isDebugEnabled) {
                    it.printStackTrace()
                }

                mLog.error("ERROR: ${it.message}")
            }))
    }

    fun insertFolder(folderName: String, fromFolderFragment: Boolean) {
        mDisposable.add(favoriteDao.insert(MyFavorite(folderName, favType = MyFavorite.T_FOLDER))
            .subscribeOn(Schedulers.io())
            .subscribe({
                if (mLog.isDebugEnabled) {
                    mLog.debug("INSERTED FOLDER")
                }

//                if (fromFolderFragment) {
//                    reloadFolderItems()
//                } else {
                    //initItems()
//                }
            }, {
                if (mLog.isDebugEnabled) {
                    it.printStackTrace()
                }

                mLog.error("ERROR: ${it.message}")
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

    override fun updateFolder(folderName: Any, fromFolderFragment: Boolean) {
        insertFolder(folderName.toString(), fromFolderFragment)
    }

    override fun hasFolder(name: String, callback: (Boolean) -> Unit, id: Int) {
        mDisposable.add(favoriteDao.hasFolder(name)
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
}