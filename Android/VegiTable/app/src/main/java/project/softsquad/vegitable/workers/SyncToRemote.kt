package project.softsquad.vegitable.workers

import android.content.Context
import android.util.Log
import androidx.work.WorkerParameters
import project.softsquad.vegitable.VegiTableDatabase
import project.softsquad.vegitable.entity.*
import project.softsquad.vegitable.getCurrentDateTime


/**
 * Author: Martha Czerwik
 * Description: Classes of Workers to carry out any syncing of local db information to remote db
 */

//region Buckets
class AddBucketToRemote(appContext: Context, workerParams: WorkerParameters) :
    androidx.work.Worker(appContext, workerParams) {

    override fun doWork(): Result {
        val appContext = applicationContext

        //for demo purposes, show notification that sync started
        //pushNotification(null, "Starting sync - adding bucket", appContext)

        return try {
            //retrieve bucket ID from work request, query room db to retrieve the bucket entity and pass this to createBucket method
            val bucketId = inputData.getLong("BUCKET_ID", 0)
            val bucket: BucketEntity = VegiTableDatabase.getInstance(applicationContext).BucketDao().getBucket(bucketId)

            //call on method within WorkerUtils.kt to insert the bucket to remote db via API
            createBucket(bucket)

            //TODO: determine how to handle this if api call not successful

            Log.i("BUCKETWORKER", "Worker ran successfully - bucket has been inserted")

            //for demo purposes, show notification that sync completed
           // pushNotification(null, "Finished sync - bucket added to remote db", appContext)
            return Result.success()

        } catch (e: Exception) {
            Log.i("BUCKETWORKER", "Worker failed - bucket could not be inserted")
           // pushNotification(null, "Sync failed", appContext)
            Result.failure()
        }
    }
}

class UpdateBucketInRemote(appContext: Context, workerParams: WorkerParameters) : androidx.work.Worker(appContext, workerParams) {
    override fun doWork(): Result {
        val appContext = applicationContext
        //pushNotification(null, "Starting sync - updating bucket", appContext)

        return try {
            //retrieve bucket ID from work request, query room db to retrieve the bucket entity and pass this to update method
            val bucketId = inputData.getLong("BUCKET_ID", 0)
            //TODO: check if this is grabbing the old version of the row
            val bucket: BucketEntity = VegiTableDatabase.getInstance(applicationContext).BucketDao().getBucket(bucketId)

            //call on method within WorkerUtils.kt to update the bucket in remote db via API
            updateBucket(bucket)

            Log.i("BUCKETWORKER", "Worker ran successfully - bucket has been updated")
            //pushNotification(null, "Finished sync -bucket updated in remote db", appContext)
            return Result.success()

        } catch (e: Exception) {
            Log.i("BUCKETWORKER", "Worker failed - bucket could not be updated")
            //pushNotification(null, "Sync failed", appContext)
            Result.failure()
        }
    }
}

class ArchiveBucketInRemote(appContext: Context, workerParams: WorkerParameters) : androidx.work.Worker(appContext, workerParams) {
    override fun doWork(): Result {
        val appContext = applicationContext
        //pushNotification(null,"Starting sync - archiving bucket", appContext)

        return try {
            //retrieve bucket ID from work request, query room db to retrieve the bucket entity and pass this to archive method
            val bucketId = inputData.getLong("BUCKET_ID", 0)
            val bucket: BucketEntity = VegiTableDatabase.getInstance(applicationContext).BucketDao().getBucket(bucketId)

            //call on method within WorkerUtils.kt to archive the bucket in remote db via API
            archiveBucket(bucket)

            Log.i("BUCKETWORKER", "Worker ran successfully - bucket has been archived")
            //pushNotification(null, "Finished sync - bucket archived in remote db", appContext)
            return Result.success()

        } catch (e: Exception) {
            Log.i("BUCKETWORKER", "Worker failed - bucket could not be archived")
           // pushNotification(null, "Sync failed", appContext)
            Result.failure()
        }
    }
}
//endregion

