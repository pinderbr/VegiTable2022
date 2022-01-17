package project.softsquad.vegitable.ui

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.navigation.NavArgument
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import project.softsquad.vegitable.R
import project.softsquad.vegitable.VegiTableDatabase
import project.softsquad.vegitable.ui.bucket.BucketListFragment
import project.softsquad.vegitable.ui.profile.LoginActivity
import project.softsquad.vegitable.viewmodel.UserViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var vegiDb: VegiTableDatabase

    //user view model
    private lateinit var userViewModel: UserViewModel

    private var receivedUserId: Long = 0

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, MainActivity::class.java )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        vegiDb = VegiTableDatabase.getInstance(applicationContext)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation_view)
        val navController = findNavController(R.id.nav_fragment)

        bottomNavigationView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            when(destination.id) {
                R.id.viewProfileFragment -> {
                    val argument = NavArgument.Builder().setDefaultValue(receivedUserId).build()
                    destination.addArgument("userId", argument)
                }
            }
        }

        val extras = intent.extras
        if (extras != null) {
            receivedUserId = extras.getLong("user")
        }
        Log.i("TEST", "Received userid: $receivedUserId")
/*
        //adding user id to user view model so it can be passed to fragment bucketlist
        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)
        userViewModel.setUserId(receivedUserId)

*/
        val bundle = Bundle()
        bundle.putLong("user", receivedUserId)
        val bucketListFragment = BucketListFragment()
        bucketListFragment.arguments = bundle

        supportFragmentManager.beginTransaction().replace(R.id.nav_fragment, bucketListFragment).commit()
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        var inflater = menuInflater
        inflater.inflate(R.menu.menu, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_about -> {
                findNavController(R.id.nav_fragment).navigate(R.id.action_menu_to_aboutFragment)
                true
            }
            R.id.action_notification_settings -> {
                findNavController(R.id.nav_fragment).navigate(R.id.action_to_notification_settings_fragment)
                true
            }
            R.id.action_signout -> {
                val dialogBuilder = AlertDialog.Builder(this)

                dialogBuilder.setMessage("Do you want to log out?")
                    .setCancelable(true)
                    .setPositiveButton("Log Out") { dialog, which -> logOut() }
                    .setNegativeButton("Cancel") { dialog, which -> dialog.cancel() }

                val alert = dialogBuilder.create()
                alert.setTitle("Log Out?")
                alert.show()
                true
            }
            else -> {
                true
            }
        }
    }

    fun logOut(){
        // TODO Log the user out of their account
        var intent = LoginActivity.newIntent(this)
        startActivity(intent)
        finish()
    }


}