package project.softsquad.vegitable.viewmodel.plant

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import project.softsquad.vegitable.VegiTableDatabase
import project.softsquad.vegitable.entity.PlantsEntity


/**
 * Author: Brianna McBurney
 * Description:
 */
class ArchivePlantListViewModel(application: Application): AndroidViewModel(application) {
    val plantDao = VegiTableDatabase.getInstance(application).PlantsDao()
    val userDao = VegiTableDatabase.getInstance(application).UsersDao()

    fun getPlantList(): LiveData<List<PlantsEntity>> {
        return plantDao.getArchivedPlants()
    }
    fun addPlant(plantEntity: PlantsEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            val id = plantDao.insert(plantEntity)
            Log.i("COPY_PLANT", id.toString())
        }
    }
}