package project.softsquad.vegitable.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import project.softsquad.vegitable.entity.BucketEntity
import project.softsquad.vegitable.entity.DeviceReadingsEntity

@Dao
interface DeviceReadingsDao {

    //to get all device readings for a particular device within a range of dates
    //TODO: need to test this
    @Query("SELECT * FROM DeviceReadings")
    fun getAll(): LiveData<List<DeviceReadingsEntity>>

    @Query("SELECT * FROM DeviceReadings WHERE deviceId_fk = :deviceId")
    fun getAllForDevice(deviceId: Int) : List<DeviceReadingsEntity>

    @Query("SELECT * FROM DeviceReadings WHERE deviceId_fk = :deviceId ORDER BY currentDateTime DESC LIMIT 1")
    fun getLatestReading(deviceId: Int): DeviceReadingsEntity

    //to get the current reading for a particular device
    //@Query("SELECT * FROM DeviceReadings WHERE deviceId_fk = :deviceId order by currentDateTime DESC LIMIT 1")
    //fun getCurrentReading(deviceId: Int): BucketEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(deviceReadings: DeviceReadingsEntity): Long

    @Query("DELETE FROM DeviceReadings WHERE deviceId_fk =:deviceId")
    fun delete(deviceId: Int)
}