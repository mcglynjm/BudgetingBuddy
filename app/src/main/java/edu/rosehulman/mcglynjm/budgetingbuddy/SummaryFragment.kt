package edu.rosehulman.mcglynjm.budgetingbuddy

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import com.google.android.gms.tasks.Continuation
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.SetOptions.merge
import kotlinx.android.synthetic.main.budget_summary.view.*
import kotlinx.android.synthetic.main.dialog_add_funds.view.*
import kotlinx.android.synthetic.main.manual_transaction.*
import kotlinx.android.synthetic.main.manual_transaction.view.*
import lecho.lib.hellocharts.model.PieChartData
import lecho.lib.hellocharts.model.SliceValue
import lecho.lib.hellocharts.view.PieChartView
import java.lang.RuntimeException

class SummaryFragment(var uid: String) : Fragment() {
    lateinit var theContext: Context
    var remainingFunds: Double? = null
    var monthlyBudget: Double? = null
    var monthlyRemaining: Double? = null
  //  var numCategory: Int? = null
    var totalSpent = 0.toDouble()
    var categoryMap = HashMap<String, Double>()

    private val usersRef = FirebaseFirestore
        .getInstance()
        .collection(Constants.USERS_COLLECTION)
        .document(uid)

    private lateinit var listenerRegistration: ListenerRegistration
//    lateinit var pieData: PieChartData


    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val fragmentViewer = context as FragmentViewer
        val view = inflater.inflate(R.layout.budget_summary, container, false)

        view.summary_trends_button.setOnClickListener{
            //fragmentViewer.viewFragment(TrendsFragment())
        }

        view.summary_history_button.setOnClickListener{
            fragmentViewer.onButtonHit(context!!.getString(R.string.history))
        }

