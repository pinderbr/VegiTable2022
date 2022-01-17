package project.softsquad.vegitable.ui.bucket

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.google.android.material.datepicker.MaterialDatePicker
import project.softsquad.vegitable.*
import project.softsquad.vegitable.chart.MyXAxisValueFormatter
import project.softsquad.vegitable.databinding.FragmentViewBucketDataHistoricalBinding
import project.softsquad.vegitable.entity.DeviceReadingsEntity
import project.softsquad.vegitable.ui.plant.ViewPlantListFragmentDirections
import project.softsquad.vegitable.viewmodel.bucket.BucketDataViewModel
import project.softsquad.vegitable.viewmodel.bucket.BucketViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

/**
 * Author: Brianna McBurney
 * Description:
 */
@RequiresApi(Build.VERSION_CODES.O)
class ViewBucketDataHistoricalFragment(var bucketId: Long) : Fragment(), OnChartValueSelectedListener {

    private lateinit var binding: FragmentViewBucketDataHistoricalBinding
    lateinit var apiInterface: ApiInterface
    private var data: List<DeviceReadingsEntity> = listOf()
    private lateinit var chart: LineChart

    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    private var startDate: String = "2021-04-08" // LocalDateTime.now().minusDays(7).format(formatter)
    private var endDate: String = "2021-09-28" // LocalDateTime.now().format(formatter)
    private var sensorIndexSelected: Int = 0
    private lateinit var sensorList: Array<String>

