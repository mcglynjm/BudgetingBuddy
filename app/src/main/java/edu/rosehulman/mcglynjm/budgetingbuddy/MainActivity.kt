package edu.rosehulman.mcglynjm.budgetingbuddy

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import androidx.fragment.app.Fragment

class MainActivity : AppCompatActivity(), FragmentViewer, TransactionSelect {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //setSupportActionBar(findViewById(R.id.toolbar))
        viewFragment(HomeFragment(), getString(R.string.home))
    }


    fun viewFragment(fragment: Fragment, name: String) {
        if (fragment != null) {
            val ft = supportFragmentManager.beginTransaction()
            ft.replace(R.id.frame_layout, fragment)
//            while (supportFragmentManager.backStackEntryCount > 0) {
//                supportFragmentManager.popBackStackImmediate()
//            }
//            ft.addToBackStack(name)
            ft.addToBackStack(getString(R.string.home))
            ft.commit()
        }
    }

    override fun onButtonHit(type: String) {
        Log.d(Constants.TAG, "BUTTON $type hit")
        if (type.equals(getString(R.string.scan))) {
            Log.d(Constants.TAG, "Making $type fragment")
            viewFragment(ScanTransactionFragment(), type)
        } else if (type.equals(getString(R.string.manual))) {
            viewFragment(ManualTransactionFragment(), type)
            Log.d(Constants.TAG, "Making $type fragment")
        } else if (type.equals(getString(R.string.history))) {
            viewFragment(TransactionHistoryFragment(), type)
            Log.d(Constants.TAG, "Making $type fragment")
        } else if (type.equals(getString(R.string.settings))) {
            viewFragment(UserSettingsFragment(), type)
            Log.d(Constants.TAG, "Making $type fragment")
        } else if(type.equals(getString(R.string.home))) {
            viewFragment(HomeFragment(), type)
            Log.d(Constants.TAG, "Making $type fragment")
        } else if(type.equals(getString(R.string.edit))) {
            val adapter = EditAdapter(this)
           viewFragment(EditBudgetFragment(adapter), type)
        } else if(type.equals(getString(R.string.summary))) {
           viewFragment(SummaryFragment(), type)
            Log.d(Constants.TAG, "Making $type fragment")
        }
    }

    override fun onTransactionSelected(transaction: ManualTransaction) {
        TODO("Not yet implemented")
        //for details in transaction history fragment

    }

}

interface FragmentViewer {
    fun onButtonHit(type: String)
}
