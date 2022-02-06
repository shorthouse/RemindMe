package dev.shorthouse.habitbuilder.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import dev.shorthouse.habitbuilder.databinding.ListItemHabitBinding
import dev.shorthouse.habitbuilder.model.Habit

class HabitListAdapter(
    private val clickListener: (Habit) -> Unit
) : ListAdapter<Habit, HabitListAdapter.HabitViewHolder>(DiffCallback) {

    class HabitViewHolder(
        private var binding: ListItemHabitBinding
    ): RecyclerView.ViewHolder(binding.root) {

        fun bind(habit: Habit) {
            binding.habit = habit
            binding.executePendingBindings()
        }
    }

    companion object DiffCallback: DiffUtil.ItemCallback<Habit>() {
        override fun areItemsTheSame(oldItem: Habit, newItem: Habit): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Habit, newItem: Habit): Boolean {
            return oldItem == newItem
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HabitViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return HabitViewHolder(
            ListItemHabitBinding.inflate(layoutInflater, parent, false)
        )
    }

    override fun onBindViewHolder(holder: HabitViewHolder, position: Int) {
        val habit = getItem(position)
        holder.itemView.setOnClickListener{
            clickListener(habit)
        }
        holder.bind(habit)
    }
}