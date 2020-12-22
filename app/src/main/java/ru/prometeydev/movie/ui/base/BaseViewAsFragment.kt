package ru.prometeydev.movie.ui.base

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel

abstract class BaseViewAsFragment<VM: ViewModel> :
    FragmentActivity(),
    BaseView<VM> {

    lateinit var viewModel: VM

}