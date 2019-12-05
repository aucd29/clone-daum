package com.example.clone_daum.ui.browser.favorite

import android.app.Application
import com.example.clone_daum.model.local.MyFavorite
import com.example.clone_daum.model.local.MyFavoriteDao
import brigitte.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.slf4j.LoggerFactory
import javax.inject.Inject
import com.example.clone_daum.R

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2019. 3. 4. <p/>
 */

class FavoriteFolderViewModel @Inject constructor(
    app: Application,
    private val mFavoriteDao: MyFavoriteDao
) : RecyclerViewModel<MyFavorite>(app) {
    companion object {
        private val mLog = LoggerFactory.getLogger(FavoriteFolderViewModel::class.java)

        const val CMD_BRS_OPEN        = "brs-open"
        const val CMD_FAVORITE_MODIFY = "favorite-modify"
    }

    private lateinit var mDisposable: CompositeDisposable

    fun initByFolder(folderId: Int, dp: CompositeDisposable) {
        mDisposable = dp

        // folder 형태의 index 값이 0
        initAdapter(R.layout.favorite_item_from_folder, R.layout.favorite_item_from_folder)
        mDisposable.add(mFavoriteDao.selectByFolderIdFlowable(folderId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe ({
                if (mLog.isDebugEnabled) {
                    mLog.debug("FAVORITE COUNT (BY FOLDER NAME) : ${it.size}")
                }

                items.set(it)
            }, ::errorLog))
    }
}