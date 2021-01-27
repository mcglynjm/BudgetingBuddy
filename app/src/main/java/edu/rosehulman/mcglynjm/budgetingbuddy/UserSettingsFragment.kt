package edu.rosehulman.mcglynjm.budgetingbuddy

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.user_settings.view.*
import java.lang.RuntimeException

class UserSettingsFragment : Fragment()  {
    lateinit var theContext: Context
    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val fragmentViewer = theContext as FragmentViewer
        val view = inflater.inflate(R.layout.user_settings, container, false)

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