package project.softsquad.vegitable.recyclerview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import project.softsquad.vegitable.R
import project.softsquad.vegitable.entity.PlantsEntity
import java.lang.ClassCastException

/**
 * Author: Martha Czerwik
 * Modified By: Brianna McBurney
 * DESC: RecyclerView to determine what information will be displayed in the cardview on the page that displays the list of archived plants
 */

class ArchivePlantListRecyclerView(private var archivedPlantList: List <PlantsEntity>, var archivedPlantFragment: Fragment,
                                   ): RecyclerView.Adapter<ArchivePlantListRecyclerView.PlantViewHolder>() {

    interface CopyPlantButtonListener {
        fun onCopyPlantButtonClick(holder: PlantViewHolder, currentArchivedPlant: PlantsEntity)
    }
    lateinit var copyPlantListener: CopyPlantButtonListener

    //holds values in the card view
    class PlantViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val plantImg: ImageView = itemView.findViewById(R.id.archivedPlantImage)
        val plantNameType: TextView = itemView.findViewById(R.id.archivedPlantNameAndType)
        val plantPhVal: TextView = itemView.findViewById(R.id.archivedPlantPH)
        val plantPPMVal: TextView = itemView.findViewById(R.id.archivedPlantPPM)
        val plantTempVal: TextView = itemView.findViewById(R.id.archivedPlantTemp)
        val plantLightVal: TextView = itemView.findViewById(R.id.archivedPlantLight)
        val plantHumidityVal: TextView = itemView.findViewById(R.id.archivedPlantHumidity)
        val plantArchiveDateVal: TextView = itemView.findViewById(R.id.archiveDate)

        val copyBtn: ImageView = itemView.findViewById(R.id.archivedCopyPlantImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlantViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.archived_plant_list_card, parent, false)

        try {
            copyPlantListener = archivedPlantFragment as CopyPlantButtonListener
        } catch (e: ClassCastException) {
            throw ClassCastException("ERROR -> $archivedPlantFragment must implement CopyPlantButtonListener")
        }

        return PlantViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: PlantViewHolder, position: Int) {
        //get current plant
        val currentPlant = archivedPlantList[position]

        //set values for each card
        holder.plantImg.setImageResource(R.drawable.vector_grass)
        holder.plantNameType.text = currentPlant.plantName + " - " + currentPlant.plantType
        holder.plantArchiveDateVal.text = currentPlant.archiveDateTime
        holder.plantPhVal.text = currentPlant.phMin.toString() + " - " + currentPlant.phMax.toString()
        holder.plantPPMVal.text = currentPlant.ppmMin.toString() + " - " + currentPlant.ppmMax.toString()
        holder.plantTempVal.text = currentPlant.temperatureMin.toString() + " - " + currentPlant.temperatureMax.toString()
        holder.plantLightVal.text = currentPlant.lightMin.toString() + " - " + currentPlant.lightMax.toString()
        holder.plantHumidityVal.text = currentPlant.humidityMin.toString() + " - " + currentPlant.humidityMin.toString()

        holder.copyBtn.setOnClickListener {
            copyPlantListener.onCopyPlantButtonClick(holder, currentPlant)
        }
    }

    //get total number of plant cards to display
    override fun getItemCount() = archivedPlantList.size

    fun setData(plantListData: List<PlantsEntity>){
        archivedPlantList = plantListData
        notifyDataSetChanged()
    }
}