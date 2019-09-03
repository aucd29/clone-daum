package brigitte

import android.os.Bundle
import android.view.View
import androidx.annotation.IdRes
import androidx.navigation.Navigation

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2019-09-03 <p/>
 */

fun View.navigate(@IdRes id: Int, bundle: Bundle? = null) =
    Navigation.findNavController(this).navigate(id, bundle)