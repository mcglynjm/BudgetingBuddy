package edu.rosehulman.mcglynjm.budgetingbuddy

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.budget_summary.view.*
import kotlinx.android.synthetic.main.manual_transaction.*
import kotlinx.android.synthetic.main.manual_transaction.view.*
import java.lang.RuntimeException

class SummaryFragment(var uid: String) : Fragment() {
    lateinit var theContext: Context
    var remainingFunds: Float = 0.toFloat()
    var monthlyBudget: Float = 0.toFloat()
    var monthlyRemaining: Float = 0.toFloat()

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
        //TODO
        //set chart graphic here (after reaearch into libraries)
        //view.chart_view.setImageBitmap()

        return view
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
    }

    fun getInitValues() {
        usersRef.get().addOnSuccessListener {snapshot: DocumentSnapshot ->
            remainingFunds = (snapshot["remainingFunds"] ?: "") as Float
            monthlyBudget = (snapshot["monthlyBudget"] ?: "") as Float
            monthlyRemaining = (snapshot["monthlyRemaining"] ?: "") as Float
        }
    }
}