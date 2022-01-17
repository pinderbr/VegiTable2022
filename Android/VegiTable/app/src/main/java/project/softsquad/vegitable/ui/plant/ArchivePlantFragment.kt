package project.softsquad.vegitable.ui.plant

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import project.softsquad.vegitable.R
import project.softsquad.vegitable.alert
import project.softsquad.vegitable.checkThresholds
import project.softsquad.vegitable.entity.PlantsEntity
import project.softsquad.vegitable.getCurrentDateTime
import project.softsquad.vegitable.recyclerview.ArchivePlantListRecyclerView
import project.softsquad.vegitable.ui.dialog.CopyPlantDialogFragment
import project.softsquad.vegitable.viewmodel.bucket.BucketViewModel
import project.softsquad.vegitable.viewmodel.plant.ArchivePlantListViewModel
import project.softsquad.vegitable.viewmodel.plant.PlantViewModel

/**
 * Author: Brianna McBurney
 * Description:
 */
class ArchivePlantFragment : Fragment(), ArchivePlantListRecyclerView.CopyPlantButtonListener, CopyPlantDialogFragment.CopyPlantDialogListener {

    private lateinit var recyclerAdapter: ArchivePlantListRecyclerView
    private val archivedPlantList: List<PlantsEntity> = listOf()
    private val bucketList: MutableMap<String, Long> = mutableMapOf()
    private val viewModel by lazy { ViewModelProvider(this).get(ArchivePlantListViewModel::class.java)}
    private val bucketViewModel: BucketViewModel by activityViewModels()
    private val plantViewModel: PlantViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_archive_plant, container, false)
        val recycler: RecyclerView = view.findViewById(R.id.archivedPlantListRecycler)

        recyclerAdapter = ArchivePlantListRecyclerView(archivedPlantList, this)

        recycler.adapter = recyclerAdapter
        recycler.layoutManager = LinearLayoutManager(context)

        bucketViewModel.readAllData.observe(viewLifecycleOwner, { existingBucketList ->
            for (bucket in existingBucketList) {
                bucketList.put(bucket.bucketName, bucket.bucketId)
            }
        })

        viewModel.getPlantList().observe(viewLifecycleOwner, {
            if(it.isEmpty()) {
                alert(activity, "NO ARCHIVED PLANTS", "No archived plants were found.")
            }
            recyclerAdapter.setData(it)
        })

        return view
    }

    override fun onCopyPlantButtonClick(holder: ArchivePlantListRecyclerView.PlantViewHolder, currentArchivedPlant: PlantsEntity) {
        val copyPlantDialogFragment = CopyPlantDialogFragment(bucketList, currentArchivedPlant)
        copyPlantDialogFragment.show(childFragmentManager, "CopyArchivedPlant")
    }

    override fun onCopyPlantDialogPositiveClick(dialog: DialogFragment, bucketId: Int, newPlant: PlantsEntity) {
//        plantViewModel.setCurrentPlant(newPlant)
//        bucketViewModel.getBucket(bucketId.toLong()).observe(viewLifecycleOwner, Observer { bucket ->
//            plantViewModel.setCurrentBucket(bucket)
//            plantViewModel.getPlants(bucket.bucketId).observe(viewLifecycleOwner, Observer { plants ->
//                plantViewModel.setcurrentPlantList(plants)
//
//                if (plantViewModel.currentPlantList.value != null) {
//                    var count = plantViewModel.currentPlantList.value!!.count()
//                    if (count <= 5) {
//                        if (checkThresholds(bucket, count, newPlant)) {
                            newPlant.plantId = 0
                            newPlant.archiveDateTime = null
                            newPlant.createDateTime = getCurrentDateTime()
                            newPlant.lastUpdateDateTime = getCurrentDateTime()
                            newPlant.bucketId_fk = bucketId
                            viewModel.addPlant(newPlant)
                            alert(activity, "Plant Copied", "The plant has been added to the bucket")
//                        } else {
//                            alert(
//                                activity,
//                                "Error",
//                                "This plant will not thrive in this bucket. Please add it to another bucket."
//                            )
//                        }
//
//                    } else {
//                        alert(
//                            activity,
//                            "Error",
//                            "This bucket is already full. Please put this plant into a new bucket or remove any old plants."
//                        )
//                    }
//                }
//            })
//        })
    }
}