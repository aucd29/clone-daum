package com.example.clone_daum.ui.viewmodel

import android.view.View
import androidx.core.text.toHtml
import brigitte.html
import com.example.clone_daum.common.PreloadConfig
import com.example.clone_daum.model.remote.RealtimeIssue
import com.example.clone_daum.ui.main.realtimeissue.RealtimeIssueViewModel
import brigitte.shield.*
import com.google.android.material.tabs.TabLayout
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.robolectric.RobolectricTestRunner
import org.slf4j.LoggerFactory

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2019-08-05 <p/>
 */
@RunWith(RobolectricTestRunner::class)
class RealtimeIssueViewModelTest: BaseRoboViewModelTest<RealtimeIssueViewModel>() {
    @Before
    @Throws(Exception::class)
    fun setup() {
        initMock()

        viewmodel = RealtimeIssueViewModel(app, config)
    }

    @Test
    fun tabChangedCallbackTest() {
        val tab = mock(TabLayout.Tab::class.java)

        repeat(2) {
            tab.position.mockReturn(it)

            if (mLog.isDebugEnabled) {
                mLog.debug("TEST CHANGE TAB $it")
            }

            viewmodel.apply {
                mockObserver(tabChangedLive).apply {
                    tabChangedCallback.get()?.onTabSelected(tab)
                    verifyChanged(tab)

                    tabChangedLive.value?.position.assertEquals(it)
                }
            }
        }
    }

    @Test
    fun loadTest() {
        mockReactiveX()

        viewmodel.apply {
            mockObserver<Pair<String, Any>>(commandEvent).apply {
                load(DUMMY_HTML)

                verifyChanged(RealtimeIssueViewModel.CMD_LOADED_ISSUE)

                viewIssueProgress.get().assertEquals(View.GONE)
                viewRetry.get().assertEquals(View.GONE) // 정상적이면 gone
                enableClick.get().assertTrue()
                currentIssue.get().assertNotNull()
            }
        }
    }

    @Test
    fun invalidLoadTest() {
        mockReactiveX()

        viewmodel.apply {
            mockObserver<Pair<String, Any>>(commandEvent).apply {
                load("")

                // 실패하면 CMD_LOADED_ISSUE 를 호출되지 않고
                verifyNeverChanged(RealtimeIssueViewModel.CMD_LOADED_ISSUE)

                // 그외 속성값 체크
                viewIssueProgress.get().assertEquals(View.GONE)
                viewRetry.get().assertEquals(View.VISIBLE)  // 비 정상이면 visible
                enableClick.get().assertFalse()
                currentIssue.get().assertNull()
            }
        }
    }

    @Test
    fun titleConvertTest() {
        viewmodel.titleConvert(null).assertEquals("")

        val issue = mock(RealtimeIssue::class.java)
        issue.index.mockReturn(1)
        issue.text.mockReturn("hello")

        viewmodel.titleConvert(issue).assertEquals("1 hello")
    }

    @Test
    fun typeConvertTest() {
        val issue = mock(RealtimeIssue::class.java)

        issue.apply {
            type.mockReturn("+")
            value.mockReturn("10")
            viewmodel.typeConvert(this)?.toHtml()
                .assertEquals("<font color='red'>↑</font> $value".html()?.toHtml())

            type.mockReturn("-")
            viewmodel.typeConvert(this)?.toHtml()
                .assertEquals("<font color='blue'>↓</font> $value".html()?.toHtml())

            type.mockReturn("N")
            viewmodel.typeConvert(this)?.toHtml()
                .assertEquals("<font color='red'>NEW</font>".html()?.toHtml())
        }
    }

