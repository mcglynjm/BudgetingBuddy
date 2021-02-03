package edu.rosehulman.mcglynjm.budgetingbuddy

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.category_row.view.*
import kotlinx.android.synthetic.main.category_row.view.name_view

class BudgetCategoryViewHolder : RecyclerView.ViewHolder {
        var context: Context?

        var editButton = itemView.edit_category_button
        var toggleBox = itemView.toggle_category_checkbox
        var nameView = itemView.name_view

        constructor(itemView: View, adapter: EditAdapter, context: Context?) : super(itemView) {
            this.context = context
            editButton.setBackgroundResource(R.drawable.edit_button)
            editButton.setOnClickListener {
                //adapter.editCategory(adapterPosition)
            }

        }


        fun bind(category: BudgetCategory) {
            toggleBox.isSelected = category.isEnabled
            nameView.text = category.name
        }
}
