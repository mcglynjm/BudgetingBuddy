package edu.rosehulman.mcglynjm.budgetingbuddy

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.dialog_edit_add.view.*

private const val ARG_UID = "UID"

lateinit var add: MenuItem

class EditBudgetFragment(var uid: String) : Fragment()  {


    private lateinit var adapter: EditAdapter

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val recyclerView = inflater.inflate(R.layout.edit_budget, container, false) as RecyclerView
        adapter = EditAdapter(context!!, uid!!)
        recyclerView.adapter = adapter
        recyclerView.layoutManager =
            LinearLayoutManager(context)

        setHasOptionsMenu(true)

        Log.d(Constants.TAG, "onCreate")

        return recyclerView
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        add = menu.add("Add Category");
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
                adapter.showAddDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}