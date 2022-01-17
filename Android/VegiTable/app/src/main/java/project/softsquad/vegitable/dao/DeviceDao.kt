package project.softsquad.vegitable.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import project.softsquad.vegitable.entity.DeviceEntity
import project.softsquad.vegitable.entity.NotificationSettingsEntity
import project.softsquad.vegitable.entity.UsersEntity

@Dao
interface DeviceDao {
    @Query("SELECT * FROM Devices where archiveDateTime IS NULL")
    fun getAllDevices(): LiveData<List<DeviceEntity>>

    @Query("SELECT * FROM Devices WHERE deviceId = :deviceId")
    fun getDevice(deviceId: Long) : DeviceEntity

    @Query("SELECT * FROM Devices WHERE deviceId = :deviceId AND archiveDateTime IS NULL")
    fun getLiveDevice(deviceId: Long) : LiveData<DeviceEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPairedDevice(device: DeviceEntity): Long

    @Update
    fun updateDeviceName(device: DeviceEntity): Int

    @Query("UPDATE Devices SET deviceName = :name, lastUpdateDateTime = :updateTime WHERE deviceId = :deviceId")
    fun updateName(name: String, updateTime: String, deviceId: Long)

    //to archive/unpair a device
    @Query("UPDATE Devices SET archiveDateTime = :updateTime, lastUpdateDateTime = :updateTime, userId_fk = null WHERE deviceId = :deviceId")
    fun unpairDevice(updateTime: String, deviceId: Long)

    @Delete
    fun deleteDevice(device: DeviceEntity)
}