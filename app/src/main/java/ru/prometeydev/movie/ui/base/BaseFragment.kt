package ru.prometeydev.movie.ui.base

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import ru.prometeydev.movie.R
import ru.prometeydev.movie.common.showMessage

open class BaseFragment : Fragment() {

    private var loader: View? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loader = view.findViewById(R.id.loader)
    }

    protected fun onError(error: String) = showMessage(error)

    protected fun handleLoading(state: StateLoading) {
        when (state) {
            is StateLoading.Loading -> loader?.isVisible = true
            else -> loader?.isVisible = false
        }
    }

}