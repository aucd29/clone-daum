package com.example.clone_daum.ui.browser.favorite

import android.app.Application
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import com.example.clone_daum.R
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

class FavoriteProcessViewModel @Inject constructor(app: Application
    , private val mFavoriteDao: MyFavoriteDao
) : CommandEventViewModel(app), IDialogAware {
    companion object {
        private val mLog = LoggerFactory.getLogger(FavoriteProcessViewModel::class.java)

        const val CMD_NAME_RESET       = "name-reset"
        const val CMD_ADDRESS_RESET    = "address-reset"

        const val CMD_FOLDER_DETAIL    = "folder-detail"
        const val CMD_FAVORITE_PROCESS = "favorite-process"
    }

    override val dialogEvent   = SingleLiveEvent<DialogParam>()

    private lateinit var mDisposable: CompositeDisposable

    val name      = ObservableField<String>()
    val url       = ObservableField<String>()
    val folder    = ObservableField<String>(string(R.string.folder_favorite))
    val enabledOk = ObservableBoolean(true)

    // MODIFY
    val title     = ObservableInt(R.string.favorite_title_add)
    var _id       = 0

    fun init(disposable: CompositeDisposable) {
        mDisposable = disposable
    }

    fun favorite(fav: MyFavorite) {
        _id = fav._id

        // 폴더에 데이터가 없으면 기본 값인 즐겨찾기이다.
        if (!fav.folder.isEmpty()) {
            folder.set(fav.folder)
        }

        name.set(fav.name)
        url.set(fav.url)
        title.set(R.string.favorite_modify_favorite)
    }

    fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        if (mLog.isDebugEnabled) {
            mLog.debug("TEXT CHANGED")
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

        if (mLog.isDebugEnabled) {
            if (stateAdd) {
                mLog.debug("ADD FAVORITE\n$name\n$url\n$folder")
            } else {
                mLog.debug("MODIFY FAVORITE\n$name\n$url\n$folder")
            }
        }

        if (stateAdd) {
            mDisposable.add(mFavoriteDao.hasUrl(url)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe ({
                    if (it > 0) {
                        if (mLog.isInfoEnabled) {
                            mLog.info("EXIST FAVORITE $url")
                        }

                        snackbar(string(R.string.brs_exist_fav_url))
                    } else {
                        insertFavorite(name, url, if (folder == string(R.string.folder_favorite)) "" else folder)
                    }
                }, {
                    if (mLog.isDebugEnabled) {
                        it.printStackTrace()
                    }

                    mLog.error("ERROR: ${it.message}")
                    snackbar(it)
                })
            )
        } else {
            val modifyData = MyFavorite(name, url, folder)
            modifyData._id = _id

            mDisposable.add(mFavoriteDao.update(modifyData)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    if (mLog.isDebugEnabled) {
                        mLog.debug("MODIFIED FAVORITE URL : $url")
                    }

                    finish()
                }, {
                    if (mLog.isDebugEnabled) {
                        it.printStackTrace()
                    }

                    mLog.error("ERROR: ${it.message}")
                    snackbar(it)
                }))
        }
    }

    private fun insertFavorite(name: String, url: String, folder: String) {
        mDisposable.add(mFavoriteDao.insert(MyFavorite(name, url, folder))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (mLog.isDebugEnabled) {
                    mLog.debug("ADDED FAVORITE URL : $url")
                }

                finish()
            }, {
                if (mLog.isDebugEnabled) {
                    it.printStackTrace()
                }

                mLog.error("ERROR: ${it.message}")
                snackbar(it)
            }))
    }
}