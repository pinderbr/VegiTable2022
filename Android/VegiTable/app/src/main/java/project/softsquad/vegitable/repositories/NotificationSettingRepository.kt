package project.softsquad.vegitable.repositories

import androidx.lifecycle.LiveData
import project.softsquad.vegitable.dao.NotificationSettingsDao
import project.softsquad.vegitable.entity.NotificationSettingsEntity
/**
 * Author: Jason Beattie
 * Description:
 */
class NotificationSettingRepository (private val notificationSettingsDao: NotificationSettingsDao)
{

    val readAllData: LiveData<NotificationSettingsEntity> = notificationSettingsDao.getAllSettings()
//
//    suspend fun addNotification(notification: NotificationSettingsEntity) {
//        notificationSettingsDao.insert(notification)
//    }
}