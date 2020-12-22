package ru.prometeydev.movie.ui.base

import android.os.Bundle
import androidx.lifecycle.ViewModel
import ru.prometeydev.movie.common.asFragmentScreen
import ru.prometeydev.movie.common.popBack
import ru.prometeydev.movie.common.show

abstract class NavigableFragment<VM: ViewModel> :
    BaseViewAsFragment<VM>() {

    override fun navigateTo(step: String) {
        show(step.asFragmentScreen())
    }

    override fun goBack() = popBack()

    companion object {

        const val STEP = "STEP"
        const val LAYOUT = "LAYOUT"

        fun bundle(step: String, layout: Int) =
            Bundle().apply {
                putString(STEP, step)
                putInt(LAYOUT, layout)
            }

    }

}