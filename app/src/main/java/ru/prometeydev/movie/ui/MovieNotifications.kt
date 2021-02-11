package ru.prometeydev.movie.ui

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import androidx.annotation.WorkerThread
import androidx.core.app.*
import androidx.core.app.NotificationManagerCompat.IMPORTANCE_HIGH
import androidx.core.graphics.drawable.IconCompat
import androidx.core.net.toUri
import ru.prometeydev.movie.R
import ru.prometeydev.movie.model.domain.Movie

class MovieNotifications(private val context: Context) {

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

    @WorkerThread
    fun show(movie: Movie) {
        val icon = IconCompat.createWithContentUri(movie.poster)
        val contentUri = "https://movie.prometeydev.ru/movie/${movie.id}".toUri()
        val person = Person.Builder()
                .setName(movie.title)
                .setIcon(icon)
                .build()

        /*val small = RemoteViews(context.packageName, R.layout.notification_small).apply {
            setTextViewText(R.id.movie_name, movie.title)
        }

        val big = RemoteViews(context.packageName, R.layout.notification_big).apply {
            setTextViewText(R.id.movie_name, movie.title)
        }*/

        val notification = NotificationCompat.Builder(context, NOTIFY_CHANNEL_ID)
                .setContentTitle(movie.title)
                .setContentText(context.getString(R.string.new_top_rated_movie_with_rating, movie.ratings.toString()))
                .setSmallIcon(R.drawable.ic_movie_notify)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setOnlyAlertOnce(true)
                //.setCustomContentView(small)
                //.setCustomBigContentView(big)
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
                //.setStyle(NotificationCompat.MessagingStyle(person))
                .setWhen(System.currentTimeMillis())
                .build()

        notificationManagerCompat.notify(NOTIFY_TAG, movie.id, notification)
    }

    fun dismiss(movieId: Int) {
        notificationManagerCompat.cancel(NOTIFY_TAG, movieId)
    }

    companion object {
        private const val NOTIFY_TAG = "movie"
        private const val NOTIFY_CHANNEL_ID = "NOTIFY_CHANNEL"
        private const val REQUEST_CONTENT = 1
    }

}