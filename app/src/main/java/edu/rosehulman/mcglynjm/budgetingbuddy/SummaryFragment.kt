package edu.rosehulman.mcglynjm.budgetingbuddy

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.budget_summary.view.*
import kotlinx.android.synthetic.main.dialog_add_funds.view.*
import kotlinx.android.synthetic.main.manual_transaction.*
import kotlinx.android.synthetic.main.manual_transaction.view.*
import java.lang.RuntimeException

class SummaryFragment(var uid: String) : Fragment() {
    lateinit var theContext: Context
    var remainingFunds: Double? = null
    var monthlyBudget: Double? = null
    var monthlyRemaining: Double? = null

    private val usersRef = FirebaseFirestore
        .getInstance()
        .collection(Constants.USERS_COLLECTION)
        .document(uid)

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


        //TODO
        //set chart graphic here (after reaearch into libraries)
        //view.chart_view.setImageBitmap()

        this.getInitValues()
//
//        view.total_balance_remaining_number.text = context!!.resources!!.getString(R.string.amount_string, remainingFunds)
//        view.monthly_balance_remaining_number.text = context!!.resources!!.getString(R.string.amount_string, monthlyRemaining)

        return view
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
            val amount = view.amount_edit_text.text.toString().toFloat()
            usersRef.get().addOnSuccessListener { snapshot: DocumentSnapshot ->
                var oldMonthlyRemaining = snapshot.getDouble("monthlyRemaining") as Double
                var oldRemainingFunds = snapshot.getDouble("remainingFunds") as Double
                Log.d(Constants.TAG, "monthlyRemaining: $monthlyRemaining")
                oldMonthlyRemaining += amount
                oldRemainingFunds += amount
                Log.d(Constants.TAG, "monthlyRemaining: $monthlyRemaining")
                usersRef.update("monthlyRemaining", oldMonthlyRemaining)
                usersRef.update("remainingFunds", oldRemainingFunds)
            }
            getInitValues()
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
        //getInitValues()
    }

    fun getInitValues() {
        usersRef.get().addOnSuccessListener {snapshot: DocumentSnapshot ->
            ///remainingFunds = (snapshot["remainingFunds"] ?: "") as Long
            this.monthlyBudget = snapshot.getDouble("monthlyBudget") as Double
            this.monthlyRemaining = snapshot.getDouble("monthlyRemaining") as Double
            this.remainingFunds = snapshot.getDouble("remainingFunds") as Double
            Log.d(Constants.TAG, "Monthly Remaining: $monthlyRemaining")
            view!!.total_balance_remaining_number.text = context!!.resources!!.getString(R.string.amount_string, remainingFunds)
            view!!.monthly_balance_remaining_number.text = context!!.resources!!.getString(R.string.amount_string, monthlyRemaining)
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