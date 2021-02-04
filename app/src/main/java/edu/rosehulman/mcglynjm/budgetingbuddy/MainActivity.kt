package edu.rosehulman.mcglynjm.budgetingbuddy

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.fragment.app.Fragment
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), FragmentViewer, TransactionSelect, SplashFragment.OnLoginButtonPressedListener {
    private val auth = FirebaseAuth.getInstance()
    lateinit var authStateListener : FirebaseAuth.AuthStateListener
    // Request code for launching the sign in Intent.
    private val RC_SIGN_IN = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        // viewFragment(HomeFragment(), getString(R.string.home))
        initializeListeners()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
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
            viewFragment(ManualTransactionFragment(auth.currentUser!!.uid), type)
            Log.d(Constants.TAG, "Making $type fragment")
        } else if (type.equals(getString(R.string.history))) {
            viewFragment(TransactionHistoryFragment(auth.currentUser!!.uid), type)
            Log.d(Constants.TAG, "Making $type fragment")
        } else if (type.equals(getString(R.string.settings))) {
            viewFragment(UserSettingsFragment(), type)
            Log.d(Constants.TAG, "Making $type fragment")
        } else if(type.equals(getString(R.string.home))) {
            viewFragment(HomeFragment(), type)
            Log.d(Constants.TAG, "Making $type fragment")
        } else if(type.equals(getString(R.string.edit))) {

           viewFragment(EditBudgetFragment(auth.currentUser?.uid!!), type)
        } else if(type.equals(getString(R.string.summary))) {
           viewFragment(SummaryFragment(auth.currentUser!!.uid), type)
            Log.d(Constants.TAG, "Making $type fragment")
        }
    }
    override fun onStart() {
        super.onStart()
        auth.addAuthStateListener(authStateListener)
    }

    override fun onStop() {
        super.onStop()
        auth.removeAuthStateListener(authStateListener)
    }

    private fun initializeListeners() {
        //TODO: Create an AuthStateListener that passes the UID
        authStateListener = FirebaseAuth.AuthStateListener {
            val user = auth.currentUser
            Log.d(Constants.TAG, "In auth listener, User: $user")
            if (user != null) {
                Log.d(Constants.TAG, "UID: ${user.uid}")
                Log.d(Constants.TAG, "Name: ${user.displayName}")
                // plus email, photoUrl, phoneNumber
                viewFragment(HomeFragment(), getString(R.string.home))
            } else {
                viewFragment(SplashFragment(), "splash")
            }
        }
    }


    override fun onLoginButtonPressed() {
        launchLoginUI()
    }

    private fun launchLoginUI() {
        // TODO: Build a login intent and startActivityForResult(intent, ...)
        // For details, see https://firebase.google.com/docs/auth/android/firebaseui#sign_in
// Choose authentication providers
        val providers = arrayListOf(
                AuthUI.IdpConfig.EmailBuilder().build(),
                AuthUI.IdpConfig.PhoneBuilder().build(),
                AuthUI.IdpConfig.GoogleBuilder().build())

// Create and launch sign-in intent
        val loginIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                //.setLogo(R.drawable.ic_launcher_custom)
                .build()

        startActivityForResult(loginIntent, RC_SIGN_IN)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_logout -> {
                // DONE: Sign out.
                auth.signOut()
                true
            }
            else -> super.onOptionsItemSelected(item)
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
