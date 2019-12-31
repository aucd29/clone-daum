package com.example.clone_daum.ui.main.setting.filemanager

import android.app.Application
import android.os.Environment
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import brigitte.RecyclerViewModel
import brigitte.edit
import brigitte.prefs
import brigitte.string
import brigitte.widget.viewpager.OffsetDividerItemDecoration
import com.example.clone_daum.R
import com.example.clone_daum.model.local.FileInfo
import com.example.clone_daum.ui.main.setting.SettingViewModel
import org.slf4j.LoggerFactory
import java.io.File
import javax.inject.Inject

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2019-12-18 <p/>
 */

class DownloadPathViewModel @Inject constructor(
    app: Application
) : RecyclerViewModel<FileInfo>(app) {

    val title       = ObservableField(string(R.string.setting_privacy))
    val currentRoot = MutableLiveData<String>()
    val itemDecoration = ObservableField(OffsetDividerItemDecoration(app,
            R.drawable.shape_divider_gray,  0, 0))

    init {
        val envDownloadPath = Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_DOWNLOADS).toString()
        val downloadPath = app.prefs().getString(SettingViewModel.PREF_DOWNLOADPATH, envDownloadPath)

        currentRoot.value = downloadPath

        initAdapter(R.layout.download_path_up_item,
            R.layout.download_path_file_item)
    }

    fun loadFileList() {
        val itemList = arrayListOf<FileInfo>()
        val current = currentRoot.value!!
        itemList.add(FileInfo(0, File(""), FileInfo.T_UP))

        if (mLog.isDebugEnabled) {
            mLog.debug("LOAD FILE LIST $current")
        }

        var i = 1
        val fp = File(current)
        fp.listFiles()?.forEach {
            if (mLog.isTraceEnabled) {
                mLog.trace("FILE PATH ${it.name}")
            }

            if (it.isDirectory) {
                itemList.add(FileInfo(i++, it))
            }
        }

        items.set(itemList)
    }

    override fun command(cmd: String, data: Any) {
        when (cmd) {
            CMD_CHOOSE_DOWNLOAD_PATH -> {
                app.prefs().edit {
                    if (mLog.isDebugEnabled) {
                        mLog.debug("CMD_CHOOSE_DOWNLOAD_PATH ${currentRoot.value}")
                    }

                    putString(SettingViewModel.PREF_DOWNLOADPATH, currentRoot.value!!)
                }

                finish()
            }
            CMD_LEAVE_DIR -> {
                val root = currentRoot.value!!

                currentRoot.value = File(root).parent
            }
            CMD_ENTER_DIR -> {
                val root = currentRoot.value!!
                val file = data as FileInfo

                currentRoot.value = File(root, file.fp.name).absolutePath
            }

            else -> super.command(cmd, data)
        }
    }

    companion object {
        private val mLog = LoggerFactory.getLogger(DownloadPathViewModel::class.java)

        const val CMD_CHOOSE_DOWNLOAD_PATH = "choose-download-path"
        const val CMD_LEAVE_DIR = "leave-dir"
        const val CMD_ENTER_DIR = "enter-dir"
    }
}