package project.softsquad.vegitable.viewmodel.bucket

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import project.softsquad.vegitable.VegiTableDatabase
import project.softsquad.vegitable.alert
import project.softsquad.vegitable.entity.BucketEntity


/**
 * Author: Brianna McBurney
 * Description:
 */
class BucketDataViewModel(application: Application): AndroidViewModel(application) {

    val currentBucketId = MutableLiveData<Long>()
    val currentBucket = MutableLiveData<BucketEntity>()
    private val bucketDao = VegiTableDatabase.getInstance(application).BucketDao()

    init {
        currentBucketId.value = 0
    }

    fun setCurrentBucketId(bucketId: Long) {
        currentBucketId.value = bucketId
        getBucket(bucketId)
    }

    fun setCurrentBucket(bucket: BucketEntity) {
        currentBucket.value = bucket
    }

    fun getBucket(bucketId: Long): LiveData<BucketEntity> {
        return bucketDao.getLiveBucket(bucketId)
    }
}