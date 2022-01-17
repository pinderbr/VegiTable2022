package project.softsquad.vegitable.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import project.softsquad.vegitable.VegiTableDatabase
import project.softsquad.vegitable.entity.NotificationSettingsEntity
import project.softsquad.vegitable.entity.UsersEntity
import project.softsquad.vegitable.getCurrentDateTime
import project.softsquad.vegitable.workers.AddBucketToRemote
import project.softsquad.vegitable.workers.AddSettingsToRemote


/**
 * Author: Brianna McBurney
 * Description:
 */
class CreateAccountViewModel(application: Application): AndroidViewModel(application) {

    val userDao = VegiTableDatabase.getInstance(application).UsersDao()
    val notificationSettingsDao = VegiTableDatabase.getInstance(application).NotificationSettingsDao()

    //instance of work manager
    private val workManager = WorkManager.getInstance(application)

    fun addUser(user: UsersEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            val id: Long? = userDao.insertUser(user)
            if(id == null) {
                Log.i("Error", "User wasn't added to the local db.")
            } else {
                Log.i("New User Created", "User was added to the local db.")
                // create new NotificationSettings in local DB for that user
                addNotificationSettings(id.toInt())
            }
        }
    }

    private fun addNotificationSettings(userId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            var currentTime = getCurrentDateTime()

            //default notifications get added
            val notificationSettings = NotificationSettingsEntity(0, false, "08:00:00",
                true, true, currentTime, currentTime, null, userId)

            //insert into local db
            var id: Long = notificationSettingsDao.insertSettings(notificationSettings)
            Log.i("SUCCESS", "ID of new record = $id")

            // Create constraints for work request
            val constraints = Constraints.Builder()
                .setRequiresBatteryNotLow(true) //to save battery life
                .setRequiredNetworkType(NetworkType.UNMETERED) //will only run when connected to Wi-Fi
                .build()

            //create input data (to pass id of db row)
            val inputData = Data.Builder()
                .putLong("SETTINGS_ID", id)
                .build()

            //create work request
            val insertSettingsToRemote = OneTimeWorkRequest.Builder(AddSettingsToRemote::class.java)
                .setConstraints(constraints)
                .setInputData(inputData)
                .build()

            //add request to the queue
            workManager.enqueue(insertSettingsToRemote)
            /*
            //val id: Long? = notificationSettingsDao.insertSettings(notificationSettings)
            if(id == null) {
                Log.i("Error", "Notification Settings for new user weren't added to the local db.")
            } else {
                Log.i("New Notification Settings Created", "Users notification settings were added to the local db.")
            }*/
        }
    }
}

//TODO: pass in proper user id (remote) to this fragment. Otherwise temp solution = get user from api call and attach to