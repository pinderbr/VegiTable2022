package project.softsquad.vegitable.viewmodel.plant

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import androidx.work.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import project.softsquad.vegitable.VegiTableDatabase
import project.softsquad.vegitable.dao.BucketDao
import project.softsquad.vegitable.dao.PlantsDao
import project.softsquad.vegitable.entity.BucketEntity
import project.softsquad.vegitable.repositories.PlantRepository
import project.softsquad.vegitable.entity.PlantsEntity
import project.softsquad.vegitable.getCurrentDateTime
import project.softsquad.vegitable.repositories.BucketRepository
import project.softsquad.vegitable.workers.*
import androidx.lifecycle.MutableLiveData


/**
 * Author: Jason Beattie
 * Modified By: Martha Czerwik
 * Description:
 */
class PlantViewModel(application: Application) : AndroidViewModel(application) {

    //var plantId: Long = 1
    val readAllData: LiveData<List<PlantsEntity>>

    val currentPlant = MutableLiveData<PlantsEntity>()
    val currentBucket = MutableLiveData<BucketEntity>()
    val currentPlantList = MutableLiveData<List<PlantsEntity>>()

    private val plantDao: PlantsDao = VegiTableDatabase.getInstance(application).PlantsDao()
    private val repository: PlantRepository = PlantRepository(plantDao)

    val bucketDao: BucketDao = VegiTableDatabase.getInstance(application).BucketDao()
    val bucketRepository: BucketRepository = BucketRepository(bucketDao)

    //instance of work manager
    private val workManager = WorkManager.getInstance(application)

    init {
        readAllData = repository.readAllData

    }

    fun setCurrentPlant(plant: PlantsEntity) {
        currentPlant.value = plant
    }

    fun setCurrentBucket(bucket: BucketEntity) {
        currentBucket.value = bucket
    }

    fun setcurrentPlantList(plants: List<PlantsEntity>) {
        currentPlantList.value = plants
    }

    fun singlePlant(id: Long): LiveData<PlantsEntity> {
        return repository.getTestPlant(id)
    }
/*
    fun getPlant(plantId: Long) :PlantsEntity {
        var plant :PlantsEntity
        viewModelScope.launch(Dispatchers.IO){
            plant = repository.getPlant(plantId)
            //plant.postValue(returnedPlant)
        }
        return plant
    }*/

    fun getPlant(plantId: Long): PlantsEntity {
        return repository.getPlant(plantId)
    }

    fun getPlants(bucketId: Long): LiveData<List<PlantsEntity>> {
        return repository.getLivePlants(bucketId.toInt())
    }

    /**
     * If validation passes on front end, this method gets called
     */
    fun addPlant(plant: PlantsEntity, bucket: BucketEntity, plantList: List<PlantsEntity>) {
        viewModelScope.launch(Dispatchers.IO) {
            //get current time so we can update lastupdatedatetime/create time to now
            val currentTime = getCurrentDateTime()

            //insert into local db
            plant.createDateTime = currentTime
            plant.lastUpdateDateTime = currentTime
            val id: Long = repository.addPlant(plant)
            Log.i("SUCCESS", "ID of new record = $id") //for testing

            //update bucket thresholds and save bucket in local db
            var updatedBucket = updateThresholds(bucket, plantList, plant)
            bucketRepository.updateBucket(updatedBucket, currentTime)

            // Create constraints for work request
            val constraints = Constraints.Builder()
                .setRequiresBatteryNotLow(true) //to save battery life
                .setRequiredNetworkType(NetworkType.UNMETERED) //will only run when connected to Wi-Fi
                .build()

            //create input data (to pass id of db row)
            val inputData = Data.Builder()
                .putLong("PLANT_ID", id)
                .putLong("BUCKET_ID", updatedBucket.bucketId)
                .build()

            //create work requests
            val insertPlantToRemote =
                OneTimeWorkRequest.Builder(AddPlantToRemote::class.java)
                    .setConstraints(constraints)
                    .setInputData(inputData)
                    .build()

            val updateBucketInRemote =
                OneTimeWorkRequest.Builder(UpdateBucketInRemote::class.java)
                    .setConstraints(constraints)
                    .setInputData(inputData)
                    .build()

            //add requests to the queue
            workManager.beginWith(insertPlantToRemote)
                .then(updateBucketInRemote)
                .enqueue()
        }
    }

