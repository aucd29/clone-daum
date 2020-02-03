package com.example.clone_daum.ui.browser.urlhistory

import android.app.Application
import androidx.databinding.ObservableBoolean
import com.example.clone_daum.R
import com.example.clone_daum.model.local.UrlHistory
import com.example.clone_daum.model.local.UrlHistoryDao
import brigitte.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.slf4j.LoggerFactory
import javax.inject.Inject


/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2019. 4. 10. <p/>
 */

class UrlHistoryViewModel @Inject constructor(
    private val urlHistoryDao: UrlHistoryDao,
    app: Application
) : RecyclerViewModel<UrlHistory>(app) {

    companion object {
        private val logger = LoggerFactory.getLogger(UrlHistoryViewModel::class.java)

        const val CMD_BRS_OPEN          = "brs-open"
        const val CMD_MODIFY            = "url-history-modify"
        const val CMD_DELETE            = "url-history-delete"
        const val CMD_EXPANDABLE_TOGGLE = "url-history-expandable-toggle"
        const val CMD_CHECKBOX_TOGGLE   = "url-history-checkbox-toggle"
    }

    val editMode     = ObservableBoolean(false)
    val enableDelete = ObservableBoolean(false)
    val selectedList = arrayListOf<UrlHistory>()

    private val dp           = CompositeDisposable()
    private val dateCal      = DateCalculator<UrlHistory>()
    private var blockingFlag = true

    init {
        dateCal.dateFormat(string(R.string.history_date_format))
    }

    fun toggleCheckbox(check: ObservableBoolean) {
        check.toggle()
    }

    fun initItems() {
        val historyLabels = app.stringArray(R.array.history_labels)

        dp.add(urlHistoryDao.select()
            .subscribeOn(Schedulers.io())
            .filter { blockingFlag }
            .map {
                // BRS 에서 finished 가 뒤늦게 들어온 경우
                // flowable 로직을 다시 타게 되는데 그리되면 toggle 을 다시 타는 문제가 존재 한다.
                // 그래서 일단 filter 를 둠
                blockingFlag = false

                if (logger.isDebugEnabled) {
                    logger.debug("RAW HISTORY DATA (${it.size})")
                }

                dateCal.clear()

                it.forEach { f -> dateCal.process(f) }
                val newList = arrayListOf<UrlHistory>()
                (DateCalculator.K_TODAY..DateCalculator.K_OTHER).forEach {
                    dateCal.mapData[it]?.let { list ->
                        val dateFormat    = dateCal.dateFormatString(it)?.let { dts -> dts } ?: ""
                        val title = String.format(historyLabels[it], dateFormat)

                        if (logger.isDebugEnabled) {
                            logger.debug("TITLE : $title")
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
                if (logger.isDebugEnabled) {
                    logger.debug("URL HISTORY ITEM (${it.size})")
                }

                items.set(it)
            }, ::errorLog))
    }

    fun deleteList(state: Boolean, item: UrlHistory) {
        if (logger.isDebugEnabled) {
            logger.debug("ITEM state: $state, id: ${item._id} $item")
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
        app.vibrate(10L)

        items.get()?.let {
            data.toggle(it, adapter.get())

            if (logger.isDebugEnabled) {
                logger.debug("URL HISTORY ITEM (${items.get()?.size})")
            }
        } ?: logger.error("ERROR: ITEMS == NULL")
    }

    private fun editModeToggle() {
        editMode.set(true)
    }

    private fun checkboxToggle() {
        items.get()?.toggleExpandableItems(UrlHistory.T_SEPERATOR) { it.check }
    }

    private fun deleteUrlHistories() {
        blockingFlag = true

        dp.add(urlHistoryDao.delete(selectedList)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (logger.isDebugEnabled) {
                    logger.debug("DELETED OK")
                }
            }, ::errorLog))
    }

    override fun onCleared() {
        dp.dispose()
        super.onCleared()
    }
}