package edu.rosehulman.mcglynjm.budgetingbuddy

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.*
import edu.rosehulman.mcglynjm.budgetingbuddy.R.layout.category_row
import kotlinx.android.synthetic.main.dialog_edit_add.view.*

class EditAdapter(var context: Context, var uid: String) : RecyclerView.Adapter<BudgetCategoryViewHolder>() {
    private val categories = ArrayList<BudgetCategory>()

    private val categoriesRef = FirebaseFirestore
        .getInstance()
        .collection(Constants.USERS_COLLECTION)
        .document(uid)
        .collection(Constants.CATEGORIES_COLLECTION)

    private lateinit var listenerRegistration: ListenerRegistration

    fun addSnapshotListener() {
        Log.d(Constants.TAG, uid)
        listenerRegistration = categoriesRef
            .orderBy(BudgetCategory.LAST_TOUCHED_KEY, Query.Direction.ASCENDING)
            .addSnapshotListener { querySnapshot, e ->
                if (e != null) {
                    Log.w(Constants.TAG, "listen error", e)
                } else {
                    processSnapshotChanges(querySnapshot!!)
                }
            }
    }

    private fun processSnapshotChanges(querySnapshot: QuerySnapshot) {
        // Snapshots has documents and documentChanges which are flagged by type,
        // so we can handle C,U,D differently.
        for (documentChange in querySnapshot.documentChanges) {
            val budgetCategory = BudgetCategory.fromSnapshot(documentChange.document)
            when (documentChange.type) {
                DocumentChange.Type.ADDED -> {
                    Log.d(Constants.TAG, "Adding $budgetCategory")
                    categories.add(0, budgetCategory)
                    notifyItemInserted(0)
                }
                DocumentChange.Type.REMOVED -> {
                    Log.d(Constants.TAG, "Removing $budgetCategory")
                    val index = categories.indexOfFirst { it.id == budgetCategory.id }
                    categories.removeAt(index)
                    notifyItemRemoved(index)
                }
                DocumentChange.Type.MODIFIED -> {
                    Log.d(Constants.TAG, "Modifying $budgetCategory")
                    val index = categories.indexOfFirst { it.id == budgetCategory.id }
                    categories[index] = budgetCategory
                    notifyItemChanged(index)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, index: Int): BudgetCategoryViewHolder {

        Log.d(Constants.TAG, "Creating view holder")

        val view = LayoutInflater.from(context).inflate(R.layout.category_row, parent, false)
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
            val amount = view.amount_edit_text.text.toString().toDouble()
            if(position >= 0) {
                edit(position, name, amount)
            }else{
                add(BudgetCategory(name, amount, true))
            }
        }

        builder.setNegativeButton(android.R.string.cancel, null)

        builder.create().show()
    }

    private fun add(budgetCategory: BudgetCategory) {
        categoriesRef.add(budgetCategory)
    }

    private fun edit(position: Int, name: String, amount: Double) {
        categories[position].name = name
        categories[position].amount = amount
        categoriesRef.document(categories[position].id).set(categories[position])
    }

}