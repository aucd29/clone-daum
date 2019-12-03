package brigitte.widget

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import javax.inject.Inject

/**
 * Created by <a href="mailto:aucd29@hanwha.com">Burke Choi</a> on 2019-07-15 <p/>
 */

open class StatusBarViewModel @Inject constructor(app: Application) : AndroidViewModel(app) {
    val statusColor = MutableLiveData<Int>()
}
