@file:JvmName("WorkerUtils")

package project.softsquad.vegitable.workers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.navigation.NavDeepLinkBuilder
import project.softsquad.vegitable.ApiInterface
import project.softsquad.vegitable.ui.MainActivity
import project.softsquad.vegitable.R
import project.softsquad.vegitable.entity.*
import project.softsquad.vegitable.viewmodel.DeviceReadingViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Author: Martha Czerwik
 * Description: Methods that workers will call upon - to create notifications, and API calls
 */

private var apiInterface: ApiInterface = ApiInterface.create()
private lateinit var deviceReadingsViewModel: DeviceReadingViewModel


/**
 * For DEMO purposes - copied code from Android developer website
 * This is just to demonstrate that the sync is working (started and finished successfully)
 * @param message Message shown on the notification
 * @param context Context needed to create Toast
 */
fun pushNotification(title: String?, message: String, context: Context) {

    // Make a channel if necessary
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        val name = "Verbose WorkManager Notifications"
        val description = "Shows notifications whenever work starts"
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel("VERBOSE_NOTIFICATION", name, importance)
        channel.description = description

        // Add the channel
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?

        notificationManager?.createNotificationChannel(channel)
    }


    // Create the notification
    //TODO: add action items to the notification (e.g., "DISMISS", onclick of the notification itself to navigate to current bucket data page if the alert is a value out of range)
//    val pendingIntent = NavDeepLinkBuilder(context)
//        .setComponentName(MainActivity::class.java)
//        .setGraph(R.navigation.nav_graph)
//        .setDestination(R.id.viewBucketDataHistoricalFragment)
//       // .setArguments(bundle)
//        .createPendingIntent()

    val snoozeIntent = Intent(context, MainActivity::class.java).apply {
        action = "snooze"
        //putExtra(EXTRA_NOTIFICATION_ID, 0)
    }
    val snoozePendingIntent: PendingIntent =
        PendingIntent.getBroadcast(context, 0, snoozeIntent, 0)

    val openIntent = Intent(context, MainActivity::class.java).apply {
        action = "snooze"
        //putExtra(EXTRA_NOTIFICATION_ID, 0)
    }
    val openPendingIntent: PendingIntent =
        PendingIntent.getBroadcast(context, 0, openIntent, 0)

    //TODO: have action items actually working
    val builder = NotificationCompat.Builder(context, "VERBOSE_NOTIFICATION")
        .setSmallIcon(R.drawable.ic_baseline_notifications_24)
        .setContentTitle(title ?: "Worker Notification")
        .setContentText(message)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setVibrate(LongArray(0))
        .setStyle(NotificationCompat.BigTextStyle()
            .bigText(message))
        //.setContentIntent(pendingIntent)
        .addAction(
            R.drawable.logo_softsquad, "DETAILS", openPendingIntent)
        .addAction(
            R.drawable.logo_vegitable, "DISMISS",
            snoozePendingIntent
        )



    // Show the notification
    NotificationManagerCompat.from(context).notify(1, builder.build())
}

//region sync buckets to remote
fun createBucket(bucket: BucketEntity) {
    //call post request from api interface
    apiInterface.addBucket(bucket).enqueue(object : Callback<BucketEntity> {
        override fun onResponse(
            call: Call<BucketEntity>?,
            response: Response<BucketEntity>?
        ) {
            if (response?.body() != null) {
                Log.i("POST SUCCESS", "Bucket has been created")
            } else {
                Log.i("POST FAILURE", "Bucket could not be created")
            }
        }

        override fun onFailure(call: Call<BucketEntity>?, t: Throwable?) {
            Log.e("POST ERROR", "Something went wrong")
        }
    })
}

fun updateBucket(bucket: BucketEntity) {
    //add params (as this is a form url encoded PUT request, so this needs to be sent as the form)
    val params = HashMap<String?, String?>()
    params["bucketName"] = bucket.bucketName
    params["imageURL"] = bucket.imageURL
    params["lastUpdateDateTime"] = bucket.lastUpdateDateTime
    params["deviceId_fk"] = bucket.deviceId_fk.toString()
    params["temperatureMin"] = bucket.temperatureMin.toString()
    params["temperatureMax"] = bucket.temperatureMax.toString()
    params["phMin"] = bucket.phMin.toString()
    params["phMax"] = bucket.phMax.toString()
    params["ppmMin"] = bucket.ppmMin.toString()
    params["ppmMax"] = bucket.ppmMax.toString()
    params["lightMin"] = bucket.lightMin.toString()
    params["lightMax"] = bucket.lightMax.toString()
    params["humidityMin"] = bucket.humidityMin.toString()
    params["humidityMax"] = bucket.humidityMax.toString()

    //call put request from api interface
    apiInterface.updateBucket(bucket.userId_fk, bucket.bucketId.toInt(), params).enqueue(object : Callback<BucketEntity> {
        override fun onResponse(
            call: Call<BucketEntity>?,
            response: Response<BucketEntity>?
        ) {
            Log.i("PUT RESPONSE", response.toString())
            if (response?.body() != null) {
                //debug
                Log.i("PUT SUCCESS", "Bucket has been updated")
            } else {
                //debug
                Log.i("PUT FAILURE", "There is no bucket with that Id")
            }
        }

        override fun onFailure(call: Call<BucketEntity>?, t: Throwable?) {
            Log.i("PUT ERROR", "Something went wrong")
        }
    })
}

