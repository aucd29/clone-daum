package brigitte.bindingadapter

import androidx.databinding.BindingAdapter
import androidx.lifecycle.MutableLiveData
import com.google.android.material.bottomnavigation.BottomNavigationView

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2019-08-09 <p/>
 */

object BottomNavigationBindingAdapter {
    @JvmStatic
    @BindingAdapter("bindNavigationItemSelectedListener")
    fun bindNavigationItemSelectedListener(view: BottomNavigationView, listener: ((Int) -> Boolean)?) {
        listener?.let {
            view.setOnNavigationItemSelectedListener { menu ->
                it.invoke(menu.itemId)
            }
        }
    }

    @JvmStatic
    @BindingAdapter("bindSelectedItemId")
    fun bindSelectedItemId(view: BottomNavigationView, id: Int) {
        view.selectedItemId = id
    }

//    @JvmStatic
//    @BindingAdapter("bindNavigationItemSelectedLive")
//    fun bindNavigationItemSelectedListener(view: BottomNavigationView, live: MutableLiveData<Int>?) {
//        live?.let {
//            view.setOnNavigationItemSelectedListener { menu ->
//                live.value = menu.itemId
//
//                true
//            }
//        }
//    }
}