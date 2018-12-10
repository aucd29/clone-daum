package com.example.clone_daum.model

import com.example.clone_daum.model.local.PopularKeywordDao
import com.example.clone_daum.model.local.SearchHistoryDao
import com.example.clone_daum.model.local.TabData
import com.example.common.jsonParse
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2018. 11. 27. <p/>
 */

//@Singleton
//class AssetRepository @Inject constructor(
//    private val tabList: Observable<ByteArray>,
//    private val disposable: CompositeDisposable
//) {
//    fun tabList(callback: (List<TabData>) -> Unit) {
//        disposable.add(tabList.subscribe {
//            callback(it.jsonParse())
//        })
//    }
//
//    fun tabList2() = tabList
//        .observeOn(Schedulers.computation())
//}

@Singleton
class DbRepository @Inject constructor(
    val searchHistoryDao: SearchHistoryDao,
    val popularKeywordDao: PopularKeywordDao
) {
}

//@Singleton
//class Repository @Inject constructor() {
//}