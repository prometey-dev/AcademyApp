package ru.prometeydev.movie.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import ru.prometeydev.movie.R
import ru.prometeydev.movie.common.showMessage

abstract class BaseFragment : Fragment() {

    private var loader: View? = null

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? = inflater.inflate(layoutId(), container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loader = view.findViewById(R.id.loader)

        initViews(view)
        startObserve()
    }

    override fun onStart() {
        super.onStart()

        loadData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        destroyViews()
    }

    protected fun <T> setStateEvent(event: Event<T>) {
        when (event.status) {
            Event.Status.LOADING -> {
                loader?.isVisible = true
            }
            Event.Status.ERROR -> {
                loader?.isVisible = false
                showMessage(event.error ?: "")
            }
            Event.Status.SUCCESS -> {
                loader?.isVisible = false
                bindViews(event.data)
            }
        }
    }

    abstract fun layoutId(): Int
    abstract fun initViews(view: View)
    abstract fun startObserve()
    abstract fun destroyViews()
    abstract fun loadData()
    abstract fun <T> bindViews(data: T)

}