fun archiveBucket(bucket: BucketEntity){
    //add params (as this is a form url encoded PUT request, so this needs to be sent as the form)
    val params = HashMap<String?, String?>()
    params["lastUpdateDateTime"] = bucket.lastUpdateDateTime
    params["archiveDateTime"] = bucket.archiveDateTime

    //call put request from api interface
    apiInterface.archiveBucket(bucket.userId_fk, bucket.bucketId.toInt(), params).enqueue(object : Callback<BucketEntity> {
        override fun onResponse(
            call: Call<BucketEntity>?,
            response: Response<BucketEntity>?
        ) {
            if (response?.body() != null) {
                //debug
                Log.i("PUT SUCCESS", "Bucket has been archived")
            } else {
                //debug
                Log.i("PUT FAILURE", "There is no bucket with that Id")
            }
        }

        override fun onFailure(call: Call<BucketEntity>?, t: Throwable?) {
            Log.e("PUT ERROR", "Something went wrong")
        }
    })
}

//endregion

//region sync plants to remote
fun createPlant(newPlant: PlantsEntity) {
    //call post request from api interface
    apiInterface.addPlant(newPlant).enqueue(object : Callback<PlantsEntity> {
        override fun onResponse(
            call: Call<PlantsEntity>?,
            response: Response<PlantsEntity>?
        ) {
            if (response?.body() != null) {
                 //debug
                Log.i("POST SUCCESS", "Plant has been created")
            } else {
                //debug
                Log.i("POST FAILURE", "Plant could not be created")
            }
        }

        override fun onFailure(call: Call<PlantsEntity>?, t: Throwable?) {
            Log.e("POST ERROR", "Something went wrong")
        }
    })
}

fun updatePlant(plant: PlantsEntity) {
    //add params (as this is a form url encoded PUT request, so this needs to be sent as the form)
    val params = HashMap<String?, Any?>()
    params["plantName"] = plant.plantName
    params["plantType"] = plant.plantType
    params["temperatureMin"] = plant.temperatureMin.toString()
    params["temperatureMax"] = plant.temperatureMax.toString()
    params["phMin"] = plant.phMin.toString()
    params["phMax"] = plant.phMax.toString()
    params["ppmMin"] = plant.ppmMin.toString()
    params["ppmMax"] = plant.ppmMax.toString()
    params["lightMin"] = plant.lightMin.toString()
    params["lightMax"] = plant.lightMax.toString()
    params["humidityMin"] = plant.humidityMin.toString()
    params["humidityMax"] = plant.humidityMax.toString()
    params["plantPhase"] = plant.plantPhase
    params["lastUpdateDateTime"] = plant.lastUpdateDateTime
    params["imageURL"] = plant.imageURL
    params["bucketId_fk"] = plant.bucketId_fk.toString()

    //call put request from api interface
    apiInterface.updatePlant(plant.userId_fk, plant.plantId.toInt(), params).enqueue(object : Callback<PlantsEntity> {
        override fun onResponse(
            call: Call<PlantsEntity>?,
            response: Response<PlantsEntity>?
        ) {
            if (response?.body() != null) {
                Log.i("PUT SUCCESS", "Plant has been updated")
            } else {
                Log.i("PUT FAILURE", "There is no plant with that Id")
            }
        }

        override fun onFailure(call: Call<PlantsEntity>?, t: Throwable?) {
            Log.e("PUT ERROR", "Something went wrong")
        }
    })
}

fun archivePlant(plant: PlantsEntity) {
    //add params (as this is a form url encoded PUT request, so this needs to be sent as the form)
    val params = HashMap<String?, Any?>()
    params["lastUpdateDateTime"] = plant.lastUpdateDateTime
    params["archiveDateTime"] = plant.archiveDateTime

    //call put request from api interface
    apiInterface.archivePlant(plant.userId_fk, plant.plantId.toInt(), params).enqueue(object : Callback<PlantsEntity> {
        override fun onResponse(
            call: Call<PlantsEntity>?,
            response: Response<PlantsEntity>?
        ) {
            if (response?.body() != null) {
                Log.i("PUT SUCCESS", "Plant has been archived")
            } else {
                Log.i("PUT FAILURE", "There is no plant with that Id")
            }
        }

        override fun onFailure(call: Call<PlantsEntity>?, t: Throwable?) {
            Log.e("PUT ERROR", "Something went wrong")
        }
    })
}


