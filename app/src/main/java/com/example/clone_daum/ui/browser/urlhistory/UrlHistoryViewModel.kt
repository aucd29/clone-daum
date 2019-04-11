package com.example.clone_daum.ui.browser.urlhistory

import android.app.Application
import com.example.clone_daum.R
import com.example.clone_daum.model.local.UrlHistory
import com.example.clone_daum.model.local.UrlHistoryDao
import com.example.common.DateCalculator
import com.example.common.RecyclerViewModel
import com.example.common.lastTimeToday
import com.example.common.string
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.slf4j.LoggerFactory
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2019. 4. 10. <p/>
 */

class UrlHistoryViewModel @Inject constructor(application: Application
    , private val mUrlHistoryDao: UrlHistoryDao
) : RecyclerViewModel<UrlHistory>(application) {

    companion object {
        private val mLog = LoggerFactory.getLogger(UrlHistoryViewModel::class.java)

        const val CMD_URL_HISTORY_MODIFY = "url-history-modify"
    }

    private lateinit var mDisposable: CompositeDisposable
    private val dateCal = DateCalculator<UrlHistory>()

    init {
        dateCal.dateFormat(string(R.string.history_date_format))
    }

    fun init(dp: CompositeDisposable) {
        mDisposable = dp
        initAdapter("url_history_guide_item")
    }

    fun initItems() {
        mDisposable.add(mUrlHistoryDao.select()
            .subscribeOn(Schedulers.io())
            .map {
                it.forEach {
                    dateCal.process(it)
                }

            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({


            }, {
                if (mLog.isDebugEnabled) {
                    it.printStackTrace()
                }

                mLog.error("ERROR: ${it.message}")
            }))
    }
}