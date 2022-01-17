package project.softsquad.vegitable.viewmodel.bucket

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.work.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import project.softsquad.vegitable.VegiTableDatabase
import project.softsquad.vegitable.repositories.BucketRepository
import project.softsquad.vegitable.entity.BucketEntity
import androidx.work.WorkManager
import androidx.work.OneTimeWorkRequest
import kotlinx.coroutines.runBlocking
import project.softsquad.vegitable.getCurrentDateTime
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.BackoffPolicy
import project.softsquad.vegitable.entity.DeviceEntity
import project.softsquad.vegitable.repositories.PlantRepository
import project.softsquad.vegitable.workers.*
import java.util.concurrent.TimeUnit


/**
 * Author: Jason Beattie
 * Modified By: Martha Czerwik
 * Description:
 */
class BucketViewModel(application: Application) : AndroidViewModel(application) {

    val currentBucketId = MutableLiveData<Long>()
    val currentBucket = MutableLiveData<BucketEntity>()
    val currentSensorId = MutableLiveData<Int>()

    fun setCurrentBucketId(bucketId: Long) {
        currentBucketId.value = bucketId
        getBucket(bucketId)
    }

    fun setCurrentSensor(sensorId: Int){
        currentSensorId.value = sensorId
    }

    fun setCurrentBucket(bucket: BucketEntity) {
        currentBucket.value = bucket
    }

    fun getSensor(deviceName: String): LiveData<DeviceEntity> {
        return repository.getBucketDevice(deviceName)
    }

    fun getBucket(bucketId: Long): LiveData<BucketEntity> {
        return repository.getLiveBucket(bucketId)
    }

    fun getLiveBucket(bucketId: Long): LiveData<BucketEntity> {
        val workingBucket = repository.getLiveBucket(bucketId)
        currentBucket.value = workingBucket.value
        return workingBucket
    }

    //create bucket list object
    val readAllData: LiveData<List<BucketEntity>>
    private val repository: BucketRepository
    private val plantRepository: PlantRepository

    //instance of work manager
    private val workManager = WorkManager.getInstance(application)

    init {
        //passing bucketDao
        val bucketDao = VegiTableDatabase.getInstance(application).BucketDao()
        val plantDao = VegiTableDatabase.getInstance(application).PlantsDao()
        repository = BucketRepository(bucketDao)
        plantRepository = PlantRepository(plantDao)
        readAllData = repository.readAllBucketData
        //currentBucketId.value = 0

    }



//    fun getPlantsByBucket() {
//        val bucketList = repository.readAllData.value
//        if (bucketList != null) {
//            for (bucket in bucketList) {
//                var plantList = getPlants(bucket.bucketId)
//                plantLists.value?.put(bucket.bucketId, plantList.value)
//            }
//        }
//        val dic = plantLists.value
//        print("hello")
//    }
//
//    private fun getPlants(bucketId: Long): LiveData<List<PlantsEntity>> {
//        return plantDao.getPlants(bucketId.toInt())
//    }

    /**
     * THIS IS A TEMP METHOD FOR DEMO PURPOSES - to display notifications. Can be partially used in future if we can figure out how to return from API response
     */
    fun showNotification(context: Context, bucket: BucketEntity){

        // Create constraints for work request
        val constraints = Constraints.Builder()
            .setRequiresBatteryNotLow(true) //to save battery life
            .setRequiredNetworkType(NetworkType.UNMETERED) //will only run when connected to Wi-Fi
            .build()

        //create input data (to pass id of db row)
        val inputData = Data.Builder()
            .putInt("DEVICE_ID", bucket.deviceId_fk)
            .putInt("USER_ID", bucket.userId_fk)
            .putString("NAME", bucket.bucketName)
            .putDouble("MIN_PH", bucket.phMin)
            .putDouble("MAX_PH", bucket.phMax)
            .putDouble("MIN_PPM", bucket.ppmMin)
            .putDouble("MAX_PPM", bucket.ppmMax)
            .putDouble("MIN_TEMP", bucket.temperatureMin)
            .putDouble("MAX_TEMP", bucket.temperatureMax)
            .putDouble("MIN_HUMID", bucket.humidityMin)
            .putDouble("MAX_HUMID", bucket.humidityMax)
            .putDouble("MIN_LIGHT", bucket.lightMin)
            .putDouble("MAX_LIGHT", bucket.lightMax)
            .putLong("BUCKET_ID", bucket.bucketId)
            .build()

        val periodicSyncDataWork =
            PeriodicWorkRequest.Builder(SimulateNotification::class.java, 15, TimeUnit.MINUTES)
                .setConstraints(constraints)
                .setInputData(inputData)
                .setBackoffCriteria(
                    BackoffPolicy.LINEAR,
                    PeriodicWorkRequest.MIN_BACKOFF_MILLIS,
                    TimeUnit.MILLISECONDS
                )
                .build()
        workManager.enqueueUniquePeriodicWork(
            "CHECK_NOTIFICATIONS",
            ExistingPeriodicWorkPolicy.REPLACE,  //Existing Periodic Work policy
            periodicSyncDataWork //work request
        )
    }

