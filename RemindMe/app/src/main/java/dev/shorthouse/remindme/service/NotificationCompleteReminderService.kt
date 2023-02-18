package dev.shorthouse.remindme.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import dagger.hilt.android.AndroidEntryPoint
import dev.shorthouse.remindme.R
import dev.shorthouse.remindme.data.ReminderRepository
import dev.shorthouse.remindme.di.IoDispatcher
import dev.shorthouse.remindme.domain.CompleteOnetimeReminderUseCase
import dev.shorthouse.remindme.domain.CompleteRepeatReminderOccurrenceUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class NotificationCompleteReminderService : Service() {

    @Inject
    @IoDispatcher
    lateinit var ioDispatcher: CoroutineDispatcher

    @Inject
    lateinit var repository: ReminderRepository

    @Inject
    lateinit var completeOnetimeReminderUseCase: CompleteOnetimeReminderUseCase

    @Inject
    lateinit var completeRepeatReminderOccurrenceUseCase: CompleteRepeatReminderOccurrenceUseCase

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val reminderId = intent?.getLongExtra(getString(R.string.intent_key_reminderId), -1L)
        if (reminderId == null || reminderId == -1L) return START_NOT_STICKY

        completeReminder(reminderId)

        return super.onStartCommand(intent, flags, startId)
    }

    private fun completeReminder(reminderId: Long) {
        CoroutineScope(ioDispatcher + SupervisorJob()).launch {
            val reminder = repository.getReminderOneShot(reminderId)

            if (reminder.isRepeatReminder()) {
                completeRepeatReminderOccurrenceUseCase(reminder)
            } else {
                completeOnetimeReminderUseCase(reminder)
            }

            this@NotificationCompleteReminderService.stopSelf()
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