//region plants
class AddPlantToRemote(appContext: Context, workerParams: WorkerParameters) :
    androidx.work.Worker(appContext, workerParams) {

    override fun doWork(): Result {
        val appContext = applicationContext

        return try {
            //retrieve plant ID from work request, query room db to retrieve the plant entity and pass this to create plant method
            val plantId = inputData.getLong("PLANT_ID", 0)
            val plant: PlantsEntity = VegiTableDatabase.getInstance(applicationContext).PlantsDao().getPlant(plantId)

            //call on method within WorkerUtils.kt to insert the plant to remote db via API
            createPlant(plant)

            //TODO: determine how to handle this if api call not successful

            Log.i("PLANTWORKER", "Worker ran successfully - plant has been inserted")

            //for demo purposes, show notification that sync completed
            //pushNotification(null, "Finished sync - plant added to remote db", appContext)
            return Result.success()

        } catch (e: Exception) {
            Log.i("PLANTWORKER", "Worker failed - plant could not be inserted")
           // pushNotification(null, "Sync failed", appContext)
            Result.failure()
        }
    }
}

class UpdatePlantInRemote(appContext: Context, workerParams: WorkerParameters) :
    androidx.work.Worker(appContext, workerParams) {

    override fun doWork(): Result {
        val appContext = applicationContext

        return try {
            //retrieve plant ID from work request, query room db to retrieve the plant entity and pass this to update plant method
            val plantId = inputData.getLong("PLANT_ID", 0)
            val plant: PlantsEntity = VegiTableDatabase.getInstance(applicationContext).PlantsDao().getPlant(plantId)

            //call on method within WorkerUtils.kt to update the plant to remote db via API
            updatePlant(plant)

            //TODO: determine how to handle this if api call not successful

            Log.i("PLANTWORKER", "Worker ran successfully - plant has been updated")

            //for demo purposes, show notification that sync completed
           // pushNotification(null,"Finished sync - plant updated in remote db", appContext)
            return Result.success()

        } catch (e: Exception) {
            Log.i("PLANTWORKER", "Worker failed - plant could not be updated")
           // pushNotification(null,"Sync failed", appContext)
            Result.failure()
        }
    }
}

class ArchivePlantInRemote(appContext: Context, workerParams: WorkerParameters) :
    androidx.work.Worker(appContext, workerParams) {

    override fun doWork(): Result {
        val appContext = applicationContext

        return try {
            //retrieve plant ID from work request, query room db to retrieve the plant entity and pass this to archive plant method
            val plantId = inputData.getLong("PLANT_ID", 0)
            val plant: PlantsEntity = VegiTableDatabase.getInstance(applicationContext).PlantsDao().getPlant(plantId)

            //call on method within WorkerUtils.kt to archive the plant to remote db via API
            archivePlant(plant)

            //TODO: determine how to handle this if api call not successful

            Log.i("PLANTWORKER", "Worker ran successfully - plant has been archived")

            return Result.success()

        } catch (e: Exception) {
            Log.i("PLANTWORKER", "Worker failed - plant could not be archived")
           // pushNotification(null, "Sync failed", appContext)
            Result.failure()
        }
    }
}

class ArchivePlantsInRemote(appContext: Context, workerParams: WorkerParameters) :
    androidx.work.Worker(appContext, workerParams) {

    override fun doWork(): Result {
        val appContext = applicationContext

        return try {
            //retrieve plant ID from work request, query room db to retrieve the plant entity and pass this to archive plant method
            val plantIds = inputData.getLongArray("PLANT_IDS")

            if (plantIds != null) {
                for (id in plantIds){
                    val plant: PlantsEntity = VegiTableDatabase.getInstance(applicationContext).PlantsDao().getPlant(id)

                    //call on method within WorkerUtils.kt to archive the plant to remote db via API
                    archivePlant(plant)
                }
            }

            //TODO: determine how to handle this if api call not successful

            Log.i("PLANTWORKER", "Worker ran successfully - plant has been archived")

            return Result.success()

        } catch (e: Exception) {
            Log.i("PLANTWORKER", "Worker failed - plant could not be archived")
           // pushNotification(null, "Sync failed", appContext)
            Result.failure()
        }
    }
}

//endregion

//region plant types
class AddPlantTypeToRemote(appContext: Context, workerParams: WorkerParameters) :
    androidx.work.Worker(appContext, workerParams) {

    override fun doWork(): Result {
        val appContext = applicationContext

        //for demo purposes, show notification that sync started
       // pushNotification(null, "Starting sync - adding plant type", appContext)

        return try {
            //retrieve plant type ID from work request, query room db to retrieve the plant type entity and pass this to create plant type method
            val plantTypeId = inputData.getLong("PLANT_TYPE_ID", 0)
            val plantType: PlantTypeEntity = VegiTableDatabase.getInstance(applicationContext).PlantTypeDao().getPlantType(plantTypeId)

            //call on method within WorkerUtils.kt to insert the plant type to remote db via API
            createPlantType(plantType)

            //TODO: determine how to handle this if api call not successful

            Log.i("PLANTTYPEWORKER", "Worker ran successfully - plant type has been inserted")

            //for demo purposes, show notification that sync completed
            //pushNotification(null, "Finished sync - plant type added to remote db", appContext)
            return Result.success()

        } catch (e: Exception) {
            Log.i("PLANTTYPEWORKER", "Worker failed - plant type could not be inserted")
           // pushNotification(null,"Sync failed", appContext)
            Result.failure()
        }
    }
}
//endregion

