package edu.rosehulman.mcglynjm.budgetingbuddy

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.home_screen.view.*
import java.lang.RuntimeException

class TransactionHistoryFragment(var uid: String)  : Fragment() {
    lateinit var theContext: Context
    private lateinit var adapter: TransactionAdapter

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val fragmentViewer = context as FragmentViewer
        val view = inflater.inflate(R.layout.transaction_history, container, false) as RecyclerView
        adapter = TransactionAdapter(context!!, uid)
        view.adapter = adapter
        adapter.addSnapshotListener()
        view.layoutManager = LinearLayoutManager(context)
        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is TransactionSelect) {
            theContext = context
        }
        else {
            throw RuntimeException(context.toString() + " must implement TransactionSelect")
        }
    }
}