    fun archivePlant(plant: PlantsEntity, bucketId: Long) = runBlocking {
        viewModelScope.launch(Dispatchers.IO) {
            //get current time so we can update lastupdatedatetime/archivetime to now
            val currentTime = getCurrentDateTime()
            plant.archiveDateTime = currentTime

            //update in local db
            repository.archivePlant(plant, currentTime)

            //get newly archived plant

            //get bucket andplants list
            var bucket = bucketRepository.getBucket(bucketId)
            var plantList = repository.getPlants(bucketId.toInt())

            //update bucket thresholds and save bucket in local db
            var updatedBucket = updateThresholds(bucket, plantList, plant)
            bucketRepository.updateBucket(updatedBucket, currentTime)

            //call on method to create work request to update remote db
            archivePlantInRemote(plant, updatedBucket)

        }
        /*
        // Create constraints for work request
        val constraints = Constraints.Builder()
            .setRequiresBatteryNotLow(true) //to save battery life
            .setRequiredNetworkType(NetworkType.UNMETERED) //will only run when connected to Wi-Fi
            .build()

        //create input data (to pass id of db row)
        val inputData = Data.Builder()
            .putLong("PLANT_ID", plant.plantId)
            .putLong("BUCKET_ID", updatedBucket.bucketId)
            .build()

        //create work request
        val archivePlantInRemote = OneTimeWorkRequest.Builder(ArchivePlantInRemote::class.java)
            .setConstraints(constraints)
            .setInputData(inputData)
            .build()

        val updateBucketInRemote = OneTimeWorkRequest.Builder(UpdateBucketInRemote::class.java)
            .setConstraints(constraints)
            .setInputData(inputData)
            .build()

        //add requests to the queue
        workManager.beginWith(archivePlantInRemote)
            .then(updateBucketInRemote)
            .enqueue()
*/
    }

    /**
     * If validation passes on front end, this method is called
     */
    fun updatePlant(plant: PlantsEntity, bucket: BucketEntity, plantList: List<PlantsEntity>) =
        runBlocking {
            viewModelScope.launch(Dispatchers.IO) {

                //TODO: insert call to method that updates thresholds here - need to first check if plant can be updated with these values

                //get current time so we can update lastupdatedatetime to now
                val currentTime = getCurrentDateTime()

                //update plant in local db
                repository.updatePlant(plant, currentTime)

                //update bucket thresholds and save bucket in local db
                val updatedBucket = updateThresholds(bucket, plantList, plant)
                bucketRepository.updateBucket(updatedBucket, currentTime)

                //call on method to create work request to update remote db
                updatePlantInRemote(plant, updatedBucket)
            }
        }

    private fun updatePlantInRemote(plant: PlantsEntity, bucket: BucketEntity) {
        //create work request to update plant and bucket in remote
        // Create constraints for work request
        val constraints = Constraints.Builder()
            .setRequiresBatteryNotLow(true) //to save battery life
            .setRequiredNetworkType(NetworkType.UNMETERED) //will only run when connected to Wi-Fi
            .build()

        //create input data (to pass id of db row)
        val inputData = Data.Builder()
            .putLong("PLANT_ID", plant.plantId)
            .putLong("BUCKET_ID", bucket.bucketId)
            .build()

        //create work request
        val updatePlantInRemote = OneTimeWorkRequest.Builder(UpdatePlantInRemote::class.java)
            .setConstraints(constraints)
            .setInputData(inputData)
            .build()

        val updateBucketInRemote = OneTimeWorkRequest.Builder(UpdateBucketInRemote::class.java)
            .setConstraints(constraints)
            .setInputData(inputData)
            .build()

        //add requests to the queue
        workManager.beginWith(updatePlantInRemote)
            .then(updateBucketInRemote)
            .enqueue()
    }

