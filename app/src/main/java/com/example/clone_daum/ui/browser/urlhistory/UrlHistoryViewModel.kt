package com.example.clone_daum.ui.browser.urlhistory

import android.app.Application
import com.example.clone_daum.R
import com.example.clone_daum.model.local.UrlHistory
import com.example.clone_daum.model.local.UrlHistoryDao
import com.example.common.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.slf4j.LoggerFactory
import javax.inject.Inject

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2019. 4. 10. <p/>
 */

class UrlHistoryViewModel @Inject constructor(application: Application
    , private val mUrlHistoryDao: UrlHistoryDao
) : RecyclerViewModel<UrlHistory>(application) {

    companion object {
        private val mLog = LoggerFactory.getLogger(UrlHistoryViewModel::class.java)

        const val CMD_BRS_OPEN           = "brs-open"
        const val CMD_URL_HISTORY_MODIFY = "url-history-modify"
        const val CMD_TOGGLE             = "toggle"
    }

    private lateinit var mDisposable: CompositeDisposable
    private val dateCal = DateCalculator<UrlHistory>()

    init {
        dateCal.dateFormat(string(R.string.history_date_format))
    }

    fun init(dp: CompositeDisposable) {
        mDisposable = dp
        initAdapter("url_history_item", "url_history_expandable_item")
    }

    fun initItems() {
        val historyLabels = stringArray(R.array.history_labels)

        mDisposable.add(mUrlHistoryDao.select()
            .subscribeOn(Schedulers.io())
            .map {
                if (mLog.isDebugEnabled) {
                    mLog.debug("RAW HISTORY DATA (${it.size})")
                }

                it.forEach {
                    dateCal.process(it)
                }

                val newList = arrayListOf<UrlHistory>()
                (DateCalculator.K_TODAY..DateCalculator.K_OTHER).forEach {
                    dateCal.mapData.get(it)?.let { list ->
                        val dateFormat    = dateCal.dateFormatString(it)?.let { it } ?: ""
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
                newList.get(0)?.let {
                    it.toggle(newList)
                }

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


    ////////////////////////////////////////////////////////////////////////////////////
    //
    // COMMAND
    //
    ////////////////////////////////////////////////////////////////////////////////////

    override fun command(cmd: String, data: Any) {
        when (cmd) {
            CMD_TOGGLE -> toggle(data as UrlHistory)
            else       -> super.command(cmd, data)
        }
    }

    private fun toggle(data: UrlHistory) {
        items.get()?.let {
            data.toggle(it, adapter.get())

            if (mLog.isDebugEnabled) {
                mLog.debug("URL HISTORY ITEM (${items.get()?.size})")
            }
        } ?: mLog.error("ERROR: ITEMS == NULL")
    }
}