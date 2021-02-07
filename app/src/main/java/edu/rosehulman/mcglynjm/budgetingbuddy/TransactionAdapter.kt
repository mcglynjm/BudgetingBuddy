package edu.rosehulman.mcglynjm.budgetingbuddy

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.*
import kotlinx.android.synthetic.main.dialog_add_funds.view.*

class TransactionAdapter(var context: Context, var uid: String) : RecyclerView.Adapter<TransactionViewHolder>() {
    private val transactions = ArrayList<ManualTransaction>()

    //TODO set up firebase listener and init
    private val transactionsRef = FirebaseFirestore
        .getInstance()
        .collection(Constants.USERS_COLLECTION)
        .document(uid)
        .collection(Constants.TRANSACTIONS_COLLECTION)

    private lateinit var listenerRegistration: ListenerRegistration

    fun addSnapshotListener() {
        Log.d(Constants.TAG, uid)
        listenerRegistration = transactionsRef
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
            val transaction = ManualTransaction.fromSnapshot(documentChange.document)
            when (documentChange.type) {
                DocumentChange.Type.ADDED -> {
                    Log.d(Constants.TAG, "Adding $transaction")
                    transactions.add(0, transaction)
                    notifyItemInserted(0)
                }
                DocumentChange.Type.REMOVED -> {
                    Log.d(Constants.TAG, "Removing $transaction")
                    val index = transactions.indexOfFirst { it.id == transaction.id }
                    transactions.removeAt(index)
                    notifyItemRemoved(index)
                }
                DocumentChange.Type.MODIFIED -> {
                    Log.d(Constants.TAG, "Modifying $transaction")
                    val index = transactions.indexOfFirst { it.id == transaction.id }
                    transactions[index] = transaction
                    notifyItemChanged(index)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.transaction_history_view, parent, false)
        return TransactionViewHolder(view, this, context)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        holder.bind(transactions[position])
    }

    override fun getItemCount(): Int {
        return transactions.size
    }

    fun selectTransaction(position: Int) {
        (context as TransactionSelect).onTransactionSelected(transactions.get(position))
    }

    fun displaySearchDialog() {
            val builder = androidx.appcompat.app.AlertDialog.Builder(context)//AlertDialog.Builder(this)
            //set options
            builder.setTitle(R.string.search_transactions)
            val view = LayoutInflater.from(context).inflate(R.layout.dialog_search_transactions, null, false)
            builder.setView(view)
            builder.setPositiveButton(android.R.string.ok) { _, _ ->
                val date = view.amount_edit_text.text.toString()
                if(date.equals("")) {
                    //set back to all
                    showAllTransactions()
                }
                else {
                    //set to searched
                    showSearchedTransactions(date)
                }
            }
            builder.setNegativeButton(android.R.string.cancel, null)
            builder.create().show()
        }

    fun showSearchedTransactions(date: String) {
        listenerRegistration.remove()
        transactions.clear()
        notifyDataSetChanged()
        transactionsRef.whereGreaterThan("date", date)
            .addSnapshotListener { querySnapshot: QuerySnapshot?, e: FirebaseFirestoreException? ->
                if (e != null) {
                    Log.e(Constants.TAG, "Listen error: $e")
                    return@addSnapshotListener
                } else {
                    processSnapshotChanges(querySnapshot!!)
                }
            }
    }

    fun showAllTransactions() {
        listenerRegistration.remove()
        transactions.clear()
        notifyDataSetChanged()
        addSnapshotListener()
    }
    }

interface TransactionSelect {
    fun onTransactionSelected(transaction: ManualTransaction)
}