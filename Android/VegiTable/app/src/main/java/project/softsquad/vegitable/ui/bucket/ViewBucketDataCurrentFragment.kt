package project.softsquad.vegitable.ui.bucket

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import project.softsquad.vegitable.ApiInterface
import project.softsquad.vegitable.alert
import project.softsquad.vegitable.databinding.FragmentViewBucketDataCurrentBinding
import project.softsquad.vegitable.entity.BucketEntity
import project.softsquad.vegitable.entity.DeviceReadingsEntity
import project.softsquad.vegitable.viewmodel.bucket.BucketDataViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Author: Jason Beattie
 * Description:
 */
class ViewBucketDataCurrentFragment(var bucketId: Long) : Fragment() {

    private lateinit var binding: FragmentViewBucketDataCurrentBinding
    lateinit var apiInterface: ApiInterface

    private val viewModel by lazy {
        ViewModelProvider(this).get(BucketDataViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        apiInterface = ApiInterface.create()
        // Inflate the layout for this fragment
        binding = FragmentViewBucketDataCurrentBinding.inflate(inflater, container, false)

        var view = binding.root
        viewModel.setCurrentBucketId(bucketId)

        viewModel.currentBucketId.observe(viewLifecycleOwner,  { bucket_id ->
            viewModel.getBucket(bucket_id).observe(viewLifecycleOwner, { bucket ->
                viewModel.setCurrentBucket(bucket)
                binding.vbBucketNameTv.text = bucket.bucketName
                apiInterface.getCurrentReading(bucket.deviceId_fk)
                    .enqueue(object : Callback<DeviceReadingsEntity> {
                        override fun onResponse(
                            call: Call<DeviceReadingsEntity>?,
                            response: Response<DeviceReadingsEntity>?
                        ) {
                            if (response?.body() != null) {
                                var reading = response.body() as DeviceReadingsEntity
                                if (reading != null) {
                                    setData(bucket, reading)
                                } else {
                                    alert(activity, "NO DATA","No sensor readings were found")
                                }
                            } else {
                                if (response != null) {
                                    Log.i(
                                        "SETTINGS  ERROR",
                                        "Something went wrong! " + response.errorBody()
                                    )
                                }
                            }
                        }

                        override fun onFailure(call: Call<DeviceReadingsEntity>?, t: Throwable?) {
                            Log.i("SETTINGS ERROR", "Something else went wrong! " + t.toString())
                            alert(activity, "NO DATA","No sensor readings were found")
                        }
                    })
            })
        })

        return view
    }

    private fun getVal(value: Double?, max: Double, min:Double): Pair<Double?, Double?> {
        if(value != null) {
            if (value > max) {
                return Pair(max, value)
            } else if (value < min) {
                return Pair(min, value)
            } else {
                return Pair(value, value)
            }
        }
        return Pair(min, null)
    }


    fun setData(bucket: BucketEntity, deviceReading: DeviceReadingsEntity) {
        // display water level
        var waterStr = "N/A"
        var waterValue = deviceReading.waterValue
        if (waterValue != null) {
            if (waterValue <= 0.0 && waterValue <= 1500.0) {
                waterStr = "Danger (almost/ is empty)"
            } else if (waterValue > 1500.0 && waterValue <= 3000.0) {
                waterStr = "Warning (25% to 75% full)"
            } else if (waterValue > 3000.0){
                waterStr = "Good"
            }
        }
        binding.vbWaterVal.text = waterStr


        //using view binding to set values
        //PH values and slider
        binding.vbPhMin.text = bucket.phMin.toString()
        binding.vbPhSlider.valueFrom = bucket.phMin.toFloat()

        binding.vbPhMax.text = bucket.phMax.toString()
        binding.vbPhSlider.valueTo = bucket.phMax.toFloat()


        val pHvalue = getVal(deviceReading.phValue, bucket.phMax, bucket.phMin)
        binding.vbPhVal.text = pHvalue.second.toString()
        binding.vbPhSlider.value = pHvalue.first.toString().toFloat()

        //PPM values and slider
        binding.vbPpmMin.text = bucket.ppmMin.toString()
        binding.vbPpmSlider.valueFrom = bucket.ppmMin.toFloat()

        binding.vbPpmMax.text = bucket.ppmMax.toString()
        binding.vbPpmSlider.valueTo = bucket.ppmMax.toFloat()

        val ppmValue = getVal(deviceReading.ppmValue, bucket.ppmMax, bucket.ppmMin)
        binding.vbPpmVal.text = ppmValue.second.toString()
        binding.vbPpmSlider.value = ppmValue.first.toString().toFloat()

        //Lumen values and slider
        binding.vbLumenMin.text = bucket.lightMin.toString()
        binding.vbLumenSlider.valueFrom = bucket.lightMin.toFloat()

        binding.vbLumenMax.text = bucket.lightMax.toString()
        binding.vbLumenSlider.valueTo = bucket.lightMax.toFloat()

        val lightValue = getVal(deviceReading.lightValue, bucket.lightMax, bucket.lightMin)
        binding.vbLumenVal.text = lightValue.second.toString()
        binding.vbLumenSlider.value = lightValue.first.toString().toFloat()

        //Humidity values and slider
        binding.vbHumidityMin.text = bucket.humidityMin.toString() +"%"
        binding.vbHumiditySlider.valueFrom = bucket.humidityMin.toFloat()

        binding.vbHumidityMax.text = bucket.humidityMax.toString() +"%"
        binding.vbHumiditySlider.valueTo = bucket.humidityMax.toFloat()

        val humidityValue = getVal(deviceReading.humidityValue, bucket.humidityMax, bucket.humidityMin)
        binding.vbHumidityVal.text = humidityValue.second.toString()
        binding.vbHumiditySlider.value = humidityValue.first.toString().toFloat()

        //Temperature values and slider
        binding.vbTemperMin.text = bucket.temperatureMin.toString() + "°C"
        binding.vbTemperSlider.valueFrom = bucket.temperatureMin.toFloat()

        binding.vbTemperMax.text = bucket.temperatureMax.toString() + "°C"
        binding.vbTemperSlider.valueTo = bucket.temperatureMax.toFloat()

        val temperatureValue = getVal(deviceReading.temperatureValue, bucket.temperatureMax, bucket.temperatureMin)
        binding.vbTemperVal.text = temperatureValue.second.toString()
        binding.vbTemperSlider.value = temperatureValue.first.toString().toFloat()
    }
}
