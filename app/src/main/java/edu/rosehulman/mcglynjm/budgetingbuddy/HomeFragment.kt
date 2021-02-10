package edu.rosehulman.mcglynjm.budgetingbuddy

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.home_screen.view.*
import java.lang.RuntimeException

class HomeFragment(var user: FirebaseUser)  : Fragment() {
    lateinit var theContext: Context

    private val userRef = FirebaseFirestore
        .getInstance()
        .collection(Constants.USERS_COLLECTION)
        .document(user.uid)

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val fragmentViewer = context as FragmentViewer
        val view = inflater.inflate(R.layout.home_screen, container, false)

        view.welcome_text_view.setText("Welcome ${user.displayName}")

        //make viewFragment in mainactivity and make it an interface method
        view.home_edit_budget_button.setOnClickListener{fragmentViewer.onButtonHit(context!!.getString(R.string.edit))}
        view.home_input_transaction_button.setOnClickListener {
            // setup the alert builder
            // setup the alert builder
            val builder: android.app.AlertDialog.Builder = android.app.AlertDialog.Builder(context)
            builder.setTitle("Enter A Transaction")
            builder.setMessage("Would you like to scan a receipt or manually enter a transaction?")

            // add the buttons
            builder.setPositiveButton("Scan") { _, _ ->
                fragmentViewer.onButtonHit(context!!.getString(R.string.scan))
            }
            builder.setNeutralButton("Cancel", null)
            builder.setNegativeButton("Manual"){ _, _ ->
                fragmentViewer.onButtonHit(context!!.getString(R.string.manual))
            }

            // create and show the alert dialog

            // create and show the alert dialog
            val dialog: AlertDialog = builder.create()
            dialog.show()
        }
        view.home_budget_history_button.setOnClickListener {fragmentViewer.onButtonHit(context!!.getString(R.string.history))}
        view.home_user_settings_button.setOnClickListener {fragmentViewer.onButtonHit(context!!.getString(R.string.settings))}

        view.pie_chart_image_view.setOnClickListener {
            fragmentViewer.onButtonHit(context!!.getString(R.string.summary))
        }
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

    fun addCategoryTransactions(){
        var categories = userRef.collection(getString(R.string.categories_collection_reference))
    }

}
