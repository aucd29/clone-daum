package brigitte.widget

import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import brigitte.string
import com.google.android.material.tabs.TabLayout

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2019-07-26 <p/>
 */

interface ITabFocus {
    fun onTabFocusIn()
    fun onTabFocusOut()
}

interface ITabPosition {
    fun onTabPosition(position: Int)
}

inline fun Fragment.observeTabFocus(livedata: LiveData<TabLayout.Tab?>,
                                    listener: ITabFocus,
                                    @StringRes resid: Int) {
    livedata.observe(this, Observer {
        it?.let { tab ->
            if (tab.text != string(resid)) {
                listener.onTabFocusOut()
                return@let
            }

            listener.onTabFocusIn()
        }
    })
}

inline fun Fragment.observeTabPosition(livedata: LiveData<TabLayout.Tab?>,
                                    listener: ITabPosition) {
    livedata.observe(this, Observer {
        listener.onTabPosition(it?.position ?: -1)
    })
}