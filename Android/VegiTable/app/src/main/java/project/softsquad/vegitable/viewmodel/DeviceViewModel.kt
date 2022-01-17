package project.softsquad.vegitable.viewmodel

import android.app.Application
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.work.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import project.softsquad.vegitable.VegiTableDatabase
import project.softsquad.vegitable.entity.DeviceEntity
import project.softsquad.vegitable.getCurrentDateTime
import project.softsquad.vegitable.repositories.DeviceRepository
import project.softsquad.vegitable.workers.*
import kotlin.collections.ArrayList

/**
 * Author: Brianna McBurney
 * Description:
 */
@RequiresApi(Build.VERSION_CODES.O)
class DeviceViewModel(application: Application): AndroidViewModel(application) {
    var deviceList: ArrayList<DeviceEntity> = ArrayList()
    val _deviceList: MutableLiveData<ArrayList<DeviceEntity>> = MutableLiveData()
    val deviceDao = VegiTableDatabase.getInstance(application).DeviceDao()

    val readAllData: LiveData<List<DeviceEntity>>
    //val testReadData: LiveData<List<DeviceEntity>>

    private val repository: DeviceRepository

    //instance of work manager
    private val workManager = WorkManager.getInstance(application)

    //testing adding repository - TODO: fix how devices are handled
    init{
        val deviceDao2 = VegiTableDatabase.getInstance(application).DeviceDao()
        repository = DeviceRepository(deviceDao2)
        readAllData = repository.readAllData
        //testReadData = repository.readAllData(currentUserId)
    }
    /*
    fun getDevice(deviceId: Long) : LiveData<DeviceEntity>{
        return deviceDao.getDevice(deviceId)
    }*/

    fun getDevice(deviceId: Long): LiveData<DeviceEntity> {
        return deviceDao.getLiveDevice(deviceId)
    }

    fun addDevice(deviceEntity: DeviceEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            //insert into local db
            //deviceEntity.lastUpdateDateTime = LocalDateTime.now().toString()
            //deviceEntity.createDateTime = LocalDateTime.now().toString()
            //deviceEntity.archiveDateTime = null
            var id: Long = repository.pairDevice(deviceEntity)
            Log.i("SUCCESS", "ID of new record = $id") //for testing
            //deviceDao.insertPairedDevice(deviceEntity)

        }
    }
/*
    fun updateDevice(deviceEntity: DeviceEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            deviceEntity.lastUpdateDateTime = LocalDateTime.now().toString()
            val numRows = deviceDao.updateDeviceName(deviceEntity)
        }
    }
*/
    fun updateDeviceName(device: DeviceEntity) = runBlocking {
        viewModelScope.launch(Dispatchers.IO) {
            //get current time so we can update lastupdatedatetime to now
            val updateTime = getCurrentDateTime()

            //update local db
            repository.updateDevice(device, updateTime)

        }
        // Create constraints for work request
        val constraints = Constraints.Builder()
            .setRequiresBatteryNotLow(true) //to save battery life
            .setRequiredNetworkType(NetworkType.UNMETERED) //will only run when connected to Wi-Fi
            .build()

        //create input data (to pass id of db row)
        val inputData = Data.Builder()
            .putLong("DEVICE_ID", device.deviceId)
            .build()

        //create work request
        val updateDeviceInRemote = OneTimeWorkRequest.Builder(UpdateDeviceInRemote::class.java)
            .setConstraints(constraints)
            .setInputData(inputData)
            .build()

        //add request to the queue
        workManager.enqueue(updateDeviceInRemote)
    }

    fun archiveDevice(deviceEntity: DeviceEntity, userId: Int) = runBlocking {
        //get current time so we can update lastupdatedatetime to now
        val updateTime = getCurrentDateTime()
        viewModelScope.launch(Dispatchers.IO) {
            //update local db
            repository.unpairDevice(deviceEntity, getCurrentDateTime())
        }
        // Create constraints for work request
        val constraints = Constraints.Builder()
            .setRequiresBatteryNotLow(true) //to save battery life
            .setRequiredNetworkType(NetworkType.UNMETERED) //will only run when connected to Wi-Fi
            .build()

        //create input data (to pass id of db row)
        val inputData = Data.Builder()
            .putLong("DEVICE_ID", deviceEntity.deviceId)
            .putInt("USER_ID", userId)
            .putString("TIME", updateTime)
            .build()

        //create work request
        val archiveDeviceInRemote = OneTimeWorkRequest.Builder(ArchiveDeviceInRemote::class.java)
            .setConstraints(constraints)
            .setInputData(inputData)
            .build()

        //add request to the queue
        workManager.enqueue(archiveDeviceInRemote)
    }
}