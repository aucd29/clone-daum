@file:Suppress("NOTHING_TO_INLINE", "unused")
package brigitte

import androidx.annotation.IdRes
import androidx.fragment.app.FragmentManager
import androidx.navigation.fragment.NavHostFragment

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2019-09-03 <p/>
 */

//fun View.navigate(@IdRes id: Int, bundle: Bundle? = null) =
//    Navigation.findNavController(this).navigate(id, bundle)

inline fun FragmentManager.obtainNavHostFragment(@IdRes id: Int) =
    findFragmentById(id) as NavHostFragment?
