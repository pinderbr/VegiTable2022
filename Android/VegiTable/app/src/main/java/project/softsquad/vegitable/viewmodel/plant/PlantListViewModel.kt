package project.softsquad.vegitable.viewmodel.plant

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import project.softsquad.vegitable.VegiTableDatabase
import project.softsquad.vegitable.entity.BucketEntity
import project.softsquad.vegitable.entity.PlantsEntity
import project.softsquad.vegitable.getCurrentDateTime

class PlantListViewModel(application: Application): AndroidViewModel(application) {

    val plantDao = VegiTableDatabase.getInstance(application).PlantsDao()
    val bucketDao = VegiTableDatabase.getInstance(application).BucketDao()

    fun getPlantList(bucketId: Int): LiveData<List<PlantsEntity>>{
        return plantDao.getPlants(bucketId)
    }

    fun getBucket(bucketId: Long): LiveData<BucketEntity>{
        return bucketDao.getLiveBucket(bucketId)
    }

    /*
    fun archivePlant(plantId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            plantDao.archive(plantId, getCurrentDateTime())
        }
    }
    */
}