package project.softsquad.vegitable.ui.profile

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import project.softsquad.vegitable.ApiInterface
import project.softsquad.vegitable.R
import project.softsquad.vegitable.ui.MainActivity
import project.softsquad.vegitable.databinding.ActivityLoginBinding
import project.softsquad.vegitable.entity.NotificationSettingsEntity
import project.softsquad.vegitable.entity.UsersEntity
import project.softsquad.vegitable.viewmodel.NotificationSettingsViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import project.softsquad.vegitable.viewmodel.UserViewModel


/**
 * Author: Brianna McBurney
 * Description:
 */
class LoginActivity : AppCompatActivity() {

    var userId: Long = 0

    //for testing
    private var testUser: UsersEntity = UsersEntity(1, "email", "password", "firstname", "lastname", "", "","","imageurl")

    //user view model
    private val viewModel by lazy {
        ViewModelProvider(this).get(UserViewModel::class.java)
    }
    private val notificationSettingsViewModel by lazy {
        ViewModelProvider(this).get(NotificationSettingsViewModel::class.java)
    }
    private val userViewModel by lazy {
        ViewModelProvider(this).get(UserViewModel::class.java)
    }

    private lateinit var binding: ActivityLoginBinding
    lateinit var apiInterface: ApiInterface
    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, LoginActivity::class.java )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)
        apiInterface = ApiInterface.create()
        binding.VegiTableLogo.setImageResource(R.drawable.logo_vegitable)

        binding.loginBtn.setOnClickListener {
            //goToMainPage(testUser) //TODO: REMOVE TEST USER
            login() // TODO uncomment to check the login

            //TODO:
        }
        binding.createAccountBtn.setOnClickListener { goToCreateAccount() }

        notificationSettingsViewModel.notificationSettings.observe(this,  { set ->

        })

    }

    private fun getNotificationSettings(userId: Long) {
        // TODO check if settings exist first then create them - NOT WORKING
        var settings: NotificationSettingsEntity?
        var existingLocalSettings = notificationSettingsViewModel.getSettings(userId)
        // check if there was no local settings found
        if (existingLocalSettings.value == null) {
            // if true then get the settings from the remote database
            apiInterface.getNotificationSettings(userId)
                .enqueue(object : Callback<NotificationSettingsEntity> {
                    override fun onResponse(
                        call: Call<NotificationSettingsEntity>?,
                        response: Response<NotificationSettingsEntity>?
                    ) {
                        if (response?.body() != null) {
                            settings = response.body() as NotificationSettingsEntity
                            if (settings != null) {
                                notificationSettingsViewModel.addNotificationSettings(settings)
                            } else {
                                Log.i("NO SETTINGS", "no notification settings for the user")
                            }
                        } else {
                            if (response != null) {
                                Log.i(
                                    "SETTINGS  ERROR",
                                    "Something went wrong! " + response.errorBody()
                                )
                            }
                        }
                    }

                    override fun onFailure(call: Call<NotificationSettingsEntity>?, t: Throwable?) {
                        Log.i("SETTINGS ERROR", "Something else went wrong! " + t.toString())
                    }
                })
        }
    }

    private fun getUser(email: String, password: String) {
        var user: UsersEntity? = null
        apiInterface.getUser(email, password).enqueue( object : Callback<UsersEntity> {
            override fun onResponse(call: Call<UsersEntity>?, response: Response<UsersEntity>?) {
                if(response?.body() != null) {
                    user = response.body()
                    if (user?.userEmail != null) {
                        Log.i("FOUND USER", "Found user with those credentials")
                        var userId: Long = user!!.userId
                        user?.let { viewModel.insertUser(it) }
                        user?.let { getNotificationSettings(userId) }
                        user?.let { goToMainPage(it) }
                    } else {
                        alert("Invalid Credentials", "The email and/or password are incorrect. Please try again!")
                    }

                } else {
                    if (response != null) {
                        alert("ERROR", "Something went wrong! " + response.errorBody())
                    }
                }
            }
            override fun onFailure(call: Call<UsersEntity>?, t: Throwable?) {
                Log.i("SETTINGS ERROR", "Something else went wrong! " + t.toString())
            }
        })
    }

    private fun checkIfDbEmpty() : Boolean{
        //call query - select * from Users where serverId = id

        if (true){
            return true
        }
        return false

    }

    /**
     * If the user exists in the remote database, call this method
     */
    private fun goToMainPage(user: UsersEntity) {

    //first check if local database user table is empty
    /*if (userViewModel.getUserByRemoteId(user.remoteId) == 0){

        //if it is empty, it means the local db has been wiped - notify user & confirm if they want to restore any previous data
        var builder = AlertDialog.Builder(this)
        builder.setMessage("Previously saved data was found - do you want to restore this data? If you select 'No', all previously entered data will be lost.")
            //if select no, wipe everything from remote associated with that user
            .setNegativeButton("No",  DialogInterface.OnClickListener { dialog, id -> userViewModel.deleteRemoteData(user) })
            //if select yes, sync remote to local by calling method in view model
            .setPositiveButton("Yes", DialogInterface.OnClickListener { dialog, id -> userViewModel.syncToLocal(user) })
        var alert = builder.create()
        alert.setTitle("Restore Data")
        alert.show()
    }*/


    // change to MainActivity
    var intent = MainActivity.newIntent(this)
        intent.putExtra("user", user.userId);
        Log.i("TEST", "Passing in userid: $user.userId")
    startActivity(intent)
    finish()

    }

    private fun goToCreateAccount() {
        var intent = CreateAccountActivity.newIntent(this)
        startActivity(intent)
    }

    private fun login() {
        var errors = ""
        if(binding.emailInput.editText?.text.toString() == "") {
            errors += "\n - You need to enter an email!"
        } else if(!Patterns.EMAIL_ADDRESS.matcher(binding.emailInput.editText?.text.toString()).matches()) {
            errors += "\n - You need to enter a  valid email!"
        }
        if(binding.passwordInput.editText?.text.toString() == "") {
            errors += "\n - You need to enter a password!"
        }

        if(errors != "") {
            var builder = AlertDialog.Builder(this)
            builder.setMessage(errors)
                .setCancelable(false)
                .setPositiveButton("Ok", DialogInterface.OnClickListener { dialog, id -> dialog.dismiss() })
            var alert = builder.create()
            alert.setTitle("Validation Errors")
            alert.show()
        } else {
            getUser(binding.emailInput.editText?.text.toString(), binding.passwordInput.editText?.text.toString())
        }
    }

    private fun alert(title:String, message: String): Dialog {
        return this?.let {
            val builder = AlertDialog.Builder(it)
            builder.setTitle(title)
            builder.setMessage(message)
            builder.setPositiveButton("Ok", DialogInterface.OnClickListener { dialog, id -> dialog.cancel() })
            builder.create()
            builder.show()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}