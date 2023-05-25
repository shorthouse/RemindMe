package dev.shorthouse.remindme.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.BackoffPolicy
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import dagger.hilt.android.AndroidEntryPoint
import dev.shorthouse.remindme.R
import dev.shorthouse.remindme.worker.UpdateRemindersTimeZoneWorker
import java.util.concurrent.TimeUnit
import javax.inject.Inject

private const val BACKOFF_DELAY_MILLIS = 10000L

@AndroidEntryPoint
class TimeZoneChangedReceiver : BroadcastReceiver() {

    @Inject
    lateinit var workManager: WorkManager

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action != Intent.ACTION_TIMEZONE_CHANGED || context == null) return

        val newTimeZone = intent.getStringExtra(context.getString(R.string.key_time_zone))

        val workRequest = OneTimeWorkRequestBuilder<UpdateRemindersTimeZoneWorker>()
            .setBackoffCriteria(
                BackoffPolicy.LINEAR,
                BACKOFF_DELAY_MILLIS,
                TimeUnit.MILLISECONDS
            )
            .setInputData(
                Data.Builder()
                    .putString(context.getString(R.string.key_time_zone), newTimeZone)
                    .build()
            )
            .build()

        workManager.enqueue(workRequest)
    }
}
