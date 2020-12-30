package ru.prometeydev.movie.ui.base

import android.os.Bundle
import androidx.fragment.app.Fragment
import ru.prometeydev.movie.common.showMessage

open class BaseFragment : Fragment() {

    protected fun onError(error: String) = showMessage(error)

}