package dev.shorthouse.remindme.utilities

import android.content.Context
import android.widget.ArrayAdapter
import android.widget.Filter
import dev.shorthouse.remindme.R

// Spinner array adapter to fix a known bug of material spinner where a recreation of the fragment
// (e.g upon screen rotation) results in the spinner drop-down only showing the currently selected item
// instead of all the possible items
// Solution class taken from https://github.com/material-components/material-components-android/issues/1464

class SpinnerArrayAdapter(context: Context, items: List<String>)
    : ArrayAdapter<String>(context, R.layout.spinner_item_repeat_interval, items) {

    private val noOpFilter = object : Filter() {
        private val noOpResult = FilterResults()
        override fun performFiltering(constraint: CharSequence?) = noOpResult
        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {}
    }

    override fun getFilter() = noOpFilter
}
