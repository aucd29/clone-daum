package com.example.clone_daum.ui.browser.favorite

import android.app.Application
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
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
import kotlinx.android.synthetic.main.folder_dialog.view.*

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2019. 3. 4. <p/>
 */

class FavoriteViewModel @Inject constructor(application: Application
    , private val favoriteDao: MyFavoriteDao
) : RecyclerViewModel<MyFavorite>(application), ICommandEventAware, IFinishFragmentAware
    , IDialogAware, ISnackbarAware {
    companion object {
        private val mLog = LoggerFactory.getLogger(FavoriteViewModel::class.java)

        const val CMD_CHECKED_FOLDER     = "checked-folder"
        const val CMD_BRS_OPEN           = "brs-open"
        const val CMD_CHOOSE_FOLDER      = "choose-folder"
        const val CMD_SHOW_FOLDER_DIALOG = "show-folder-fragment"
    }

    override val commandEvent  = SingleLiveEvent<Pair<String, Any>>()
    override val finishEvent   = SingleLiveEvent<Void>()
    override val dialogEvent   = SingleLiveEvent<DialogParam>()
    override val snackbarEvent = SingleLiveEvent<String>()

    lateinit var dp: CompositeDisposable
    var selectedPosition: Int = 0

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // FAVORITE FRAGMENT
    //
    ////////////////////////////////////////////////////////////////////////////////////

    fun initShowAll(dp: CompositeDisposable) {
        this.dp = dp

        initAdapter(arrayOf("favorite_item_folder", "favorite_item"))
        dp.add(favoriteDao.selectShowAll().subscribe {
            if (mLog.isDebugEnabled) {
                mLog.debug("FAVORITE COUNT : ${it.size}")
            }

            items.set(it)
        })
    }

    fun initShowFolder(dp: CompositeDisposable) {
        this.dp = dp

        initAdapter(arrayOf("favorite_item_folder", "favorite_item"))
        dp.add(favoriteDao.selectShowFolder().subscribe {
            if (mLog.isDebugEnabled) {
                mLog.debug("FAVORITE COUNT : ${it.size}")
            }

            items.set(it)
        })
    }

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // FAVORITE FRAGMENT, FOLDER FRAGMENT
    //
    ////////////////////////////////////////////////////////////////////////////////////

    fun insertFolder(folderName: String, fromFolderFragment: Boolean) {
        dp.add(favoriteDao.insert(MyFavorite(folderName, favType = MyFavorite.T_FOLDER))
            .subscribeOn(Schedulers.io())
            .subscribe({
                if (fromFolderFragment) {
                    reloadFolderItems()
                } else {
                    initShowAll(dp)
                }
            }, ::snackbar))
    }

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // FOLDER FRAGMENT
    //
    ////////////////////////////////////////////////////////////////////////////////////

    fun initFolder(dp: CompositeDisposable) {
        this.dp = dp

        initAdapter("folder_item")
        reloadFolderItems()
    }

    fun reloadFolderItems() {
        if (mLog.isDebugEnabled) {
            mLog.debug("RELOAD FOLDER LIST")
        }

        dp.add(favoriteDao.selectShowFolder()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                if (mLog.isDebugEnabled) {
                    mLog.debug("FOLDER COUNT : ${it.size}")
                }

                // 첫 번째 위치에 즐겨찾기를 추가
                val list = it.toMutableList()
                list.add(0, MyFavorite(string(R.string.favorite_title), favType = MyFavorite.T_FOLDER))

                items.set(list)
            })
    }

    fun firstWord(name: String): String {
        val firstWord = name.substring(0, 1)

        if (mLog.isDebugEnabled) {
            mLog.debug("FIRST WORD : $firstWord")
        }

        return firstWord
    }

    fun currentFolder() = selectedPosition to items.get()!!.get(selectedPosition).name

    override fun commandEvent(cmd: String, data: Any) {
        when (cmd) {
            CMD_CHECKED_FOLDER -> {
                // 선택 된 위치 값을 표현 하기 위해 화면 갱신을 줌
                val oldPos  = selectedPosition
                selectedPosition = data as Int

                adapter.get()?.let {
                    it.notifyItemChanged(oldPos)
                    it.notifyItemChanged(selectedPosition)
                }
            }
        }

        super.commandEvent(cmd, data)
    }
}

//
//
//

object FavoriteAddFolder {
    fun showDialog(context: Context, viewModel: FavoriteViewModel, fromFolderFragment: Boolean) {
        // FIXME 추후 수정해야할 부분

        val view = LayoutInflater.from(context).inflate(R.layout.folder_dialog, null)
        val params = DialogParam(view = view)

        viewModel.dialog(params)

        view.apply {
            ok.isEnabled = !folder_name.text.isNullOrEmpty()

            folder_name.addTextChangedListener(object: TextWatcher {
                override fun afterTextChanged(s: Editable?) { }
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    ok.isEnabled = !folder_name.text.isNullOrEmpty()
                }
            })

            ok.setOnClickListener {
                params.dialog?.dismiss()
                viewModel.insertFolder(folder_name.text.toString(), fromFolderFragment)
            }

            cancel.setOnClickListener { params.dialog?.dismiss() }
        }
    }
}