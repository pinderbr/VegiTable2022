package project.softsquad.vegitable.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import project.softsquad.vegitable.dao.UsersDao
import project.softsquad.vegitable.entity.UsersEntity
import project.softsquad.vegitable.getCurrentDateTime

class UserRepository(private val userDao: UsersDao) {

    val readAllData: LiveData<UsersEntity> = userDao.getAllUsers()

    fun addUser(newUser: UsersEntity) {

    }

    fun getUserByRemoteId(id: Int) : UsersEntity {
        return userDao.getUser(id.toLong())
    }

    //fun getUser(id: Long) : Int {
       // return userDao.getUser(id)
    //}

    suspend fun updateUser(user: UsersEntity){
        user.lastUpdateDateTime = getCurrentDateTime()
        return userDao.updateUser(user.userEmail, user.userPassword, user.userFirstName, user.userLastName, user.lastUpdateDateTime, user.userId)
    }

}