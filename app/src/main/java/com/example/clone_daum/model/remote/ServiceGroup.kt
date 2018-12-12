package com.example.clone_daum.model.remote

import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2018. 12. 3. <p/>
 */

interface GithubService {
    // https://raw.githubusercontent.com/aucd29/clone-daum/merge_search/dumy/popular-keyword.json
    @GET("/aucd29/clone-daum/merge_search/dumy/popular-keyword.json")
    fun popularKeywordList(): Observable<List<String>>
}

interface DaumService {
    // https://msuggest.search.daum.net/sushi/mobile/get?htype=position&q=d
    /*
    {
      "q" : "d",
      "tltm" : null,
      "subkeys" : [ "db 손해보험", "db 손해보험 다이렉트", "dvdprime", "dhl", "ddp", "dsr", "dhl 배송조회", "dgb생명", "dmz", "daum.net", "dart", "dc 인사이드", "dvd 플레이어", "despacito", "dji", "db 하이텍", "db 다이렉트 자동차보험", "daum", "diy", "draw" ],
      "highlighted" : [ [ [ 0, 1 ] ], [ [ 0, 1 ] ], [ [ 0, 1 ] ], [ [ 0, 1 ] ], [ [ 0, 1 ] ], [ [ 0, 1 ] ], [ [ 0, 1 ] ], [ [ 0, 1 ] ], [ [ 0, 1 ] ], [ [ 0, 1 ] ], [ [ 0, 1 ] ], [ [ 0, 1 ] ], [ [ 0, 1 ] ], [ [ 0, 1 ] ], [ [ 0, 1 ] ], [ [ 0, 1 ] ], [ [ 0, 1 ] ], [ [ 0, 1 ] ], [ [ 0, 1 ] ], [ [ 0, 1 ] ] ]
    }
    */
    @GET("/sushi/mobile/get")
    fun suggest(@Query("q") query: String,
                @Query("htype") htype: String = "position"): Observable<Suggest>
}