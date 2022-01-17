package project.softsquad.vegitable.repositories

import androidx.lifecycle.LiveData
import project.softsquad.vegitable.dao.PlantsDao
import project.softsquad.vegitable.entity.PlantsEntity

/**
 * Author: Jason Beattie
 * Modified By: Martha Czerwik
 * Description:
 */
class PlantRepository(private val plantsDao: PlantsDao) {
    val readAllData: LiveData<List<PlantsEntity>> = plantsDao.getAll()

    fun getTestPlant(plantId: Long): LiveData<PlantsEntity> {
        return plantsDao.getTestPlant(plantId)
    }

    fun getPlant(plantId: Long): PlantsEntity {
        return plantsDao.getPlant(plantId)
    }

    fun getLivePlants(bucketId: Int) : LiveData<List<PlantsEntity>>{
        return plantsDao.getLivePlantsFromBucketId(bucketId)
    }

    fun getPlants(bucketId: Int): List<PlantsEntity>{
        return plantsDao.getPlantsFromBucketId(bucketId)
    }

    fun addPlant(plant: PlantsEntity): Long {
        return plantsDao.insert(plant)
    }

    fun archivePlant(plant: PlantsEntity, currentTime: String) {
        return plantsDao.archive(plant.plantId, currentTime)
    }

    fun updatePlant(plant: PlantsEntity, updateTime: String) {
        plantsDao.update(
            plant.plantId,
            plant.plantType,
            plant.plantName,
            plant.temperatureMin,
            plant.temperatureMax,
            plant.phMin,
            plant.phMax,
            plant.ppmMin,
            plant.ppmMax,
            plant.lightMin,
            plant.lightMax,
            plant.humidityMin,
            plant.humidityMax,
            plant.plantPhase,
            updateTime,
            plant.imageURL,
            plant.bucketId_fk
        )
    }
}