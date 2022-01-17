package project.softsquad.vegitable.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.work.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import project.softsquad.vegitable.VegiTableDatabase
import project.softsquad.vegitable.dao.NotificationSettingsDao
import project.softsquad.vegitable.dao.UsersDao
import project.softsquad.vegitable.entity.DeviceReadingsEntity
import project.softsquad.vegitable.entity.NotificationSettingsEntity
import project.softsquad.vegitable.entity.UsersEntity
import project.softsquad.vegitable.getCurrentDateTime
import project.softsquad.vegitable.repositories.NotificationSettingRepository
import project.softsquad.vegitable.repositories.UserRepository
import project.softsquad.vegitable.workers.AddSettingsToRemote
import project.softsquad.vegitable.workers.UpdateSettingsInRemote

/**
 * Author: Brianna McBurney
 * Description:
 */
class NotificationSettingsViewModel(application: Application): AndroidViewModel(application) {

    //instance of work manager
    private val workManager = WorkManager.getInstance(application)

    private val repository: UserRepository
    private var userDao: UsersDao = VegiTableDatabase.getInstance(application).UsersDao()
    private var currentUser: LiveData<UsersEntity>

    val notificationSettings: LiveData<NotificationSettingsEntity>
    private val nsRepo: NotificationSettingRepository
    private var nsDao: NotificationSettingsDao = VegiTableDatabase.getInstance(getApplication()).NotificationSettingsDao()
//    private var notificationSettings: NotificationSettingsEntity = NotificationSettingsEntity(0, true, "08:00:00", true, true, null, null, null, 0)

    private var notSet = MutableLiveData<NotificationSettingsEntity>()

    private val _dailyNotification : MutableLiveData<Boolean> = MutableLiveData<Boolean>()
    private val _dailyNotificationTime : MutableLiveData<String> = MutableLiveData<String>()
    private val _alertNotification : MutableLiveData<Boolean> = MutableLiveData<Boolean>()
    private val _deviceNotification : MutableLiveData<Boolean> = MutableLiveData<Boolean>()

    init {
        //passing userDao
        repository = UserRepository(userDao)
        currentUser = repository.readAllData

        nsRepo = NotificationSettingRepository(nsDao)
        notificationSettings = nsRepo.readAllData
    }

    fun getSettings(userId: Long):LiveData<NotificationSettingsEntity> {
        val sett = nsDao.getSettingsForUser(userId)
        notSet.value = sett.value
        return sett
    }

    fun addNotificationSettings(settings: NotificationSettingsEntity?) {
        viewModelScope.launch(Dispatchers.IO) {
            var currentTime = getCurrentDateTime()

            //insert into local db
            if (settings != null) {
                settings.createDateTime = currentTime
                settings.lastUpdateDateTime = currentTime
                var id: Long = nsDao.insertSettings(settings)
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
            }
        }
    }

    val getDailyNotification: LiveData<Boolean>
        get() = _dailyNotification

    val getDailyNotificationTime: LiveData<String>
        get() = _dailyNotificationTime

    val getAlertNotification: LiveData<Boolean>
        get() = _alertNotification

    val getDeviceNotification: LiveData<Boolean>
        get() = _deviceNotification

    private fun updateSettingsDB() = runBlocking{
        viewModelScope.launch(Dispatchers.IO) {
            //get current time so we can update lastupdatedatetime to now
            val currentTime = getCurrentDateTime()

            notificationSettings.value?.lastUpdateDateTime = currentTime

            //update local db
            notificationSettings.value?.let { nsDao.updateSettings(it) }
            Log.i("Updated", "Settings updated")
        }

            // Create constraints for work request
            val constraints = Constraints.Builder()
                .setRequiresBatteryNotLow(true) //to save battery life
                .setRequiredNetworkType(NetworkType.UNMETERED) //will only run when connected to Wi-Fi
                .build()

            //create input data (to pass id of db row)
            val inputData = Data.Builder()
                .putLong("SETTINGS_ID", notificationSettings.value!!.notificationSettingsId)
                .build()

            //create work request
            val updateSettingsInRemote = OneTimeWorkRequest.Builder(UpdateSettingsInRemote::class.java)
                .setConstraints(constraints)
                .setInputData(inputData)
                .build()

            workManager.enqueue(updateSettingsInRemote)

    }

    fun updateDailyNotification(value: Boolean) {
        notificationSettings.value?.dailyNotification = value
        _dailyNotification.value = value
        updateSettingsDB()
    }

    fun updateDailyNotificationTime(value: String) {
        notificationSettings.value?.dailyNotificationTime = value
        _dailyNotificationTime.value = value
        updateSettingsDB()
    }

    fun updateAlertNotification(value: Boolean) {
        notificationSettings.value?.alertNotification = value
        _alertNotification.value = value
        updateSettingsDB()
    }

    fun updateDeviceNotification(value: Boolean) {
        notificationSettings.value?.deviceNotification = value
        _deviceNotification.value = value
        updateSettingsDB()
    }

    private fun Boolean.toInt() = if(this) 1 else 0

//    fun updateSettings(data: NotificationSettingsEntity) {
//        notificationSettings.value.dailyNotification = data.dailyNotification
//        notificationSettings.value.dailyNotificationTime = data.dailyNotificationTime
//        notificationSettings.value.alertNotification = data.alertNotification
//        notificationSettings.value.deviceNotification = data.deviceNotification
//
//        _dailyNotification.value = data.dailyNotification
//        _dailyNotificationTime.value = data.dailyNotificationTime
//        _alertNotification.value = data.alertNotification
//        _deviceNotification.value = data.deviceNotification
//        updateSettingsDB()
//    }

}