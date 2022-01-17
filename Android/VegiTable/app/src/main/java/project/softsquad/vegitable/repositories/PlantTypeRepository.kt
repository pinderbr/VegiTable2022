package project.softsquad.vegitable.repositories

import androidx.lifecycle.LiveData
import project.softsquad.vegitable.dao.PlantTypeDao
import project.softsquad.vegitable.entity.PlantTypeEntity

/**
 * Author: Martha Czerwik
 * Description:
 */

class PlantTypeRepository(private val plantTypeDao: PlantTypeDao) {
    val readAllData: LiveData<List<PlantTypeEntity>> = plantTypeDao.getAll()

    fun addPlantType(plantType: PlantTypeEntity): Long {
        return plantTypeDao.insert(plantType)
    }

    fun getPlantTypeByName(plantName: String) : LiveData<PlantTypeEntity> {
        return plantTypeDao.getPlantTypeByName(plantName)
    }

}