package com.example.clone_daum.model.remote

import com.example.common.IRecyclerDiff

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2019. 1. 2. <p/>
 */

data class Sitemap(
    val name:  String,
    val icon:  String,
    val url:   String,
    val isApp: Boolean
) : IRecyclerDiff {
    override fun compare(item: IRecyclerDiff) =
        name == (item as Sitemap).name
}