    private fun archivePlantInRemote(plant: PlantsEntity, bucket: BucketEntity) {
        //create work request to update plant and bucket in remote
        // Create constraints for work request
        val constraints = Constraints.Builder()
            .setRequiresBatteryNotLow(true) //to save battery life
            .setRequiredNetworkType(NetworkType.UNMETERED) //will only run when connected to Wi-Fi
            .build()

        //create input data (to pass id of db row)
        val inputData = Data.Builder()
            .putLong("PLANT_ID", plant.plantId)
            .putLong("BUCKET_ID", bucket.bucketId)
            .build()

        //create work request
        val archivePlantInRemote = OneTimeWorkRequest.Builder(ArchivePlantInRemote::class.java)
            .setConstraints(constraints)
            .setInputData(inputData)
            .build()

        val updateBucketInRemote = OneTimeWorkRequest.Builder(UpdateBucketInRemote::class.java)
            .setConstraints(constraints)
            .setInputData(inputData)
            .build()

        //add requests to the queue
        workManager.beginWith(archivePlantInRemote)
            .then(updateBucketInRemote)
            .enqueue()
    }

    /**
     * Method to update a bucket's threshold values, once a plant is updated, removed or added to the bucket
     */
    fun updateThresholds(
        bucket: BucketEntity,
        plants: List<PlantsEntity>,
        plant: PlantsEntity
    ): BucketEntity {
        var minPh: Double
        var maxPh: Double
        var minPpm: Double
        var maxPpm: Double
        var minTemp: Double
        var maxTemp: Double
        var minHumid: Double
        var maxhumid: Double
        var minLight: Double
        var maxLight: Double
        if (plant.archiveDateTime == null) {
            //get min/max values from plant and declare as min/max
            minPh = plant.phMin
             maxPh = plant.phMax
             minPpm = plant.ppmMin
             maxPpm = plant.ppmMax
             minTemp = plant.temperatureMin
             maxTemp = plant.temperatureMax
             minHumid = plant.humidityMin
             maxhumid = plant.humidityMax
             minLight = plant.lightMin
             maxLight = plant.lightMax
        }else {
             minPh = 0.0
             maxPh = 999999.0
             minPpm = 0.0
             maxPpm = 999999.0
             minTemp = 0.0
             maxTemp = 999999.0
             minHumid = 0.0
             maxhumid = 999999.0
             minLight = 0.0
             maxLight = 999999.0
        }

        //loop through rest of plants to determine if min/max will be adjusted to fit all plants
        for (plant in plants) {
            if (plant.phMin > minPh) {
                minPh = plant.phMin
            }
            if (plant.phMax < maxPh) {
                maxPh = plant.phMax
            }
            if (plant.ppmMin > minPpm) {
                minPpm = plant.ppmMin
            }
            if (plant.ppmMax < maxPpm) {
                maxPpm = plant.ppmMax
            }
            if (plant.temperatureMin > minTemp) {
                minTemp = plant.temperatureMin
            }
            if (plant.temperatureMax < maxTemp) {
                maxTemp = plant.temperatureMax
            }
            if (plant.humidityMin > minHumid) {
                minHumid = plant.humidityMin
            }
            if (plant.humidityMax < maxhumid) {
                maxhumid = plant.humidityMax
            }
            if (plant.lightMin > minLight) {
                minLight = plant.lightMin
            }
            if (plant.lightMax < maxLight) {
                maxLight = plant.lightMax
            }

        }

        //set min and max values to the bucket and return
        bucket.phMin = minPh
        bucket.phMax = maxPh
        bucket.ppmMin = minPpm
        bucket.ppmMax = maxPpm
        bucket.temperatureMin = minTemp
        bucket.temperatureMax = maxTemp
        bucket.humidityMin = minHumid
        bucket.humidityMax = maxhumid
        bucket.lightMin = minLight
        bucket.lightMax = maxLight

        return bucket

    }


    /**
     * Method to check if the plant being added/updated willworkwith the bucket's threshold

    fun checkThresholds(bucket: BucketEntity, numPlants: Int, plant: PlantsEntity) : Boolean {
    if (numPlants == 0){
    //return true if bucket has no plants - no errors can be caused
    return true
    }
    //return false if any min/max values will conflict
    return !(plant.phMin >= bucket.phMax ||
    plant.phMax <= bucket.phMin ||
    plant.ppmMin >= bucket.ppmMax ||
    plant.ppmMax <= bucket.ppmMin ||
    plant.temperatureMin >= bucket.temperatureMax ||
    plant.temperatureMax <= bucket.temperatureMin ||
    plant.lightMin >= bucket.lightMax ||
    plant.lightMax <= bucket.lightMin ||
    plant.humidityMin >= bucket.humidityMax ||
    plant.humidityMax <= bucket.humidityMin)

    //otherwise return true, no conflicts
    return true
    }
     */
}