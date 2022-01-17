package project.softsquad.vegitable.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import project.softsquad.vegitable.entity.BucketEntity
import project.softsquad.vegitable.entity.DeviceEntity

@Dao
interface BucketDao {
    @Query("SELECT * FROM Buckets WHERE archiveDateTime IS NULL")
    fun getAll(): LiveData<List<BucketEntity>>

    @Query("SELECT * FROM Buckets WHERE bucketId = :bucketId")
    fun getBucket(bucketId: Long): BucketEntity

    @Query("SELECT * FROM Buckets WHERE bucketId = :bucketId")
    fun getLiveBucket(bucketId: Long): LiveData<BucketEntity>

    @Query("SELECT* FROM Buckets WHERE userId_fk = :userId AND archiveDateTime IS NULL")
    fun getBuckets(userId: Long): LiveData<List<BucketEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(bucket: BucketEntity): Long

    //update only the fields that can be updated via front end + update time
    @Query("UPDATE Buckets SET bucketName = :bucketName, deviceId_fk = :device, imageURL = :imageUrl, phMin = :minPh, phMax = :maxPh, ppmMin = :minPpm, ppmMax = :maxPpm, temperatureMin = :minTemp, temperatureMax = :maxTemp, lightMin = :minLight, lightMax = :maxLight, humidityMin = :minHumid, humidityMax = :maxHumid, lastUpdateDateTime = :updateTime WHERE bucketId = :bucketId")
    suspend fun update(
        bucketId: Long,
        bucketName: String,
        imageUrl: String,
        updateTime: String,
        device: Int,
        minPh: Double,
        maxPh: Double,
        minPpm: Double,
        maxPpm: Double,
        minTemp: Double,
        maxTemp: Double,
        minLight: Double,
        maxLight: Double,
        minHumid: Double,
        maxHumid: Double
    )

    @Query("SELECT * FROM Devices WHERE deviceName LIKE :deviceName")
    fun getLiveDevice(deviceName: String) : LiveData<DeviceEntity>

    //update threshold values + update time
    @Query("UPDATE Buckets SET phMin = :minPh, phMax = :maxPh, ppmMin = :minPpm, ppmMax = :maxPpm, temperatureMin = :minTemp, temperatureMax = :maxTemp, lightMin = :minLight, lightMax = :maxLight, humidityMin = :minHumid, humidityMax = :maxHumid, lastUpdateDateTime = :updateTime WHERE bucketId = :bucketId")
    fun updateThresholds(
        bucketId: Long,
        minPh: Double,
        maxPh: Double,
        minPpm: Double,
        maxPpm: Double,
        minTemp: Double,
        maxTemp: Double,
        minLight: Double,
        maxLight: Double,
        minHumid: Double,
        maxHumid: Double,
        updateTime: String
    )

    //archive bucket
    @Query("UPDATE Buckets SET archiveDateTime = :updateTime, lastUpdateDateTime = :updateTime WHERE bucketId = :bucketId")
    fun archive(updateTime: String, bucketId: Long)
}
