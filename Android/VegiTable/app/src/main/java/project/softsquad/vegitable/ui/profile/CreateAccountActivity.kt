package project.softsquad.vegitable.ui.profile

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import project.softsquad.vegitable.ApiInterface
import project.softsquad.vegitable.R
import project.softsquad.vegitable.VegiTableDatabase
import project.softsquad.vegitable.dao.UsersDao
import project.softsquad.vegitable.databinding.ActivityCreateAccountBinding
import project.softsquad.vegitable.entity.NotificationSettingsEntity
import project.softsquad.vegitable.entity.UsersEntity
import project.softsquad.vegitable.viewmodel.CreateAccountViewModel
import project.softsquad.vegitable.viewmodel.NotificationSettingsViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDateTime

/**
 * Author: Brianna McBurney
 * Description:
 */
@RequiresApi(Build.VERSION_CODES.O)
class CreateAccountActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateAccountBinding
    private lateinit var userDao: UsersDao
    lateinit var apiInterface: ApiInterface
    private val createUserViewModel by lazy { ViewModelProvider(this).get(CreateAccountViewModel::class.java) }
    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, CreateAccountActivity::class.java )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateAccountBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)
        binding.VegiTableLogo.setImageResource(R.drawable.logo_vegitable)
        apiInterface = ApiInterface.create()
        userDao = VegiTableDatabase.getInstance(this).UsersDao()
        binding.createAccountBtn.setOnClickListener { createAccount() }
    }

    private fun addNewUser() {
        val newUser = UsersEntity(0, binding.emailInput.editText?.text.toString(), binding.passwordInput.editText?.text.toString(),
            binding.firstNameInput.editText?.text.toString(), binding.lastNameInput.editText?.text.toString(), LocalDateTime.now().toString(), LocalDateTime.now().toString(),
        null, null)
        getRemoteUserByEmail(newUser)
    }
    private fun getRemoteUserByEmail(newUser: UsersEntity) {
        var numRows = 0
        apiInterface.getUserByEmail(newUser.userEmail).enqueue( object : Callback<Int> {
            override fun onResponse(call: Call<Int>?, response: Response<Int>?) {
                if(response?.body() != null) {
                    numRows = response.body()!!
                    if(numRows == 0) {
                        addRemoteUser(newUser)
                    } else {
                        alert("Existing Account", "There is already an account that uses that email! Please try a different one.")
                    }
                } else {
                    if (response != null) {
                        alert("ERROR", "Something went wrong! " + response.errorBody())
                    }
                }
            }
            override fun onFailure(call: Call<Int>?, t: Throwable?) {
                alert("ERROR", "Something else went wrong! " + t.toString())
            }
        })
    }


    private fun addRemoteUser(newUser: UsersEntity) {
        apiInterface.addUser(newUser).enqueue( object : Callback<UsersEntity> {
            override fun onResponse(call: Call<UsersEntity>?, response: Response<UsersEntity>?) {
                if(response?.body() != null) {
                    val createdUser = response.body() as UsersEntity
                    createUserViewModel.addUser(createdUser)
                    confirmAccountCreated()
                } else {
                    if (response != null) {
                        alert("ERROR", "Something went wrong! " + response.errorBody())
                    }
                }
            }
            override fun onFailure(call: Call<UsersEntity>?, t: Throwable?) {
                alert("ERROR", "Something else went wrong! " + t.toString())
            }
        })
    }

    private fun confirmAccountCreated() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Account Created")
        builder.setMessage("You have created an account! Please login using your new credentials")
        builder.setPositiveButton("Ok") { dialog, id ->
            dialog.cancel()
            var intent = LoginActivity.newIntent(this)
            startActivity(intent)
            finish()
        }
        builder.create()
        builder.show()
    }

    private fun createAccount() {
        var errors = ""
        if(binding.firstNameInput.editText?.text.toString() == "") {
            errors += "\n - You need to enter a first name!"
        }
        if(binding.lastNameInput.editText?.text.toString() == "") {
            errors += "\n - You need to enter a last name!"
        }
        if(binding.emailInput.editText?.text.toString() == "") {
            errors += "\n - You need to enter an email!"
        } else if(!Patterns.EMAIL_ADDRESS.matcher(binding.emailInput.editText?.text.toString()).matches()) {
            errors += "\n - You need to enter a  valid email!"
        }
        if(binding.passwordInput.editText?.text.toString() == "") {
            errors += "\n - You need to enter a password!"
        }
        if(binding.passwordInput.editText?.text.toString() != "" && binding.confirmPasswordInput.editText?.text.toString() == "") {
            errors += "\n - You need to confirm the password!"
        } else if (binding.passwordInput.editText?.text.toString() != binding.confirmPasswordInput.editText?.text.toString()) {
            errors += "\n - Passwords do not match!"
        }

        if(errors != "") {
            alert("Validation errors", errors)
        } else {
            addNewUser()
        }
    }

    private fun alert(title:String, message: String): Dialog {
        return let {
            val builder = AlertDialog.Builder(this)
            builder.setTitle(title)
            builder.setMessage(message)
            builder.setPositiveButton("Ok") { dialog, id -> dialog.cancel() }
            builder.create()
            builder.show()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}