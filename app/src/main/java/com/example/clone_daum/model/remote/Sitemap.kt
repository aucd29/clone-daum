package com.example.clone_daum.model.remote

import brigitte.IRecyclerDiff

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2019. 1. 2. <p/>
 */

data class Sitemap(
    val name:  String,
    val icon:  String,
    val url:   String,
    val isApp: Boolean
) : IRecyclerDiff {
    override fun itemSame(item: IRecyclerDiff): Boolean  =
        this == (item as Sitemap)

    override fun contentsSame(item: IRecyclerDiff) =
        name == (item as Sitemap).name
}