package project.softsquad.vegitable.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import project.softsquad.vegitable.entity.PlantTypeEntity

@Dao
interface PlantTypeDao {
    @Query("SELECT * FROM PlantTypes")
    fun getAll(): LiveData<List<PlantTypeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(plantType: PlantTypeEntity): Long

    @Query("SELECT * FROM PlantTypes WHERE plantTypeId = :plantTypeId")
    fun getPlantType(plantTypeId: Long): PlantTypeEntity

    @Query("SELECT * FROM PlantTypes WHERE plantTypeName = :name")
    fun getPlantTypeByName(name: String): LiveData<PlantTypeEntity>

}