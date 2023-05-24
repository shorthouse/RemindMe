package dev.shorthouse.remindme.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.work.BackoffPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import dagger.hilt.android.AndroidEntryPoint
import dev.shorthouse.remindme.worker.RescheduleReminderNotificationsWorker
import java.util.concurrent.TimeUnit
import javax.inject.Inject

private const val BACKOFF_DELAY_MILLIS = 10000L

@AndroidEntryPoint
class BootCompletedReceiver : BroadcastReceiver() {

    @Inject
    lateinit var workManager: WorkManager

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == "android.intent.action.BOOT_COMPLETED" && context != null) {
            val workRequest = OneTimeWorkRequestBuilder<RescheduleReminderNotificationsWorker>()
                .setBackoffCriteria(
                    BackoffPolicy.LINEAR,
                    BACKOFF_DELAY_MILLIS,
                    TimeUnit.MILLISECONDS
                )
                .build()

            workManager.enqueue(workRequest)
            Log.d("HDS", "work queued")
        }
    }
}
