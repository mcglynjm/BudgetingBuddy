package edu.rosehulman.mcglynjm.budgetingbuddy

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment

class MainActivity : AppCompatActivity(), FragmentViewer{
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))
    }


    override fun viewFragment(fragment: Fragment) {
        if (fragment != null) {

            val ft = supportFragmentManager.beginTransaction()
            ft.replace(R.id.frame_layout, fragment)
            while (supportFragmentManager.backStackEntryCount > 0) {
                supportFragmentManager.popBackStackImmediate()
            }
            ft.commit()
        }
    }
}

interface FragmentViewer {
    fun viewFragment(fragment: Fragment)
}
