package project.softsquad.vegitable.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import project.softsquad.vegitable.entity.PlantsEntity

@Dao
interface PlantsDao {
    @Query("SELECT * FROM Plants")
    fun getAll(): LiveData<List<PlantsEntity>>

    @Query("SELECT * FROM Plants WHERE plantId = :plantId AND archiveDateTime IS NULL")
    fun getTestPlant(plantId: Long) : LiveData<PlantsEntity>

    //@Query("SELECT * FROM Plants WHERE userId_fk = :userId AND archiveDateTime IS NOT NULL")
    //fun getArchivedPlants(userId: Long) : LiveData<List<PlantsEntity>>

    @Query("SELECT * FROM Plants WHERE archiveDateTime IS NOT NULL")
    fun getArchivedPlants() : LiveData<List<PlantsEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(plant: PlantsEntity): Long

    //update only relevant datetime fields for plant's row when archiving
    @Query("UPDATE Plants SET archiveDateTime = :currentTime, lastUpdateDateTime = :currentTime WHERE plantId = :id")
    fun archive(id: Long, currentTime: String)

    //update all fields that are relevant in the UI to update plant
    @Query("UPDATE Plants SET plantType = :type, plantName =:name, temperatureMin = :tempMin, temperatureMax = :tempMax, phMin = :phMin, phMax = :phMax, ppmMin = :ppmMin, ppmMax = :ppmMax, lightMin = :lightMin, lightMax = :lightMax, humidityMin = :humidMin, humidityMax = :humidMax, plantPhase = :phase, lastUpdateDateTime = :updateTime, imageURL = :image, bucketId_fk = :bucket WHERE plantId = :plantId")
    fun update(
        plantId: Long,
        type: String,
        name: String,
        tempMin: Double,
        tempMax: Double,
        phMin: Double,
        phMax: Double,
        ppmMin: Double,
        ppmMax: Double,
        lightMin: Double,
        lightMax: Double,
        humidMin: Double,
        humidMax: Double,
        phase: String,
        updateTime: String,
        image: String,
        bucket: Int
    )

    @Query("SELECT * FROM Plants WHERE plantId = :plantId")
    fun getPlant(plantId: Long): PlantsEntity

    @Query("SELECT * FROM Plants WHERE bucketId_fk = :bucketId  AND archiveDateTime IS NULL")
    fun getPlants(bucketId: Int): LiveData<List<PlantsEntity>>

    @Query("SELECT * FROM Plants WHERE bucketId_fk = :bucketId AND archiveDateTime IS NULL")
    fun getLivePlantsFromBucketId(bucketId: Int): LiveData<List<PlantsEntity>>

    @Query("SELECT * FROM Plants WHERE bucketId_fk = :bucketId AND archiveDateTime IS NULL")
    fun getPlantsFromBucketId(bucketId: Int): List<PlantsEntity>
}