 package edu.rosehulman.mcglynjm.budgetingbuddy

import android.content.Context
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.category_row.view.*
import kotlinx.android.synthetic.main.category_row.view.name_view

class BudgetCategoryViewHolder : RecyclerView.ViewHolder {
        var context: Context?

        lateinit var adapter: EditAdapter

        var editButton = itemView.edit_category_button
        var toggleBox = itemView.toggle_category_checkbox
        var nameView = itemView.name_view
        var amountView = itemView.amount_view

        constructor(itemView: View, eAdapter: EditAdapter, context: Context?) : super(itemView) {
            this.context = context
            this.adapter = eAdapter
            editButton.setBackgroundResource(R.drawable.edit_button)
            editButton.setOnClickListener {
                adapter.showAddDialog(adapterPosition)
            }

        }


        fun bind(category: BudgetCategory) {
            toggleBox.isChecked = category.enabled
            nameView.text = category.name
            amountView.text = "$${category.amount}"

            toggleBox.setOnClickListener {
                adapter.changeEnabled(adapterPosition, toggleBox.isChecked)
                Log.d(Constants.TAG, "Set ${category.name} to ${toggleBox.isChecked}")
            }
        }
}
