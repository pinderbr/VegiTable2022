package project.softsquad.vegitable

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import androidx.fragment.app.FragmentActivity
import java.util.*
import java.text.SimpleDateFormat
import project.softsquad.vegitable.entity.*

fun getCurrentDateTime(): String {
    //get current time so we can update lastupdatedatetime/createdatetime/archivedatetime to now
    val date = Calendar.getInstance().time
    return date.toString("yyyy-MM-dd HH:mm:ss")
}

fun Date.toString(format: String, locale: Locale = Locale.getDefault()): String {
    val formatter = SimpleDateFormat(format, locale)
    return formatter.format(this)
}

fun alert(activity: FragmentActivity?, title:String, message: String): Dialog {
    return activity?.let {
        val builder = AlertDialog.Builder(it)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton("Ok", DialogInterface.OnClickListener { dialog, id -> dialog.cancel() })
        builder.create()
        builder.show()
    } ?: throw IllegalStateException("Activity cannot be null")
}

/**
 * Method to check if the plant being added/updated willworkwith the bucket's threshold
 */
fun checkThresholds(bucket: BucketEntity, numPlants: Int, plant: PlantsEntity) : Boolean {
    if (numPlants == 0){
        //return true if bucket has no plants - no errors can be caused
        return true
    }
    //return false if any min/max values will conflict
    return !(plant.phMin >= bucket.phMax ||
            plant.phMax <= bucket.phMin ||
            plant.ppmMin >= bucket.ppmMax ||
            plant.ppmMax <= bucket.ppmMin ||
            plant.temperatureMin >= bucket.temperatureMax ||
            plant.temperatureMax <= bucket.temperatureMin ||
            plant.lightMin >= bucket.lightMax ||
            plant.lightMax <= bucket.lightMin ||
            plant.humidityMin >= bucket.humidityMax ||
            plant.humidityMax <= bucket.humidityMin)

    //otherwise return true, no conflicts
    return true
}