//endregion

//region sync plant types to remote
fun createPlantType(newPlantType: PlantTypeEntity) {
    //call post request from api interface
    apiInterface.addPlantType(newPlantType).enqueue(object : Callback<PlantTypeEntity> {
        override fun onResponse(
            call: Call<PlantTypeEntity>?,
            response: Response<PlantTypeEntity>?
        ) {
            if (response?.body() != null) {
                //debug
                Log.i("POST SUCCESS", "Plant type has been created")
            } else {
                //debug
                Log.i("POST FAILURE", "Plant type could not be created")
            }
        }

        override fun onFailure(call: Call<PlantTypeEntity>?, t: Throwable?) {
            Log.e("POST ERROR", "Something went wrong")
        }
    })
}
//endregion

//region sync user to remote
fun updateUser(user: UsersEntity) {
    val params = HashMap<String?, String?>()
    params["userEmail"] = user.userEmail
    params["userPassword"] = user.userPassword
    params["userFirstName"] = user.userFirstName
    params["userLastName"] = user.userLastName
    params["lastUpdateDateTime"] = user.lastUpdateDateTime
    params["imageURL"] = user.imageURL

    apiInterface.updateUser(user.userId.toInt(), params)
        .enqueue(object : Callback<UsersEntity> {
            override fun onResponse(
                call: Call<UsersEntity>?,
                response: Response<UsersEntity>?
            ) {
                if (response?.body() != null) {
                    Log.i("PUT SUCCESS", "User has been updated")
                } else {
                    if (response != null) {
                        Log.i("PUT FAILURE", "Something went wrong! " + response.errorBody())
                    }
                }
            }
            override fun onFailure(call: Call<UsersEntity>?, t: Throwable?) {
                Log.i("PUT ERROR","Something went wrong")
            }
        })
}
/*
fun updateUser(user: UsersEntity) {
    //call put request from api interface
    apiInterface.updateUser(user.remoteId, user).enqueue(object : Callback<UsersEntity> {
        override fun onResponse(
            call: Call<UsersEntity>?,
            response: Response<UsersEntity>?
        ) {
            if (response?.body() != null) {
                Log.i("PUT SUCCESS", "User has been updated")
            } else {
                Log.i("PUT FAILURE", "There is no user with that Id")
            }
        }

        override fun onFailure(call: Call<UsersEntity>?, t: Throwable?) {
            Log.e("PUT ERROR", "Something went wrong")
        }
    })
}
*/
fun updateUserLocalId(localId: Long, remoteId: Int){
    //call put request from api interface
    apiInterface.updateUserLocalId(remoteId, localId).enqueue(object : Callback<UsersEntity> {
        override fun onResponse(
            call: Call<UsersEntity>?,
            response: Response<UsersEntity>?
        ) {
            if (response?.body() != null) {
                Log.i("PUT SUCCESS", "User's local id has been updated in remote")
            } else {
                Log.i("PUT FAILURE", "There is no user with that Id in remote")
            }
        }

        override fun onFailure(call: Call<UsersEntity>?, t: Throwable?) {
            Log.e("PUT ERROR", "Something went wrong")
        }
    })
}


//endregion

//region sync notification settings and logs to remote
fun createNotificationSetting(setting: NotificationSettingsEntity) {
    //call post request from api interface
    apiInterface.createNotificationSettings(setting).enqueue(object : Callback<NotificationSettingsEntity> {
        override fun onResponse(
            call: Call<NotificationSettingsEntity>?,
            response: Response<NotificationSettingsEntity>?
        ) {
            if (response?.body() != null) {
                //debug
                Log.i("POST SUCCESS", "Notification settings have been created")
            } else {
                //debug
                Log.i("POST FAILURE", "Notification settings could not be created")
            }
        }

        override fun onFailure(call: Call<NotificationSettingsEntity>?, t: Throwable?) {
            Log.e("POST ERROR", "Something went wrong")
        }
    })
}

