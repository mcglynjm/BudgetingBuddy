package edu.rosehulman.mcglynjm.budgetingbuddy

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.home_screen.view.*
import java.lang.RuntimeException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.abs

class HomeFragment(var user: FirebaseUser)  : Fragment() {
    lateinit var theContext: Context

    private val userRef = FirebaseFirestore
        .getInstance()
        .collection(Constants.USERS_COLLECTION)
        .document(user.uid)

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val fragmentViewer = context as FragmentViewer
        val view = inflater.inflate(R.layout.home_screen, container, false)

        view.welcome_text_view.setText("Welcome ${user.displayName}")

        //make viewFragment in mainactivity and make it an interface method
        view.home_edit_budget_button.setOnClickListener{fragmentViewer.onButtonHit(context!!.getString(R.string.edit))}
        view.home_input_transaction_button.setOnClickListener {
            // setup the alert builder
            // setup the alert builder
            val builder: android.app.AlertDialog.Builder = android.app.AlertDialog.Builder(context)
            builder.setTitle("Enter A Transaction")
            builder.setMessage("Would you like to scan a receipt or manually enter a transaction?")

            // add the buttons
            builder.setPositiveButton("Scan") { _, _ ->
                fragmentViewer.onButtonHit(context!!.getString(R.string.scan))
            }
            builder.setNeutralButton("Cancel", null)
            builder.setNegativeButton("Manual"){ _, _ ->
                fragmentViewer.onButtonHit(context!!.getString(R.string.manual))
            }

            // create and show the alert dialog

            // create and show the alert dialog
            val dialog: AlertDialog = builder.create()
            dialog.show()
        }
        view.home_budget_history_button.setOnClickListener {fragmentViewer.onButtonHit(context!!.getString(R.string.history))}

