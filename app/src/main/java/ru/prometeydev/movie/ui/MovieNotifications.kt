package ru.prometeydev.movie.ui

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.widget.RemoteViews
import androidx.annotation.IdRes
import androidx.annotation.WorkerThread
import androidx.core.app.*
import androidx.core.app.NotificationManagerCompat.IMPORTANCE_HIGH
import androidx.core.graphics.drawable.toBitmap
import androidx.core.net.toUri
import coil.ImageLoader
import coil.annotation.ExperimentalCoilApi
import coil.request.ImageRequest
import coil.target.Target
import ru.prometeydev.movie.R
import ru.prometeydev.movie.model.domain.Movie



class MovieNotifications(private val context: Context) {

    private val imageLoader = ImageLoader.Builder(context)
            .availableMemoryPercentage(0.25)
            .crossfade(true)
            .build()

    private val notificationManagerCompat = NotificationManagerCompat.from(context)

    fun initialize() {
        if (notificationManagerCompat.getNotificationChannel(NOTIFY_CHANNEL_ID) == null) {
            notificationManagerCompat.createNotificationChannel(
                NotificationChannelCompat.Builder(NOTIFY_CHANNEL_ID, IMPORTANCE_HIGH)
                        .setName(context.getString(R.string.chanel_new_top_rated_movie))
                        .setDescription(context.getString(R.string.chanel_new_top_rated_movie_description))
                        .build()
            )
        }
    }

    @ExperimentalCoilApi
    @WorkerThread
    suspend fun show(movie: Movie) {
        val contentUri = "https://movie.prometeydev.ru/movie/${movie.id}".toUri()

        val smallContentView = RemoteViews(context.packageName, R.layout.notification_small).apply {
            setTextViewText(R.id.appears, context.getString(R.string.chanel_new_top_rated_movie_description))
            setTextViewText(R.id.movie_name, movie.title)
            setTextViewText(R.id.rating, context.getString(R.string.rating_text, movie.ratings.toString()))
        }

        val bigContentView = RemoteViews(context.packageName, R.layout.notification_big).apply {
            setTextViewText(R.id.appears, context.getString(R.string.chanel_new_top_rated_movie_description))
            setTextViewText(R.id.movie_name, movie.title)
            setTextViewText(R.id.rating, context.getString(R.string.rating_text, movie.ratings.toString()))
            setTextViewText(R.id.genres, movie.genres.joinToString { it.name })
            setTextViewText(R.id.overview, movie.overview)
        }

        loadImage(smallContentView, R.id.movie_image, movie.poster)
        loadImage(bigContentView, R.id.movie_image, movie.poster)

        val notification = NotificationCompat.Builder(context, NOTIFY_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_movie_notify)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setOnlyAlertOnce(true)
                .setCustomContentView(smallContentView)
                .setCustomBigContentView(bigContentView)
                .setContentIntent(
                    PendingIntent.getActivity(
                            context,
                            REQUEST_CONTENT,
                            Intent(context, MainActivity::class.java)
                                    .setAction(Intent.ACTION_VIEW)
                                    .setData(contentUri),
                            PendingIntent.FLAG_UPDATE_CURRENT
                    )
                )
                .setWhen(System.currentTimeMillis())
                .build()

        notificationManagerCompat.notify(NOTIFY_TAG, movie.id, notification)
    }

    fun dismiss(movieId: Int) {
        notificationManagerCompat.cancel(NOTIFY_TAG, movieId)
    }

    @ExperimentalCoilApi
    private suspend fun loadImage(remoteViews: RemoteViews, resId: Int, uri: String?) {
        val target = RemoteViewsTarget(context, ComponentName(context.packageName, "MainActivity"), remoteViews, resId)
        val request = ImageRequest.Builder(context)
                .data(uri)
                .target(target)
                .build()
        imageLoader.enqueue(request).await()
    }

    companion object {
        private const val NOTIFY_TAG = "movie"
        private const val NOTIFY_CHANNEL_ID = "NOTIFY_CHANNEL"
        private const val REQUEST_CONTENT = 1
    }

    private class RemoteViewsTarget(
            private val context: Context,
            private val componentName: ComponentName,
            private val remoteViews: RemoteViews,
            @IdRes private val imageViewResId: Int
    ) : Target {

        override fun onStart(placeholder: Drawable?) = setDrawable(placeholder)

        override fun onError(error: Drawable?) = setDrawable(error)

        override fun onSuccess(result: Drawable) = setDrawable(result)

        private fun setDrawable(drawable: Drawable?) {
            remoteViews.setImageViewBitmap(imageViewResId, drawable?.toBitmap())
            AppWidgetManager.getInstance(context).updateAppWidget(componentName, remoteViews)
        }
    }

}