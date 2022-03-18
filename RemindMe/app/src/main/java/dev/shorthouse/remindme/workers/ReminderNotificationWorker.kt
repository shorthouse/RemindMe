package dev.shorthouse.remindme.worker

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters


class ReminderNotificationWorker(appContext: Context, workerParams: WorkerParameters) :
    Worker(appContext, workerParams) {
    override fun doWork(): Result {

        // Do the work here


        // Indicate whether the work finished successfully with the Result
        return Result.success()
    }
}
