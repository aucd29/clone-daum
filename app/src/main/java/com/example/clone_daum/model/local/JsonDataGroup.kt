package com.example.clone_daum.model.local

import com.example.common.IRecyclerDiff
import java.io.Serializable

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2018. 11. 27. <p/>
 */

data class TabData(
    val name: String,
    val url: String
) : Serializable

data class BrowserSubMenu(
    val icon: String,
    var iconResid: Int,
    val name: String
) : Serializable, IRecyclerDiff {
    override fun compare(item: IRecyclerDiff): Boolean {
        val nitem = item as BrowserSubMenu

        return this.icon == nitem.icon && this.name == nitem.name
    }
}