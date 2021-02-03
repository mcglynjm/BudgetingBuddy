package edu.rosehulman.mcglynjm.budgetingbuddy

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import androidx.fragment.app.Fragment
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity(), FragmentViewer, TransactionSelect, SplashFragment.OnLoginButtonPressedListener {
    private val auth = FirebaseAuth.getInstance()
    lateinit var authStateListener : FirebaseAuth.AuthStateListener
    // Request code for launching the sign in Intent.
    private val RC_SIGN_IN = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //setSupportActionBar(findViewById(R.id.toolbar))
        //viewFragment(HomeFragment(), getString(R.string.home))
        initializeListeners()
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
    override fun onStart() {
        super.onStart()
        auth.addAuthStateListener(authStateListener)
    }

    override fun onStop() {
        super.onStop()
        auth.removeAuthStateListener(authStateListener)
    }
    private fun initializeListeners() {
        // TODO: Create an AuthStateListener that passes the UID
        // to the MovieQuoteFragment if the user is logged in
        // and goes back to the Splash fragment otherwise.
        // See https://firebase.google.com/docs/auth/users#the_user_lifecycle
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

    private fun switchToSplashFragment() {
        viewFragment(SplashFragment(), "splash")
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


    override fun onTransactionSelected(transaction: ManualTransaction) {
        TODO("Not yet implemented")
        //for details in transaction history fragment

    }

}

interface FragmentViewer {
    fun onButtonHit(type: String)
}
