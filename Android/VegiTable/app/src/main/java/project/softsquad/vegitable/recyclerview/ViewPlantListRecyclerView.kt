package project.softsquad.vegitable.recyclerview

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import project.softsquad.vegitable.R
import project.softsquad.vegitable.entity.PlantsEntity
import project.softsquad.vegitable.ui.plant.ViewPlantListFragment
import java.lang.ClassCastException


/**
 * Author: Martha Czerwik
 * Modified By: Jason Beattie, Brianna McBurney
 * DESC: RecyclerView to determine what information will be displayed in the cardview on the page that displays the list of plants (active plants)
 */

class ViewPlantListRecyclerView(private var plantList: List<PlantsEntity>, var viewPlantListFragment: Fragment, private val onItemClicked: (PlantsEntity) -> Unit):
    RecyclerView.Adapter<ViewPlantListRecyclerView.PlantViewHolder>() {

    interface EditPlantDialogListener {
        fun onEditDialogPositiveClick(holder: PlantViewHolder, currentPlant: PlantsEntity)
    }
    interface ArchivePlantDialogListener {
        fun onArchiveDialogPositiveClick(holder: PlantViewHolder, currentPlant: PlantsEntity)
    }
    lateinit var editListener: EditPlantDialogListener
    lateinit var archiveListener: ArchivePlantDialogListener

    //holds values in the card view
    class PlantViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val plantImg: ImageView = itemView.findViewById(R.id.viewPlantImage)
        val plantNameType: TextView = itemView.findViewById(R.id.viewPlantNameAndType)
        val plantPhVal: TextView = itemView.findViewById(R.id.plantPhVal)
        val plantPPMVal: TextView = itemView.findViewById(R.id.plantPpmVal)
        val plantTempVal: TextView = itemView.findViewById(R.id.plantTempVal)
        val plantLightVal: TextView = itemView.findViewById(R.id.plantLightVal)
        val plantHumidityVal: TextView = itemView.findViewById(R.id.plantHumidityVal)
        val plantPhaseVal: TextView = itemView.findViewById(R.id.viewPlantDetailsPhase)
        val archiveBtn: ImageView = itemView.findViewById(R.id.archive_bucket_button)
        val editBtn: ImageView = itemView.findViewById(R.id.editPlantButtonOnPlantListView)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlantViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.plant_list_card, parent, false)

        try {
            editListener = viewPlantListFragment as ViewPlantListFragment
        } catch (e: ClassCastException) {
            throw ClassCastException("ERROR -> $viewPlantListFragment must implement EditPlantDialogListener")
        }
        try {
            archiveListener = viewPlantListFragment as ViewPlantListFragment
        } catch (e: ClassCastException) {
            throw ClassCastException("ERROR -> $viewPlantListFragment must implement ArchivePlantDialogListener")
        }

        return PlantViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: PlantViewHolder, position: Int) {
        //get current plant
        val currentPlant = plantList[position]

        //set values for each card
        holder.plantImg.setImageResource(R.drawable.vector_grass)
        holder.plantNameType.text = currentPlant.plantName + " - " + currentPlant.plantType
        holder.plantPhaseVal.text = currentPlant.plantPhase
        holder.plantPhVal.text = currentPlant.phMin.toString() + " - " +  currentPlant.phMax.toString()
        holder.plantPPMVal.text = currentPlant.ppmMin.toString() + " - " +  currentPlant.ppmMax.toString()
        holder.plantTempVal.text = currentPlant.temperatureMin.toString() + " - " +  currentPlant.temperatureMax.toString()
        holder.plantLightVal.text = currentPlant.lightMin.toString() + " - " +  currentPlant.lightMax.toString()
        holder.plantHumidityVal.text = currentPlant.humidityMin.toString() + " - " +  currentPlant.humidityMax.toString()


        holder.itemView.setOnClickListener{
            onItemClicked(currentPlant)
        }
        holder.editBtn.setOnClickListener {
            editListener.onEditDialogPositiveClick(holder, currentPlant)
        }
        holder.archiveBtn.setOnClickListener {
            archiveListener.onArchiveDialogPositiveClick(holder, currentPlant)
        }

        //when user taps on the plant - navigate to view plant data
        /*holder.itemView.setOnClickListener { v ->

            //TODO("add functionality to navigate to plant data page")
            val fragment = v?.context as AppCompatActivity
            val fragment2 = UpdatePlantFragment()
            //findNavController(fragment).navigate(R.id.action_viewPlantListFragment_to_updatePlantFragment)
            Toast.makeText(v.context, "Testing",
                Toast.LENGTH_SHORT
            ).show()

            fragment.supportFragmentManager.beginTransaction()
                .replace(R.id.viewPlantListLayout, fragment2).addToBackStack(null).commit()
        }*/

    }

    fun setData(plantListData: List<PlantsEntity>){
        plantList = plantListData
        notifyDataSetChanged()
    }

    //get total number of plant cards to display
    override fun getItemCount() = plantList.size

}
