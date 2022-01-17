package project.softsquad.vegitable.repositories

import androidx.lifecycle.LiveData
import project.softsquad.vegitable.dao.DeviceDao
import project.softsquad.vegitable.entity.DeviceEntity

class DeviceRepository(private val deviceDao: DeviceDao){

    val readAllData: LiveData<List<DeviceEntity>> = deviceDao.getAllDevices()

//    fun readAllData(userId: Int): LiveData<List<DeviceEntity>> {
//        return deviceDao.getAllDevices(userId)
//    }

    fun pairDevice(device: DeviceEntity): Long {
        return deviceDao.insertPairedDevice(device)
    }

    fun unpairDevice(device: DeviceEntity, currentTime: String) {
        deviceDao.unpairDevice(currentTime, device.deviceId)
    }

    fun updateDevice(device: DeviceEntity, currentTime: String) {
        deviceDao.updateName(device.deviceName, currentTime, device.deviceId)
    }
}