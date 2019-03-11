package com.example.clone_daum.ui.browser.favorite

import android.app.Application
import androidx.databinding.ObservableInt
import com.example.clone_daum.model.local.MyFavorite
import com.example.clone_daum.model.local.MyFavoriteDao
import com.example.common.ICommandEventAware
import com.example.common.IFinishFragmentAware
import com.example.common.RecyclerViewModel
import com.example.common.arch.SingleLiveEvent
import io.reactivex.disposables.CompositeDisposable
import org.slf4j.LoggerFactory
import javax.inject.Inject

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2019. 3. 4. <p/>
 */

class FavoriteViewModel @Inject constructor(application: Application
    , private val favoriteDao: MyFavoriteDao
) : RecyclerViewModel<MyFavorite> (application), ICommandEventAware, IFinishFragmentAware {
    companion object {
        private val mLog = LoggerFactory.getLogger(FavoriteViewModel::class.java)
    }

    override val commandEvent = SingleLiveEvent<Pair<String, Any>>()
    override val finishEvent  = SingleLiveEvent<Void>()

    lateinit var dp: CompositeDisposable

    val count = ObservableInt(0)

    fun init(dp: CompositeDisposable) {
        this.dp = dp

        initAdapter(arrayOf("favorite_item", "favorite_folder_item"))
        dp.add(favoriteDao.selectMain().subscribe {
            if (mLog.isDebugEnabled) {
                mLog.debug("FAVORITE COUNT : ${it.size}")
            }

            count.set(it.size)
            items.set(it)
        })
    }

    fun initFolder(dp: CompositeDisposable) {
        this.dp = dp

        initAdapter("folder_item")
        dp.add(favoriteDao.selectFolder().subscribe {
            if (mLog.isDebugEnabled) {
                mLog.debug("FOLDER COUNT : ${it.size}")
            }

            items.set(it)
        })
    }
}