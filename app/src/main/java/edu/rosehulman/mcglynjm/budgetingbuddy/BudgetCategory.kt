package edu.rosehulman.mcglynjm.budgetingbuddy

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.ServerTimestamp

data class BudgetCategory(
    var name: String = "",
    var amount: Double = 0.00,
    var isEnabled: Boolean = false) {
    @get:Exclude
    var id = ""
    @ServerTimestamp
    var lastTouched: Timestamp? = null

    companion object {
        const val LAST_TOUCHED_KEY = "lastTouched"

        fun fromSnapshot(snapshot: DocumentSnapshot): BudgetCategory {
            val movieQuote = snapshot.toObject(BudgetCategory::class.java)!!
            movieQuote.id = snapshot.id
            return movieQuote
        }
    }
}