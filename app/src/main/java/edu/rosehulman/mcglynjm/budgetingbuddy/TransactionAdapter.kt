package edu.rosehulman.mcglynjm.budgetingbuddy

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class TransactionAdapter(var context: Context) : RecyclerView.Adapter<TransactionViewHolder>() {
    private val transactions = ArrayList<ManualTransaction>()

    //TODO set up firebase listener and init

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        TODO("Not yet implemented")
        
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        return transactions.size
    }

    fun selectTransaction(position: Int) {
        (context as TransactionSelect).onTransactionSelected(transactions.get(position))
    }
}

interface TransactionSelect {
    fun onTransactionSelected(transaction: ManualTransaction)
}