fun updateNotificationSetting(setting: NotificationSettingsEntity) {
    //add params (as this is a form url encoded PUT request, so this needs to be sent as the form)
    val params = HashMap<String?, Any?>()
    params["dailyNotification"] = if (setting.dailyNotification) 1 else 0
    params["dailyNotificationTime"] = setting.dailyNotificationTime
    params["alertNotification"] = if (setting.alertNotification) 1 else 0
    params["deviceNotification"] = if (setting.deviceNotification) 1 else 0
    params["lastUpdateDateTime"] = setting.lastUpdateDateTime

    //call put request from api interface
    apiInterface.updateNotificationSettings(setting.userId_fk, params).enqueue(object : Callback<NotificationSettingsEntity> {
        override fun onResponse(
            call: Call<NotificationSettingsEntity>?,
            response: Response<NotificationSettingsEntity>?
        ) {
            if (response?.body() != null) {
                //debug
                Log.i("PUT SUCCESS", "Notification settings have been updated")
            } else {
                //debug
                Log.i("PUT FAILURE", "Notification settings could not be updated")
            }
        }

        override fun onFailure(call: Call<NotificationSettingsEntity>?, t: Throwable?) {
            Log.e("PUT ERROR", "Something went wrong")
        }
    })
}

fun createNotificationLog(setting: NotificationLogEntity) {
    //call post request from api interface
    apiInterface.createNotificationLog(setting).enqueue(object : Callback<NotificationLogEntity> {
        override fun onResponse(
            call: Call<NotificationLogEntity>?,
            response: Response<NotificationLogEntity>?
        ) {
            if (response?.body() != null) {
                //debug
                Log.i("POST SUCCESS", "Notification log has been created")
            } else {
                //debug
                Log.i("POST FAILURE", "Notification log could not be created")
            }
        }

        override fun onFailure(call: Call<NotificationLogEntity>?, t: Throwable?) {
            Log.e("POST ERROR", "Something went wrong")
        }
    })
}
//endregion

//TODO: check if remainder of device inserts/updates will be done locally first, if so, add them here
//region sync devices to remote
fun updateDevice(device: DeviceEntity) {
    val params = HashMap<String?, String?>()
    params["deviceName"] = device.deviceName
    params["lastUpdateDateTime"] = device.lastUpdateDateTime
    device.userId_fk?.let {
        apiInterface.updateDeviceName(it, device.deviceId.toInt(), params)
        .enqueue(object : Callback<DeviceEntity> {
            override fun onResponse(
                call: Call<DeviceEntity>?,
                response: Response<DeviceEntity>?
            ) {
                if (response?.body() != null) {
                    Log.i("PUT SUCCESS", "Device has been updated")
                } else {
                    if (response != null) {
                        Log.i("PUT FAILURE", "Something went wrong! " + response.errorBody())
                    }
                }
            }
            override fun onFailure(call: Call<DeviceEntity>?, t: Throwable?) {
                Log.i("PUT ERROR","Something went wrong")
            }
        })
    }

}

fun archiveDevice(device: Long, userId: Int, updateTime: String) {
    val params = HashMap<String?, String?>()
    params["lastUpdateDateTime"] = updateTime
    params["archiveDateTime"] = updateTime

        apiInterface.removeDevice(userId, device.toInt(), params)
        .enqueue(object : Callback<DeviceEntity> {
            override fun onResponse(
                call: Call<DeviceEntity>?,
                response: Response<DeviceEntity>?
            ) {
                if (response?.body() != null) {
                    Log.i("PUT SUCCESS", "Device has been archived")
                } else {
                    if (response != null) {
                        Log.i("PUT FAILURE", "Something went wrong! " + response.errorBody())
                    }
                }
            }
            override fun onFailure(call: Call<DeviceEntity>?, t: Throwable?) {
                Log.i("PUT ERROR","Something went wrong")
            }
        })

}

//endregion

//region sync devicereadings to local

private lateinit var readingss: ArrayList<DeviceReadingsEntity>
/*
 fun getDeviceReadings(deviceId: Int) : List<DeviceReadingsEntity> {
    var readings = ArrayList<DeviceReadingsEntity>()
    apiInterface.getLatestReadings(deviceId).enqueue(object : Callback<List<DeviceReadingsEntity>> {
        override fun onResponse(call: Call<List<DeviceReadingsEntity>>?, response: Response<List<DeviceReadingsEntity>>?) {
            if(response?.body() != null) {
                Log.i("GET SUCCESS", "Device readings have been fetched")
                readings = response.body() as ArrayList<DeviceReadingsEntity>
                readingss = readings
                //result.success(readings)

                //delete old readings if any exist
                //if (deviceReadingDao.getAllForDevice(deviceId) != null){
                //    deviceReadingDao.delete(deviceId)
                //}

                //insert new readings
                //for (reading in readings) {
                //    val id: Long = VegiTableDatabase.getInstance(context).DeviceReadingsDao().insert(reading)
                //    Log.i("SUCCESS", "ID of new record = $id")
                //}

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
     return readingss
     Log.i("TEST", "list of readings = $readingss")
}
*/


//endregion


