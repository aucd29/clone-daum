package com.example.clone_daum.ui.browser.favorite

import android.app.Application
import com.example.clone_daum.model.local.MyFavorite
import com.example.clone_daum.model.local.MyFavoriteDao
import brigitte.*
import brigitte.di.dagger.module.RxSchedulers
import io.reactivex.disposables.CompositeDisposable
import org.slf4j.LoggerFactory
import javax.inject.Inject

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2019. 3. 4. <p/>
 */

class FavoriteFolderViewModel @Inject constructor(
    app: Application,
    private val myFavoriteDao: MyFavoriteDao,
    private val schedulers: RxSchedulers
) : RecyclerViewModel2<MyFavorite>(app) {
    companion object {
        private val logger = LoggerFactory.getLogger(FavoriteFolderViewModel::class.java)

        const val CMD_BRS_OPEN        = "brs-open"
        const val CMD_FAVORITE_MODIFY = "favorite-modify"
    }

    private val dp = CompositeDisposable()

    fun initByFolder(folderId: Int) {
        dp.add(myFavoriteDao.selectByFolderIdFlowable(folderId)
            .subscribeOn(schedulers.io())
            .observeOn(schedulers.ui())
            .subscribe ({
                if (logger.isDebugEnabled) {
                    logger.debug("FAVORITE COUNT (BY FOLDER NAME) : ${it.size}")
                }

                items.set(it)
            }, ::errorLog))
    }

    override fun onCleared() {
        dp.dispose()

        super.onCleared()
    }
}