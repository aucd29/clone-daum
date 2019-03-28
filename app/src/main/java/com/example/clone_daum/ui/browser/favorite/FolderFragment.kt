package com.example.clone_daum.ui.browser.favorite

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import com.example.clone_daum.R
import com.example.clone_daum.databinding.FolderFragmentBinding
import com.example.common.BaseDaggerFragment
import com.example.common.DialogParam
import com.example.common.finish
import com.example.common.showKeyboard
import dagger.android.ContributesAndroidInjector
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.folder_dialog.view.*
import org.slf4j.LoggerFactory

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2019. 3. 8. <p/>
 */

class FolderFragment
    : BaseDaggerFragment<FolderFragmentBinding, FavoriteViewModel>() {

    override fun initViewBinding() {
    }

    override fun initViewModelEvents() {
        val position = arguments!!.getInt("position", 0)

        mViewModel.initFolder(mDisposable, position)
    }

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // ICommandEventAware
    //
    ////////////////////////////////////////////////////////////////////////////////////

    override fun onCommandEvent(cmd: String, data: Any) {
        FavoriteViewModel.apply {
            when (cmd) {
                CMD_SHOW_FOLDER_DIALOG -> FolderDialog.show(requireContext(), mViewModel, true)
                CMD_CHANGE_FOLDER      -> changeFolderName()
            }
        }
    }

    private fun changeFolderName() {
        val frgmt = parentFragment
        if (frgmt is FavoriteAddFragment) {
            val pair = mViewModel.currentFolder()
            frgmt.changeFolderName(pair.first, pair.second)
        }

        finish()
    }

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // MODULE
    //
    ////////////////////////////////////////////////////////////////////////////////////

    @dagger.Module
    abstract class Module {
        @ContributesAndroidInjector
        abstract fun contributeInjector(): FolderFragment
    }
}

////////////////////////////////////////////////////////////////////////////////////
//
// FolderDialog
//
////////////////////////////////////////////////////////////////////////////////////

object FolderDialog {
    private val mLog = LoggerFactory.getLogger(FolderDialog::class.java)

    fun show(context: Context, viewModel: FavoriteViewModel, fromFolderFragment: Boolean) {
        // FIXME 추후 수정해야할 부분

        val view = LayoutInflater.from(context).inflate(R.layout.folder_dialog, null)
        val params = DialogParam(view = view)
        val disposable = CompositeDisposable()

        viewModel.dialog(params)


        view.apply {
            ok.isEnabled = !folder_name.text.isNullOrEmpty()

            folder_name.addTextChangedListener(object: TextWatcher {
                override fun afterTextChanged(s: Editable?) { }
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (folder_name.text.isNullOrEmpty()) {
                        ok.isEnabled = false
                        return
                    }

                    viewModel.hasFolder(disposable, folder_name.text.toString()) {
                        // 이미 동일 명의 폴더가 존재하면 경고 문구를 표기하고 버튼을 비활성화 시켜야 한다.
                        ok.isEnabled = !it
                        has_folder_name.visibility = if (it) View.VISIBLE else View.INVISIBLE
                    }
                }
            })
            folder_name.showKeyboard()

            ok.setOnClickListener {
                disposable.dispose()
                params.dialog?.dismiss()
                viewModel.insertFolder(folder_name.text.toString(), fromFolderFragment)
            }

            cancel.setOnClickListener {
                disposable.dispose()
                params.dialog?.dismiss()
            }
        }
    }
}