package edu.rosehulman.mcglynjm.budgetingbuddy

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

lateinit var add: MenuItem

class EditBudgetFragment(var adapter: EditAdapter) : Fragment()  {
    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val recyclerView = inflater.inflate(R.layout.edit_budget, container, false) as RecyclerView
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)

        setHasOptionsMenu(true)

        return recyclerView
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        add = menu.add("Add Category");
        add.setIcon(R.drawable.ic_baseline_add_24);
        add.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM)
    }
}