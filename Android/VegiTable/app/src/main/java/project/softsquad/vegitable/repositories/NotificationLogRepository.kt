package project.softsquad.vegitable.cards

import androidx.lifecycle.LiveData
import project.softsquad.vegitable.dao.BucketDao
import project.softsquad.vegitable.dao.NotificationLogDao
import project.softsquad.vegitable.entity.BucketEntity
import project.softsquad.vegitable.entity.NotificationLogEntity
/**
 * Author: Jason Beattie
 * Description:
 */
class NotificationLogRepository (private val notificationLogDao: NotificationLogDao)
{
    val readAllData: LiveData<List<NotificationLogEntity>> = notificationLogDao.getAll()
    val readDaily: LiveData<List<NotificationLogEntity>> = notificationLogDao.getDaily()
    val readAlert: LiveData<List<NotificationLogEntity>> = notificationLogDao.getAlert()
    val readDevice: LiveData<List<NotificationLogEntity>> = notificationLogDao.getDevice()

    fun addLog(log: NotificationLogEntity) {
        notificationLogDao.insert(log)
    }
}