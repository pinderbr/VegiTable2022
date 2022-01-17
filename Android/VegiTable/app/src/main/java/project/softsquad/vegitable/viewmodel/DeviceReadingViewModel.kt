package project.softsquad.vegitable.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import androidx.work.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import project.softsquad.vegitable.ApiInterface
import project.softsquad.vegitable.VegiTableDatabase
import project.softsquad.vegitable.dao.DeviceReadingsDao
import project.softsquad.vegitable.entity.DeviceReadingsEntity
import project.softsquad.vegitable.repositories.DeviceReadingRepository
import project.softsquad.vegitable.workers.AddBucketToRemote
import project.softsquad.vegitable.workers.InsertCurrentReadingToLocal
import project.softsquad.vegitable.workers.SyncDeviceReadingsToLocal
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.reflect.Array

class  DeviceReadingViewModel (application: Application) : AndroidViewModel(application) {

    private val repository: DeviceReadingRepository

    private var apiInterface: ApiInterface = ApiInterface.create()


    //create device reading list object
    val readAllData: LiveData<List<DeviceReadingsEntity>>

    //instance of work manager
    private val workManager = WorkManager.getInstance(application)

    val retrievedReadings = MutableLiveData<List<DeviceReadingsEntity>>()

    fun setRetrievedReadings(readings: List<DeviceReadingsEntity>){
        retrievedReadings.value = readings
    }

    init {
        //passing bucketDao
        val deviceReadingsDao = VegiTableDatabase.getInstance(application).DeviceReadingsDao()
        repository = DeviceReadingRepository(deviceReadingsDao)
        readAllData = repository.readAllData
    }
/*
    fun getDeviceReadings(deviceId: Int, lastReadingTime: String) {
        var readings = ArrayList<DeviceReadingsEntity>()
        apiInterface.getLatestReadings(deviceId, lastReadingTime).enqueue(object :
            Callback<List<DeviceReadingsEntity>> {
            override fun onResponse(call: Call<List<DeviceReadingsEntity>>?, response: Response<List<DeviceReadingsEntity>>?) {
                if(response?.body() != null) {
                    Log.i("GET SUCCESS", "Device readings have been fetched")
                    readings = response.body() as ArrayList<DeviceReadingsEntity>

                    //delete old readings if any exist
                    if (repository.getAllForDevice(deviceId) != null){
                        repository.delete(deviceId)
                    }

                    //insert new readings
                    for (reading in readings) {
                        val id: Long = repository.insertReading(reading)
                        Log.i("SUCCESS", "ID of new record = $id")
                    }

                } else {
                    if (response != null) {
                        Log.e("GET FAILURE", "Device readings dont exist for this device")
                    }
                }
            }
            override fun onFailure(call: Call<List<DeviceReadingsEntity>>?, t: Throwable?) {
                Log.e("GET ERROR", "Something went wrong")
            }
        })
    }
*/
    //pass in array of device ids (long)
    //fun syncReadingsToLocal(devices: LongArray){
        fun syncReadingsToLocal() {
    viewModelScope.launch(Dispatchers.IO) {
        var devices = longArrayOf(2)
        for (id in devices){

                // Create constraints for work request
                val constraints = Constraints.Builder()
                    .setRequiresBatteryNotLow(true) //to save battery life
                    .setRequiredNetworkType(NetworkType.UNMETERED) //will only run when connected to Wi-Fi
                    .build()

                //create input data (to pass device id and timestamp)
                val inputData = Data.Builder()
                    .putInt("DEVICE_ID", id.toInt())
                   // .putString("READING", lastReadingTime)
                    .build()

                //create work request to get readings since last one, delete all old
                val syncReadingsToLocal = OneTimeWorkRequest.Builder(SyncDeviceReadingsToLocal::class.java)
                    .setConstraints(constraints)
                    .setInputData(inputData)
                    .build()
/*
                val insertCurrentReadingToLocal = OneTimeWorkRequest.Builder(
                    InsertCurrentReadingToLocal::class.java)
                    .setConstraints(constraints)
                    .build()
*/
                //add request to the queue
                workManager
                .beginWith(syncReadingsToLocal)
               // .then(insertCurrentReadingToLocal)
                .enqueue()

            }
       // }
    }}

}