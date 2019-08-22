package com.example.clone_daum.model.remote

import brigitte.*
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import org.slf4j.LoggerFactory

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2019. 1. 9. <p/>
 */

// 데이터 구조
//
//[{
//    "type": "hotissue",
//    "list": [{
//        "rank": "1",
//        "keyword": "황미나",
//        "value": "162",
//        "type": "+",
//        "param": "rtmaxcoll=1TH",
//        "linkurl": "https://m.search.daum.net/search?w=tot&q=%ED%99%A9%EB%AF%B8%EB%82%98&amp;DA=ATG&amp;nil_mtopsearch=issuekwd&amp;logical=issue&amp;pin=issue"
//    }, {
//        "rank": "2",
//        "keyword": "김한길",
//        "value": "91",
//        "type": "+",
//        "param": "rtmaxcoll=1TH",
//        "linkurl": "https://m.search.daum.net/search?w=tot&q=%EA%B9%80%ED%95%9C%EA%B8%B8&amp;DA=ATG&amp;nil_mtopsearch=issuekwd&amp;logical=issue&amp;pin=issue"
//    }]
//}]

//"type": "news",
//"list": [{
//    "linkurl_mobile": "https://m.search.daum.net/search?w=tot&q=%EC%B4%9B%EB%B6%88%EC%A7%91%ED%9A%8C",
//    "linkurl_pc": "https://search.daum.net/search?w=tot&q=%EC%B4%9B%EB%B6%88%EC%A7%91%ED%9A%8C",
//    "param": "pin=news",
//    "param_pc": "f=news&rtmaxcoll=EOA,1TH",
//    "utf8Keyword": "%EC%B4%9B%EB%B6%88%EC%A7%91%ED%9A%8C",
//    "rank": "1",
//    "param_mobile": "pin=news",
//    "linkurl": "https://m.search.daum.net/search?w=tot&q=%EC%B4%9B%EB%B6%88%EC%A7%91%ED%9A%8C&amp;DA=QWG&amp;pin=news",
//    "keyword": "촛불집회",
//    "type": "+",
//    "value": "61",
//    "euckrKeyword": "%C3%D0%BA%D2%C1%FD%C8%B8"
//}


@JsonIgnoreProperties
data class RealtimeIssue (
    @JsonProperty("rank")
    val index: String,
    @JsonProperty("linkurl")
    val url: String,
    @JsonProperty("keyword")
    val text: String,
    val type: String,
    val value: String
) : IRecyclerDiff {
    override fun itemSame(item: IRecyclerDiff): Boolean  =
        this.index == (item as RealtimeIssue).index

    override fun contentsSame(item: IRecyclerDiff): Boolean {
        val newItem = item as RealtimeIssue
        return url == newItem.url && text == newItem.text
    }
}

data class RealtimeIssueType(
    val type: String,
    val list: List<RealtimeIssue>
)
