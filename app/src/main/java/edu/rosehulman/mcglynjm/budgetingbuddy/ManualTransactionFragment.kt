package edu.rosehulman.mcglynjm.budgetingbuddy

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.manual_transaction.*
import kotlinx.android.synthetic.main.manual_transaction.view.*
import java.lang.RuntimeException
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ManualTransactionFragment(var uid: String) : Fragment() {
    //TODO add listener here
    private val usersRef = FirebaseFirestore
        .getInstance()
        .collection(Constants.USERS_COLLECTION)
        .document(uid)

    private val transactionsRef = FirebaseFirestore
        .getInstance()
        .collection(Constants.USERS_COLLECTION)
        .document(uid)
        .collection(Constants.TRANSACTIONS_COLLECTION)

    lateinit var theContext: Context
    lateinit var renewsLayout: RelativeLayout
    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val fragmentViewer = context as FragmentViewer
        val view = inflater.inflate(R.layout.manual_transaction, container, false)
        renewsLayout = view.renew_layout
        view.cancel_button.setOnClickListener { fragmentViewer.onButtonHit(context!!.getString(R.string.home)) }

        view.ok_button.setOnClickListener {
            if(amount_edit_text_view.text.length == 1){
                amount_edit_text_view.setText("$0")
            }
            makeNewTransaction(amount_edit_text_view.text.toString(), type_edit_text_view.text.toString(), item_edit_text_view.text.toString(), calcRenews(renewsLayout))
             fragmentViewer.onButtonHit(context!!.getString(R.string.home))
        }
        return view
    }

    private fun makeNewTransaction(amount: String, type: String, items: String, renews: Renews) {
        //TODO
        //add to firebase here
        //from https://www.programiz.com/kotlin-programming/examples/current-date-time
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy")
        val formatted = current.format(formatter)
        Log.d(Constants.TAG, "DATE = $formatted")
        Log.d(Constants.TAG, "AMOUNT = $amount")
        Log.d(Constants.TAG, "RENEWS = ${renews.name}")
        val transaction = ManualTransaction(amount.subSequence(1, amount.length).toString().toDouble(), type, items, renews, formatted)
        //update user remaining balance
//        var monthlyRemaining: Double?
//        var totalRemaining: Double?
        usersRef.get().addOnSuccessListener { snapshot: DocumentSnapshot ->
            //var monthlyRemaining = (snapshot["monthlyRemaining"] ?: "") as Float
            var monthlyRemaining = snapshot.getDouble("monthlyRemaining") as Double
            var totalRemaining = snapshot.getDouble("remainingFunds") as Double
            Log.d(Constants.TAG, "monthlyRemaining: $monthlyRemaining")
            monthlyRemaining -= transaction.amount
            totalRemaining -= transaction.amount
            Log.d(Constants.TAG, "monthlyRemaining: $monthlyRemaining")
            usersRef.update("monthlyRemaining", monthlyRemaining)
            usersRef.update("remainingFunds", totalRemaining)
        }
        //add to firebase
        transactionsRef.add(transaction)
    }

    private fun calcRenews(renewLayout: RelativeLayout): Renews {
        if (renewLayout.month_button.isChecked == true) {
            return Renews.MONTH_1
        } else if (renewLayout.month_button3.isChecked == true) {
            return Renews.MONTH_3
        } else if (renewLayout.month_button4.isChecked == true) {
            return Renews.MONTH_4
        } else if (renewLayout.month_button6.isChecked == true) {
            return Renews.MONTH_6
        } else if (renewLayout.year_button.isChecked == true) {
            return Renews.YEAR_1
        } else {
            return Renews.NEVER
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is TransactionSelect) {
            theContext = context
        } else {
            throw RuntimeException(context.toString() + " must implement TransactionSelect")
        }
    }
}
       