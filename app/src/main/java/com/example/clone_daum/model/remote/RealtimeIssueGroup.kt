package com.example.clone_daum.model.remote

import com.example.common.BaseXPath
import com.example.common.IRecyclerDiff
import com.example.common.urldecode
import org.slf4j.LoggerFactory

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2019. 1. 9. <p/>
 */

data class RealtimeIssue (
    val index: Int,
    val url: String,
    var text: String
) : IRecyclerDiff {
    override fun compare(item: IRecyclerDiff): Boolean {
        val newItem = item as RealtimeIssue
        return url == newItem.url && text == newItem.text
    }
}

class RealtimeIssueParser: BaseXPath() {
    companion object {
        private val mLog = LoggerFactory.getLogger(RealtimeIssueParser::class.java)
        private const val REMOVE_WORD    = " 이슈검색어"
        private const val REALTIME_ISSUE = "실시간이슈"
    }

    val realtimeIssueList = arrayListOf<Pair<String, List<RealtimeIssue>>>()

// 파싱을 방지하려고 url 을 이래 놓은건가?
// --
// SAMPLE DATA
// --
//    <div id="footerHotissueRankingDiv_channel_news1">
//    <div class='keyissue_area '>
//    <strong class="screen_out">전체 이슈검색어</strong>
//    <ul class="list_issue">
//    <li><a href="https://m.search.daum.net/search?w=tot&q=%EC%A7%84%ED%98%95&amp;DA=13H&amp;nil_mtopsearch=issuekwd&amp;logical=issue&amp;pin=issue" class="link_issue"><em class="num_issue">1</em><span class="screen_out">위</span><span class="txt_issue">진형</span></a></li>
//    </ul>
//    <ul class="list_issue">
//    <li><a href="https://m.search.daum.net/search?w=tot&q=%ED%99%8D%EC%88%98%ED%98%84&amp;DA=13H&amp;nil_mtopsearch=issuekwd&amp;logical=issue&amp;pin=issue" class="link_issue"><em class="num_issue">6</em><span class="screen_out">위</span><span class="txt_issue">홍수현</span></a></li>
//    </ul>
//    </div>
//
//    <div class='keyissue_area on'>
//    <strong class="screen_out">뉴스 이슈검색어</strong>
//    <ul class="list_issue">
//    <li><a href="https://m.search.daum.net/search?w=tot&q=%EC%B2%AD%EB%85%84+%EB%82%B4%EC%9D%BC%EC%B1%84%EC%9B%80%EA%B3%B5%EC%A0%9C&amp;DA=13I&amp;pin=news" class="link_issue"><em class="num_issue">1</em><span class="screen_out">위</span><span class="txt_issue">청년 내일채움공제</span></a></li>
//    </ul>
//    <ul class="list_issue">
//    <li><a href="https://m.search.daum.net/search?w=tot&q=%EA%B0%95%EC%95%84%EC%A7%80+3%EB%A7%88%EB%A6%AC&amp;DA=13I&amp;pin=news" class="link_issue"><em class="num_issue">6</em><span class="screen_out">위</span><span class="txt_issue">강아지 3마리</span></a></li>
//    </ul>
//    </div>
//
//    <div class='keyissue_area '>
//    <strong class="screen_out">연예 이슈검색어</strong>
//    <ul class="list_issue">
//    <li><a href="https://m.search.daum.net/search?w=tot&q=%ED%95%98%EC%97%B0%EC%88%98+%EB%85%BC%EB%9E%80&amp;DA=13K&amp;pin=entertain" class="link_issue"><em class="num_issue">1</em><span class="screen_out">위</span><span class="txt_issue">하연수 논란</span></a></li>
//    </ul>
//    <ul class="list_issue">
//    <li><a href="https://m.search.daum.net/search?w=tot&q=%EB%B0%95%EC%9D%B8%ED%99%98&amp;DA=13K&amp;pin=entertain" class="link_issue"><em class="num_issue">6</em><span class="screen_out">위</span><span class="txt_issue">박인환</span></a></li>
//    </ul>
//    </div>
//
//    <div class='keyissue_area '>
//    <strong class="screen_out">스포츠 이슈검색어</strong>
//    <ul class="list_issue">
//    <li><a href="https://m.search.daum.net/search?w=tot&q=%EC%9D%B4%EB%9D%BC%ED%81%AC+%EB%B2%A0%ED%8A%B8%EB%82%A8&amp;DA=13J&amp;pin=sports" class="link_issue"><em class="num_issue">1</em><span class="screen_out">위</span><span class="txt_issue">이라크 베트남</span></a></li>
//    </ul>
//    <ul class="list_issue">
//    <li><a href="https://m.search.daum.net/search?w=tot&q=%ED%99%A9%ED%9D%AC%EC%B0%AC&amp;DA=13J&amp;pin=sports" class="link_issue"><em class="num_issue">6</em><span class="screen_out">위</span><span class="txt_issue">황희찬</span></a></li>
//    </ul>
//    </div>
//    </div>
//    </div>

    override fun parsing() {
        var exp = "count(//strong)"
        val strongCnt = int(exp)

        var k = 1
        while(k <= strongCnt) {
            exp = "/div/div[$k]/strong/text()"
            var label = string(exp).replace(REMOVE_WORD, "")
            if ("전체" == label) {
                label = REALTIME_ISSUE
            }

            if (mLog.isDebugEnabled) {
                mLog.debug("LABEL : $label")
            }

            exp = "count(/div/div[$k]/ul)"
            val ulCnt = int(exp)

            val issueList = arrayListOf<RealtimeIssue>()

            var i = 1
            var count = 1
            while (i <= ulCnt) {
                exp = "count(/div/div[$k]/ul[$i]/li)"
                val liCnt = int(exp)

                var j = 1
                while (j <= liCnt) {
                    exp = "/div/div[$k]/ul[$i]/li[$j]/a/@href"
                    val url = string(exp).urldecode()

                    exp = "/div/div[$k]/ul[$i]/li[$j]/a/span[@class='txt_issue']/text()"
                    val text = string(exp)

                    issueList.add(RealtimeIssue(count, url, text))

                    ++count
                    ++j
                }

                ++i
            }

            realtimeIssueList.add(label to issueList)
            ++k
        }
    }
}