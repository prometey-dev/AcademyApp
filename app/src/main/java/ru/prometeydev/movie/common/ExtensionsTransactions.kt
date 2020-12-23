package ru.prometeydev.movie.common

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import ru.prometeydev.movie.R

fun FragmentActivity.setAsRoot(fragment: Fragment) = ignoreIllegalStateException {
    supportFragmentManager.beginTransaction()
        .add(R.id.main_container, fragment)
        .commit()
}

fun Fragment.show(fragment: Fragment) = ignoreIllegalStateException {
    parentFragmentManager.beginTransaction()
        .replace(R.id.main_container, fragment)
        .addToBackStack(null)
        .commit()
}

fun Fragment.popBack() = ignoreIllegalStateException {
    parentFragmentManager.popBackStack()
}

private fun ignoreIllegalStateException(action: () -> Unit) {
    try {
        action.invoke()
    } catch (ignored: IllegalStateException) {
        Log.w("IllegalStateException", ignored)
    }
}