package brigitte.widget

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import javax.inject.Inject

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2019-07-15 <p/>
 */

open class StatusBarViewModel @Inject constructor(val app: Application) : ViewModel() {
    val statusColor = MutableLiveData<Int>()
}
