package edu.rosehulman.mcglynjm.budgetingbuddy

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.android.synthetic.main.dialog_add_funds.view.*
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
        setHasOptionsMenu(true)
        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        add = menu.add("Search Transactions");
        add.setIcon(R.drawable.ic_baseline_arrow_drop_up_24);
        add.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        Log.d(Constants.TAG, "Menu item selected ID: ${item.itemId}")

        return when (item.itemId) {
            add.itemId-> {
                adapter.displaySearchDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
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