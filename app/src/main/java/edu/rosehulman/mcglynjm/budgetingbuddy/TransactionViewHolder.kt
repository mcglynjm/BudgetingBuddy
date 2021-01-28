package edu.rosehulman.mcglynjm.budgetingbuddy

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.category_row.view.*
import kotlinx.android.synthetic.main.category_row.view.name_view
import kotlinx.android.synthetic.main.transaction_history_view.view.*

class TransactionViewHolder : RecyclerView.ViewHolder {
    var context: Context?
    var dateView = itemView.date_view
    var nameView = itemView.name_view

    var amountView = itemView.amount_view
    constructor(itemView: View, adapter: TransactionAdapter, context: Context?) : super(itemView) {
        this.context = context
        itemView.transaction_card_view.setOnClickListener {
            //TODO add transaction detail fragment here
            adapter.selectTransaction(adapterPosition)
        }
    }


    fun bind(transaction: ManualTransaction) {
        nameView.text = transaction.type
        dateView.text = transaction.date
        amountView.text = transaction.amount.toString()
    }

}
