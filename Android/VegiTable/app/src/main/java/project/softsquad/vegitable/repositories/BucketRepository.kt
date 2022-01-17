package project.softsquad.vegitable.repositories

import androidx.lifecycle.LiveData
import project.softsquad.vegitable.dao.BucketDao
import project.softsquad.vegitable.entity.BucketEntity
import project.softsquad.vegitable.entity.DeviceEntity

/**
 * Author: Jason Beattie
 * Modified By: Martha Czerwik
 * Description:
 */
class BucketRepository(private val bucketDao: BucketDao) {

    val readAllBucketData: LiveData<List<BucketEntity>> = bucketDao.getAll() // TODO replace with actual user ID

    fun addBucket(bucket: BucketEntity) : Long {
        return bucketDao.insert(bucket)
    }

    fun getBucket(bucketId: Long) : BucketEntity{
        return bucketDao.getBucket(bucketId)
    }

    fun getLiveBucket(bucketId: Long) : LiveData<BucketEntity>{
        return bucketDao.getLiveBucket(bucketId)
    }

    fun getBucketDevice(deviceName: String) : LiveData<DeviceEntity>{
        return bucketDao.getLiveDevice(deviceName)
    }

    fun getBuckets(userId: Long): LiveData<List<BucketEntity>>{
        return bucketDao.getBuckets(userId)
    }

    //for front end updating
    suspend fun updateBucket(bucket: BucketEntity, currentTime: String) {
        return bucketDao.update(bucket.bucketId, bucket.bucketName, bucket.imageURL, currentTime, bucket.deviceId_fk, bucket.phMin, bucket.phMax, bucket.ppmMin, bucket.ppmMax, bucket.temperatureMin, bucket.temperatureMax, bucket.lightMin, bucket.lightMax, bucket.humidityMin, bucket.humidityMax)
    }

    //to update thresholds when a new plant is added to bucket/plant is updated/plant is archived out of a bucket
    fun updateThresholds(bucket: BucketEntity, currentTime: String){
        return bucketDao.updateThresholds(bucket.bucketId, bucket.phMin, bucket.phMax, bucket.ppmMin, bucket.ppmMax, bucket.temperatureMin, bucket.temperatureMax, bucket.lightMin, bucket.lightMax, bucket.humidityMin, bucket.humidityMax, currentTime)
    }

    fun archiveBucket(bucket: BucketEntity, currentTime: String) {
        return bucketDao.archive(currentTime, bucket.bucketId)
    }

}