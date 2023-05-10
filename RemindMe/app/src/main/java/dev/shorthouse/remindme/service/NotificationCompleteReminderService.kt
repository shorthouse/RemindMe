package dev.shorthouse.remindme.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import dagger.hilt.android.AndroidEntryPoint
import dev.shorthouse.remindme.R
import dev.shorthouse.remindme.data.source.local.ReminderRepository
import dev.shorthouse.remindme.di.IoDispatcher
import dev.shorthouse.remindme.domain.reminder.CompleteReminderUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class NotificationCompleteReminderService : Service() {

    @Inject
    lateinit var reminderRepository: ReminderRepository

    @Inject
    lateinit var completeReminderUseCase: CompleteReminderUseCase

    @Inject
    @IoDispatcher
    lateinit var ioDispatcher: CoroutineDispatcher

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val reminderId = intent?.getLongExtra(getString(R.string.intent_key_reminderId), -1L)
        if (reminderId == null || reminderId == -1L) return START_NOT_STICKY

        completeReminder(reminderId)

        return super.onStartCommand(intent, flags, startId)
    }

    private fun completeReminder(reminderId: Long) {
        CoroutineScope(ioDispatcher + SupervisorJob()).launch {
            val reminder = reminderRepository.getReminderOneShot(reminderId)

            completeReminderUseCase(reminder)

            this@NotificationCompleteReminderService.stopSelf()
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
