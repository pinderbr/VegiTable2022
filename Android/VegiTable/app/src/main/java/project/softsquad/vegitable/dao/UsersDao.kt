package project.softsquad.vegitable.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import project.softsquad.vegitable.entity.NotificationSettingsEntity
import project.softsquad.vegitable.entity.UsersEntity

@Dao
interface UsersDao {
    //@Query("SELECT * FROM Users")
    //fun getAllUsers(): LiveData<List<UsersEntity>>

    @Query("SELECT * FROM Users ORDER BY userId ASC LIMIT 1")
    fun getAllUsers(): LiveData<UsersEntity>

    //get user by local id
    @Query("SELECT * FROM Users where userId = :userId LIMIT 1")
    fun getUserById(userId: Long): UsersEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUser(user: UsersEntity): Long

    //get user by remote id
    @Query("SELECT * FROM Users WHERE userId = :userId")
    fun getUser(userId: Long): UsersEntity

    @Query("SELECT * FROM Users WHERE userId = :userId")
    fun getLiveUser(userId: Long): LiveData<UsersEntity>

    //@Update
    //fun updateUser(user: UsersEntity): Int

    @Query("UPDATE Users SET userEmail = :email, userPassword = :password, userFirstName = :firstName, userLastName = :lastName, lastUpdateDateTime = :updateTime WHERE userId = :userId")
    fun updateUser(email: String, password: String, firstName: String, lastName: String, updateTime: String?, userId: Long)
}