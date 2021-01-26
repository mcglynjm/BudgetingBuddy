package edu.rosehulman.mcglynjm.budgetingbuddy

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.manual_transaction.*
import kotlinx.android.synthetic.main.manual_transaction.view.*
import java.lang.RuntimeException

class ManualTransactionFragment : Fragment() {

    lateinit var theContext: Context
    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val fragmentViewer = context as FragmentViewer
        val view = inflater.inflate(R.layout.manual_transaction, container, false)
        //TODO
        //make viewFragment in mainactivity and make it an interface method
        view.cancel_button.setOnClickListener{fragmentViewer.onButtonHit(context!!.getString(R.string.home))}

        view.ok_button.setOnClickListener {
            makeNewTransaction(amount_edit_text_view.text.toString(), type_edit_text_view.text.toString(), item_edit_text_view.text.toString(), calcRenews(it.renew_layout))
        }

        return view
    }

    private fun makeNewTransaction(amount: String, type: String, items: String, renews: Renews) {
        //TODO
        //add to firebase here
        val transaction = ManualTransaction(amount.toFloat(), type, items, renews)

    }

    private fun calcRenews(renewLayout: RelativeLayout?): Renews {
        if(renewLayout?.month_button?.isSelected == true) {
            return Renews.MONTH_1
        }
        else if(renewLayout?.month_button3?.isSelected == true) {
            return Renews.MONTH_3
        }
        else if(renewLayout?.month_button4?.isSelected == true) {
            return Renews.MONTH_4
        }
        else if(renewLayout?.month_button6?.isSelected == true) {
            return Renews.MONTH_6
        }
        else if(renewLayout?.year_button?.isSelected == true) {
            return Renews.YEAR_1
        }
        else  {
            return Renews.NEVER
        }
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
