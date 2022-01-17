package project.softsquad.vegitable.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.*
import project.softsquad.vegitable.entity.NotificationLogEntity

@Dao
interface NotificationLogDao {
    @Query("SELECT * FROM NotificationLogs")
    fun getAll(): LiveData<List<NotificationLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(notificationLog: NotificationLogEntity)

    @Query("SELECT * FROM NotificationLogs WHERE notificationLogId = :logId")
    fun getLog(logId: Long): NotificationLogEntity

    @Query("SELECT * FROM NotificationLogs WHERE notificationType = 'Daily'")
    fun getDaily(): LiveData<List<NotificationLogEntity>>

    @Query("SELECT * FROM NotificationLogs WHERE notificationType = 'Alert'")
    fun getAlert(): LiveData<List<NotificationLogEntity>>

    @Query("SELECT * FROM NotificationLogs WHERE notificationType = 'Device'")
    fun getDevice(): LiveData<List<NotificationLogEntity>>
}