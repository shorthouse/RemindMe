package dev.shorthouse.habitbuilder.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import dev.shorthouse.habitbuilder.databinding.ListItemReminderBinding
import dev.shorthouse.habitbuilder.model.Reminder

class AllReminderListAdapter(
    private val clickListener: (Reminder) -> Unit)
    : AbstractReminderListAdapter(clickListener) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReminderViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ReminderViewHolder(
            ListItemReminderBinding.inflate(layoutInflater, parent, false)
        )
    }
}