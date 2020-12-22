package ru.prometeydev.movie.ui.base

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel

interface BaseView<VM: ViewModel> : CanHandleNavigation, CanHandleLoading