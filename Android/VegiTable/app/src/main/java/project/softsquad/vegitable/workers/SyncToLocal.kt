package project.softsquad.vegitable.workers

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.supervisorScope
import project.softsquad.vegitable.ApiInterface
import project.softsquad.vegitable.VegiTableDatabase
import project.softsquad.vegitable.entity.DeviceReadingsEntity
import project.softsquad.vegitable.entity.NotificationLogEntity
import project.softsquad.vegitable.entity.NotificationSettingsEntity
import project.softsquad.vegitable.getCurrentDateTime
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Author: Martha Czerwik
 * Description: Classes of Workers to carry out any syncing of remote db information to local db.
 * Classes here will be used to restore all of the user's information into their local database after app data has been cleared.
 * DeviceReadings will also be periodically synced every 4 hours and pulled into the local db
 */

private var apiInterface: ApiInterface = ApiInterface.create()
var readings = ArrayList<DeviceReadingsEntity>()

class SimulateNotification(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val appContext = applicationContext
        //pushNotification(null,"Starting sync - inserting to db", appContext)

        return try {
                //retrieve device id and bucket min/max values from input of work request
                val deviceId = inputData.getInt("DEVICE_ID", 0)
                val bucketId = inputData.getLong("BUCKET_ID", 0).toInt()
                val userId = inputData.getInt("USER_ID", 0)
                val name = inputData.getString("NAME")
                val minPh = inputData.getDouble("MIN_PH", 0.0)
                val maxPh = inputData.getDouble("MAX_PH", 0.0)
                val minPpm = inputData.getDouble("MIN_PPM", 0.0)
                val maxPpm = inputData.getDouble("MAX_PPM", 0.0)
                val minTemp = inputData.getDouble("MIN_TEMP", 0.0)
                val maxTemp = inputData.getDouble("MAX_TEMP", 0.0)
                val minHumid = inputData.getDouble("MIN_HUMID", 0.0)
                val maxHumid = inputData.getDouble("MAX_HUMID", 0.0)
                val minLight = inputData.getDouble("MIN_LIGHT", 0.0)
                val maxLight = inputData.getDouble("MAX_LIGHT", 0.0)

            //api call to get latest reading (for demo)
            //check if latest readings fall within range
            apiInterface.getCurrentReading(deviceId).enqueue(object : Callback<DeviceReadingsEntity> {
                override fun onResponse(call: Call<DeviceReadingsEntity>?, response: Response<DeviceReadingsEntity>?) {
                    if(response?.body() != null) {
                        Log.i("GET SUCCESS", "Device readings have been fetched: ${response.body()}")
                        var reading = response.body() as DeviceReadingsEntity

                        val notificationMessage = StringBuilder()

                        if (reading.errorReading != null) {
                            notificationMessage.append(reading.errorReading.toString())
                            pushNotification("Your device may need repair!", notificationMessage.toString(), appContext)

                            //insert notification into database
                        } else {
                            if (reading.phValue!! < minPh ){
                                notificationMessage.append("The pH levels are too low, try adding 1ml of pH+ per gallon of water and wait to see results.\n")
                            }
                            if (reading.phValue!! > maxPh ){
                                notificationMessage.append("The pH levels are too high, try adding 1ml of pH- per fallon of water and wait to see results.\n")
                            }
                            if (reading.ppmValue!! < minPpm ){
                                notificationMessage.append("The PPM levels are too low, try adding more of your nutrient mixture.\n")
                            }
                            if (reading.ppmValue!! > maxPpm ){
                                notificationMessage.append("The PPM levels are too high, try adding water to level it out.\n")
                            }
                            if (reading.temperatureValue!! < minTemp ){
                                notificationMessage.append("The temperature is too low, try warming up the room.\n")
                            }
                            if (reading.temperatureValue!! > maxTemp ){
                                notificationMessage.append("The temperature is too high, try cooling down the room.\n")
                            }
                            if (reading.humidityValue!! < minHumid ){
                                notificationMessage.append("Humidity levels are too low, try turning up your humidifier.\n")
                            }
                            if (reading.humidityValue!! > maxHumid ){
                                notificationMessage.append("The humidity levels are too high, try turning on more fans or adding a dehumidifier.\n")
                            }
                            if (reading.lightValue!! < minLight ){
                                notificationMessage.append("There is not enough light for ideal growth try lowering your lights or add more.\n")
                            }
                            if (reading.lightValue!! > maxLight ){
                                notificationMessage.append("There is too much light for ideal growth, try raising your lights or dimming them.\n")
                            }
                            notificationMessage.append("\nTo see more information about your bucket's ideal range for plant growth, tap on DETAILS.")

                            //insert notification to database
                            var currentTime: String = getCurrentDateTime()
                            var notification : NotificationLogEntity = NotificationLogEntity(0, "Alert", currentTime,currentTime, currentTime, notificationMessage.toString(), bucketId, userId)
                            createNotificationLog(notification)
                            Thread.sleep(5000)
                            pushNotification("$name needs adjusting!", notificationMessage.toString(), appContext)


                        }

                    } else {
                        if (response != null) {
                            Log.e("GET FAILURE", "Device readings dont exist for this device")
                        }
                    }
                }
                override fun onFailure(call: Call<DeviceReadingsEntity>?, t: Throwable?) {
                    Log.e("GET ERROR", "Something went wrong")
                }
            })

             Result.success()

        } catch (e: Exception) {
            Log.i("DEVINSERTWORKER", "Worker failed - notification could not be generated")
            Result.failure()
        }
    }
}














