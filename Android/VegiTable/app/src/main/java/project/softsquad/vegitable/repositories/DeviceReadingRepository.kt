package project.softsquad.vegitable.repositories

import androidx.lifecycle.LiveData
import project.softsquad.vegitable.dao.DeviceReadingsDao
import project.softsquad.vegitable.entity.DeviceReadingsEntity

class DeviceReadingRepository(private val readingsDao: DeviceReadingsDao)
{
    val readAllData: LiveData<List<DeviceReadingsEntity>> = readingsDao.getAll()

    //used to insert readings to local after syncing from remote
    fun insertReading(reading: DeviceReadingsEntity) : Long {
        return readingsDao.insert(reading)
    }

    //to get single, most recent reading for a device
    fun getCurrentReading(deviceId: Int) : DeviceReadingsEntity{
        return readingsDao.getLatestReading(deviceId)
    }

    fun getAllForDevice(deviceId: Int): List<DeviceReadingsEntity>{
        return readingsDao.getAllForDevice(deviceId)
    }

    fun delete(deviceId: Int){
        readingsDao.delete(deviceId)
    }

    //to get range of readings fora device, based on start and end dates
    fun getHistoricalReading(deviceId: Int, startDate: String, endDate: String){
       // readingsDao.getAll(deviceId, startDate, endDate)
    }
}