package project.softsquad.vegitable.viewmodel

import android.app.Application
import androidx.lifecycle.*
import project.softsquad.vegitable.VegiTableDatabase
import project.softsquad.vegitable.dao.UsersDao
import project.softsquad.vegitable.entity.UsersEntity
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import project.softsquad.vegitable.getCurrentDateTime
import java.time.LocalDateTime
import project.softsquad.vegitable.repositories.UserRepository


/**
 * Author: Brianna McBurney
 * Description:
 */

@RequiresApi(Build.VERSION_CODES.O)
class ViewProfileViewModel(application: Application): AndroidViewModel(application) {

    val readAllData: LiveData<UsersEntity> //changed to get onlythe first user as a temp solution

    private val repository: UserRepository

    private var userDao: UsersDao = VegiTableDatabase.getInstance(application).UsersDao()
    private var user = MutableLiveData<UsersEntity>()

    init {
        val userDao2 = VegiTableDatabase.getInstance(application).UsersDao()
        //passing userDao
        repository = UserRepository(userDao2)
        readAllData = repository.readAllData
    }

    fun getCurrentUser(userId: Long): LiveData<UsersEntity> {
        var currentUser =  userDao.getLiveUser(userId)
        user.value = currentUser.value
        return currentUser
    }

    fun updateUserData(firstName: String, lastName: String, email: String, password: String) : UsersEntity? {
        user.value?.userFirstName = firstName
        user.value?.userLastName = lastName
        user.value?.userEmail = email
        if (password != "") user.value?.userPassword = password
        user.value?.lastUpdateDateTime = LocalDateTime.now().toString()
        var rowCount: Int? = 0
        viewModelScope.launch(Dispatchers.IO) {
            user.value?.lastUpdateDateTime = getCurrentDateTime()
            /*
            rowCount = user.let { userDao?.updateUser(it) }
            if(rowCount != 0) {
                // TODO let user know their profile was updated
            } else {
                // TODO let user know their profile WASNT updated
            }*/

        }
        return user.value
    }
}