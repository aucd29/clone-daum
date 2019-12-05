package com.example.clone_daum.ui.main.bookmark

import android.app.Application
import brigitte.RecyclerViewModel
import com.example.clone_daum.model.remote.Bookmark
import javax.inject.Inject

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2019-12-05 <p/>
 */

class BookmarkViewModel @Inject constructor(
    app: Application
) : RecyclerViewModel<Bookmark>(app) {

}