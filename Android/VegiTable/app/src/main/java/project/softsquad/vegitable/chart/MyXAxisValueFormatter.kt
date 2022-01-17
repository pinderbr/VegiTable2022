package project.softsquad.vegitable.chart

import android.os.Build
import androidx.annotation.RequiresApi
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.formatter.ValueFormatter
import java.time.LocalDateTime
import java.time.ZoneOffset

/**
 * Author: Brianna McBurney
 * Description:
 */
class MyXAxisValueFormatter : ValueFormatter() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun getAxisLabel(value: Float, axis: AxisBase?): String {
        val date = LocalDateTime.ofEpochSecond(value.toLong(), 0, ZoneOffset.UTC).toString()
        val dateParts = date.split("T")
        return dateParts[0]
    }


}