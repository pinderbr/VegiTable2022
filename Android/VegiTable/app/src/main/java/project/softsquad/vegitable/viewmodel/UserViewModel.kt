package project.softsquad.vegitable.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.work.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import project.softsquad.vegitable.VegiTableDatabase
import project.softsquad.vegitable.entity.BucketEntity
import project.softsquad.vegitable.entity.UsersEntity
import project.softsquad.vegitable.getCurrentDateTime
import project.softsquad.vegitable.repositories.BucketRepository
import project.softsquad.vegitable.repositories.UserRepository
import project.softsquad.vegitable.workers.*

class UserViewModel(application: Application): AndroidViewModel(application) {

    val userId = MutableLiveData<Long>()
    val userDao = VegiTableDatabase.getInstance(application).UsersDao()

    fun insertUser(newUser: UsersEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            val user = userDao.getUser(newUser.userId)
            if (user == null) {
                newUser.lastUpdateDateTime = getCurrentDateTime()
                newUser.createDateTime = getCurrentDateTime()
                var id = userDao.insertUser(newUser)
                print(id)
            } else {
                Log.i("USER EXISTS","user already exists in local db")
            }
        }
    }

    fun setUserId(id: Long){
        userId.value = id
    }

    fun returnUserId(): LiveData<Long>{
        return userId
    }

    //create user list object (will not have a list technically)
   // val readAllData: LiveData<List<UsersEntity>>
    val readAllData: LiveData<UsersEntity> //changed to get onlythe first user as a temp solution

    private val repository: UserRepository

    //instance of work manager
    private val workManager = WorkManager.getInstance(application)

    init{
        val userDao2 = VegiTableDatabase.getInstance(application).UsersDao()
        //passing userDao
        repository = UserRepository(userDao2)
        readAllData = repository.readAllData
      //  userId.value = passedInId
        //userId.value = 0
    }

   // fun getUserByRemoteId(id: Int) : Int {
        //viewModelScope.launch(Dispatchers.IO) {
           // return repository.getUserByRemoteId(id)
        //}
   // }



    /**
     * If local database is empty, this method is called to begin the sync from remote to local
     */
    /*
    fun syncToLocal(user: UsersEntity){
        viewModelScope.launch(Dispatchers.IO) {

            //TODO:add the below into chained work requests

            //1. insert user object into Local
            var id: Long = repository.addUser(user)
            Log.i("SUCCESS", "ID of new record = $id")

            //2. add work request to call API method to update localId of user
            //create input data (to pass id of db row)
            val userIds = Data.Builder()
                .putLong("LOCAL_ID", id)
                .putInt("REMOTE_ID", user.remoteId)
                .build()

            val updateIdsRequest = OneTimeWorkRequest.Builder(UpdateUserIdInRemote::class.java)
                .setInputData(userIds)
                .build()



            //3. GET devices (pass in remote user id) and insert into local
            val syncDevicesLocally = OneTimeWorkRequest.Builder(SyncDevicesToLocal::class.java)
            .setInputData(userIds)
            .build()

            //continuation = continuation.then(syncDevicesLocally)

            //3. call API method to update localIds
            val syncDevicesRemotely = OneTimeWorkRequest.Builder(UpdateDeviceIdsInRemote::class.java)
                .setInputData(userIds)
                .build()


            //create chain of work - to be done in order to avoid any conflicts
            var continuation = workManager.beginWith(updateIdsRequest)
                .then(syncDevicesLocally)
                .then(syncDevicesRemotely)

            // Start the work
            continuation.enqueue()



            //3. GET NotificationSettings
            //3a. insert into local
            //3b. call API method to update localId

            //4. GET PlantTypes
            //4a. insert into local
            //4b. call API method to update localIds

            //5. GET Buckets
            //5a. insert into local
            //5b. call API method to update localId

            //6. GET Plants
            //6a. insert into local
            //6b. call API method to update localId

            //7. GET NotificationLogs
            //7a. insert into local
            //7b. call API method to update localId


        }
    }*/

    /**
     * If data has been wiped locally, and upon log in user does NOT want to restore data, wipe everything from remote linked to that user aside from User table
     */
    fun deleteRemoteData(user: UsersEntity){

    }

    /**
     * If validation on front end passes, this method is called to update the user in the local database, and it sets up a work request to do the same via API call once constraints are met (connected to wi-fi and phone is charging)
     */
    fun updateUser(user: UsersEntity)= runBlocking{
        viewModelScope.launch(Dispatchers.IO) {
            user.lastUpdateDateTime = getCurrentDateTime()
            //update local db
            repository.updateUser(user)
        }

        // Create constraints for work request
        val constraints = Constraints.Builder()
            .setRequiresBatteryNotLow(true) //to save battery life
            .setRequiredNetworkType(NetworkType.UNMETERED) //will only run when connected to Wi-Fi
            .build()

        //create input data (to pass id of db row)
        val inputData = Data.Builder()
            .putLong("USER_ID", user.userId)
            .build()

        //create work request
        val updateUserInRemote = OneTimeWorkRequest.Builder(UpdateUserInRemote::class.java)
            .setConstraints(constraints)
            .setInputData(inputData)
            .build()

        workManager.enqueue(updateUserInRemote)
    }
}