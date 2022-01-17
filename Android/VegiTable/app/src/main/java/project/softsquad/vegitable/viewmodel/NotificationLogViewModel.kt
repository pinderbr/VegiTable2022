package project.softsquad.vegitable.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import project.softsquad.vegitable.VegiTableDatabase
import project.softsquad.vegitable.cards.NotificationLogRepository
import project.softsquad.vegitable.entity.NotificationLogEntity
import project.softsquad.vegitable.getCurrentDateTime

/**
 * Author: Jason Beattie
 * Description:
 */
class NotificationLogViewModel(application: Application): AndroidViewModel(application) {

    val readAllData: LiveData<List<NotificationLogEntity>>
    val readDaily: LiveData<List<NotificationLogEntity>>
    val readAlert: LiveData<List<NotificationLogEntity>>
    val readDevice: LiveData<List<NotificationLogEntity>>

    private val logRepository: NotificationLogRepository

    init{
        val notificationLogDao = VegiTableDatabase.getInstance(application).NotificationLogDao()
        logRepository = NotificationLogRepository(notificationLogDao)
        readAllData = logRepository.readAllData
        readDaily = logRepository.readDaily
        readAlert = logRepository.readAlert
        readDevice = logRepository.readDevice
    }

    fun insertNotification(){
        val daily: NotificationLogEntity = NotificationLogEntity(1, "Daily", getCurrentDateTime(), getCurrentDateTime(), getCurrentDateTime(), "This is a daily message", 1, 15)
        val alert: NotificationLogEntity = NotificationLogEntity(2, "Alert", getCurrentDateTime(), getCurrentDateTime(), getCurrentDateTime(), "This is an alert message", 1, 15)
        val device: NotificationLogEntity = NotificationLogEntity(3, "Device", getCurrentDateTime(), getCurrentDateTime(), getCurrentDateTime(), "This is a device message", 1, 15)
        viewModelScope.launch(Dispatchers.IO){
            logRepository.addLog(daily)
            logRepository.addLog(alert)
            logRepository.addLog(device)
        }

    }

}