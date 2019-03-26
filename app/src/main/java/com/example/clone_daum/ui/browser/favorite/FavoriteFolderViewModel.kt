package com.example.clone_daum.ui.browser.favorite

import android.app.Application
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

class FavoriteFolderViewModel @Inject constructor(application: Application
    , private val favoriteDao: MyFavoriteDao
) : RecyclerViewModel<MyFavorite>(application), ICommandEventAware, IFinishFragmentAware {
    companion object {
        private val mLog = LoggerFactory.getLogger(FavoriteFolderViewModel::class.java)

        const val CMD_BRS_OPEN = "brs-open"
    }

    override val commandEvent = SingleLiveEvent<Pair<String, Any>>()
    override val finishEvent  = SingleLiveEvent<Void>()

    lateinit var dp: CompositeDisposable

    fun initByFolder(folderName: String, dp: CompositeDisposable) {
        this.dp = dp

        // folder 형태의 index 값이 0
        initAdapter(arrayOf("favorite_item_from_folder", "favorite_item_from_folder"))
        dp.add(favoriteDao.selectByFolderName(folderName)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                if (mLog.isDebugEnabled) {
                    mLog.debug("FAVORITE COUNT (BY FOLDER NAME) : ${it.size}")
                }

                items.set(it)
            })
    }
}