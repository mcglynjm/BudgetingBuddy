package edu.rosehulman.mcglynjm.budgetingbuddy

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.home_screen.view.*

class TransactionHistoryFragment  : Fragment() {
    lateinit var theContext: Context
    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val fragmentViewer = theContext as FragmentViewer
        val view = inflater.inflate(R.layout.transaction_history, container, false)

        return view
    }
}