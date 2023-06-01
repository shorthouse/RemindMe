package dev.shorthouse.remindme.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dagger.hilt.android.AndroidEntryPoint
import dev.shorthouse.remindme.R
import dev.shorthouse.remindme.data.Result
import dev.shorthouse.remindme.domain.reminder.CompleteReminderUseCase
import dev.shorthouse.remindme.domain.reminder.GetReminderUseCase
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

@AndroidEntryPoint
class NotificationActionDoneReceiver : BroadcastReceiver() {

    @Inject
    lateinit var getReminderUseCase: GetReminderUseCase

    @Inject
    lateinit var completeReminderUseCase: CompleteReminderUseCase

    override fun onReceive(context: Context?, intent: Intent?) {
        val reminderId = intent?.getLongExtra(
            context?.getString(R.string.intent_key_reminderId),
            -1L
        )

        if (reminderId == null || reminderId == -1L) return

        CoroutineScope(SupervisorJob()).launch {
            val result = getReminderUseCase(reminderId)

            if (result is Result.Success) {
                completeReminderUseCase(result.data)
            }
        }
    }
}