//THIS DOESNT WORK FFFFFSSSS
class SyncDeviceReadingsToLocal(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {

    private suspend fun getDeviceReadings(deviceId: Int) : List<DeviceReadingsEntity> {
       // val repository: DeviceReadingRepository = DeviceReadingRepository(VegiTableDatabase.getInstance(applicationContext).DeviceReadingsDao())

        apiInterface.getLatestReadings(deviceId).enqueue(object : Callback<List<DeviceReadingsEntity>> {
            override fun onResponse(call: Call<List<DeviceReadingsEntity>>?, response: Response<List<DeviceReadingsEntity>>?) {
                if(response?.body() != null) {
                    Log.i("GET SUCCESS", "Device readings have been fetched: ${response.body()}")
                    readings = response.body() as ArrayList<DeviceReadingsEntity>
                    for (reading in readings){
                        test(reading)
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
        Log.i("TEST", "list of readings = $readings")

        return readings
    }



    fun test(reading: DeviceReadingsEntity) {
        //coroutineScope {
            /*if (VegiTableDatabase.getInstance(applicationContext).DeviceReadingsDao().getAllForDevice(deviceId) != null){
                VegiTableDatabase.getInstance(applicationContext).DeviceReadingsDao().delete(deviceId)
            }
*/
            //for (reading in readings){
                VegiTableDatabase.getInstance(applicationContext).DeviceReadingsDao().insert(reading)
           // }
        //}
    }

    private fun createOutputData(readingId: Long, readingTime: String, phVal: Double?, tempVal: Double?, ppmVal: Double?, waterVal: Double?, humidVal: Double?, lightVal: Double?, errors: String?, devId: Int): Data {
        return Data.Builder()
            .putLong("id", readingId)
            .putString("time", readingTime)
            .putDouble("ph", phVal ?: 0.0)
            .putDouble("temp", tempVal ?: 0.0)
            .putDouble("ppm", ppmVal?: 0.0)
            .putDouble("water", waterVal?: 0.0)
            .putDouble("humid", humidVal?: 0.0)
            .putDouble("light", lightVal?: 0.0)
            .putString("error", errors?: "")
            .putInt("device", devId)
            .build()
    }

    override suspend fun doWork(): Result {
        val appContext = applicationContext

        return try {
                val deviceId = inputData.getInt("DEVICE_ID", 0)
            var outputData: Data
            apiInterface.getLatestReadings(deviceId).enqueue(object : Callback<List<DeviceReadingsEntity>> {
                override fun onResponse(call: Call<List<DeviceReadingsEntity>>?, response: Response<List<DeviceReadingsEntity>>?) {
                    if(response?.body() != null) {
                        Log.i("GET SUCCESS", "Device readings have been fetched")
                        //var readings = response.body() as DeviceReadingsEntity
                        //outputData = createOutputData(readings.deviceReadingId, readings.currentDateTimeStr, readings.phValue, readings.temperatureValue, readings.ppmValue, readings.waterValue, readings.humidityValue, readings.lightValue, readings.errorReading, readings.deviceId_fk)
                       // Result.success(outputData)
                        var readings = response.body() as List<DeviceReadingsEntity>
                        for (reading in readings){

                            VegiTableDatabase.getInstance(applicationContext).DeviceReadingsDao().insert(reading)
                        }

                       // outputData = createOutputData(readings.deviceReadingId, readings.currentDateTime, readings.phValue, readings.temperatureValue, readings.ppmValue, readings.waterValue, readings.humidityValue, readings.lightValue, readings.errorReading, readings.deviceId_fk)
                       // Result.success(outputData)

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

            Log.i("DEVREADINGSYNC", "Worker ran successfully - device readings have been synced")

            pushNotification(null, "Finished device reading sync", appContext)

            return Result.success()

        } catch (e: Exception) {
            Log.i("DEVREADINGSYNC", "Worker failed - device readings could not be synced")
            Log.i("DEVREADINGSYNC", e.toString())
            pushNotification(null, "Sync failed", appContext)
            Result.failure()
        }
    }
}

//THIS DOESNT WORK
class InsertCurrentReadingToLocal(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val appContext = applicationContext

        //for demo purposes, show notification that sync started
        pushNotification(null,"Starting sync - inserting to db", appContext)

        return try {
            coroutineScope {
                //retrieve device reading entity from output data of worker 1, query room db to insert
                val id = inputData.getLong("id", 0)
                val time = inputData.getString("time")
                val ph = inputData.getDouble("ph", 0.0)
                val temp = inputData.getDouble("temp", 0.0)
                val ppm = inputData.getDouble("ppm", 0.0)
                val water = inputData.getDouble("water", 0.0)
                val humid = inputData.getDouble("humid", 0.0)
                val light = inputData.getDouble("light", 0.0)
                val errors = inputData.getString("error")
                val devId = inputData.getInt("device", 0)

                val reading = DeviceReadingsEntity(
                    id,
                    time ?: "",
                    errors,
                    ph,
                    temp,
                    ppm,
                    water,
                    humid,
                    light,
                    devId
                )

                VegiTableDatabase.getInstance(applicationContext).DeviceReadingsDao()
                    .insert(reading)

                Log.i("DEVINSERTWORKER", "Worker ran successfully - reading has been inserted")

                //for demo purposes, show notification that sync completed
                pushNotification(null, "Finished sync - reading added to local db", appContext)
                Result.success()
            }
        } catch (e: Exception) {
            Log.i("DEVINSERTWORKER", "Worker failed - reading could not be inserted")
            pushNotification(null, "Sync failed", appContext)
            Result.failure()
        }
    }
}