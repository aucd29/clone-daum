package com.example.clone_daum.ui.browser.favorite

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import com.example.clone_daum.R
import com.example.clone_daum.databinding.FolderFragmentBinding
import com.example.common.BaseDaggerFragment
import com.example.common.DialogParam
import dagger.Module
import dagger.android.ContributesAndroidInjector
import kotlinx.android.synthetic.main.folder_dialog.view.*

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2019. 3. 8. <p/>
 */

class FolderFragment : BaseDaggerFragment<FolderFragmentBinding, FavoriteViewModel>() {
    override fun initViewBinding() {

    }

    override fun initViewModelEvents() {
        mViewModel.initFolder(mDisposable)
    }

    override fun onCommandEvent(cmd: String, data: Any) {
        FavoriteViewModel.apply {
            when (cmd) {
                CMD_ADDED_FOLDER -> {

                    // FIXME 추후 수정해야할 부분

                    val view = LayoutInflater.from(context).inflate(R.layout.folder_dialog, null)
                    val params = DialogParam(view = view)

                    mViewModel.dialog(params)

                    view.apply {
                        ok.isEnabled = !view.folder_name.text.isNullOrEmpty()

                        folder_name.addTextChangedListener(object: TextWatcher {
                            override fun afterTextChanged(s: Editable?) { }
                            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
                            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                                ok.isEnabled = !folder_name.text.isNullOrEmpty()
                            }
                        })

                        ok.setOnClickListener {
                            params.dialog?.dismiss()
                            mViewModel.addFolder(folder_name.text.toString())
                        }

                        cancel.setOnClickListener { params.dialog?.dismiss() }
                    }
                }
            }
        }
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