    /**
     * If validation on front end passes, this method is called to store the new bucket in the local database, and it sets up a work request to do the same via API call once constraints are met (connected to wi-fi and battery is not low)
     */
    fun addBucket(bucket: BucketEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            var currentTime = getCurrentDateTime()
            //insert into local db
            bucket.createDateTime = currentTime
            bucket.lastUpdateDateTime = currentTime
            var id: Long = repository.addBucket(bucket)
            Log.i("SUCCESS", "ID of new record = $id")

            // Create constraints for work request
            val constraints = Constraints.Builder()
                .setRequiresBatteryNotLow(true) //to save battery life
                .setRequiredNetworkType(NetworkType.UNMETERED) //will only run when connected to Wi-Fi
                .build()

            //create input data (to pass id of db row)
            val inputData = Data.Builder()
                .putLong("BUCKET_ID", id)
                .build()

            //create work request
            val insertBucketToRemote = OneTimeWorkRequest.Builder(AddBucketToRemote::class.java)
                .setConstraints(constraints)
                .setInputData(inputData)
                .build()

            //add request to the queue
            workManager.enqueue(insertBucketToRemote)
        }
    }

    /**
     * If validation on front end passes, this method is called to update the bucket in the local database, and it sets up a work request to do the same via API call once constraints are met (connected to wi-fi and battery is not low)
     */

    fun updateBucket(bucket: BucketEntity) = runBlocking {
        viewModelScope.launch(Dispatchers.IO) {

            //get current time so we can update lastupdatedatetime to now
            val currentTime = getCurrentDateTime()

            //update local db
            repository.updateBucket(bucket, currentTime)

        }

        // Create constraints for work request
        val constraints = Constraints.Builder()
            .setRequiresBatteryNotLow(true) //to save battery life
            .setRequiredNetworkType(NetworkType.UNMETERED) //will only run when connected to Wi-Fi
            .build()

        //create input data (to pass id of db row)
        val inputData = Data.Builder()
            .putLong("BUCKET_ID", bucket.bucketId)
            .build()

        //create work request
        val updateBucketInRemote = OneTimeWorkRequest.Builder(UpdateBucketInRemote::class.java)
            .setConstraints(constraints)
            .setInputData(inputData)
            .build()

        workManager.enqueue(updateBucketInRemote)

    }

    /**
     * this method is called to archive the bucket in the local database, and it sets up a work request to do the same via API call once constraints are met (connected to wi-fi and phone is not low)
     */
    fun archiveBucket(bucket: BucketEntity) = runBlocking {
        viewModelScope.launch(Dispatchers.IO) {

            //get current time so we can update lastupdatedatetime and archivedate to now
            val currentTime = getCurrentDateTime()

            //update local db - archive bucket as well as all plans belonging to bucket
            repository.archiveBucket(bucket, currentTime)

            //get list of plants associated with this bucket, loop through each to archive
            var plantList = plantRepository.getPlants(bucket.bucketId.toInt())
            val tempArr = arrayOf<Long>()

            var tempList: MutableList<Long> = tempArr.toMutableList()

            for (plant in plantList){
                plantRepository.archivePlant(plant, currentTime)
                tempList.add(plant.plantId)
            }
            var plantIds = tempList.toLongArray()
            archiveInRemote(bucket, plantIds)

        }


    }

    private fun archiveInRemote(bucket: BucketEntity, plantIds: LongArray){
        // Create constraints for work request
        val constraints = Constraints.Builder()
            .setRequiresBatteryNotLow(true) //to save battery life
            .setRequiredNetworkType(NetworkType.UNMETERED) //will only run when connected to Wi-Fi
            .build()

        //create input data (to pass id of db row)
        val inputData = Data.Builder()
            .putLong("BUCKET_ID", bucket.bucketId)
            .putLongArray("PLANT_IDS", plantIds)
            .build()

        //create work requests
        val archiveBucketInRemote =
            OneTimeWorkRequest.Builder(ArchiveBucketInRemote::class.java)
                .setConstraints(constraints)
                .setInputData(inputData)
                .build()

        val archivePlantsInRemote =
            OneTimeWorkRequest.Builder(ArchivePlantsInRemote::class.java)
                .setConstraints(constraints)
                .setInputData(inputData)
                .build()

        workManager.beginWith(archiveBucketInRemote)
            .then(archivePlantsInRemote)
            .enqueue()
    }

}