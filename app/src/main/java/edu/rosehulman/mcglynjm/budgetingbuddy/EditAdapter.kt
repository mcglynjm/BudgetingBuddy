package edu.rosehulman.mcglynjm.budgetingbuddy

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import edu.rosehulman.mcglynjm.budgetingbuddy.R.layout.category_row
import kotlinx.android.synthetic.main.dialog_edit_add.view.*

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

    fun showAddDialog(position: Int = -1){
        val builder = androidx.appcompat.app.AlertDialog.Builder(context)//AlertDialog.Builder(this)
        //set options
        builder.setTitle(
                if(position >= 0){
                    R.string.dialog_title_add
                }else{
                    R.string.dialog_title_edit
                })

        val view = LayoutInflater.from(context).inflate(R.layout.dialog_edit_add, null, false)

        builder.setView(view)

        if(position >= 0){
            view.category_edit_text.setText(categories[position].name)
            view.amount_edit_text.setText(categories[position].amount.toString())
        }

        builder.setPositiveButton(android.R.string.ok){_,_ ->
            //TODO
            val name = view.category_edit_text.text.toString()
            val amount = view.amount_edit_text.text.toString()
            if(position >= 0) {
                //edit(position, name, amount)
            }else{
                //add(BudgetCategory(name, amount, true))
            }
        }

        builder.setNegativeButton(android.R.string.cancel, null)

        builder.create().show()
    }

}