package com.example.clone_daum.ui.browser.favorite

import android.annotation.SuppressLint
import android.app.Application
import android.view.MotionEvent
import android.view.View
import androidx.core.view.MotionEventCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.clone_daum.databinding.FavoriteModifyItemBinding
import com.example.clone_daum.databinding.FavoriteModifyItemFolderBinding
import com.example.clone_daum.model.local.MyFavorite
import com.example.clone_daum.model.local.MyFavoriteDao
import com.example.common.*
import com.example.common.arch.SingleLiveEvent
import io.reactivex.disposables.CompositeDisposable
import org.slf4j.LoggerFactory
import java.util.*
import javax.inject.Inject

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2019. 3. 4. <p/>
 */

class FavoriteModifyViewModel @Inject constructor(application: Application
    , private val favoriteDao: MyFavoriteDao
) : RecyclerViewModel<MyFavorite>(application), ICommandEventAware, IFinishFragmentAware {
    companion object {
        private val mLog = LoggerFactory.getLogger(FavoriteModifyViewModel::class.java)

        const val CMD_SELECT_ALL    = "select-all"
        const val CMD_CHOOSE_DELETE = "choose-delete"
        const val CMD_POPUP_MENU    = "popup-menu"
    }

    override val commandEvent = SingleLiveEvent<Pair<String, Any>>()
    override val finishEvent  = SingleLiveEvent<Void>()

    lateinit var dp: CompositeDisposable


    @SuppressLint("ClickableViewAccessibility")
    fun initShowAll(dp: CompositeDisposable) {
        this.dp = dp

        initAdapter(arrayOf("favorite_modify_item_folder", "favorite_modify_item"))
        dp.add(favoriteDao.selectShowAll().subscribe {
            if (mLog.isDebugEnabled) {
                mLog.debug("FAVORITE MODIFY COUNT : ${it.size}")
            }

            items.set(it)
        })

        initItemTouchHelper(ItemMovedCallback { from, to ->

            // 디비 수정
        }) { binding ->
            // drag 할 대상 뷰 선택
            when (binding) {
                is FavoriteModifyItemBinding       -> binding.dragArea
                is FavoriteModifyItemFolderBinding -> binding.dragArea
                else -> null
            }
        }
    }

    fun firstWord(name: String): String {
        val firstWord = name.substring(0, 1)

        if (mLog.isDebugEnabled) {
            mLog.debug("FIRST WORD : $firstWord")
        }

        return firstWord
    }

    override fun commandEvent(cmd: String, data: Any) {
        when (cmd) {
            CMD_SELECT_ALL -> items.get()?.forEach {
                it.check = true
            }
            CMD_CHOOSE_DELETE -> {
                if (mLog.isDebugEnabled) {
                    mLog.debug("DELETE CHOOSE ITEMS")
                }

                val favorites = arrayListOf<MyFavorite>()
                items.get()?.forEach {
                    if (it.check) {
                        favorites.add(it)
                    }
                }

                favoriteDao.delete(favorites)
            }
            else -> super.commandEvent(cmd, data)
        }
    }
}
