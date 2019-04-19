package com.example.clone_daum.ui.browser.favorite

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.Fragment
import com.example.clone_daum.R
import com.example.clone_daum.model.local.MyFavorite
import com.example.common.DialogParam
import com.example.common.dialog
import com.example.common.showKeyboard
import kotlinx.android.synthetic.main.folder_dialog.view.*

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2019. 4. 3. <p/>
 */

// IFolder 는 FavoriteViewModel, FavoriteModifyViewModel, FolderViewModel 에서 사용되며
// 각자 다른 형태로 구현된다.
interface IFolder {
    fun hasFolder(name: String, callback: (Boolean) -> Unit, id: Int = -1)
    fun processFolder(folderName: Any)
}

object FolderDialog {
    fun show(frgmt: Fragment, viewModel: IFolder, favorite: MyFavorite? = null) {
        // FIXME 추후 수정해야할 부분
        // 일단은 kotlin extension 을 테스트 하기 위한 코드

        val view = LayoutInflater.from(frgmt.requireContext()).inflate(R.layout.folder_dialog, null)
        val params = DialogParam(view = view)

        frgmt.dialog(params)

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

                    viewModel.hasFolder(folder_name.text.toString(), {
                        // 이미 동일 명의 폴더가 존재하면 경고 문구를 표기하고 버튼을 비활성화 시켜야 한다.
                        ok.isEnabled = !it
                        has_folder_name.visibility = if (it) View.VISIBLE else View.INVISIBLE
                    }, if (favorite == null) -1 else favorite._id)
                }
            })
            folder_name.showKeyboard()
            favorite?.let { folder_name.setText(favorite.name) }

            ok.setOnClickListener {
                params.dialog?.dismiss()

                if (favorite == null) {
                    viewModel.processFolder(folder_name.text.toString())
                } else {
                    val oldName = favorite.name
                    favorite.name = folder_name.text.toString()

                    viewModel.processFolder(oldName to favorite)
                }
            }

            cancel.setOnClickListener {
                params.dialog?.dismiss()
            }
        }
    }
}