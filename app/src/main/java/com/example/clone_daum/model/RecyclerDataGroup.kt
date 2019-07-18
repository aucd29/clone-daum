package com.example.clone_daum.model

import com.example.clone_daum.model.local.UrlHistory
import brigitte.IRecyclerDiff
import brigitte.IRecyclerExpandable
import brigitte.IRecyclerItem

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2018. 12. 12. <p/>
 */

// SearchHistory, SuggestItem 에서 사용
interface ISearchRecyclerData : IRecyclerDiff, IRecyclerItem
