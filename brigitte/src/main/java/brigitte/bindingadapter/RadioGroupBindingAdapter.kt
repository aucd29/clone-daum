@file:Suppress("NOTHING_TO_INLINE", "unused")
package brigitte.bindingadapter

import android.widget.RadioGroup
import androidx.databinding.BindingAdapter
import org.slf4j.LoggerFactory

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2019-08-12 <p/>
 */

object RadioGroupBindingAdapter {
    private val mLog = LoggerFactory.getLogger(RadioGroupBindingAdapter::class.java)

    @JvmStatic
    @BindingAdapter("bindCheckedChangeListener")
    fun bindCheckedChangeListener(view: RadioGroup, listener: ((Int) -> Unit)?) {
        if (mLog.isDebugEnabled) {
            mLog.debug("BIND CHECKED CHANGE LISTENER $listener")
        }

        listener?.let {
            view.setOnCheckedChangeListener { _, id ->
                it.invoke(id)
            }
        }
    }

    @JvmStatic
    @BindingAdapter("bindCheckId")
    fun bindCheckId(view: RadioGroup, id: Int) {
        if (mLog.isDebugEnabled) {
            mLog.debug("BIND CHECK ID $id")
        }

        view.check(id)
    }
}