//region users
//TODO: DOUBLE CHECK IF THIS IS GOOD

class UpdateUserInRemote(appContext: Context, workerParams: WorkerParameters) : androidx.work.Worker(appContext, workerParams) {
    override fun doWork(): Result {
        val appContext = applicationContext
       // pushNotification(null, "Starting sync - updating user", appContext)

        return try {
            //retrieve user ID from work request, query room db to retrieve the user entity and pass this to update method
            val userId = inputData.getLong("USER_ID", 0)
            val user: UsersEntity = VegiTableDatabase.getInstance(applicationContext).UsersDao().getUserById(userId)

            //call on method within WorkerUtils.kt to update the user in remote db via API
            updateUser(user)

            Log.i("USERWORKER", "Worker ran successfully - user has been updated")

           // pushNotification(null,"Finished sync - user updated in remote db", appContext)
            return Result.success()

        } catch (e: Exception) {
            Log.i("USERWORKER", "Worker failed - user could not be updated")
           // pushNotification(null,"Sync failed", appContext)
            Result.failure()
        }
    }
}

class UpdateUserIdInRemote(appContext: Context, workerParams: WorkerParameters) :
    androidx.work.Worker(appContext, workerParams) {
    override fun doWork(): Result {
        val appContext = applicationContext

        //for demo purposes, show notification that sync started
        //pushNotification(null, "Backing up data...", appContext)

        return try {
            //retrieve user local and remote ID from work request, and pass this to update method
            val localId = inputData.getLong("LOCAL_ID", 0)
            val remoteId = inputData.getInt("REMOTE_ID", 0)

            //call on method within WorkerUtils.kt to update the user in remote db via API
            updateUserLocalId(localId, remoteId)

            Log.i("USERSYNC", "Worker ran successfully - user has been synced")

            //pushNotification(null, "Finished user sync", appContext)
            return Result.success()

        } catch (e: Exception) {
            Log.i("USERSYNC", "Worker failed - user could not be synced")
            //pushNotification(null,"Sync failed", appContext)
            Result.failure()
        }
    }
}
//endregion

//TODO - add device classes if needed (if doing changes locally first)
//region devices
//for updating device name
class UpdateDeviceInRemote(appContext: Context, workerParams: WorkerParameters) :
    androidx.work.Worker(appContext, workerParams) {

    override fun doWork(): Result {
        val appContext = applicationContext

        //for demo purposes, show notification that sync started
       // pushNotification(null, "Starting sync - updating device", appContext)

        return try {
            //retrieve device ID from work request, query room db to retrieve the device entity and pass this to update method
            val deviceId = inputData.getLong("DEVICE_ID", 0)
            val device: DeviceEntity = VegiTableDatabase.getInstance(applicationContext).DeviceDao().getDevice(deviceId)

            //call on method within WorkerUtils.kt to archive the plant to remote db via API
            updateDevice(device)

            //TODO: determine how to handle this if api call not successful

            Log.i("DEVICEWORKER", "Worker ran successfully - device has been updated")

            //for demo purposes, show notification that sync completed
            //pushNotification(null, "Finished sync - device updated in remote db", appContext)
            return Result.success()

        } catch (e: Exception) {
            Log.i("DEVICEWORKER", "Worker failed - device could not be updated")
            //pushNotification(null, "Sync failed", appContext)
            Result.failure()
        }
    }
}

