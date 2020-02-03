package com.example.clone_daum.ui.browser.favorite

import android.app.Application
import android.view.View
import androidx.core.content.ContextCompat
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableInt
import com.example.clone_daum.databinding.FavoriteModifyItemBinding
import com.example.clone_daum.databinding.FavoriteModifyItemFolderBinding
import com.example.clone_daum.model.local.MyFavorite
import com.example.clone_daum.model.local.MyFavoriteDao
import brigitte.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.slf4j.LoggerFactory
import javax.inject.Inject
import com.example.clone_daum.R
import io.reactivex.Completable

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2019. 3. 4. <p/>
 */

class FavoriteModifyViewModel @Inject constructor(
    app: Application,
    private val favoriteDao: MyFavoriteDao
) : RecyclerViewModel<MyFavorite>(app), IFolder {
    companion object {
        private val logger = LoggerFactory.getLogger(FavoriteModifyViewModel::class.java)

        const val CMD_SELECT_ALL      = "select-all"
        const val CMD_SELECTED_DELETE = "selected-delete"
        const val CMD_POPUP_MENU      = "popup-menu"
    }

    val selectedList = arrayListOf<MyFavorite>()
    val visibleEmpty = ObservableInt(View.GONE) // 화면 깜박임 때문에 추가
    val enableDelete = ObservableBoolean(false)

    private var dp = CompositeDisposable()
    private var folderId: Int = 0

    private fun initItems(folderId: Int, notify: (() -> Unit)? = null) {
        if (logger.isDebugEnabled) {
            logger.debug("INIT ITEMS")
        }

        val fav = if (folderId == 0) {
            favoriteDao.selectShowAll()
        } else {
            favoriteDao.selectByFolderId(folderId)
        }

        // flowable 로 하면 디비 갱신 다시 쿼리를 전달 받아서 해주긴 하는데
        // touch helper 구조상 처음에만 쿼리를 전달 받도록 함
        dp.add(fav.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (logger.isDebugEnabled) {
                    logger.debug("FAVORITE MODIFY COUNT : ${it.size} ${it.hashCode()}")
                }

                visibleEmpty.set(if (it.isNotEmpty()) View.GONE else View.VISIBLE)
                items.set(it)

                notify?.invoke()
            }, {
                errorLog(it)
                snackbar(it)
            }))
    }

    fun init(folderId: Int) {
        this.folderId = folderId

        initItems(folderId)
        initItemTouchHelper(ItemMovedCallback { from, to ->
            items.get()?.let {
                if (logger.isDebugEnabled) {
                    logger.debug("SWAP INDEX $from -> $to")
                }

                val fromData = it[from]
                val toData   = it[to]

                fromData.swapDate(toData)

                dp.add(favoriteDao.update(fromData, toData)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe ({
                        if (logger.isDebugEnabled) {
                            logger.debug("UPDATED DATE")
                        }
                    }, { e ->
                        errorLog(e)
                        snackbar(e)
                    }))
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

        if (logger.isTraceEnabled) {
            logger.trace("FIRST WORD : $firstWord")
        }

        return firstWord
    }

    fun rowColor(state: Boolean) = if (state) {
        ContextCompat.getColor(app, R.color.alpha_orange)
    } else {
        ContextCompat.getColor(app, R.color.alpha_white)
    }

    fun toggleCheckbox(fav: MyFavorite) {
        fav.check.set(!fav.check.get())
    }

    // https://stackoverflow.com/questions/37582267/how-to-perform-two-way-data-binding-with-a-togglebutton
//    fun onCheckedChanged(view: CompoundButton, isChecked: Boolean) {
//        if (logger.isDebugEnabled) {
//            logger.debug("CHECKED CHANGED : ${view.id} = $isChecked")
//        }
//    }

    // https://stackoverflow.com/questions/37582267/how-to-perform-two-way-data-binding-with-a-togglebutton
    fun deleteList(state: Boolean, item: MyFavorite) {
        if (logger.isDebugEnabled) {
            logger.debug("ITEM state : $state, id: ${item._id} ($item)")
        }

        if (state) {
            selectedList.add(item)
        } else {
            selectedList.remove(item)
        }

        enableDelete.set(selectedList.size > 0)
    }

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // ICommandEvent
    //
    ////////////////////////////////////////////////////////////////////////////////////

    override fun command(cmd: String, data: Any) {
        when (cmd) {
            CMD_SELECT_ALL      -> selectAll()
            CMD_SELECTED_DELETE -> deleteSelectedItem()
            else                -> super.command(cmd, data)
        }
    }

    private fun selectAll() {
        items.get()?.toggleItems { it.check }
    }

    private fun deleteSelectedItem() {
        enableDelete.set(false)

        if (logger.isDebugEnabled) {
            logger.debug("DELETE SELECTED ITEMS (${selectedList.size})")
        }

        // 폴더의 경우 폴더명을 찾아서 해당 폴더를 가진 fav 를 모두 삭제 한다.
        val folderIdList = arrayListOf<Int>()
        selectedList.forEach {
            if (it.type == MyFavorite.T_FOLDER) {
                folderIdList.add(it._id)
            }
        }

        //  TODO zip 처리 필요

        if (folderIdList.size > 0) {
            dp.add(Completable.fromAction { favoriteDao.deleteByFolderIds(folderIdList) }
                .subscribeOn(Schedulers.io())
                .subscribe {
                    if (logger.isDebugEnabled) {
                        logger.debug("DELETE FAVORITE FOLDER LIST : $folderIdList")
                    }
                })
        }

        dp.add(favoriteDao.delete(selectedList)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (logger.isDebugEnabled) {
                    logger.debug("DELETED FAVORITE ITEMS")
                }

                // modify fragment 에서는 single 로 call 하고 있으므로
                // 화면을 갱신 시켜줘야 한다.

                initItems(folderId) {
                    adapter.get()?.notifyDataSetChanged()
                }
            }, ::errorLog))
    }

    fun updateFavorite(fav: MyFavorite, callback: (Boolean) -> Unit) {
        dp.add(favoriteDao.update(fav)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (logger.isDebugEnabled) {
                    logger.debug("UPDATED FAVORITE FOLDER : ${fav.name}")
                }

                callback.invoke(true)
            }, {
                errorLog(it)
                snackbar(it)

                callback.invoke(false)
            }))
    }

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // IFolder
    //
    ////////////////////////////////////////////////////////////////////////////////////

    override fun processFolder(data: Any) {
        val pair = data as Pair<String, MyFavorite>
        if (logger.isDebugEnabled) {
            logger.debug("UPDATE FAVORITE (OLD: ${pair.first}, NEW: ${pair.second.name})")
        }

        val fav = pair.second
        updateFavorite(fav) {
            finish(false)
        }
    }

    override fun hasFolder(name: String, callback: (Boolean) -> Unit, id: Int) {
        dp.add(favoriteDao.hasFolder(name, id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (logger.isDebugEnabled) {
                    if (it > 0) {
                        logger.debug("HAS FAVORITE FOLDER : $name ($it)")
                    }
                }

                callback(it > 0)
            }, {
                errorLog(it)
                callback(false)
            }))
    }

    override fun onCleared() {
        dp.dispose()

        super.onCleared()
    }
}
