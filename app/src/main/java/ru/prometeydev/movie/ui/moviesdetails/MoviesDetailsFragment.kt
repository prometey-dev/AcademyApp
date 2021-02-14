package ru.prometeydev.movie.ui.moviesdetails

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.CalendarContract
import android.provider.CalendarContract.Events
import android.provider.Settings
import android.view.View
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresPermission
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.android.viewmodel.ext.android.viewModel
import ru.prometeydev.movie.R
import ru.prometeydev.movie.common.popBack
import ru.prometeydev.movie.common.showMessage
import ru.prometeydev.movie.model.domain.Movie
import ru.prometeydev.movie.ui.base.BaseFragment
import ru.prometeydev.movie.ui.movieslist.calculateStarsCount
import java.util.*


class MoviesDetailsFragment : BaseFragment<Movie>() {

    private val viewModel: MoviesDetailsViewModel by viewModel()

    private var recycler: RecyclerView? = null
    private var buttonBack: TextView? = null
    private var movieBackdrop: ImageView? = null
    private var ageLimit: TextView? = null
    private var movieName: TextView? = null
    private var movieGenre: TextView? = null
    private var rating: RatingBar? = null
    private var reviewsCount: TextView? = null
    private var timeMovie: TextView? = null
    private var overview: TextView? = null

    private var buttonSchedule: AppCompatButton? = null
    private var buttonTrailer: AppCompatButton? = null

    private var movieTitle = ""
    private var isRationaleShown = false
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>

    @SuppressLint("MissingPermission")
    override fun onAttach(context: Context) {
        super.onAttach(context)

        requestPermissionLauncher = registerForActivityResult(
                ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) {
                onCalendarPermissionGrated()
            } else {
                onCalendarPermissionNotGranted()
            }
        }
    }

    override fun onDetach() {
        requestPermissionLauncher.unregister()
        super.onDetach()
    }

    override fun layoutId() = R.layout.fragment_movies_details

    override fun initViews(view: View) {
        buttonBack = view.findViewById(R.id.button_back)
        movieBackdrop = view.findViewById(R.id.movie_logo)
        ageLimit = view.findViewById(R.id.age_limit)
        movieName = view.findViewById(R.id.movie_name)
        movieGenre = view.findViewById(R.id.movie_genre)
        rating = view.findViewById(R.id.rating)
        reviewsCount = view.findViewById(R.id.reviews_count)
        timeMovie = view.findViewById(R.id.time_movie)
        overview = view.findViewById(R.id.description)
        buttonSchedule = view.findViewById(R.id.schedule_watch)
        buttonTrailer = view.findViewById(R.id.watch_trailer)

        recycler = view.findViewById<RecyclerView>(R.id.actor_list).apply {
            layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)

            adapter = ActorsAdapter()

            val itemDecorator = DividerItemDecoration(context, DividerItemDecoration.HORIZONTAL)
            ContextCompat.getDrawable(context, R.drawable.shape_divider_actors)?.let {
                itemDecorator.setDrawable(it)
            }

            addItemDecoration(itemDecorator)
        }
    }

    override fun destroyViews() {
        buttonBack = null
        movieBackdrop = null
        ageLimit = null
        movieName = null
        movieGenre = null
        rating = null
        reviewsCount = null
        timeMovie = null
        overview = null
        recycler?.adapter = null
        recycler = null
    }

    override fun startObserve() {
        job?.cancel()
        job = lifecycleScope.launch {
            viewModel.stateFlow.collectLatest {
                this@MoviesDetailsFragment.setStateEvent(it)
            }
        }
    }

    override fun loadData() {
        arguments?.let {
            val movieId = it.getInt(MOVIE_ID)
            viewModel.loadMovie(movieId)
        }
    }

    override fun bindViews(data: Movie) {
        buttonBack?.setOnClickListener {
            popBack()
        }
        movieBackdrop?.load(data.backdrop)
        ageLimit?.text = getString(R.string.age_limit, data.minimumAge)
        movieName?.text = data.title
        movieGenre?.text = data.genres.joinToString { it.name }
        rating?.rating = data.ratings.calculateStarsCount()

        reviewsCount?.text = getString(R.string.reviews, data.numberOfRatings)

        timeMovie?.apply {
            this.text = getString(R.string.movie_time, data.runtime)
            this.isVisible = data.runtime != null
        }

        overview?.text = data.overview

        (recycler?.adapter as? ActorsAdapter)?.apply {
            bindActors(data.actors)
        }

        buttonSchedule?.setOnClickListener {
            movieTitle = data.title
            onAddToCalendar()
        }

        buttonTrailer?.setOnClickListener {
            onWatchTrailer(data.video ?: "")
        }
    }

    private fun onAddToCalendar() {
        when {
            ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.WRITE_CALENDAR)
                    == PackageManager.PERMISSION_GRANTED -> onCalendarPermissionGrated()
            shouldShowRequestPermissionRationale(Manifest.permission.WRITE_CALENDAR) ->
                showCalendarPermissionExplanationDialog()
            isRationaleShown -> showCalendarPermissionDeniedDialog()
            else -> requestCalendarPermission()
        }
    }

    private fun requestCalendarPermission() {
        requestPermissionLauncher.launch(Manifest.permission.WRITE_CALENDAR)
    }

    @RequiresPermission(Manifest.permission.WRITE_CALENDAR)
    private fun onCalendarPermissionGrated() {
        val calendarIntent  = Intent(Intent.ACTION_INSERT)
        calendarIntent.type = "vnd.android.cursor.item/event"
        calendarIntent.putExtra(Events.TITLE, getString(R.string.calendar_event_title, movieTitle))
        calendarIntent.putExtra(Events.DESCRIPTION, getString(R.string.button_schedule_watch))
        calendarIntent.putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, false)
        calendarIntent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, System.currentTimeMillis())
        calendarIntent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, System.currentTimeMillis())
        startActivity(calendarIntent)
    }

    private fun onCalendarPermissionNotGranted() {
        showMessage(getString(R.string.permission_not_granted_text))
    }

    private fun showCalendarPermissionExplanationDialog() {
        AlertDialog.Builder(requireContext())
                .setMessage(R.string.permission_dialog_explanation_text)
                .setPositiveButton(R.string.dialog_positive_button) { dialog, _ ->
                    isRationaleShown = true
                    requestCalendarPermission()
                    dialog.dismiss()
                }
                .setNegativeButton(R.string.dialog_negative_button) { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
    }

    private fun showCalendarPermissionDeniedDialog() {
        AlertDialog.Builder(requireContext())
                .setMessage(R.string.permission_dialog_denied_text)
                .setPositiveButton(R.string.dialog_positive_button) { dialog, _ ->
                    startActivity(
                        Intent(
                            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                            Uri.parse("package:" + requireContext().packageName)
                        )
                    )
                    dialog.dismiss()
                }
                .setNegativeButton(R.string.dialog_negative_button) { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
    }

    private fun onWatchTrailer(video: String) {
        if (video.isNotEmpty()) {
            Intent(Intent.ACTION_VIEW, Uri.parse(video)).also {
                startActivity(it)
            }
        } else {
            showMessage(getString(R.string.no_video_found_message))
        }
    }

    companion object {

        fun instance(movieId: Int) = MoviesDetailsFragment().apply {
            arguments = Bundle().apply {
                putInt(MOVIE_ID, movieId)
            }
        }

        private const val MOVIE_ID = "MOVIE_ID"

    }

}