        view.pie_chart_image_view.setOnClickListener {
            fragmentViewer.onButtonHit(context!!.getString(R.string.summary))
        }
        view.pie_chart_image_view.setImageResource(R.drawable.chart_image)
        return view
    }

    private fun checkRenews() {
        userRef.collection(Constants.TRANSACTIONS_COLLECTION).whereNotEqualTo("renews", Renews.NEVER).get().addOnSuccessListener { querySnapshot->
            Log.d(Constants.TAG,"Checking Renews for ${querySnapshot.size()} transactions")
            var total = 0.toDouble();
            for(transaction in querySnapshot.documents) {
                val transactionObject = ManualTransaction.fromSnapshot(transaction)
                val current = LocalDateTime.now()
                val dayFormatter = DateTimeFormatter.ofPattern("dd")
                val monthFormatter = DateTimeFormatter.ofPattern("MM")
                val yearFormatter = DateTimeFormatter.ofPattern("yyyy")
                val dayFormatted = current.format(dayFormatter)
                val monthFormatted = current.format(monthFormatter)
                val yearFormatted = current.format(yearFormatter)
                Log.d(Constants.TAG,"Transaction ${transactionObject.type} for ${transactionObject.amount} hasRenewed ${transactionObject.hasRenewed}")
                val transactionDay = transactionObject.date.substring(0,2)
                val transactionMonth = transactionObject.date.substring(3,5)
                val transactionYear = transactionObject.date.substring(6,10)
                val transactionRenewed = transactionObject.hasRenewed
                if(transactionObject.renews == Renews.MONTH_1) {
                    if(transactionMonth != monthFormatted && transactionYear == yearFormatted && !transactionRenewed) {
                        transactionObject.renews = Renews.NEVER
                        val current = LocalDateTime.now()
                        val formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy")
                        val formatted = current.format(formatter)
                        transactionObject.date = formatted
                        transaction.reference.update("hasRenewed", true)
                        userRef.collection(Constants.TRANSACTIONS_COLLECTION).add(transactionObject)
                        total += transactionObject.amount
                        Log.d(
                            Constants.TAG,
                            "Renewed: ${transaction.getDouble("amount")} on category ${
                                transaction.get("type")
                            }"
                        )
                    }
                    else if(transactionMonth == monthFormatted && transactionYear != yearFormatted && !transactionRenewed) {
                        transactionObject.renews = Renews.NEVER
                        val current = LocalDateTime.now()
                        val formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy")
                        val formatted = current.format(formatter)
                        transactionObject.date = formatted
                        transaction.reference.update("hasRenewed", true)
                        userRef.collection(Constants.TRANSACTIONS_COLLECTION).add(transactionObject)
                        total += transactionObject.amount
                        Log.d(
                            Constants.TAG,
                            "Renewed: ${transaction.getDouble("amount")} on category ${
                                transaction.get("type")
                            }"
                        )
                    }
                    else if(transactionMonth != monthFormatted && transactionYear == yearFormatted && transactionRenewed) {
                        //do nothing
                    }
                    else if(transactionMonth == monthFormatted && transactionYear != yearFormatted && transactionRenewed) {
                        //do nothing
                    }
                    else {
                        transaction.reference.update("hasRenewed", false)
                    }
                }
                else if(transactionObject.renews == Renews.MONTH_3) {
                    if(abs(monthFormatted.toInt()-transactionMonth.toInt()) %3 == 0 && !transactionRenewed) {
                        transactionObject.renews = Renews.NEVER
                        val current = LocalDateTime.now()
                        val formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy")
                        val formatted = current.format(formatter)
                        transactionObject.date = formatted
                        transaction.reference.update("hasRenewed", true)
                        userRef.collection(Constants.TRANSACTIONS_COLLECTION).add(transactionObject)
                        total += transactionObject.amount
                        Log.d(
                            Constants.TAG,
                            "Renewed: ${transaction.getDouble("amount")} on category ${
                                transaction.get("type")
                            }"
                        )
                    }
                    else if(abs(monthFormatted.toInt()-transactionMonth.toInt()) %3 == 0 && transactionRenewed) {
                    //do nothing
                    }
                    else {
                        transaction.reference.update("hasRenewed", false)
                    }
                }
                else if(transactionObject.renews == Renews.MONTH_4) {
                    if(abs(monthFormatted.toInt()-transactionMonth.toInt()) %4 == 0 && !transactionRenewed) {
                        transactionObject.renews = Renews.NEVER
                        val current = LocalDateTime.now()
                        val formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy")
                        val formatted = current.format(formatter)
                        transactionObject.date = formatted
                        transaction.reference.update("hasRenewed", true)
                        userRef.collection(Constants.TRANSACTIONS_COLLECTION).add(transactionObject)
                        total += transactionObject.amount
                        Log.d(
                            Constants.TAG,
                            "Renewed: ${transaction.getDouble("amount")} on category ${
                                transaction.get("type")
                            }"
                        )
                    }
                   else if(abs(monthFormatted.toInt()-transactionMonth.toInt()) %4 == 0 && transactionRenewed) {
                       //do nothing
                    }
                    else {
                        transaction.reference.update("hasRenewed", false)
                    }
                }
                else if(transactionObject.renews == Renews.MONTH_6) {
                    if(abs(monthFormatted.toInt()-transactionMonth.toInt()) %6 == 0 && !transactionRenewed) {
                        transactionObject.renews = Renews.NEVER
                        val current = LocalDateTime.now()
                        val formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy")
                        val formatted = current.format(formatter)
                        transactionObject.date = formatted
                        transaction.reference.update("hasRenewed", true)
                        userRef.collection(Constants.TRANSACTIONS_COLLECTION).add(transactionObject)
                        updateFunds(transactionObject.amount)
                        Log.d(
                            Constants.TAG,
                            "Renewed: ${transaction.getDouble("amount")} on category ${
                                transaction.get("type")
                            }"
                        )
                    }
                    else if(abs(monthFormatted.toInt()-transactionMonth.toInt()) %6 == 0 && transactionRenewed) {
                        //do nothing
                    }
                    else {
                        transaction.reference.update("hasRenewed", false)
                    }
                }
                else if(transactionObject.renews == Renews.YEAR_1) {
                    if(transactionMonth == monthFormatted && transactionYear != yearFormatted && !transactionRenewed) {
                        transactionObject.renews = Renews.NEVER
                        val current = LocalDateTime.now()
                        val formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy")
                        val formatted = current.format(formatter)
                        transactionObject.date = formatted
                        transaction.reference.update("hasRenewed", true)
                        userRef.collection(Constants.TRANSACTIONS_COLLECTION).add(transactionObject)
                        updateFunds(transactionObject.amount)
                        Log.d(
                            Constants.TAG,
                            "Renewed: ${transaction.getDouble("amount")} on category ${
                                transaction.get("type")
                            }"
                        )
                    }
                    else if(transactionMonth == monthFormatted && transactionYear != yearFormatted && transactionRenewed) {
                        //do nothing
                    }
                    else {
                        transaction.reference.update("hasRenewed", false)
                        Log.d(
                            Constants.TAG, "Already renewed: ${transaction.getDouble("amount")} on category ${transaction.get("type")}")
                    }
                }
            }
            updateFunds(total)
        }
    }

    fun updateFunds(amount: Double) {
        userRef.get().addOnSuccessListener { snapshot: DocumentSnapshot ->
            //var monthlyRemaining = (snapshot["monthlyRemaining"] ?: "") as Float
            var monthlyRemaining = (snapshot.getDouble("monthlyRemaining") ?: 0.00) as Double
            var totalRemaining = (snapshot.getDouble("remainingFunds") ?: 0.00) as Double
            Log.d(Constants.TAG, "monthlyRemaining: $monthlyRemaining")
            monthlyRemaining -= amount
            totalRemaining -= amount
            Log.d(Constants.TAG, "monthlyRemaining: $monthlyRemaining")
            userRef.update("monthlyRemaining", monthlyRemaining)
            userRef.update("remainingFunds", totalRemaining)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is FragmentViewer) {
            theContext = context
            checkRenews()
        }
        else {
            throw RuntimeException(context.toString() + " must implement FragmentViewer")
        }
    }

    fun addCategoryTransactions(){
        var categories = userRef.collection(getString(R.string.categories_collection_reference))
    }

}
