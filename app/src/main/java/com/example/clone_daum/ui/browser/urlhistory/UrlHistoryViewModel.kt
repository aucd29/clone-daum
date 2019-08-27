package com.example.clone_daum.ui.browser.urlhistory

import android.app.Application
import androidx.databinding.ObservableBoolean
import com.example.clone_daum.R
import com.example.clone_daum.model.local.UrlHistory
import com.example.clone_daum.model.local.UrlHistoryDao
import brigitte.*
import brigitte.viewmodel.string
import brigitte.viewmodel.stringArray
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.slf4j.LoggerFactory
import javax.inject.Inject


/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2019. 4. 10. <p/>
 */

class UrlHistoryViewModel @Inject constructor(application: Application
    , private val mUrlHistoryDao: UrlHistoryDao
) : RecyclerViewModel<UrlHistory>(application) {

    companion object {
        private val mLog = LoggerFactory.getLogger(UrlHistoryViewModel::class.java)

        const val CMD_BRS_OPEN          = "brs-open"
        const val CMD_MODIFY            = "url-history-modify"
        const val CMD_DELETE            = "url-history-delete"
        const val CMD_EXPANDABLE_TOGGLE = "url-history-expandable-toggle"
        const val CMD_CHECKBOX_TOGGLE   = "url-history-checkbox-toggle"
    }

    private lateinit var mDisposable: CompositeDisposable
    private val dateCal   = DateCalculator<UrlHistory>()
    private var blockingFlag = true

    val editMode     = ObservableBoolean(false)
    val enableDelete = ObservableBoolean(false)
    val selectedList = arrayListOf<UrlHistory>()

    init {
        dateCal.dateFormat(string(R.string.history_date_format))
    }

    fun init(dp: CompositeDisposable) {
        mDisposable = dp
        initAdapter(R.layout.url_history_item, R.layout.url_history_expandable_item)
    }

    fun toggleCheckbox(check: ObservableBoolean) {
        check.toggle()
    }

    fun initItems() {
        val historyLabels = stringArray(R.array.history_labels)

        mDisposable.add(mUrlHistoryDao.select()
            .subscribeOn(Schedulers.io())
            .filter { blockingFlag }
            .map {
                // BRS 에서 finished 가 뒤늦게 들어온 경우
                // flowable 로직을 다시 타게 되는데 그리되면 toggle 을 다시 타는 문제가 존재 한다.
                // 그래서 일단 filter 를 둠
                blockingFlag = false

                if (mLog.isDebugEnabled) {
                    mLog.debug("RAW HISTORY DATA (${it.size})")
                }

                dateCal.clear()

                it.forEach { f -> dateCal.process(f) }
                val newList = arrayListOf<UrlHistory>()
                (DateCalculator.K_TODAY..DateCalculator.K_OTHER).forEach {
                    dateCal.mapData[it]?.let { list ->
                        val dateFormat    = dateCal.dateFormatString(it)?.let { dts -> dts } ?: ""
                        val title = String.format(historyLabels[it], dateFormat)

                        if (mLog.isDebugEnabled) {
                            mLog.debug("TITLE : $title")
                        }

                        newList.add(UrlHistory(title, null, 0).apply {
                            type      = UrlHistory.T_SEPERATOR
                            childList = list
                        })
                    }
                }

                // 0번째 아이템은 화면에 보이도록
                newList[0].toggle(newList, adapter.get())

                newList
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (mLog.isDebugEnabled) {
                    mLog.debug("URL HISTORY ITEM (${it.size})")
                }

                items.set(it)
            }, ::errorLog))
    }

    fun deleteList(state: Boolean, item: UrlHistory) {
        if (mLog.isDebugEnabled) {
            mLog.debug("ITEM state: $state, id: ${item._id} $item")
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
    // COMMAND
    //
    ////////////////////////////////////////////////////////////////////////////////////

    override fun command(cmd: String, data: Any) {
        when (cmd) {
            CMD_MODIFY            -> editModeToggle()
            CMD_DELETE            -> deleteUrlHistories()
            CMD_EXPANDABLE_TOGGLE -> toggle(data as UrlHistory)
            CMD_CHECKBOX_TOGGLE   -> checkboxToggle()
            else -> super.command(cmd, data)
        }
    }

    private fun toggle(data: UrlHistory) {
        vibrate(10L)

        items.get()?.let {
            data.toggle(it, adapter.get())

            if (mLog.isDebugEnabled) {
                mLog.debug("URL HISTORY ITEM (${items.get()?.size})")
            }
        } ?: mLog.error("ERROR: ITEMS == NULL")
    }

    private fun editModeToggle() {
        editMode.set(true)
    }

    private fun checkboxToggle() {
        items.get()?.toggleExpandableItems(UrlHistory.T_SEPERATOR) { it.check }
    }

    private fun deleteUrlHistories() {
        blockingFlag = true

        mDisposable.add(mUrlHistoryDao.delete(selectedList)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (mLog.isDebugEnabled) {
                    mLog.debug("DELETED OK")
                }
            }, ::errorLog))
    }
}