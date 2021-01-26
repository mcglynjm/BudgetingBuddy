package edu.rosehulman.mcglynjm.budgetingbuddy

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment

class MainActivity : AppCompatActivity(), FragmentViewer {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //setSupportActionBar(findViewById(R.id.toolbar))
        viewFragment(HomeFragment())
    }


    fun viewFragment(fragment: Fragment) {
        if (fragment != null) {
            val ft = supportFragmentManager.beginTransaction()
            ft.replace(R.id.frame_layout, fragment)
            while (supportFragmentManager.backStackEntryCount > 0) {
                supportFragmentManager.popBackStackImmediate()
            }
            ft.commit()
        }
    }

    override fun onButtonHit(type: String) {
        Log.d(Constants.TAG, "BUTTON $type hit")
        if (type.equals(R.string.scan)) {
            Log.d(Constants.TAG, "Making $type fragment")
            viewFragment(ScanTransactionFragment())

        } else if (type.equals(getString(R.string.manual))) {
            viewFragment(ManualTransactionFragment())
            Log.d(Constants.TAG, "Making $type fragment")
        } else if (type.equals(getString(R.string.history))) {
            viewFragment(TransactionHistoryFragment())
            Log.d(Constants.TAG, "Making $type fragment")
        } else if (type.equals(getString(R.string.settings))) {
            viewFragment(UserSettingsFragment())
            Log.d(Constants.TAG, "Making $type fragment")
        } else if(type.equals(getString(R.string.home))) {
            viewFragment(HomeFragment())
            Log.d(Constants.TAG, "Making $type fragment")
        } else if(type.equals(getString(R.string.edit))) {
           // viewFragment(EditBudgetFragment())
        }
    }
}

interface FragmentViewer {
    fun onButtonHit(type: String)
}
