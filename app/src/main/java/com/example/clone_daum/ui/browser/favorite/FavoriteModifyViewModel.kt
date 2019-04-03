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
    , ISnackbarAware, IFolder {
    companion object {
        private val mLog = LoggerFactory.getLogger(FavoriteModifyViewModel::class.java)

        const val CMD_SELECT_ALL      = "select-all"
        const val CMD_SELECTED_DELETE = "selected-delete"
        const val CMD_POPUP_MENU      = "popup-menu"
    }

    override val commandEvent  = SingleLiveEvent<Pair<String, Any>>()
    override val finishEvent   = SingleLiveEvent<Void>()
    override val snackbarEvent = SingleLiveEvent<String>()

    private lateinit var mDisposable: CompositeDisposable
    private var mFolderName: String? = null

    val selectedList = arrayListOf<MyFavorite>()
    val visibleEmpty = ObservableInt(View.GONE) // 화면 깜박임 때문에 추가
    val enableDelete = ObservableBoolean(false)

    private fun initItems(folder: String?) {
        if (mLog.isDebugEnabled) {
            mLog.debug("INIT ITEMS")
        }

        // flowable 로 하면 디비 갱신 다시 쿼리를 전달 받아서 해주긴 하는데
        // touch helper 구조상 처음에만 쿼리를 전달 받도록 함
        mDisposable.add(favoriteDao.selectShowAll()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (mLog.isDebugEnabled) {
                    mLog.debug("FAVORITE MODIFY COUNT : ${it.size} ${it.hashCode()}")
                }

                visibleEmpty.set(if (it.size > 0) View.GONE else View.VISIBLE)
                items.set(it)
            }, ::snackbar))
    }

    fun init(folder: String?, dp: CompositeDisposable) {
        mDisposable = dp
        mFolderName = folder

        initAdapter(arrayOf("favorite_modify_item_folder", "favorite_modify_item"))
        adapter.get()?.isNotifyAll = true

        initItems(folder)
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

        if (mLog.isTraceEnabled) {
            mLog.trace("FIRST WORD : $firstWord")
        }

        return firstWord
    }

    fun rowColor(state: Boolean) = if (state) {
        ContextCompat.getColor(app, R.color.alpha_orange)
    } else {
        ContextCompat.getColor(app, R.color.alpha_white)
    }

    // https://stackoverflow.com/questions/37582267/how-to-perform-two-way-data-binding-with-a-togglebutton
//    fun onCheckedChanged(view: CompoundButton, isChecked: Boolean) {
//        if (mLog.isDebugEnabled) {
//            mLog.debug("CHECKED CHANGED : ${view.id} = $isChecked")
//        }
//    }

    // https://stackoverflow.com/questions/37582267/how-to-perform-two-way-data-binding-with-a-togglebutton
    fun deleteList(state: Boolean, item: MyFavorite) {
        if (mLog.isDebugEnabled) {
            mLog.debug("ITEM state : $state, id: ${item._id} ($item)")
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

    override fun commandEvent(cmd: String, data: Any) {
        when (cmd) {
            CMD_SELECT_ALL      -> selectAll()
            CMD_SELECTED_DELETE -> deleteSelectedItem()
            else -> super.commandEvent(cmd, data)
        }
    }

    private fun selectAll() {
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

    private fun deleteSelectedItem() {
        enableDelete.set(false)

        if (mLog.isDebugEnabled) {
            mLog.debug("DELETE SELECTED ITEMS (${selectedList.size})")
        }

        // 폴더의 경우 폴더명을 찾아서 해당 폴더를 가진 fav 를 모두 삭제 한다.
        val folderNameList = arrayListOf<String>()
        selectedList.forEach {
            if (it.type() == MyFavorite.T_FOLDER) {
                folderNameList.add(it.name)
            }
        }

        if (folderNameList.size > 0) {
            mDisposable.add(Completable.fromAction { favoriteDao.deleteByFolderNames(folderNameList) }
                .subscribeOn(Schedulers.io())
                .subscribe {
                    if (mLog.isDebugEnabled) {
                        mLog.debug("DELETE FAVORITE FOLDER LIST : $folderNameList")
                    }
                })
        }

        mDisposable.add(favoriteDao.delete(selectedList)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                if (mLog.isDebugEnabled) {
                    mLog.debug("DELETED FAVORITE ITEMS")
                }

                // modify fragment 에서는 single 로 call 하고 있으므로
                // 화면을 갱신 시켜줘야 한다.
                initItems(mFolderName)
            })
    }

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // IFolder
    //
    ////////////////////////////////////////////////////////////////////////////////////

    override fun updateFolder(favorite: Any, fromFolderFragment: Boolean) {
        if (mLog.isDebugEnabled) {
            mLog.debug("UPDATE FAVORITE")
        }

        val fav = favorite as MyFavorite
        mDisposable.add(favoriteDao.update(fav)
            .subscribeOn(Schedulers.io())
            .subscribe({
                initItems(mFolderName)
            }, ::snackbar))
    }

    override fun hasFolder(name: String, callback: (Boolean) -> Unit, id: Int) {
        mDisposable.add(favoriteDao.hasFolder(name, id)
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