    private val viewModel by lazy {
        ViewModelProvider(this).get(BucketDataViewModel::class.java)
    }
    private val bucketViewModel by lazy {
        ViewModelProvider(this).get(BucketViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentViewBucketDataHistoricalBinding.inflate(inflater, container, false)
        val view = binding.root

        viewModel.setCurrentBucketId(bucketId)
        apiInterface = ApiInterface.create()
        sensorList = resources.getStringArray(R.array.sensor_list)

        updateDate()

        // add options to drop down menu
        val adapter = ArrayAdapter(requireContext(), R.layout.textview_sensor_list_item, sensorList)
        binding.sensorToShowAuto.setAdapter(adapter)
        binding.sensorToShowAuto.setText(adapter.getItem(0).toString(), false)

        viewModel.setCurrentBucketId(bucketId)
        viewModel.currentBucketId.observe(viewLifecycleOwner,  { bucket_id ->
            viewModel.getBucket(bucket_id).observe(viewLifecycleOwner, { bucket ->
                viewModel.setCurrentBucket(bucket)
                binding.editDateRangeBtn.setOnClickListener { openDateRangePicker() }
                binding.sensorToShowAuto.setOnItemClickListener { parent, view, position, id ->
                    sensorIndexSelected = position
                    val selectedSensor = sensorList[position]
                    val title = "${bucket?.bucketName} - $selectedSensor"
                    binding.bucketSensorName.text = title
                    updateChart()
                }
            })
        })

        return view
    }

    private fun getReadings(startDate: String, endDate: String) {
        apiInterface.getHistoricalReadings(101, startDate, endDate) // TODO use dynamic deviceid
            .enqueue(object : Callback<List<DeviceReadingsEntity>> {
                override fun onResponse(
                    call: Call<List<DeviceReadingsEntity>>?,
                    response: Response<List<DeviceReadingsEntity>>?
                ) {
                    if (response?.body() != null) {
                        data = response?.body() as List<DeviceReadingsEntity>
                        updateChart()
                        Log.i("GOT READINGS", "Got historical device readings")
                    } else {
                        Log.i("Error", "Something went wrong please try again later.")
                    }
                }
                override fun onFailure(call: Call<List<DeviceReadingsEntity>>?, t: Throwable?) {
                    Log.i("Profile Not Found","We couldn't find your profile on the remote DB. Please try again!")
                }
            })
    }

    private fun openDateRangePicker() {
        val dateRangePicker = MaterialDatePicker.Builder.dateRangePicker().setTitleText("Select dates").build()
        dateRangePicker.addOnPositiveButtonClickListener {
            // Create a date format, then a date object with our offset
            // TODO both dates are a day behind
            val simpleFormat = SimpleDateFormat("yyyy-MM-dd", Locale.CANADA)

            startDate = simpleFormat.format(Date(it.first))
            endDate = simpleFormat.format(Date(it.second))
            Log.i("start date", startDate.toString())
            Log.i("end date", endDate.toString())
            updateDate()
        }
        dateRangePicker.show(childFragmentManager, "DateRangeSelector")
    }

    private fun updateDate() {
        val newDate = "$startDate to $endDate"
        binding.dateRangeText.setText(newDate)
        getReadings(startDate, endDate)
    }

    private fun updateChart() {
        if (this::chart.isInitialized) {
            chart.data.removeDataSet(0)
        }

        generateChart()
        chart.notifyDataSetChanged()
        chart.invalidate()
        chart.setOnClickListener {
            bucketViewModel.getBucket(bucketId).observe(viewLifecycleOwner, { bucket ->
                context?.let { it1 -> bucketViewModel.showNotification(it1, bucket) }
            })
        }
    }

    // TODO remove/fix bc its not working
    override fun onValueSelected(e: Entry?, h: Highlight?) {
        val date = e?.x?.toLong()?.let { LocalDateTime.ofEpochSecond(it, 0, ZoneOffset.UTC).toString() }
        val dateParts = date?.split("T")
        val dateStr = dateParts?.get(0)
       alert(activity, "Data Selected", "Date: " + dateStr + ", Value: " + e?.y)
    }

    override fun onNothingSelected() {    }

    // TODO figure out why this is called twice
    private fun getDataSet(): ArrayList<Entry> {
        val entries: ArrayList<Entry> = ArrayList()
        if(data.isEmpty()) {
            alert(activity, "NO DATA","No sensor readings were found between $startDate and $endDate" )
        } else {
            val currentBucket = viewModel.currentBucket.value
            for (dataPoint in data) {
                var value: Double? = 0.0
                var min = 0.0
                var max = 100000.0
                when (sensorIndexSelected) {
                    0 -> {
                        value = dataPoint.ppmValue
                        if (currentBucket != null) {
                            min = currentBucket.ppmMin
                            max = currentBucket.ppmMax
                        }
                    }
                    1 -> {
                        value = dataPoint.phValue
                        if (currentBucket != null) {
                            min = currentBucket.phMin
                            max = currentBucket.phMax
                        }
                    }
                    2 -> {
                        value = dataPoint.temperatureValue
                        if (currentBucket != null) {
                            min = currentBucket.temperatureMin
                            max = currentBucket.temperatureMax
                        }
                    }
                    3 -> {
                        value = dataPoint.humidityValue
                        if (currentBucket != null) {
                            min = currentBucket.humidityMin
                            max = currentBucket.humidityMax
                        }
                    }
                    4 -> {
                        value = dataPoint.lightValue
                        if (currentBucket != null) {
                            min = currentBucket.lightMin
                            max = currentBucket.lightMax
                        }
                    }
                }
                if (value != null) {
                    var dateTimeFloat =
                        getDateInMilliSeconds(stringToLocalDateTime(dataPoint.currentDateTimeStr))
                    entries.add(Entry(dateTimeFloat, doubleToFloat(value)))
                }

                // TODO update min/max with bucket min/max
                chart.axisLeft.removeAllLimitLines()
                chart.axisLeft.addLimitLine(
                    createLimitLine(
                        "Max -> $max",
                        max.toFloat(),
                        LimitLine.LimitLabelPosition.LEFT_BOTTOM
                    )
                )
                chart.axisLeft.addLimitLine(
                    createLimitLine(
                        "Min -> $min",
                        min.toFloat(),
                        LimitLine.LimitLabelPosition.LEFT_TOP
                    )
                )
            }
        }
        return entries
    }

    // https://github.com/PhilJay/MPAndroidChart
    // https://weeklycoding.com/mpandroidchart-documentation/getting-started/
    // https://medium.com/@makkenasrinivasarao1/line-chart-implementation-with-mpandroidchart-af3dd11804a7
    private fun generateChart() {
        chart = binding.lineChart
        styleChartAxis()

        var dataSet = LineDataSet(getDataSet(), sensorList[sensorIndexSelected])
        dataSet = styleData(dataSet)

        val lineData = LineData(dataSet)
        chart.data = lineData
        chart.invalidate() // refresh
    }

    private fun createLimitLine(label: String, value: Float, position: LimitLine.LimitLabelPosition): LimitLine {
        var ll: LimitLine = LimitLine(value, label)
        ll.lineWidth = 4f
        ll.enableDashedLine(10f, 10f, 0f)
        ll.labelPosition = position
        ll.textSize = 15f
        return ll
    }

    private fun styleChartAxis() {
        chart.description.isEnabled = false;

        // style X axis
        chart.xAxis.valueFormatter = MyXAxisValueFormatter()
        chart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        chart.xAxis.labelRotationAngle = 315f
        chart.xAxis.gridLineWidth = 0.5f

        // style Y axis
        chart.axisLeft.setDrawLimitLinesBehindData(true)
        chart.axisLeft.textSize = 15f
        chart.axisLeft.gridLineWidth = 0.5f

        chart.axisRight.isEnabled = false // hide side second y axis on right side
    }

    // TODO? BELOW -> change circle color based on the value
    // https://github.com/PhilJay/MPAndroidChart/issues/142
    private fun styleData(dataSet:LineDataSet) : LineDataSet {
        // style circles
        dataSet.setCircleColor(Color.CYAN)
        dataSet.circleHoleColor = Color.CYAN
        dataSet.circleRadius = 10f

        // style line
        dataSet.color = Color.BLACK
        dataSet.lineWidth = 1f

        // style data labels
        dataSet.valueTextSize = 15f

        return dataSet
    }

    private fun doubleToFloat(value: Double?): Float {
        return value?.toFloat() ?: 0f
    }

    private fun stringToLocalDateTime(date: String): LocalDateTime {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        return LocalDateTime.parse(date, formatter)
    }

    private fun getDateInMilliSeconds(dateLogged: LocalDateTime ): Float {
        return dateLogged.toEpochSecond(ZoneOffset.UTC).toFloat()
    }
}