//to archive device
class ArchiveDeviceInRemote(appContext: Context, workerParams: WorkerParameters) :
    androidx.work.Worker(appContext, workerParams) {

    override fun doWork(): Result {
        val appContext = applicationContext

        //for demo purposes, show notification that sync started
        //pushNotification(null, "Starting sync - archiving device", appContext)

        return try {
            //retrieve device ID from work request, query room db to retrieve the device entity and pass this to update method
            val deviceId = inputData.getLong("DEVICE_ID", 0)
            val userId = inputData.getInt("USER_ID", 0)
            val updateTime = inputData.getString("TIME")
            //val device: DeviceEntity = VegiTableDatabase.getInstance(applicationContext).DeviceDao().getDevice(deviceId)

            //call on method within WorkerUtils.kt to archive the plant to remote db via API
            if (updateTime != null) {
                archiveDevice(deviceId, userId, updateTime)
            }

            //TODO: determine how to handle this if api call not successful

            Log.i("DEVICEWORKER", "Worker ran successfully - device has been updated")

            //for demo purposes, show notification that sync completed
            //pushNotification(null, "Finished sync - device updated in remote db", appContext)
            return Result.success()

        } catch (e: Exception) {
            Log.i("DEVICEWORKER", "Worker failed - device could not be updated")
            //pushNotification(null, "Sync failed", appContext)
            Result.failure()
        }
    }
}


//endregion

//region notification settings and logs
class AddSettingsToRemote(appContext: Context, workerParams: WorkerParameters) :
    androidx.work.Worker(appContext, workerParams) {

    override fun doWork(): Result {
        val appContext = applicationContext

        return try {
            //retrieve notification settings ID from work request, query room db to retrieve the settings entity and pass this to create settings method
            val settingsId = inputData.getLong("SETTINGS_ID", 0)
            val settings: NotificationSettingsEntity = VegiTableDatabase.getInstance(applicationContext).NotificationSettingsDao().getSetting(settingsId)

            //call on method within WorkerUtils.kt to insert the settings to remote db via API
            createNotificationSetting(settings)

            //TODO: determine how to handle this if api call not successful

            Log.i("SETTINGSWORKER", "Worker ran successfully - settings have been inserted")

            //for demo purposes, show notification that sync completed
            //pushNotification(null, "Finished sync - settings have been added to remote db", appContext)
            return Result.success()

        } catch (e: Exception) {
            Log.i("SETTINGSWORKER", "Worker failed - settings could not be inserted")
            //pushNotification(null, "Sync failed", appContext)
            Result.failure()
        }
    }
}

class UpdateSettingsInRemote(appContext: Context, workerParams: WorkerParameters) : androidx.work.Worker(appContext, workerParams) {
    override fun doWork(): Result {
        val appContext = applicationContext

        return try {
            //retrieve settings ID from work request, query room db to retrieve the settings entity and pass this to update method
            val settingsId = inputData.getLong("SETTINGS_ID", 0)
            val settings: NotificationSettingsEntity = VegiTableDatabase.getInstance(applicationContext).NotificationSettingsDao().getSetting(settingsId)

            //call on method within WorkerUtils.kt to update the bucket in remote db via API
            updateNotificationSetting(settings)

            Log.i("SETTINGSWORKER", "Worker ran successfully - settings have been updated")
            //pushNotification(null, "Finished sync - settings updated in remote db", appContext)
            return Result.success()

        } catch (e: Exception) {
            Log.i("SETTINGSWORKER", "Worker failed - settings could not be updated")
            //pushNotification(null, "Sync failed", appContext)
            Result.failure()
        }
    }
}

//TODO: confirm if notifications will be done locally - if yes, this method will be used to sync to remote. If no, this is not needed
class AddLogsToRemote(appContext: Context, workerParams: WorkerParameters) :
    androidx.work.Worker(appContext, workerParams) {

    override fun doWork(): Result {
        val appContext = applicationContext

        //for demo purposes, show notification that sync started
       // pushNotification(null, "Starting sync - adding logs", appContext)

        return try {
            //retrieve notification log ID from work request, query room db to retrieve the logs entity and pass this to create logs method
            val logId = inputData.getLong("LOG_ID", 0)
            val log: NotificationLogEntity = VegiTableDatabase.getInstance(applicationContext).NotificationLogDao().getLog(logId)

            //call on method within WorkerUtils.kt to insert the log to remote db via API
            createNotificationLog(log)

            //TODO: determine how to handle this if api call not successful

            Log.i("LOGWORKER", "Worker ran successfully - log has been inserted")

            //for demo purposes, show notification that sync completed
            //pushNotification(null, "Finished sync - loghas been added to remote db", appContext)
            return Result.success()

        } catch (e: Exception) {
            Log.i("LOGWORKER", "Worker failed - log could not be inserted")
            //pushNotification(null, "Sync failed", appContext)
            Result.failure()
        }
    }
}
//endregion