package edu.rosehulman.mcglynjm.budgetingbuddy

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.home_screen.view.*

class HomeFragment  : Fragment() {
    lateinit var theContext: Context
    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val fragmentViewer = theContext as FragmentViewer
        val view = inflater.inflate(R.layout.home_screen, container, false)
        //TODO
        //make viewFragment in mainactivity and make it an interface method
        view.home_edit_budget_button.setOnClickListener{fragmentViewer.viewFragment(EditBudgetFragment())}
        view.home_input_transaction_button.setOnClickListener {
            // setup the alert builder
            // setup the alert builder
            val builder: android.app.AlertDialog.Builder = android.app.AlertDialog.Builder(theContext)
            builder.setTitle("Enter A Transaction")
            builder.setMessage("Would you like to scan a receipt or manually enter a transaction?")

            // add the buttons
            builder.setPositiveButton("Scan") { _, _ ->
                fragmentViewer.viewFragment(ScanTransactionFragment())
            }
            builder.setNeutralButton("Cancel", null)
            builder.setNegativeButton("Manual"){ _, _ ->
                fragmentViewer.viewFragment(ManualTransactionFragment())
            }

            // create and show the alert dialog

            // create and show the alert dialog
            val dialog: AlertDialog = builder.create()
            dialog.show()
        }
        view.home_budget_history_button.setOnClickListener {fragmentViewer.viewFragment(TransactionHistoryFragment())}
        view.home_user_settings_button.setOnClickListener {fragmentViewer.viewFragment(UserSettingsFragment())}

        return view
    }
}