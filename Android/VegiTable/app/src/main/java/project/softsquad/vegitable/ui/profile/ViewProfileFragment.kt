package project.softsquad.vegitable.ui.profile

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Build
import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import project.softsquad.vegitable.ApiInterface
import project.softsquad.vegitable.R
import project.softsquad.vegitable.alert
import project.softsquad.vegitable.databinding.FragmentViewProfileBinding
import project.softsquad.vegitable.entity.UsersEntity
import project.softsquad.vegitable.ui.BucketDataFragmentArgs
import project.softsquad.vegitable.viewmodel.UserViewModel
import project.softsquad.vegitable.viewmodel.ViewProfileViewModel

/**
 * Author: Brianna McBurney
 * Description:
 */
@RequiresApi(Build.VERSION_CODES.O)
class ViewProfileFragment : Fragment() {

    private lateinit var binding: FragmentViewProfileBinding
    lateinit var apiInterface: ApiInterface

    private var userId: Long = 15
    private val navigationArgs: ViewProfileFragmentArgs by navArgs()

    //user view model
    private lateinit var userViewModel: UserViewModel

    private val viewModel by lazy {
        ViewModelProvider(this).get(ViewProfileViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View? {
        binding = FragmentViewProfileBinding.inflate(inflater, container, false)
        val view = binding.root
        binding.userProfilePicImg.setImageResource(R.drawable.profile_icon)
        apiInterface = ApiInterface.create()

//        userId = navigationArgs.userId

        /*viewModel.getCurrentUser(userId).observe(viewLifecycleOwner, { currentUser ->
            binding.userEmailTxt.text = currentUser.userEmail
            binding.emailInput.editText?.setText(currentUser.userEmail)

            binding.firstNameInput.editText?.setText(currentUser.userFirstName)
            binding.firstNameTxt.text = currentUser.userFirstName

            binding.lastNameInput.editText?.setText(currentUser.userLastName)
            binding.lastNameTxt.text = currentUser.userLastName
        })*/

        viewModel.readAllData.observe(viewLifecycleOwner, { currentUser ->
            binding.userEmailTxt.text = currentUser.userEmail
            binding.emailInput.editText?.setText(currentUser.userEmail)

            binding.firstNameInput.editText?.setText(currentUser.userFirstName)
            binding.firstNameTxt.text = currentUser.userFirstName

            binding.lastNameInput.editText?.setText(currentUser.userLastName)
            binding.lastNameTxt.text = currentUser.userLastName
        })

        binding.updateAccountBtn.setOnClickListener { updateUser() }

        return view
    }

    private fun updateUser() {
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
        if(binding.passwordInput.editText?.text.toString() != "" && binding.confirmPasswordInput.editText?.text.toString() == "") {
            errors += "\n - You need to confirm the password!"
        } else if ((binding.passwordInput.editText?.text.toString() != "" && binding.confirmPasswordInput.editText?.text.toString() != "") &&
            binding.passwordInput.editText?.text.toString() != binding.confirmPasswordInput.editText?.text.toString()) {
            errors += "\n - Passwords do not match!"
        }

        if(errors != "") {
            var builder = AlertDialog.Builder(requireActivity())
            builder.setMessage(errors)
                .setCancelable(false)
                .setPositiveButton("Ok", DialogInterface.OnClickListener {dialog, id -> dialog.dismiss() })
            var alert = builder.create()
            alert.setTitle("Validation Errors")
            alert.show()
        } else {
            var currentUser = viewModel.readAllData
            var updatedUser: UsersEntity? = viewModel.updateUserData(binding.firstNameInput.editText?.text.toString(), binding.lastNameInput.editText?.text.toString(), binding.emailInput.editText?.text.toString(),
                binding.passwordInput.editText?.text.toString())
            if(binding.passwordInput.editText?.text.toString() != "" && binding.passwordInput.editText?.text.toString() != currentUser.value?.userPassword) {
                // sending updated user object to user view model, where it will be sent to get updated first in local db, then to remote
                if(updatedUser != null) {
                    userViewModel.updateUser(updatedUser)
                }
            }
            // TODO find a way to actually check that the update was successful
            alert(activity,"Profile Updated", "Your profile has been updated")
        }
    }

    // API call
    /*
    private fun updateProfile(user: UsersEntity) {
        apiInterface.updateUser(user.userId.toInt(), user)
            .enqueue(object : Callback<UsersEntity> {
                override fun onResponse(
                    call: Call<UsersEntity>?,
                    response: Response<UsersEntity>?
                ) {
                    if (response?.body() != null) {
                        Log.i("UPDATED DEVICE", "Device has been updated")
                    } else {
                        Log.i("Error", "Something went wrong please try again later.")
                    }
                }
                override fun onFailure(call: Call<UsersEntity>?, t: Throwable?) {
                    alert(activity,"Profile Not Found","We couldn't find your profile on the remote DB. Please try again!")
                }
            })
    }*/
}