package project.softsquad.vegitable.ui.plant

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import project.softsquad.vegitable.R
import project.softsquad.vegitable.entity.BucketEntity
import project.softsquad.vegitable.entity.PlantsEntity
import project.softsquad.vegitable.repositories.PlantListCard
import project.softsquad.vegitable.recyclerview.ViewPlantListRecyclerView
import project.softsquad.vegitable.ui.bucket.BucketListFragmentDirections
import project.softsquad.vegitable.viewmodel.bucket.BucketViewModel
import project.softsquad.vegitable.viewmodel.plant.PlantListViewModel
import project.softsquad.vegitable.viewmodel.plant.PlantViewModel
import java.util.*

/**
 * Author: Martha Czerwik
 * Modified By: Brianna McBurney
 *
 * Description:
 * Code for the fragment displaying the list of plants (within a bucket - when the list of buckets is displayed, clicking on one bucket leads you to this list view)
 * Each plant will be displayed in a card (recycle view - card view)
 * You can edit a plant in its respective card, copy it to create a duplicate card, or archive it (safe delete - you remove it from your current list, but you can view archived plants in a separate fragment)
 * All values are collected from a call to the app server's API, or if the data has not changed since the last call, it will be taken from local storage
 */
//private lateinit var binding: FragmentViewPlantListBinding

class ViewPlantListFragment : Fragment(), ViewPlantListRecyclerView.EditPlantDialogListener,
    ViewPlantListRecyclerView.ArchivePlantDialogListener {

    private lateinit var recyclerAdapter: ViewPlantListRecyclerView
    private val viewModel by lazy { ViewModelProvider(this).get(PlantListViewModel::class.java) }
    private val plantViewModel by lazy { ViewModelProvider(this).get(PlantViewModel::class.java) }
    private val bucketViewModel by lazy { ViewModelProvider(this).get(BucketViewModel::class.java) }

    private val navigationArgs: ViewPlantListFragmentArgs by navArgs()

    val tempList: List<PlantsEntity> = listOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_view_plant_list, container, false)

        //create temp list
        //val list = createTempList(4)
        //get access to adapter and pass in the list of plants

        val recycler: RecyclerView = view.findViewById(R.id.viewPlantListRecycler)

        recyclerAdapter = ViewPlantListRecyclerView(tempList, this) {
            val action =
                ViewPlantListFragmentDirections.actionViewPlantListFragmentToUpdatePlantFragment(
                    it.plantId,
                    it.bucketId_fk.toLong(),
                    it.userId_fk.toLong()
                )
            findNavController().navigate(action)
        }

        recycler.adapter = recyclerAdapter

        viewModel.getPlantList(navigationArgs.bucketId.toInt())
            .observe(
                viewLifecycleOwner, { recyclerAdapter.setData(it) }
            )
        //get access to adapter and pass in the lsit of plants
        //binding.viewPlantListRecycler.adapter = ViewPlantListRecyclerView(list)

        //add scrolling
        //binding.viewPlantListRecycler.layoutManager = LinearLayoutManager(context)
        recycler.layoutManager = LinearLayoutManager(context)

        //wrap elements
        //binding.viewPlantListRecycler.setHasFixedSize(true)
        recycler.setHasFixedSize(true)

        //new plant button
        val newPlantBtn: Button = view.findViewById(R.id.addPlantBtn2)
        newPlantBtn.setOnClickListener {
            //findNavController().navigate(R.id.action_viewPlantListFragment_to_addPlantFragment)
            val action =
                ViewPlantListFragmentDirections.actionViewPlantListFragmentToAddPlantFragment(
                    navigationArgs.bucketId,
                    navigationArgs.userId
                )
            findNavController().navigate(action)
        }


        val bucketDataBtn: Button = view.findViewById(R.id.viewBucketDataBtn)
        bucketDataBtn.setOnClickListener() {
            //TODO: THIS IS JUST FOR DEMO PURPOSES/TEMPORARY SOLUTION FOR NOTIFICATIONS
            val action =
                ViewPlantListFragmentDirections.actionViewPlantListFragmentToBucketDataFragment(
                    navigationArgs.bucketId
                )
            findNavController().navigate(action)
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val id = navigationArgs.bucketId

    }

    //TODO: this is just for testing purposes - will need to populate list of plants from our db via API call
    private fun createTempList(size: Int): List<PlantListCard> {
        val list = ArrayList<PlantListCard>()

        for (i in 0 until size) {
            var image = R.drawable.vector_grass
            var name = "Plant Name - Plant Type"
            var phase = "Phase: ####"
            var ph = "pH: ## - ##"
            var ppm = "PPM: ## - ##"
            var temp = "Temperature: ## - ##"
            var light = "Light: ## - ##"
            var humidity = "Humidity: ## - ##"
            var water = "Water: ## - ##"

            val item = PlantListCard(image, name, phase, ph, ppm, temp, light, humidity, water)
            list += item
        }
        return list
    }

    override fun onEditDialogPositiveClick(
        holder: ViewPlantListRecyclerView.PlantViewHolder,
        currentPlant: PlantsEntity
    ) {
        val action =
            ViewPlantListFragmentDirections.actionViewPlantListFragmentToUpdatePlantFragment(
                currentPlant.plantId,
                currentPlant.bucketId_fk.toLong(),
                currentPlant.userId_fk.toLong()
            )
        findNavController().navigate(action)
    }

    override fun onArchiveDialogPositiveClick(
        holder: ViewPlantListRecyclerView.PlantViewHolder,
        currentPlant: PlantsEntity
    ) {
        val builder = AlertDialog.Builder(activity)
        builder.setTitle("Archive Plant")
            .setMessage("Are you sure you want to archive ${currentPlant.plantName}?")
            .setPositiveButton(
                "Yes",
                DialogInterface.OnClickListener { dialog, id ->
                    plantViewModel.archivePlant(
                        currentPlant,
                        navigationArgs.bucketId
                    )
                })
            .setNegativeButton(
                "Cancel",
                DialogInterface.OnClickListener { dialog, id -> dialog.cancel() })
        builder.create()
        builder.show()
    }


}