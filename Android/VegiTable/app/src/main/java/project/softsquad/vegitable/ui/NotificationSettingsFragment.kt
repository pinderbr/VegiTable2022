package project.softsquad.vegitable.ui

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import project.softsquad.vegitable.databinding.FragmentNotificationSettingsBinding
import project.softsquad.vegitable.viewmodel.NotificationSettingsViewModel
import project.softsquad.vegitable.viewmodel.UserViewModel

/**
 * Author: Brianna McBurney
 * Description:
 */

@RequiresApi(Build.VERSION_CODES.O)
class NotificationSettingsFragment : Fragment() {
    private lateinit var binding:FragmentNotificationSettingsBinding

    private val viewModel by lazy {
        ViewModelProvider(this).get(NotificationSettingsViewModel::class.java)
    }
    private val userViewModel by lazy {
        ViewModelProvider(this).get(UserViewModel::class.java)
    }

    private var hour: Int = 8
    private var minute: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View? {
        binding = FragmentNotificationSettingsBinding.inflate(inflater, container, false)
        var view = binding.root

        userViewModel.readAllData.observe(viewLifecycleOwner, { user ->
            viewModel.notificationSettings.observe(viewLifecycleOwner,  { set ->
                if (set != null) {
                    binding.alertNotificationSwitch.isChecked = set.alertNotification
                    binding.dailyNotificationSwitch.isChecked = set.dailyNotification
                    binding.sensorNotificationsSwitch.isChecked = set.deviceNotification
                    binding.selectedTimeText.text = convertTimeTo12h(set.dailyNotificationTime)
                }
            })
        })

        viewModel.getDailyNotification.observe(viewLifecycleOwner, {
            binding.dailyNotificationSwitch.isChecked = it
        })
        viewModel.getDailyNotificationTime.observe(viewLifecycleOwner, {
            binding.selectedTimeText.text = convertTimeTo12h(it)
        })
        viewModel.getAlertNotification.observe(viewLifecycleOwner, { binding.alertNotificationSwitch.isChecked = it })
        viewModel.getDeviceNotification.observe(viewLifecycleOwner, { binding.sensorNotificationsSwitch.isChecked = it })

        binding.selectTimeBtn.setOnClickListener { openTimePicker() }
        binding.dailyNotificationSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            viewModel.updateDailyNotification(isChecked)
        }
        binding.alertNotificationSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            viewModel.updateAlertNotification(isChecked)
        }
        binding.sensorNotificationsSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            viewModel.updateDeviceNotification(isChecked)
        }

        return view
    }
//    private fun Int.toBoolean() = this == 1
//    private fun convertTimeTo24h() {
//        val currentTime = binding.selectedTimeText.text.toString() // ex. 8:00 AM
//        val hourTemp = currentTime.split(":".toRegex())[0].toInt() // get the hour
//        val minAmPm = currentTime.split(":".toRegex())[1] // get the minutes and AM/PM
//        minute = minAmPm.split("\\s".toRegex())[0].toInt() // get minutes
//        val period = minAmPm.split("\\s".toRegex())[1] // get AM/PM
//        if (period == "PM") {
//            hour = hourTemp + 12 // covert to 24h
//        }
//        Log.i("Current Time", "Hour: $hour, Minute: $minute")
//    }

    private fun openTimePicker() {
        // open the time picker
        val timePicker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_12H)
            .setHour(hour).setMinute(minute)
            .build()
        timePicker.show(childFragmentManager, "TAG")

        timePicker.addOnPositiveButtonClickListener {
            updateDailyNotificationTime(timePicker)
        }
    }

    private fun convertTimeTo12h(time: String): String {
        var timeParts = time.split(":")
        hour = timeParts[0].toInt()
        minute = timeParts[1].toInt()
        val minuteStr = String.format("%02d", minute)
        var period = "AM"
        var tempHour = hour
        if (hour > 12) {
            period = "PM"
            tempHour -= 12
        }
        return "$tempHour:$minuteStr $period"
    }

    private fun updateDailyNotificationTime(timePicker: MaterialTimePicker) {
        hour = timePicker.hour // 24h
        minute = timePicker.minute // 24h
        val minuteStr = String.format("%02d", minute) // 24h & 12h
        val hourStr = String.format("%02d", hour) // 24h
        var timeStr24h = "$hourStr:$minuteStr:00"
        binding.selectedTimeText.text = convertTimeTo12h(timeStr24h)
        viewModel.updateDailyNotificationTime(timeStr24h)
    }
}