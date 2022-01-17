package project.softsquad.vegitable.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import project.softsquad.vegitable.entity.NotificationSettingsEntity

@Dao
interface NotificationSettingsDao {
    @Query("SELECT * FROM NotificationSettings where userId_fk = :userId LIMIT 1")
    fun getSettingsForUser(userId: Long): LiveData<NotificationSettingsEntity>

    @Query("SELECT * FROM NotificationSettings LIMIT 1")
    fun getAllSettings(): LiveData<NotificationSettingsEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSettings(notification: NotificationSettingsEntity): Long

    @Query("SELECT * FROM NotificationSettings WHERE notificationSettingsId = :settingId LIMIT 1")
    fun getSetting(settingId: Long): NotificationSettingsEntity

    @Query("SELECT * FROM NotificationSettings WHERE notificationSettingsId = :settingId LIMIT 1")
    fun getLiveSetting(settingId: Long): LiveData<NotificationSettingsEntity>

    @Update
    fun updateSettings(settings: NotificationSettingsEntity)
}