        view.summary_edit_button.setOnClickListener{
            fragmentViewer.onButtonHit(context!!.getString(R.string.edit))
        }
        setHasOptionsMenu(true)

//        this.getInitValues()
//
//        view.total_balance_remaining_number.text = context!!.resources!!.getString(R.string.amount_string, remainingFunds)
//        view.monthly_balance_remaining_number.text = context!!.resources!!.getString(R.string.amount_string, monthlyRemaining)
        //TODO
        //set chart graphic here (after reaearch into libraries)
        //use the totals of each category

//        val pieChartView: PieChartView = view.chart_view as PieChartView
//        pieChartView.pieChartData = pieData
        return ChartTask().execute(view, usersRef, theContext)
        //return view
    }

    private fun getCategorySum(categoryName: String): Double {
        var spent = 0.toDouble()
        usersRef.collection(Constants.TRANSACTIONS_COLLECTION).whereEqualTo("type", categoryName).get().addOnSuccessListener { querySnapshot ->
            for(transaction in querySnapshot.documents) {
                spent += (transaction.getDouble("amount")  ?: 0.00)
                Log.d(Constants.TAG, "Spent: $spent on category $categoryName")

            }
        }
        return spent
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        add = menu.add("Add Funds");
        add.setIcon(R.drawable.ic_baseline_add_24);
        add.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        Log.d(Constants.TAG, "Menu item selected ID: ${item.itemId}")

        return when (item.itemId) {
            add.itemId-> {
                updateFunds()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun updateFunds() {
        val builder = androidx.appcompat.app.AlertDialog.Builder(theContext)//AlertDialog.Builder(this)
        //set options
        builder.setTitle(R.string.add_funds)
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_add_funds, null, false)
        builder.setView(view)
        builder.setPositiveButton(android.R.string.ok){_,_ ->


            val totalAmount = ("0" + view.total_amount_edit_text.text.toString()).toDouble()
            val monthlyAmount = ("0" + view.monthly_amount_edit_text.text.toString()).toDouble()

            Log.d(Constants.TAG, "adding  $$totalAmount to the total and $$monthlyAmount monthly")

            usersRef.get().addOnSuccessListener { snapshot: DocumentSnapshot ->
                var oldMonthlyRemaining = (snapshot.getDouble("monthlyRemaining")  ?: 0.00)as Double
                var oldRemainingFunds = (snapshot.getDouble("remainingFunds") ?: 0.00) as Double
                oldMonthlyRemaining += monthlyAmount
                oldRemainingFunds += totalAmount

                Log.d(Constants.TAG, "new total $$oldRemainingFunds and $$oldMonthlyRemaining monthly")

                Log.d(Constants.TAG, "monthlyRemaining: $monthlyRemaining")
                usersRef.set(mapOf("monthlyRemaining" to oldMonthlyRemaining, "remainingFunds" to oldRemainingFunds), merge())
            }
            //getInitValues()
        }
        builder.setNegativeButton(android.R.string.cancel, null)

        builder.create().show()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is FragmentViewer) {
            theContext = context
        }
        else {
            throw RuntimeException(context.toString() + " must implement FragmentViewer")
        }
        getInitValues()
        //makeChartData()
    }

    private fun makeChartData() {
        //TODO
        //set chart graphic here (after reaearch into libraries)
        //use the totals of each category
        var pieDataArray = ArrayList<SliceValue>()
        var categoryTask =  usersRef.collection(Constants.CATEGORIES_COLLECTION)
            .get().addOnSuccessListener { querySnapshot ->
                //numCategory = querySnapshot.documents.size
                //Log.d(Constants.TAG, "$numCategory : Categories")
            }
            .addOnCompleteListener {
                for (category in it.result?.documents!!) {
                    var categoryName = (category.data?.get("name") as String)
                    // var categorySum = getCategorySum(categoryName)
                    // var spent = 0.toDouble()
                    var task = usersRef.collection(Constants.TRANSACTIONS_COLLECTION)
                        .whereEqualTo("type", categoryName).get()
                        .addOnSuccessListener { querySnapshot ->
//                        for(transaction in querySnapshot.documents) {
//                            spent += (transaction.getDouble("amount")  ?: 0.00)
//                            Log.d(Constants.TAG, "Spent: $spent on category $categoryName")
//
//                        }
                        }
                    task.continueWith(Continuation<QuerySnapshot, Double> {
                        it.result?.sumByDouble { it.getDouble("amount") ?: 0.toDouble() }
                    })
                        .addOnCompleteListener { task ->
                            var spent = task.result ?: 0.toDouble()
                            totalSpent += spent

                            pieDataArray.add(
                                SliceValue(
                                    spent.toFloat(),
                                    R.color.green
                                ).setLabel(getString(R.string.chart_label, categoryName, spent))
                            )
                        }
                    // var spent = task.result?.sumByDouble { it.getDouble("amount") ?: 0.toDouble() } ?: 0.toDouble()
                    //categoryMap[(category.data?.get("name") as String)] = categorySum
                }

                Log.d(Constants.TAG, "Pie SLices: ${pieDataArray.size}")
//                pieData = PieChartData(pieDataArray)
//                pieData.setHasLabels(true)
//                pieData.valueLabelTextSize = 12
//                pieData.isValueLabelBackgroundEnabled = false
            }
    }

    override fun onDetach() {
        super.onDetach()
        listenerRegistration.remove()
    }

    fun getInitValues() {
        listenerRegistration =  usersRef.addSnapshotListener { querySnapshot, e ->
            if (e != null) {
                Log.e(Constants.TAG, "Listen error: $e")
                return@addSnapshotListener
            }
            ///remainingFunds = (snapshot["remainingFunds"] ?: "") as Long
            this.monthlyBudget = (querySnapshot!!.getDouble("monthlyBudget")  ?: 0.00)as Double
            this.monthlyRemaining = (querySnapshot!!.getDouble("monthlyRemaining") ?: 0.00) as Double
            this.remainingFunds = (querySnapshot!!.getDouble("remainingFunds")  ?: 0.00)as Double
            Log.d(Constants.TAG, "Total Remaining: $remainingFunds")
            Log.d(Constants.TAG, "Monthly Remaining: $monthlyRemaining")
            view!!.total_balance_remaining_number.text = context!!.resources!!.getString(R.string.amount_string, remainingFunds)//"$$remainingFunds"//context!!.resources!!.getString(R.string.amount_string, remainingFunds)
            view!!.monthly_balance_remaining_number.text = context!!.resources!!.getString(R.string.amount_string, monthlyRemaining)//"$$monthlyRemaining"
            if(this.remainingFunds!! > 0) {
                view!!.total_balance_remaining_number.setTextColor(context!!.resources.getColor(R.color.green))
            }
            else {
                view!!.total_balance_remaining_number.setTextColor(context!!.resources.getColor(R.color.red))
            }
            if(this.monthlyRemaining!! > 0) {
                view!!.monthly_balance_remaining_number.setTextColor(context!!.resources.getColor(R.color.green))
            }
            else {
                view!!.monthly_balance_remaining_number.setTextColor(context!!.resources.getColor(R.color.red))
            }
        }
    }
}