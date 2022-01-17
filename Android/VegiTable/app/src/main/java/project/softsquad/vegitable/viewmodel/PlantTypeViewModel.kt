package project.softsquad.vegitable.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import androidx.work.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import project.softsquad.vegitable.VegiTableDatabase
import project.softsquad.vegitable.repositories.PlantTypeRepository
import project.softsquad.vegitable.entity.PlantTypeEntity
import project.softsquad.vegitable.getCurrentDateTime
import project.softsquad.vegitable.workers.AddPlantTypeToRemote

/**
 * Author: Martha Czerwik
 * Description:
 */

class PlantTypeViewModel(application: Application): AndroidViewModel(application) {

    val readAllData: LiveData<List<PlantTypeEntity>>
    private val repository: PlantTypeRepository

    //instance of work manager
    private val workManager = WorkManager.getInstance(application)

    init{
        val plantTypeDao = VegiTableDatabase.getInstance(application).PlantTypeDao()
        repository = PlantTypeRepository(plantTypeDao)
        readAllData = repository.readAllData
    }

    fun getPlantTypeByName(plantName : String): LiveData<PlantTypeEntity> {
        return repository.getPlantTypeByName(plantName)
    }

    /**
     * If validation passes on front end, this method gets called
     */
    fun addPlantType(plantType: PlantTypeEntity){
        viewModelScope.launch(Dispatchers.IO) {
            var currentTime = getCurrentDateTime()
            plantType.createDateTime = currentTime
            plantType.lastUpdateDateTime = currentTime

            //insert into local db
            var id: Long = repository.addPlantType(plantType)
            Log.i("SUCCESS", "ID of new record = $id") //for testing

            // Create constraints for work request
            val constraints = Constraints.Builder()
                .setRequiresBatteryNotLow(true) //to save battery life
                .setRequiredNetworkType(NetworkType.UNMETERED) //will only run when connected to Wi-Fi
                .build()

            //create input data (to pass id of db row)
            val inputData = Data.Builder()
                .putLong("PLANT_TYPE_ID", id)
                .build()

            //create work request
            val insertPlantTypeToRemote = OneTimeWorkRequest.Builder(AddPlantTypeToRemote::class.java)
                .setConstraints(constraints)
                .setInputData(inputData)
                .build()

            //add request to the queue
            workManager.enqueue(insertPlantTypeToRemote)

        }
    }
}