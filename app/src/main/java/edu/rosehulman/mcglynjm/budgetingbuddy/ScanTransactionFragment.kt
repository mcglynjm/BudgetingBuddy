package edu.rosehulman.mcglynjm.budgetingbuddy

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.manual_transaction.*
import kotlinx.android.synthetic.main.manual_transaction.view.*
import java.lang.RuntimeException

class ScanTransactionFragment  : Fragment() {
    lateinit var theContext: Context

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val fragmentViewer = context as FragmentViewer
        val view = inflater.inflate(R.layout.scan_transaction, container, false)

        view.cancel_button.setOnClickListener { fragmentViewer.onButtonHit(context!!.getString(R.string.home)) }


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