package edu.rosehulman.mcglynjm.budgetingbuddy

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.budgey_summary.view.*
import kotlinx.android.synthetic.main.manual_transaction.*
import kotlinx.android.synthetic.main.manual_transaction.view.*
import java.lang.RuntimeException

class SummaryFragment : Fragment() {
    lateinit var theContext: Context
    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val fragmentViewer = theContext as FragmentViewer

        val view = inflater.inflate(R.layout.budgey_summary, container, false)

        view.summary_trends_button.setOnClickListener{
            fragmentViewer.viewFragment(TrendsFragment())
        }

        view.summary_history_button.setOnClickListener{
            fragmentViewer.viewFragment(TransactionHistoryFragment())
        }

        view.summary_edit_button.setOnClickListener{
            fragmentViewer.viewFragment(EditBudgetFragment())
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
    }
}