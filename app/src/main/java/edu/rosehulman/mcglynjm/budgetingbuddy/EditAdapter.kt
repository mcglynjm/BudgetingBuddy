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

    private val userRef = FirebaseFirestore
    .getInstance()
    .collection(Constants.USERS_COLLECTION)
    .document(uid)

    private val categoriesRef = userRef
        .collection(Constants.CATEGORIES_COLLECTION)

    private val transactionsRef = userRef
        .collection(Constants.TRANSACTIONS_COLLECTION)

    private lateinit var listenerRegistration: ListenerRegistration

    fun addSnapshotListener() {
        Log.d(Constants.TAG, uid)
        listenerRegistration = categoriesRef
            .orderBy(BudgetCategory.LAST_TOUCHED_KEY, Query.Direction.ASCENDING)
            .addSnapshotListener { querySnapshot, e ->
                if (e != null) {
                    Log.d(Constants.TAG, "Adding listener failed")
                    Log.w(Constants.TAG, "listen error", e)
                } else {
                    Log.d(Constants.TAG, "Listener Added")
                    processSnapshotChanges(querySnapshot!!)
                }
            }
    }

    private fun processSnapshotChanges(querySnapshot: QuerySnapshot) {
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


        userRef.get().addOnSuccessListener { snapshot: DocumentSnapshot ->
            var monthlyRemaining = (snapshot.getDouble("monthlyRemaining")  ?: 0.00)as Double

            monthlyRemaining += budgetCategory.amount

            Log.d(Constants.TAG, "added $${budgetCategory.amount} for ${budgetCategory.name} now $$monthlyRemaining monthly")

            userRef.set(mapOf("monthlyRemaining" to monthlyRemaining),
                SetOptions.merge()
            )
        }
    }

    private fun edit(position: Int, name: String, amount: Double) {
        val budgetCategory  =  categories[position]
        val amountChange = amount - budgetCategory.amount
        val oldName = budgetCategory.name
        budgetCategory.name = name
        budgetCategory.amount = amount
        categoriesRef.document(categories[position].id).set(categories[position])

        transactionsRef.whereEqualTo("type", oldName).get()
            .addOnSuccessListener{documents ->
                for (document in documents){
                    transactionsRef.document(document.id).update("type", name)
                }
            }


        userRef.get().addOnSuccessListener { snapshot: DocumentSnapshot ->
            var monthlyRemaining = (snapshot.getDouble("monthlyRemaining")  ?: 0.00)as Double

            monthlyRemaining += amountChange

            Log.d(Constants.TAG, "adjusted ${budgetCategory.name} by $${amountChange} to $${budgetCategory.amount} now $$monthlyRemaining monthly")

            userRef.set(mapOf("monthlyRemaining" to monthlyRemaining),
                SetOptions.merge()
            )
        }
    }

    fun changeEnabled(position: Int, isEnabled: Boolean){
        val category = categories[position]
        category.enabled = isEnabled
        categoriesRef.document(categories[position].id).set(categories[position])

        userRef.get().addOnSuccessListener { snapshot: DocumentSnapshot ->
            var monthlyRemaining = (snapshot.getDouble("monthlyRemaining")  ?: 0.00)as Double

            if(isEnabled) {
                monthlyRemaining += category.amount
                Log.d(Constants.TAG, "added $${category.amount} for ${category.name} now $$monthlyRemaining monthly")
            }else{
                monthlyRemaining -= category.amount
                Log.d(Constants.TAG, "removed $${category.amount} for ${category.name} now $$monthlyRemaining monthly")
            }

            userRef.set(mapOf("monthlyRemaining" to monthlyRemaining),
                SetOptions.merge()
            )
        }
    }

}