package edu.rosehulman.mcglynjm.budgetingbuddy

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.ServerTimestamp
import java.util.*

class ManualTransaction(var amount: Long = 0.toLong(), var type: String = "", var items: String = "", var renews: Renews = Renews.NEVER, var date: String) {
    @get:Exclude
    var id = ""
    @ServerTimestamp
    var lastTouched: Timestamp? = null

    companion object {
        const val LAST_TOUCHED_KEY = "lastTouched"

        fun fromSnapshot(snapshot: DocumentSnapshot): ManualTransaction {
            val transaction = snapshot.toObject(ManualTransaction::class.java)!!
            transaction.id = snapshot.id
            return transaction
        }
    }
}