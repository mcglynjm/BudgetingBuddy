package edu.rosehulman.mcglynjm.budgetingbuddy

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class EditAdapter : RecyclerView.Adapter<BudgetCategoryViewHolder>() {
    private val categories = ArrayList<BudgetCategory>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BudgetCategoryViewHolder {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: BudgetCategoryViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        return categories.size
    }

}