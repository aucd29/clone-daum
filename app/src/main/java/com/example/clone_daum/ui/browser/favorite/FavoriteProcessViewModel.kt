package com.example.clone_daum.ui.browser.favorite

import android.app.Application
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import com.example.clone_daum.R
import com.example.clone_daum.model.local.MyFavorite
import com.example.clone_daum.model.local.MyFavoriteDao
import brigitte.*
import brigitte.arch.SingleLiveEvent
import brigitte.viewmodel.CommandEventViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.slf4j.LoggerFactory
import javax.inject.Inject

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2019. 3. 4. <p/>
 */

class FavoriteProcessViewModel @Inject constructor(
    app: Application,
    private val favoriteDao: MyFavoriteDao
) : CommandEventViewModel(app), IDialogAware {
    companion object {
        private val logger = LoggerFactory.getLogger(FavoriteProcessViewModel::class.java)

        const val CMD_NAME_RESET       = "name-reset"
        const val CMD_ADDRESS_RESET    = "address-reset"

        const val CMD_FOLDER_DETAIL    = "folder-detail"
        const val CMD_FAVORITE_PROCESS = "favorite-process"
    }

    val name      = ObservableField<String>()
    val url       = ObservableField<String>()
    val folder    = ObservableField(string(R.string.folder_favorite))
    val enabledOk = ObservableBoolean(true)
    var folderId  = 0

    // MODIFY
    val title     = ObservableInt(R.string.favorite_title_add)
    var _id       = 0

    override val dialogEvent   = SingleLiveEvent<DialogParam>()
    private val dp = CompositeDisposable()

    fun favorite(fav: MyFavorite) {
        _id = fav._id

        // MODIFY

        dp.add(favoriteDao.selectFolderName(fav.folderId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                folder.set(it)
            }, { errorLog(it, logger) }))

        folderId = fav.folderId

        name.set(fav.name)
        url.set(fav.url)
        title.set(R.string.favorite_modify_favorite)
    }

    fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        if (logger.isDebugEnabled) {
            logger.debug("TEXT CHANGED")
        }

        // 데이터 변화 시 ok 버튼을 활성화 또는 비활화 시켜야 함
        enabledOk.set(!name.get().isNullOrEmpty() && !url.get().isNullOrEmpty())
    }

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // ICommandEventAware
    //
    ////////////////////////////////////////////////////////////////////////////////////

    override fun command(cmd: String, data: Any) {
        when (cmd) {
            CMD_NAME_RESET       -> name.set("")
            CMD_ADDRESS_RESET    -> url.set("")
            CMD_FAVORITE_PROCESS -> favoriteProcess()
            else                 -> super.command(cmd, data)
        }
    }

    private fun favoriteProcess() {
        val name   = name.get()!!
        val url    = url.get()!!
        val folder = folder.get()!!
        val stateAdd = _id == 0

        if (logger.isDebugEnabled) {
            if (stateAdd) {
                logger.debug("ADD FAVORITE\n$name\n$url\n$folder")
            } else {
                logger.debug("MODIFY FAVORITE\n$name\n$url\n$folder")
            }
        }

        if (stateAdd) {
            dp.add(favoriteDao.hasUrl(url)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe ({
                    if (it == 0) {
                        //insertFavorite(name, url, if (folder == string(R.string.folder_favorite)) "" else folder)
                        insertFavorite(name, url, folderId)
                    } else {
                        if (logger.isInfoEnabled) {
                            logger.info("EXIST FAVORITE $url")
                        }

                        snackbar(R.string.brs_exist_fav_url)
                    }
                }, {
                    errorLog(it, logger)
                    snackbar(it)
                })
            )
        } else {
            val modifyData = MyFavorite(name, url, folderId)
            modifyData._id = _id

            dp.add(favoriteDao.update(modifyData)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    if (logger.isDebugEnabled) {
                        logger.debug("MODIFIED FAVORITE URL : $url")
                    }

                    finish()
                }, {
                    errorLog(it, logger)
                    snackbar(it)
                }))
        }
    }

    private fun insertFavorite(name: String, url: String, folderId: Int) {
        dp.add(favoriteDao.insert(MyFavorite(name, url, folderId))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (logger.isDebugEnabled) {
                    logger.debug("ADDED FAVORITE URL : $url")
                }

                finish()
            }, {
                errorLog(it, logger)
                snackbar(it)
            }))
    }

    override fun onCleared() {
        dp.dispose()

        super.onCleared()
    }
}