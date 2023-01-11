//package dev.shorthouse.remindme.adapter
//
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import androidx.databinding.DataBindingUtil
//import androidx.navigation.findNavController
//import androidx.recyclerview.widget.DiffUtil
//import androidx.recyclerview.widget.ListAdapter
//import androidx.recyclerview.widget.RecyclerView
//import dev.shorthouse.remindme.R
//import dev.shorthouse.remindme.databinding.ListItemAllReminderBinding
//import dev.shorthouse.remindme.fragments.ListHomeFragmentDirections
//import dev.shorthouse.remindme.model.Reminder
//
//class AllListAdapter : ListAdapter<Reminder, AllListAdapter.ViewHolder>(AllReminderDiffCallback()) {
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//        return ViewHolder(
//            DataBindingUtil.inflate(
//                LayoutInflater.from(parent.context),
//                R.layout.list_item_all_reminder,
//                parent,
//                false
//            )
//        )
//    }
//
//    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        holder.bind(getItem(position))
//    }
//
//    class ViewHolder(private var binding: ListItemAllReminderBinding) : RecyclerView.ViewHolder(binding.root) {
//        fun bind(item: Reminder) {
//            binding.apply {
//                reminder = item
//
//                setDetailsClickListener { view ->
//                    navigateToReminderDetails(item.id, view)
//                }
//
//                executePendingBindings()
//            }
//        }
//
//        private fun navigateToReminderDetails(reminderId: Long, view: View) {
//            val action = ListHomeFragmentDirections.actionListContainerToDetails(reminderId)
//            view.findNavController().navigate(action)
//        }
//    }
//}
//
//private class AllReminderDiffCallback : DiffUtil.ItemCallback<Reminder>() {
//    override fun areItemsTheSame(oldItem: Reminder, newItem: Reminder) = oldItem.id == newItem.id
//    override fun areContentsTheSame(oldItem: Reminder, newItem: Reminder) = oldItem == newItem
//}
