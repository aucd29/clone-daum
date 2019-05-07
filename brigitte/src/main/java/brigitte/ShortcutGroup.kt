@file:Suppress("NOTHING_TO_INLINE", "unused")

package brigitte

import android.app.Activity
import android.content.Intent
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.drawable.Icon
import android.net.Uri
import android.os.Build
import androidx.fragment.app.Fragment

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2019. 4. 3. <p/>
 */

inline fun Activity.shortcut(params: ShortcutParams) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(params.link))

    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N_MR1) {
        systemService<ShortcutManager>()?.apply {
            if (isRequestPinShortcutSupported) {
                requestPinShortcut(ShortcutInfo.Builder(applicationContext, params.link)
                    .setShortLabel(params.shortLabel)
                    .setLongLabel(params.longLabel)
                    .setIcon(Icon.createWithResource(applicationContext, params.icon))
                    .setIntent(intent)
                    .build(), null)
            }
        }
    } else {
        val i = Intent()
        intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent)
        intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, params.longLabel)
        intent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
            Intent.ShortcutIconResource.fromContext(applicationContext, params.icon))
        intent.putExtra("duplicate", false)

        // add the shortcut
        i.action = "com.android.launcher.action.INSTALL_SHORTCUT"
        sendBroadcast(i)
    }
}

inline fun Fragment.shortcut(params: ShortcutParams) {
    requireActivity().shortcut(params)
}

data class ShortcutParams(
    val link: String,
    val icon: Int,
    val longLabel: String = "",
    val shortLabel: String = ""
)