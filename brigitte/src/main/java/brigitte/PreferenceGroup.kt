@file:Suppress("NOTHING_TO_INLINE", "unused")
package brigitte

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.fragment.app.Fragment
import androidx.lifecycle.AndroidViewModel

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2018. 1. 24.. <p/>
 */
@SuppressLint("CommitPrefEdits")

const val K_SHARED_PREFERECE = "burke.prefs"

inline fun Context.prefs(): SharedPreferences = getSharedPreferences(K_SHARED_PREFERECE, Context.MODE_PRIVATE)
inline fun Fragment.prefs(): SharedPreferences = requireContext().prefs()
inline fun AndroidViewModel.prefs(): SharedPreferences = getApplication<Application>().prefs()


// set : prefs().edit { putBoolean(K_RECENT_SEARCH, showSearchRecyclerLayout) }
// get : prefs().getBoolean(K_RECENT_SEARCH, true)
inline fun Context.prefs(commit: Boolean = true, action: SharedPreferences.Editor.() -> Unit) {
    prefs().edit(commit, action)
}

inline fun Fragment.prefs(commit: Boolean = true, action: SharedPreferences.Editor.() -> Unit) {
    requireContext().prefs(commit, action)
}

inline fun AndroidViewModel.prefs(commit: Boolean = true, action: SharedPreferences.Editor.() -> Unit) {
    getApplication<Application>().prefs(commit, action)
}

////////////////////////////////////////////////////////////////////////////////////
//
// SHARED PREFERENCE STRING
//
////////////////////////////////////////////////////////////////////////////////////

inline fun SharedPreferences.Editor.string(key: String, value: String?): SharedPreferences.Editor =
        putString(key, value)

inline fun SharedPreferences.Editor.encodeString(key: String, value: String?): SharedPreferences.Editor =
        putString(key, value?.encodeBase64())

inline fun SharedPreferences.decodeString(key: String, defaultValue: String? = null): String? =
        getString(key, defaultValue)?.decodeBase64()

inline fun SharedPreferences.string(key: String, defaultValue: String? = null): String? =
        getString(key, defaultValue)

////////////////////////////////////////////////////////////////////////////////////
//
// SHARED PREFERENCE BOOLEAN
//
////////////////////////////////////////////////////////////////////////////////////

inline fun SharedPreferences.Editor.bool(key: String, value: Boolean): SharedPreferences.Editor {
    return putBoolean(key, value)
}

inline fun SharedPreferences.bool(key: String, defaultValue: Boolean = false) =
        getBoolean(key, defaultValue)

////////////////////////////////////////////////////////////////////////////////////
//
// SHARED PREFERENCE INT
//
////////////////////////////////////////////////////////////////////////////////////

inline fun SharedPreferences.Editor.int(key: String, value: Int) =
        putInt(key, value)

inline fun SharedPreferences.int(key: String, defaultValue: Int = 0) =
        getInt(key, defaultValue)

////////////////////////////////////////////////////////////////////////////////////
//
// SHARED PREFERENCE FLOAT
//
////////////////////////////////////////////////////////////////////////////////////

inline fun SharedPreferences.Editor.float(key: String, value: Float) =
        putFloat(key, value)

inline fun SharedPreferences.float(key: String, defaultValue: Float = 0f) =
        getFloat(key, defaultValue)

////////////////////////////////////////////////////////////////////////////////////
//
// SHARED PREFERENCE LONG
//
////////////////////////////////////////////////////////////////////////////////////

inline fun SharedPreferences.Editor.long(key: String, value: Long) =
        putLong(key, value)

inline fun SharedPreferences.long(key: String, defaultValue: Long = 0) =
        getLong(key, defaultValue)

////////////////////////////////////////////////////////////////////////////////////
//
// KTX
//
////////////////////////////////////////////////////////////////////////////////////

// https://github.com/android/android-ktx/blob/master/src/main/java/androidx/core/content/SharedPreferences.kt

/**
 * Allows editing of this preference instance with a call to
 * [apply][SharedPreferences.Editor.apply] or
 * [commit][SharedPreferences.Editor.commit] to persist the changes.
 *
 * Default behaviour is [apply][SharedPreferences.Editor.apply].
 *
 * ```
 * prefs.edit {
 *     putString("key", value)
 * }
 * ```
 * To [commit][SharedPreferences.Editor.commit] changes:
 * ```
 * prefs.edit(commit = true) {
 *     putString("key", value)
 * }
 * ```
 */
@SuppressLint("ApplySharedPref")
inline fun SharedPreferences.edit(commit: Boolean = true,
                                  action: SharedPreferences.Editor.() -> Unit) {
    val editor = edit()
    action(editor)

    if (commit) {
        editor.commit()
    } else {
        editor.apply()
    }
}

