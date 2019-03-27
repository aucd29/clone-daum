package com.example.clone_daum.ui.browser.favorite

import android.annotation.SuppressLint
import android.app.Application
import androidx.core.content.ContextCompat
import com.example.clone_daum.databinding.FavoriteModifyItemBinding
import com.example.clone_daum.databinding.FavoriteModifyItemFolderBinding
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
import io.reactivex.Completable

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2019. 3. 4. <p/>
 */

class FavoriteModifyViewModel @Inject constructor(application: Application
    , private val favoriteDao: MyFavoriteDao
) : RecyclerViewModel<MyFavorite>(application), ICommandEventAware, IFinishFragmentAware
    , ISnackbarAware {
    companion object {
        private val mLog = LoggerFactory.getLogger(FavoriteModifyViewModel::class.java)

        const val CMD_SELECT_ALL    = "select-all"
        const val CMD_CHOOSE_DELETE = "choose-delete"
        const val CMD_POPUP_MENU    = "popup-menu"
    }

    override val commandEvent  = SingleLiveEvent<Pair<String, Any>>()
    override val finishEvent   = SingleLiveEvent<Void>()
    override val snackbarEvent = SingleLiveEvent<String>()

    lateinit var dp: CompositeDisposable

    private fun initItems() {
        if (mLog.isDebugEnabled) {
            mLog.debug("INIT ITEMS")
        }

        // flowable 로 하면 디비 갱신 다시 쿼리를 전달 받아서 해주긴 하는데
        // touch helper 구조상 처음에만 쿼리를 전달 받도록 함
        dp.add(favoriteDao.selectShowAll()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (mLog.isDebugEnabled) {
                    mLog.debug("FAVORITE MODIFY COUNT : ${it.size}")
                }

                items.set(it)
            }, ::snackbar))
    }

    fun initShowAll(dp: CompositeDisposable) {
        this.dp = dp

        initAdapter(arrayOf("favorite_modify_item_folder", "favorite_modify_item"))
        initItems()
        initItemTouchHelper(ItemMovedCallback { from, to ->
            items.get()?.let {
                if (mLog.isDebugEnabled) {
                    mLog.debug("SWAP INDEX $from -> $to")
                }

                val fromData = it.get(from)
                val toData   = it.get(to)

                fromData.swapDate(toData)

                dp.add(favoriteDao.update(fromData, toData)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe ({
                    if (mLog.isDebugEnabled) {
                        mLog.debug("UPDATED DATE")
                    }
                }, ::snackbar))
            }
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

    fun rowColor(state: Boolean) = if (state) {
        ContextCompat.getColor(app, R.color.alpha_orange)
    } else {
        ContextCompat.getColor(app, android.R.color.white)
    }

    override fun commandEvent(cmd: String, data: Any) {
        when (cmd) {
            CMD_SELECT_ALL -> {
                items.get()?.let {
                    // 하나라도 true 가 아니라면
                    var changeAllTrue = false

                    for (item in it) {
                        if (item.check.get() == false) {
                            changeAllTrue = true
                            break
                        }
                    }

                    if (changeAllTrue) {
                        if (mLog.isDebugEnabled) {
                            mLog.debug("CHECKBOX CHECKED ALL")
                        }

                        it.forEach { it.check.set(true) }
                    } else {
                        if (mLog.isDebugEnabled) {
                            mLog.debug("CHECKBOX TOGGLE")
                        }

                        it.forEach { it.check.set(!it.check.get()) }
                    }
                }
            }

            CMD_CHOOSE_DELETE -> {
                if (mLog.isDebugEnabled) {
                    mLog.debug("DELETE CHOOSE ITEMS")
                }

                items.get()?.forEach {
                    if (it.check.get()) {
                        if (mLog.isDebugEnabled) {
                            mLog.debug("DELETE ITEM : ${it._id}")
                        }

                        dp.add(favoriteDao.delete(it)
                            .subscribeOn(Schedulers.io())
                            .subscribe {
                                if (mLog.isDebugEnabled) {
                                    mLog.debug("DELETE ID ${it._id}")
                                }
                            })

                        dp.add(Completable.fromAction { favoriteDao.delete(it.name) }
                            .subscribeOn(Schedulers.io())
                            .subscribe {
                                if (mLog.isDebugEnabled) {
                                    mLog.debug("DELETE FOLDER : ${it.name}")
                                }
                            })
                    }
                }

                // modify fragment 에서는 single 로 call 하고 있으므로
                // 화면을 갱신 시켜줘야 한다.
                initItems()
            }
            else -> super.commandEvent(cmd, data)
        }
    }
}
