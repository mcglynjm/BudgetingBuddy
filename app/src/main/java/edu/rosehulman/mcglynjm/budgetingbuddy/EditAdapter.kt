package edu.rosehulman.mcglynjm.budgetingbuddy

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import edu.rosehulman.mcglynjm.budgetingbuddy.R.layout.category_row

class EditAdapter(var context: Context) : RecyclerView.Adapter<BudgetCategoryViewHolder>() {
    private val categories = ArrayList<BudgetCategory>()

    override fun onCreateViewHolder(parent: ViewGroup, index: Int): BudgetCategoryViewHolder {

        Log.d(Constants.TAG, "Creating view holder")

        val view = LayoutInflater.from(context).inflate(category_row, parent, false)
        return BudgetCategoryViewHolder(view, this, context)
    }

    override fun onBindViewHolder(holder: BudgetCategoryViewHolder, position: Int) {
        holder.bind(categories[position])
    }

    override fun getItemCount(): Int {
        return categories.size
    }

}