    @Test
    fun layoutTranslationYTest() {
        viewmodel.apply {
            layoutTranslationY(10f)
            layoutTranslationY.get().assertEquals(10f)
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////
    //
    // MOCK
    //
    ////////////////////////////////////////////////////////////////////////////////////
    
    companion object {
        private val mLog = LoggerFactory.getLogger(RealtimeIssueViewModelTest::class.java)

        const val DUMMY_HTML = """    var hotissueData = include([{"type":"hotissue","list":[{"param":"rtmaxcoll=1TH","utf8Keyword":"%EC%A3%BC%EC%98%A5%EC%88%9C","rank":"1","linkurl":"https://m.search.daum.net/search?w=tot&q=%EC%A3%BC%EC%98%A5%EC%88%9C&amp;DA=ATG&amp;nil_mtopsearch=issuekwd&amp;logical=issue&amp;pin=issue","keyword":"주옥순","type":"+","value":"211","euckrKeyword":"%C1%D6%BF%C1%BC%F8"},{"param":"rtmaxcoll=1TH","utf8Keyword":"%ED%83%9C%ED%92%8D+%EA%B2%BD%EB%A1%9C","rank":"2","linkurl":"https://m.search.daum.net/search?w=tot&q=%ED%83%9C%ED%92%8D+%EA%B2%BD%EB%A1%9C&amp;DA=ATG&amp;nil_mtopsearch=issuekwd&amp;logical=issue&amp;pin=issue","keyword":"태풍 경로","type":"+","value":"189","euckrKeyword":"%C5%C2%C7%B3+%B0%E6%B7%CE"},{"param":"rtmaxcoll=1TH","utf8Keyword":"%EC%95%88%EC%84%B1+%ED%99%94%EC%9E%AC","rank":"3","linkurl":"https://m.search.daum.net/search?w=tot&q=%EC%95%88%EC%84%B1+%ED%99%94%EC%9E%AC&amp;DA=ATG&amp;nil_mtopsearch=issuekwd&amp;logical=issue&amp;pin=issue","keyword":"안성 화재","type":"+","value":"73","euckrKeyword":"%BE%C8%BC%BA+%C8%AD%C0%E7"},{"param":"rtmaxcoll=1TH","utf8Keyword":"%EC%9D%B4%EC%9E%AC%EB%A3%A1","rank":"4","linkurl":"https://m.search.daum.net/search?w=tot&q=%EC%9D%B4%EC%9E%AC%EB%A3%A1&amp;DA=ATG&amp;nil_mtopsearch=issuekwd&amp;logical=issue&amp;pin=issue","keyword":"이재룡","type":"+","value":"70","euckrKeyword":"%C0%CC%C0%E7%B7%E6"},{"param":"rtmaxcoll=1TH","utf8Keyword":"%EA%B3%BD%EC%83%81%EB%8F%84","rank":"5","linkurl":"https://m.search.daum.net/search?w=tot&q=%EA%B3%BD%EC%83%81%EB%8F%84&amp;DA=ATG&amp;nil_mtopsearch=issuekwd&amp;logical=issue&amp;pin=issue","keyword":"곽상도","type":"+","value":"58","euckrKeyword":"%B0%FB%BB%F3%B5%B5"},{"param":"rtmaxcoll=1TH","utf8Keyword":"%EB%85%B8%EC%98%81%EB%AF%BC","rank":"6","linkurl":"https://m.search.daum.net/search?w=tot&q=%EB%85%B8%EC%98%81%EB%AF%BC&amp;DA=ATG&amp;nil_mtopsearch=issuekwd&amp;logical=issue&amp;pin=issue","keyword":"노영민","type":"+","value":"46","euckrKeyword":"%B3%EB%BF%B5%B9%CE"},{"param":"rtmaxcoll=1TH","utf8Keyword":"%EC%A0%95%EB%A1%A0%EA%B4%80","rank":"7","linkurl":"https://m.search.daum.net/search?w=tot&q=%EC%A0%95%EB%A1%A0%EA%B4%80&amp;DA=ATG&amp;nil_mtopsearch=issuekwd&amp;logical=issue&amp;pin=issue","keyword":"정론관","type":"+","value":"40","euckrKeyword":"%C1%A4%B7%D0%B0%FC"},{"param":"rtmaxcoll=1TH","utf8Keyword":"%EC%98%A4%EC%8A%B9%ED%99%98","rank":"8","linkurl":"https://m.search.daum.net/search?w=tot&q=%EC%98%A4%EC%8A%B9%ED%99%98&amp;DA=ATG&amp;nil_mtopsearch=issuekwd&amp;logical=issue&amp;pin=issue","keyword":"오승환","type":"+","value":"38","euckrKeyword":"%BF%C0%BD%C2%C8%AF"},{"param":"rtmaxcoll=1TH","utf8Keyword":"%EB%AA%A8%EB%AA%A8","rank":"9","linkurl":"https://m.search.daum.net/search?w=tot&q=%EB%AA%A8%EB%AA%A8&amp;DA=ATG&amp;nil_mtopsearch=issuekwd&amp;logical=issue&amp;pin=issue","keyword":"모모","type":"+","value":"45","euckrKeyword":"%B8%F0%B8%F0"},{"param":"rtmaxcoll=1TH","utf8Keyword":"%EA%B9%80%ED%95%B4%EA%B3%B5%ED%95%AD","rank":"10","linkurl":"https://m.search.daum.net/search?w=tot&q=%EA%B9%80%ED%95%B4%EA%B3%B5%ED%95%AD&amp;DA=ATG&amp;nil_mtopsearch=issuekwd&amp;logical=issue&amp;pin=issue","keyword":"김해공항","type":"+","value":"36","euckrKeyword":"%B1%E8%C7%D8%B0%F8%C7%D7"}]},{"type":"news","list":[{"linkurl_mobile":"https://m.search.daum.net/search?w=tot&q=%EA%B3%B5%EB%A7%A4%EB%8F%84","linkurl_pc":"https://search.daum.net/search?w=tot&q=%EA%B3%B5%EB%A7%A4%EB%8F%84","param":"pin=news","param_pc":"f=news&rtmaxcoll=EOA,1TH","utf8Keyword":"%EA%B3%B5%EB%A7%A4%EB%8F%84","rank":"1","param_mobile":"pin=news","linkurl":"https://m.search.daum.net/search?w=tot&q=%EA%B3%B5%EB%A7%A4%EB%8F%84&amp;DA=QWG&amp;pin=news","keyword":"공매도","type":"+","value":"120","euckrKeyword":"%B0%F8%B8%C5%B5%B5"},{"linkurl_mobile":"https://m.search.daum.net/search?w=tot&q=%EB%AA%85%EC%84%B1%EA%B5%90%ED%9A%8C","linkurl_pc":"https://search.daum.net/search?w=tot&q=%EB%AA%85%EC%84%B1%EA%B5%90%ED%9A%8C","param":"pin=news","param_pc":"f=news&rtmaxcoll=EOA,1TH","utf8Keyword":"%EB%AA%85%EC%84%B1%EA%B5%90%ED%9A%8C","rank":"2","param_mobile":"pin=news","linkurl":"https://m.search.daum.net/search?w=tot&q=%EB%AA%85%EC%84%B1%EA%B5%90%ED%9A%8C&amp;DA=QWG&amp;pin=news","keyword":"명성교회","type":"+","value":"116","euckrKeyword":"%B8%ED%BC%BA%B1%B3%C8%B8"},{"linkurl_mobile":"https://m.search.daum.net/search?w=tot&q=%EB%85%B8%EC%98%81%EB%AF%BC+%EA%B3%BD%EC%83%81%EB%8F%84","linkurl_pc":"https://search.daum.net/search?w=tot&q=%EB%85%B8%EC%98%81%EB%AF%BC+%EA%B3%BD%EC%83%81%EB%8F%84","param":"pin=news","param_pc":"f=news&rtmaxcoll=EOA,1TH","utf8Keyword":"%EB%85%B8%EC%98%81%EB%AF%BC+%EA%B3%BD%EC%83%81%EB%8F%84","rank":"3","param_mobile":"pin=news","linkurl":"https://m.search.daum.net/search?w=tot&q=%EB%85%B8%EC%98%81%EB%AF%BC+%EA%B3%BD%EC%83%81%EB%8F%84&amp;DA=QWG&amp;pin=news","keyword":"노영민 곽상도","type":"+","value":"50","euckrKeyword":"%B3%EB%BF%B5%B9%CE+%B0%FB%BB%F3%B5%B5"},{"linkurl_mobile":"https://m.search.daum.net/search?w=tot&q=%ED%83%9C%ED%92%8D%EC%A7%84%EB%A1%9C","linkurl_pc":"https://search.daum.net/search?w=tot&q=%ED%83%9C%ED%92%8D%EC%A7%84%EB%A1%9C","param":"pin=news","param_pc":"f=news&rtmaxcoll=EOA,1TH","utf8Keyword":"%ED%83%9C%ED%92%8D%EC%A7%84%EB%A1%9C","rank":"4","param_mobile":"pin=news","linkurl":"https://m.search.daum.net/search?w=tot&q=%ED%83%9C%ED%92%8D%EC%A7%84%EB%A1%9C&amp;DA=QWG&amp;pin=news","keyword":"태풍진로","type":"+","value":"48","euckrKeyword":"%C5%C2%C7%B3%C1%F8%B7%CE"},{"linkurl_mobile":"https://m.search.daum.net/search?w=tot&q=%EC%88%9C%EC%B2%9C+%EA%B5%90%ED%86%B5%EC%82%AC%EA%B3%A0","linkurl_pc":"https://search.daum.net/search?w=tot&q=%EC%88%9C%EC%B2%9C+%EA%B5%90%ED%86%B5%EC%82%AC%EA%B3%A0","param":"pin=news","param_pc":"f=news&rtmaxcoll=EOA,1TH","utf8Keyword":"%EC%88%9C%EC%B2%9C+%EA%B5%90%ED%86%B5%EC%82%AC%EA%B3%A0","rank":"5","param_mobile":"pin=news","linkurl":"https://m.search.daum.net/search?w=tot&q=%EC%88%9C%EC%B2%9C+%EA%B5%90%ED%86%B5%EC%82%AC%EA%B3%A0&amp;DA=QWG&amp;pin=news","keyword":"순천 교통사고","type":"+","value":"42","euckrKeyword":"%BC%F8%C3%B5+%B1%B3%C5%EB%BB%E7%B0%ED"},{"linkurl_mobile":"https://m.search.daum.net/search?w=tot&q=%EC%86%8C%EB%B0%A9%EA%B4%80+%EC%82%AC%EB%A7%9D","linkurl_pc":"https://search.daum.net/search?w=tot&q=%EC%86%8C%EB%B0%A9%EA%B4%80+%EC%82%AC%EB%A7%9D","param":"pin=news","param_pc":"f=news&rtmaxcoll=EOA,1TH","utf8Keyword":"%EC%86%8C%EB%B0%A9%EA%B4%80+%EC%82%AC%EB%A7%9D","rank":"6","param_mobile":"pin=news","linkurl":"https://m.search.daum.net/search?w=tot&q=%EC%86%8C%EB%B0%A9%EA%B4%80+%EC%82%AC%EB%A7%9D&amp;DA=QWG&amp;pin=news","keyword":"소방관 사망","type":"+","value":"51","euckrKeyword":"%BC%D2%B9%E6%B0%FC+%BB%E7%B8%C1"},{"linkurl_mobile":"https://m.search.daum.net/search?w=tot&q=%EC%B2%AD%EB%85%84+%EA%B5%AC%EC%A7%81%ED%99%9C%EB%8F%99%EC%A7%80%EC%9B%90%EA%B8%88","linkurl_pc":"https://search.daum.net/search?w=tot&q=%EC%B2%AD%EB%85%84+%EA%B5%AC%EC%A7%81%ED%99%9C%EB%8F%99%EC%A7%80%EC%9B%90%EA%B8%88","param":"pin=news","param_pc":"f=news&rtmaxcoll=EOA,1TH","utf8Keyword":"%EC%B2%AD%EB%85%84+%EA%B5%AC%EC%A7%81%ED%99%9C%EB%8F%99%EC%A7%80%EC%9B%90%EA%B8%88","rank":"7","param_mobile":"pin=news","linkurl":"https://m.search.daum.net/search?w=tot&q=%EC%B2%AD%EB%85%84+%EA%B5%AC%EC%A7%81%ED%99%9C%EB%8F%99%EC%A7%80%EC%9B%90%EA%B8%88&amp;DA=QWG&amp;pin=news","keyword":"청년 구직활동지원금","type":"+","value":"55","euckrKeyword":"%C3%BB%B3%E2+%B1%B8%C1%F7%C8%B0%B5%BF%C1%F6%BF%F8%B1%DD"},{"linkurl_mobile":"https://m.search.daum.net/search?w=tot&q=%EC%9D%B4%EC%9E%AC%EC%9A%A9","linkurl_pc":"https://search.daum.net/search?w=tot&q=%EC%9D%B4%EC%9E%AC%EC%9A%A9","param":"pin=news","param_pc":"f=news&rtmaxcoll=EOA,1TH","utf8Keyword":"%EC%9D%B4%EC%9E%AC%EC%9A%A9","rank":"8","param_mobile":"pin=news","linkurl":"https://m.search.daum.net/search?w=tot&q=%EC%9D%B4%EC%9E%AC%EC%9A%A9&amp;DA=QWG&amp;pin=news","keyword":"이재용","type":"+","value":"44","euckrKeyword":"%C0%CC%C0%E7%BF%EB"},{"linkurl_mobile":"https://m.search.daum.net/search?w=tot&q=%EB%AC%B4%EB%B9%84%EC%9E%90","linkurl_pc":"https://search.daum.net/search?w=tot&q=%EB%AC%B4%EB%B9%84%EC%9E%90","param":"pin=news","param_pc":"f=news&rtmaxcoll=EOA,1TH","utf8Keyword":"%EB%AC%B4%EB%B9%84%EC%9E%90","rank":"9","param_mobile":"pin=news","linkurl":"https://m.search.daum.net/search?w=tot&q=%EB%AC%B4%EB%B9%84%EC%9E%90&amp;DA=QWG&amp;pin=news","keyword":"무비자","type":"+","value":"37","euckrKeyword":"%B9%AB%BA%F1%C0%DA"},{"linkurl_mobile":"https://m.search.daum.net/search?w=tot&q=%ED%99%98%EC%9C%A8+%EC%A1%B0%EC%9E%91%EA%B5%AD","linkurl_pc":"https://search.daum.net/search?w=tot&q=%ED%99%98%EC%9C%A8+%EC%A1%B0%EC%9E%91%EA%B5%AD","param":"pin=news","param_pc":"f=news&rtmaxcoll=EOA,1TH","utf8Keyword":"%ED%99%98%EC%9C%A8+%EC%A1%B0%EC%9E%91%EA%B5%AD","rank":"10","param_mobile":"pin=news","linkurl":"https://m.search.daum.net/search?w=tot&q=%ED%99%98%EC%9C%A8+%EC%A1%B0%EC%9E%91%EA%B5%AD&amp;DA=QWG&amp;pin=news","keyword":"환율 조작국","type":"+","value":"37","euckrKeyword":"%C8%AF%C0%B2+%C1%B6%C0%DB%B1%B9"}]},{"type":"enter","list":[{"linkurl_mobile":"https://m.search.daum.net/search?w=tot&q=%EC%A0%84%EC%A7%80%ED%98%84","linkurl_pc":"https://search.daum.net/search?w=tot&q=%EC%A0%84%EC%A7%80%ED%98%84","param":"pin=entertain","param_pc":"f=ent&rtmaxcoll=DRU,1TH","utf8Keyword":"%EC%A0%84%EC%A7%80%ED%98%84","rank":"1","param_mobile":"pin=entertain","linkurl":"https://m.search.daum.net/search?w=tot&q=%EC%A0%84%EC%A7%80%ED%98%84&amp;DA=UAG&amp;pin=entertain","keyword":"전지현","type":"+","value":"327","euckrKeyword":"%C0%FC%C1%F6%C7%F6"},{"linkurl_mobile":"https://m.search.daum.net/search?w=tot&q=%EC%95%88%EC%A0%A4%EB%A6%AC%EB%82%98+%EC%A1%B8%EB%A6%AC","linkurl_pc":"https://search.daum.net/search?w=tot&q=%EC%95%88%EC%A0%A4%EB%A6%AC%EB%82%98+%EC%A1%B8%EB%A6%AC","param":"pin=entertain","param_pc":"f=ent&rtmaxcoll=DRU,1TH","utf8Keyword":"%EC%95%88%EC%A0%A4%EB%A6%AC%EB%82%98+%EC%A1%B8%EB%A6%AC","rank":"2","param_mobile":"pin=entertain","linkurl":"https://m.search.daum.net/search?w=tot&q=%EC%95%88%EC%A0%A4%EB%A6%AC%EB%82%98+%EC%A1%B8%EB%A6%AC&amp;DA=UAG&amp;pin=entertain","keyword":"안젤리나 졸리","type":"+","value":"163","euckrKeyword":"%BE%C8%C1%A9%B8%AE%B3%AA+%C1%B9%B8%AE"},{"linkurl_mobile":"https://m.search.daum.net/search?w=tot&q=%EC%BC%80%EB%B9%88+%EB%82%98","linkurl_pc":"https://search.daum.net/search?w=tot&q=%EC%BC%80%EB%B9%88+%EB%82%98","param":"pin=entertain","param_pc":"f=ent&rtmaxcoll=DRU,1TH","utf8Keyword":"%EC%BC%80%EB%B9%88+%EB%82%98","rank":"3","param_mobile":"pin=entertain","linkurl":"https://m.search.daum.net/search?w=tot&q=%EC%BC%80%EB%B9%88+%EB%82%98&amp;DA=UAG&amp;pin=entertain","keyword":"케빈 나","type":"+","value":"169","euckrKeyword":"%C4%C9%BA%F3+%B3%AA"},{"linkurl_mobile":"https://m.search.daum.net/search?w=tot&q=%EC%9C%A4%EC%83%81%ED%98%84","linkurl_pc":"https://search.daum.net/search?w=tot&q=%EC%9C%A4%EC%83%81%ED%98%84","param":"pin=entertain","param_pc":"f=ent&rtmaxcoll=DRU,1TH","utf8Keyword":"%EC%9C%A4%EC%83%81%ED%98%84","rank":"4","param_mobile":"pin=entertain","linkurl":"https://m.search.daum.net/search?w=tot&q=%EC%9C%A4%EC%83%81%ED%98%84&amp;DA=UAG&amp;pin=entertain","keyword":"윤상현","type":"+","value":"63","euckrKeyword":"%C0%B1%BB%F3%C7%F6"},{"linkurl_mobile":"https://m.search.daum.net/search?w=tot&q=%EA%B5%AC%EC%A4%80%EC%97%BD+%EC%98%A4%EC%A7%80%ED%98%9C","linkurl_pc":"https://search.daum.net/search?w=tot&q=%EA%B5%AC%EC%A4%80%EC%97%BD+%EC%98%A4%EC%A7%80%ED%98%9C","param":"pin=entertain","param_pc":"f=ent&rtmaxcoll=DRU,1TH","utf8Keyword":"%EA%B5%AC%EC%A4%80%EC%97%BD+%EC%98%A4%EC%A7%80%ED%98%9C","rank":"5","param_mobile":"pin=entertain","linkurl":"https://m.search.daum.net/search?w=tot&q=%EA%B5%AC%EC%A4%80%EC%97%BD+%EC%98%A4%EC%A7%80%ED%98%9C&amp;DA=UAG&amp;pin=entertain","keyword":"구준엽 오지혜","type":"+","value":"51","euckrKeyword":"%B1%B8%C1%D8%BF%B1+%BF%C0%C1%F6%C7%FD"},{"linkurl_mobile":"https://m.search.daum.net/search?w=tot&q=%EC%9C%A0%EB%8B%88%ED%81%B4%EB%A1%9C","linkurl_pc":"https://search.daum.net/search?w=tot&q=%EC%9C%A0%EB%8B%88%ED%81%B4%EB%A1%9C","param":"pin=entertain","param_pc":"f=ent&rtmaxcoll=DRU,1TH","utf8Keyword":"%EC%9C%A0%EB%8B%88%ED%81%B4%EB%A1%9C","rank":"6","param_mobile":"pin=entertain","linkurl":"https://m.search.daum.net/search?w=tot&q=%EC%9C%A0%EB%8B%88%ED%81%B4%EB%A1%9C&amp;DA=UAG&amp;pin=entertain","keyword":"유니클로","type":"new","value":"0","euckrKeyword":"%C0%AF%B4%CF%C5%AC%B7%CE"},{"linkurl_mobile":"https://m.search.daum.net/search?w=tot&q=%EB%A7%A4%EB%8D%95%EC%8A%A4","linkurl_pc":"https://search.daum.net/search?w=tot&q=%EB%A7%A4%EB%8D%95%EC%8A%A4","param":"pin=entertain","param_pc":"f=ent&rtmaxcoll=DRU,1TH","utf8Keyword":"%EB%A7%A4%EB%8D%95%EC%8A%A4","rank":"7","param_mobile":"pin=entertain","linkurl":"https://m.search.daum.net/search?w=tot&q=%EB%A7%A4%EB%8D%95%EC%8A%A4&amp;DA=UAG&amp;pin=entertain","keyword":"매덕스","type":"+","value":"130","euckrKeyword":"%B8%C5%B4%F6%BD%BA"},{"linkurl_mobile":"https://m.search.daum.net/search?w=tot&q=%EB%A7%A5%EC%BB%AC%EB%A6%AC+%EC%BB%AC%ED%82%A8","linkurl_pc":"https://search.daum.net/search?w=tot&q=%EB%A7%A5%EC%BB%AC%EB%A6%AC+%EC%BB%AC%ED%82%A8","param":"pin=entertain","param_pc":"f=ent&rtmaxcoll=DRU,1TH","utf8Keyword":"%EB%A7%A5%EC%BB%AC%EB%A6%AC+%EC%BB%AC%ED%82%A8","rank":"8","param_mobile":"pin=entertain","linkurl":"https://m.search.daum.net/search?w=tot&q=%EB%A7%A5%EC%BB%AC%EB%A6%AC+%EC%BB%AC%ED%82%A8&amp;DA=UAG&amp;pin=entertain","keyword":"맥컬리 컬킨","type":"+","value":"81","euckrKeyword":"%B8%C6%C4%C3%B8%AE+%C4%C3%C5%B2"},{"linkurl_mobile":"https://m.search.daum.net/search?w=tot&q=%EB%8B%A4%EB%8B%88%EC%97%98+%EB%A6%B0%EB%8D%B0%EB%A7%8C","linkurl_pc":"https://search.daum.net/search?w=tot&q=%EB%8B%A4%EB%8B%88%EC%97%98+%EB%A6%B0%EB%8D%B0%EB%A7%8C","param":"pin=entertain","param_pc":"f=ent&rtmaxcoll=DRU,1TH","utf8Keyword":"%EB%8B%A4%EB%8B%88%EC%97%98+%EB%A6%B0%EB%8D%B0%EB%A7%8C","rank":"9","param_mobile":"pin=entertain","linkurl":"https://m.search.daum.net/search?w=tot&q=%EB%8B%A4%EB%8B%88%EC%97%98+%EB%A6%B0%EB%8D%B0%EB%A7%8C&amp;DA=UAG&amp;pin=entertain","keyword":"다니엘 린데만","type":"+","value":"62","euckrKeyword":"%B4%D9%B4%CF%BF%A4+%B8%B0%B5%A5%B8%B8"},{"linkurl_mobile":"https://m.search.daum.net/search?w=tot&q=%EC%97%91%EC%86%8C","linkurl_pc":"https://search.daum.net/search?w=tot&q=%EC%97%91%EC%86%8C","param":"pin=entertain","param_pc":"f=ent&rtmaxcoll=DRU,1TH","utf8Keyword":"%EC%97%91%EC%86%8C","rank":"10","param_mobile":"pin=entertain","linkurl":"https://m.search.daum.net/search?w=tot&q=%EC%97%91%EC%86%8C&amp;DA=UAG&amp;pin=entertain","keyword":"엑소","type":"+","value":"51","euckrKeyword":"%BF%A2%BC%D2"}]},{"type":"sports","list":[{"linkurl_mobile":"https://m.search.daum.net/search?w=tot&q=%EC%B6%94%EC%8B%A0%EC%88%98+%EC%95%84%EB%93%A4","linkurl_pc":"https://search.daum.net/search?w=tot&q=%EC%B6%94%EC%8B%A0%EC%88%98+%EC%95%84%EB%93%A4","param":"pin=sports","param_pc":"f=sports&rtmaxcoll=MBK,1TH","utf8Keyword":"%EC%B6%94%EC%8B%A0%EC%88%98+%EC%95%84%EB%93%A4","rank":"1","param_mobile":"pin=sports","linkurl":"https://m.search.daum.net/search?w=tot&q=%EC%B6%94%EC%8B%A0%EC%88%98+%EC%95%84%EB%93%A4&amp;DA=XEO&amp;pin=sports","keyword":"추신수 아들","type":"+","value":"825","euckrKeyword":"%C3%DF%BD%C5%BC%F6+%BE%C6%B5%E9"},{"linkurl_mobile":"https://m.search.daum.net/search?w=tot&q=%EA%B9%80%EC%9E%90%EC%9D%B8","linkurl_pc":"https://search.daum.net/search?w=tot&q=%EA%B9%80%EC%9E%90%EC%9D%B8","param":"pin=sports","param_pc":"f=sports&rtmaxcoll=MBK,1TH","utf8Keyword":"%EA%B9%80%EC%9E%90%EC%9D%B8","rank":"2","param_mobile":"pin=sports","linkurl":"https://m.search.daum.net/search?w=tot&q=%EA%B9%80%EC%9E%90%EC%9D%B8&amp;DA=XEO&amp;pin=sports","keyword":"김자인","type":"+","value":"110","euckrKeyword":"%B1%E8%C0%DA%C0%CE"},{"linkurl_mobile":"https://m.search.daum.net/search?w=tot&q=%EB%8F%84%EC%BF%84+%EC%98%AC%EB%A6%BC%ED%94%BD","linkurl_pc":"https://search.daum.net/search?w=tot&q=%EB%8F%84%EC%BF%84+%EC%98%AC%EB%A6%BC%ED%94%BD","param":"pin=sports","param_pc":"f=sports&rtmaxcoll=MBK,1TH","utf8Keyword":"%EB%8F%84%EC%BF%84+%EC%98%AC%EB%A6%BC%ED%94%BD","rank":"3","param_mobile":"pin=sports","linkurl":"https://m.search.daum.net/search?w=tot&q=%EB%8F%84%EC%BF%84+%EC%98%AC%EB%A6%BC%ED%94%BD&amp;DA=XEO&amp;pin=sports","keyword":"도쿄 올림픽","type":"new","value":"0","euckrKeyword":"%B5%B5%C4%EC+%BF%C3%B8%B2%C7%C8"},{"linkurl_mobile":"https://m.search.daum.net/search?w=tot&q=%EC%86%90%ED%9D%A5%EB%AF%BC+%EC%9C%A0%EB%8B%88%ED%8F%BC","linkurl_pc":"https://search.daum.net/search?w=tot&q=%EC%86%90%ED%9D%A5%EB%AF%BC+%EC%9C%A0%EB%8B%88%ED%8F%BC","param":"pin=sports","param_pc":"f=sports&rtmaxcoll=MBK,1TH","utf8Keyword":"%EC%86%90%ED%9D%A5%EB%AF%BC+%EC%9C%A0%EB%8B%88%ED%8F%BC","rank":"4","param_mobile":"pin=sports","linkurl":"https://m.search.daum.net/search?w=tot&q=%EC%86%90%ED%9D%A5%EB%AF%BC+%EC%9C%A0%EB%8B%88%ED%8F%BC&amp;DA=XEO&amp;pin=sports","keyword":"손흥민 유니폼","type":"+","value":"66","euckrKeyword":"%BC%D5%C8%EF%B9%CE+%C0%AF%B4%CF%C6%FB"},{"linkurl_mobile":"https://m.search.daum.net/search?w=tot&q=%EC%86%8C%EB%A1%9C%EC%B9%B4","linkurl_pc":"https://search.daum.net/search?w=tot&q=%EC%86%8C%EB%A1%9C%EC%B9%B4","param":"pin=sports","param_pc":"f=sports&rtmaxcoll=MBK,1TH","utf8Keyword":"%EC%86%8C%EB%A1%9C%EC%B9%B4","rank":"5","param_mobile":"pin=sports","linkurl":"https://m.search.daum.net/search?w=tot&q=%EC%86%8C%EB%A1%9C%EC%B9%B4&amp;DA=XEO&amp;pin=sports","keyword":"소로카","type":"+","value":"53","euckrKeyword":"%BC%D2%B7%CE%C4%AB"},{"linkurl_mobile":"https://m.search.daum.net/search?w=tot&q=%EC%97%90%EB%A6%AD%EC%84%BC","linkurl_pc":"https://search.daum.net/search?w=tot&q=%EC%97%90%EB%A6%AD%EC%84%BC","param":"pin=sports","param_pc":"f=sports&rtmaxcoll=MBK,1TH","utf8Keyword":"%EC%97%90%EB%A6%AD%EC%84%BC","rank":"6","param_mobile":"pin=sports","linkurl":"https://m.search.daum.net/search?w=tot&q=%EC%97%90%EB%A6%AD%EC%84%BC&amp;DA=XEO&amp;pin=sports","keyword":"에릭센","type":"+","value":"52","euckrKeyword":"%BF%A1%B8%AF%BC%BE"},{"linkurl_mobile":"https://m.search.daum.net/search?w=tot&q=%EC%98%90%EB%A6%AC%EC%B9%98","linkurl_pc":"https://search.daum.net/search?w=tot&q=%EC%98%90%EB%A6%AC%EC%B9%98","param":"pin=sports","param_pc":"f=sports&rtmaxcoll=MBK,1TH","utf8Keyword":"%EC%98%90%EB%A6%AC%EC%B9%98","rank":"7","param_mobile":"pin=sports","linkurl":"https://m.search.daum.net/search?w=tot&q=%EC%98%90%EB%A6%AC%EC%B9%98&amp;DA=XEO&amp;pin=sports","keyword":"옐리치","type":"+","value":"48","euckrKeyword":"%BF%BB%B8%AE%C4%A1"},{"linkurl_mobile":"https://m.search.daum.net/search?w=tot&q=%EC%A0%90%ED%94%84%ED%88%AC%EC%96%B4","linkurl_pc":"https://search.daum.net/search?w=tot&q=%EC%A0%90%ED%94%84%ED%88%AC%EC%96%B4","param":"pin=sports","param_pc":"f=sports&rtmaxcoll=MBK,1TH","utf8Keyword":"%EC%A0%90%ED%94%84%ED%88%AC%EC%96%B4","rank":"8","param_mobile":"pin=sports","linkurl":"https://m.search.daum.net/search?w=tot&q=%EC%A0%90%ED%94%84%ED%88%AC%EC%96%B4&amp;DA=XEO&amp;pin=sports","keyword":"점프투어","type":"+","value":"41","euckrKeyword":"%C1%A1%C7%C1%C5%F5%BE%EE"},{"linkurl_mobile":"https://m.search.daum.net/search?w=tot&q=%EB%A7%A5%EC%8A%A4+%EC%8A%88%EC%96%B4%EC%A0%B8","linkurl_pc":"https://search.daum.net/search?w=tot&q=%EB%A7%A5%EC%8A%A4+%EC%8A%88%EC%96%B4%EC%A0%B8","param":"pin=sports","param_pc":"f=sports&rtmaxcoll=MBK,1TH","utf8Keyword":"%EB%A7%A5%EC%8A%A4+%EC%8A%88%EC%96%B4%EC%A0%B8","rank":"9","param_mobile":"pin=sports","linkurl":"https://m.search.daum.net/search?w=tot&q=%EB%A7%A5%EC%8A%A4+%EC%8A%88%EC%96%B4%EC%A0%B8&amp;DA=XEO&amp;pin=sports","keyword":"맥스 슈어져","type":"+","value":"40","euckrKeyword":"%B8%C6%BD%BA+%BD%B4%BE%EE%C1%AE"},{"linkurl_mobile":"https://m.search.daum.net/search?w=tot&q=%EB%B2%A8%EB%A6%B0%EC%A0%80","linkurl_pc":"https://search.daum.net/search?w=tot&q=%EB%B2%A8%EB%A6%B0%EC%A0%80","param":"pin=sports","param_pc":"f=sports&rtmaxcoll=MBK,1TH","utf8Keyword":"%EB%B2%A8%EB%A6%B0%EC%A0%80","rank":"10","param_mobile":"pin=sports","linkurl":"https://m.search.daum.net/search?w=tot&q=%EB%B2%A8%EB%A6%B0%EC%A0%80&amp;DA=XEO&amp;pin=sports","keyword":"벨린저","type":"+","value":"34","euckrKeyword":"%BA%A7%B8%B0%C0%FA"}]}]);    """
    }

    @Mock lateinit var config: PreloadConfig

    override fun initMock() {
        super.initMock()

        mockNetwork()
    }
}