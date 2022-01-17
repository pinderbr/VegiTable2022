package project.softsquad.vegitable

import project.softsquad.vegitable.entity.*
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

/**
 * Author: Brianna McBurney
 * Description: Interface which holds all API routes
 */
interface ApiInterface {

    //region Device calls
    @GET("devices/{Id}")
    fun getDevices(@Path("Id") userId: Int) : Call<List<DeviceEntity>>

    @GET("device/{Id}")
    fun getDeviceById(@Path("Id") deviceId: Int) : Call<DeviceEntity>

    @PUT("device") // device?device=id&user=id
    fun updateDevice(@Query("device") deviceId: Int, @Query("user") userId: Int) : Call<DeviceEntity>

    @FormUrlEncoded
    @PUT("user/{userId}/device/{deviceId}?archive=true")
    fun removeDevice(@Path("userId") userId: Int, @Path("deviceId") deviceId: Int, @FieldMap params: HashMap<String?, String?>) : Call<DeviceEntity>

    @FormUrlEncoded
    @PUT("user/{userId}/device/{deviceId}?archive=false")
    fun updateDeviceName(@Path("userId") userId: Int, @Path("deviceId") deviceId: Int, @FieldMap params: HashMap<String?, String?>) : Call<DeviceEntity>
    //endregion

    //region User calls
    @POST("user")
    fun addUser(@Body user: UsersEntity): Call<UsersEntity>

    @FormUrlEncoded
    @PUT("user/{Id}")
    fun updateUser(@Path("Id") userId: Int, @FieldMap params: HashMap<String?, String?>): Call<UsersEntity>

    @GET("user/login")
    fun getUser(@Query("email") email: String, @Query("password") password: String): Call<UsersEntity>

    @GET("existingUser")
    fun getUserByEmail(@Query("email") email: String): Call<Int>

    @PUT("user")
    fun updateUserLocalId(@Query("remoteId") remoteId: Int, @Query("localId") localId: Long): Call<UsersEntity>



    //endregion

    //region Notification Settings calls
    @GET("settings/{Id}")
    fun getNotificationSettings(@Path("Id") userId: Long) : Call<NotificationSettingsEntity>

    @FormUrlEncoded
    @PUT("settings/{Id}")
    fun updateNotificationSettings(@Path("Id") userId: Int, @FieldMap params: HashMap<String?, Any?>) : Call<NotificationSettingsEntity>

    @POST("settings")
    fun createNotificationSettings(@Body settings: NotificationSettingsEntity) : Call<NotificationSettingsEntity>
    //endregion

    //region notification log calls
    @POST("log")
    fun createNotificationLog(@Body log: NotificationLogEntity) : Call<NotificationLogEntity>

    @GET("logs/{Id}/{type}")
    fun getNotificationLogs(@Path("Id") userId: Long, @Path("type") type: String) : Call<List<NotificationLogEntity>>

    //endregion

    //region plant related requests
    @POST("plant")
    fun addPlant(@Body plant: PlantsEntity): Call<PlantsEntity>

    @GET("user/{userId}/bucket/{bucketId}/plants")
    fun getPlants(@Path("userId") userId: Int, @Path("bucketId") bucketId: Int) : Call<List<PlantsEntity>>

    @GET("user/{userId}/plant/{plantId}")
    fun getPlant(@Path("userId") userId: Int, @Path("plantId") plantId: Int) : Call<PlantsEntity>

    @FormUrlEncoded
    @PUT("user/{userId}/plant/{plantId}")
    fun updatePlant(@Path("userId") userId: Int, @Path("plantId") plantId: Int, @FieldMap params: HashMap<String?, Any?>) : Call<PlantsEntity>

    @FormUrlEncoded
    @PUT("delete/user/{userId}/plant/{plantId}")
    fun archivePlant(@Path("userId") userId: Int, @Path("plantId") plantId: Int, @FieldMap params: HashMap<String?, Any?>) : Call<PlantsEntity>

    //endregion

    //region bucket related requests

    @GET("user/{id}/buckets")
    fun getBuckets(@Path("id") userId: Int) : Call<List<BucketEntity>>

    @GET("user/{userId}/bucket/{bucketId}")
    fun getBucket(@Path("userId") userId: Int, @Path("bucketId") bucketId: Int) : Call<BucketEntity>

    @FormUrlEncoded
    @PUT("user/{userId}/bucket/{bucketId}")
    fun updateBucket(@Path("userId") userId: Int, @Path("bucketId") bucketId: Int, @FieldMap params: HashMap<String?, String?>) : Call<BucketEntity>

    @POST("bucket")
    fun addBucket(@Body bucket: BucketEntity): Call<BucketEntity>

    @FormUrlEncoded
    @PUT("delete/user/{userId}/bucket/{bucketId}")
    fun archiveBucket(@Path("userId") userId: Int, @Path("bucketId") bucketId: Int, @FieldMap params: HashMap<String?, String?>): Call<BucketEntity>

    //endregion

    //region plant type related requests
    @POST("plantType")
    fun addPlantType(@Body plant: PlantTypeEntity): Call<PlantTypeEntity>

    //to pull from our temporary "API" of plant types, available to all users
    @GET("plantTypes")
    fun getDefaultPlantTypes() : Call<List<PlantTypeEntity>>

    @GET("plantType")
    fun getDefaultPlantTypeByName(@Query("name") name: String) : Call<PlantTypeEntity>

    //a single user's list of plant types they created themselves
    @GET("user/{userId}/plantTypes")
    fun getPlantTypes(@Path("userId") userId: Int) : Call<List<PlantTypeEntity>>

    @GET("user/{userId}/plantType/{plantTypeId}")
    fun getPlantType(@Path("userId") userId: Int, @Path("plantTypeId") plantTypeId: Int) : Call<PlantTypeEntity>
    //endregion

    //region device reading related requests
    @GET("deviceReading/{id}")
    fun getCurrentReading(@Path("id") deviceId: Int) : Call<DeviceReadingsEntity>

    //get all historical values for a device between two dates
    @GET("deviceReading/historical/{id}")
    fun getHistoricalReading(@Path("id") deviceId: Int, @Query("startDate") startDate: String, @Query("endDate") endDate: String) : Call<List<DeviceReadingsEntity>>

    //get readings from remote that havent been synced to local yet (readings saved in remote since last reading in local)
    @GET("deviceReadings/{id}")
    fun getLatestReadings(@Path("id") deviceId: Int): Call<List<DeviceReadingsEntity>>

    //TODO: add API call for historical device readings
    @GET("deviceReading/historical/{deviceId}") // yyyy-mm-dd
    fun getHistoricalReadings(@Path("deviceId") deviceId: Int, @Query("startDate") startDate: String, @Query("endDate") endDate: String) : Call<List<DeviceReadingsEntity>>

    //endregion

    companion object {
        var BASE_URL = "http://vegitableapi-env.eba-pyj572qp.ca-central-1.elasticbeanstalk.com/api/"
        fun create() : ApiInterface {
            val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL)
                .build()
            return retrofit.create(ApiInterface::class.java)
        }
    }
}