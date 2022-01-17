package project.softsquad.vegitable.ui

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import project.softsquad.vegitable.ApiInterface
import project.softsquad.vegitable.alert
import project.softsquad.vegitable.databinding.FragmentDevicesBinding
import project.softsquad.vegitable.entity.DeviceEntity
import project.softsquad.vegitable.entity.DeviceReadingsEntity
import project.softsquad.vegitable.recyclerview.DeviceListRecyclerView
import project.softsquad.vegitable.ui.dialog.EditDeviceDialogFragment
import project.softsquad.vegitable.ui.dialog.PairDeviceDialogFragment
import project.softsquad.vegitable.viewmodel.DeviceViewModel
import project.softsquad.vegitable.viewmodel.UserViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.collections.ArrayList

/**
 * Author: Brianna McBurney
 * Description:
 */
@RequiresApi(Build.VERSION_CODES.O)
class DevicesFragment : Fragment(), PairDeviceDialogFragment.PairingDialogListener, DeviceListRecyclerView.EditDeviceDialogListener,
    EditDeviceDialogFragment.SaveDeviceDialogListener, DeviceListRecyclerView.UnpairDeviceDialogListener {

    private lateinit var binding: FragmentDevicesBinding
    //private var currentUserId: Int = 15
    private val viewModel by lazy {
        ViewModelProvider(this).get(DeviceViewModel::class.java)
    }
    private val userViewModel: UserViewModel by activityViewModels()
    lateinit var recyclerView: RecyclerView
    lateinit var recyclerAdapter: DeviceListRecyclerView
    lateinit var apiInterface: ApiInterface

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentDevicesBinding.inflate(inflater, container, false)
        val view = binding.root

        apiInterface = ApiInterface.create()

        //currentUserId = userViewModel.userId.value?.toInt() ?: 0

        userViewModel.readAllData.observe(viewLifecycleOwner, { currentUser ->
            viewModel.readAllData.observe(viewLifecycleOwner, { devices ->
                recyclerAdapter.setData(devices)
                recyclerAdapter.notifyDataSetChanged()
                if(devices.isEmpty()){
                    alert(activity, "No Devices Found", "You haven't paired any devices yet.")
                }
            })
        })
        recyclerView = binding.devicesRecyclerView
        recyclerAdapter = DeviceListRecyclerView(this)
        recyclerView.adapter = recyclerAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        binding.pairDeviceBtn.setOnClickListener {
            val pairDeviceDialog = PairDeviceDialogFragment()
            pairDeviceDialog.show(childFragmentManager, "PairingDevice")
        }

        return view
    }

    private fun pairDevice(deviceToUpdate: DeviceEntity) {
        viewModel.getDevice(deviceToUpdate.deviceId).observe(viewLifecycleOwner, { device ->
            userViewModel.readAllData.observe(viewLifecycleOwner, { user ->
                if(device != null && device.userId_fk == user.userId.toInt()){
//                    alert(activity, "Already Paired", "You have already paired this device!")
                } else if(deviceToUpdate.userId_fk != 0 && deviceToUpdate.userId_fk != user.userId.toInt()) {
                    // device already assigned to another user
                    alert(activity, "Not Available", "That device isn't available for pairing, it belongs to another user.")
                } else {
                    //pair device via API, then copy into local database (within api call method)
                    deviceToUpdate.userId_fk = user.userId.toInt()
                    pairRemoteDevice(deviceToUpdate)
                }
            })
        })

        /*
        if(deviceToUpdate.userId_fk == userViewModel.userId.value?.toInt() && ) {
            // user has already paired the device
            alert(activity, "Already Paired", "You have already paired this device!")
        } else if(deviceToUpdate.userId_fk != 0 && deviceToUpdate.userId_fk != userViewModel.userId.value?.toInt()) {
            // device already assigned to another user
            alert(activity, "Not Available", "That device isn't available for pairing, it belongs to another user.")
        } else {
            //pair device via API, then copy into local database (within api call method)
            deviceToUpdate.userId_fk = userViewModel.userId.value?.toInt()
            pairRemoteDevice(deviceToUpdate)
        }*/
    }

    //region Device Api Calls
    private fun getRemoteDevice(deviceId: Int) {
        var device: DeviceEntity? = null
        apiInterface.getDeviceById(deviceId).enqueue( object : Callback<DeviceEntity> {
            override fun onResponse(call: Call<DeviceEntity>?, response: Response<DeviceEntity>?) {
                if(response?.body() != null) {
                    Log.i("FOUND DEVICE", "Found device")
                    device = response.body()
                    device?.let { pairDevice(it) }
                } else {
                    if (response != null) {
                        alert(activity, "ERROR", "Something went wrong! " + response.errorBody())
                    }
                }
            }
            override fun onFailure(call: Call<DeviceEntity>?, t: Throwable?) {
                alert(activity, "No Device Found", "There is no device with that id. Please try again!")
            }
        })
    }

    //endregion

    //region OnClickListeners
    override fun onPairDialogPositiveClick(dialog: DialogFragment, deviceIdInput: String) {
        // convert string to int
        var id: Int = deviceIdInput.toInt()
        // check DB to make sure serial number is valid
        getRemoteDevice(id)
    }

    override fun onEditDialogPositiveClick(holder: DeviceListRecyclerView.DeviceViewHolder, currentDevice: DeviceEntity) {
        // open dialog to edit device name
        var editDeviceDialog = EditDeviceDialogFragment(currentDevice)
        editDeviceDialog.show(childFragmentManager, "EditDeviceName")
    }

    override fun onSaveDialogPositiveClick(dialog: DialogFragment, deviceName: String, currentDevice: DeviceEntity) {
        Log.i("SAVE", "saving new device name")
        if (deviceName == currentDevice.deviceName) {
            alert(activity, "No Updated", "New name matches the old name.")
        } else {
            currentDevice.deviceName = deviceName
            viewModel.updateDeviceName(currentDevice) //send device object to viewmodel class to update locally then remotely
        }
    }

    //https://stackoverflow.com/questions/56287917/create-an-alertdialog-in-a-fragment-using-kotlin
    override fun onUnpairDialogPositiveClick(holder: DeviceListRecyclerView.DeviceViewHolder, currentDevice: DeviceEntity) {
        val builder = AlertDialog.Builder(activity)
        builder.setTitle("Unpair Device")
            .setMessage("Are you sure you want to unpair ${currentDevice.deviceName}?")
            .setPositiveButton("Yes", DialogInterface.OnClickListener { dialog, id -> currentDevice.userId_fk?.let {
                viewModel.archiveDevice(currentDevice,
                    it
                )
            } })
            .setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, id -> dialog.cancel() })
        builder.create()
        builder.show()
    }
    //endregion

    //region API calls
    private fun pairRemoteDevice(deviceToUpdate: DeviceEntity) {
        var device: DeviceEntity

        deviceToUpdate.userId_fk?.let {
            apiInterface.updateDevice(deviceToUpdate.deviceId.toInt(), it).enqueue(object : Callback<DeviceEntity> {
                override fun onResponse(call: Call<DeviceEntity>?, response: Response<DeviceEntity>?) {
                    if (response?.body() != null) {
                        Log.i("UPDATED DEVICE", "Device has been updated")
                        alert(activity,"Device Paired", "You have successfully paired a new device")
                        device = response.body() as DeviceEntity
                        // then copy into local database
                        viewModel.addDevice(device)

                    } else {
                        if (response != null) {
                            alert(activity, "ERROR", "Something went wrong! " + response.errorBody())
                        }
                    }
                }

                override fun onFailure(call: Call<DeviceEntity>?, t: Throwable?) {
                    alert(activity, "No Device Found", "There is no device with that id. Please try again!")
                }
            })
        }


    }
/*
    private fun unpairRemoteDevice(device: DeviceEntity) {
        apiInterface.removeDevice(device.userId_fk, device.deviceId.toInt()).enqueue(object : Callback<DeviceEntity> {
            override fun onResponse(call: Call<DeviceEntity>?, response: Response<DeviceEntity>?) {
                if (response?.body() != null) {
                    alert(activity, "Device Unpaired", "You have successfully unpaired ${device.deviceName}")
                    viewModel.archiveDevice(device)
                } else {
                    if (response != null) {
                        alert(activity, "ERROR", "Something went wrong! " + response.errorBody())
                    }
                }
            }

            override fun onFailure(call: Call<DeviceEntity>?, t: Throwable?) {
                alert(activity, "No Device Found", "There is no device with that id. Please try again!")
            }
        })
    }

    private fun updateRemoteDeviceName(deviceToUpdate: DeviceEntity) {
        val params = HashMap<String?, String?>()
        params["deviceName"] = deviceToUpdate.deviceName
        apiInterface.updateDeviceName(deviceToUpdate.userId_fk, deviceToUpdate.deviceId.toInt(), params)
            .enqueue(object : Callback<DeviceEntity> {
                override fun onResponse(
                    call: Call<DeviceEntity>?,
                    response: Response<DeviceEntity>?
                ) {
                    if (response?.body() != null) {
                        Log.i("UPDATED DEVICE", "Device has been updated")
                        alert(activity, "Device Updated", "The device name has been updated")
                        getRemoteDeviceList(currentUserId)
                    } else {
                        if (response != null) {
                            alert(activity, "ERROR", "Something went wrong! " + response.errorBody())
                        }
                    }
                }
                override fun onFailure(call: Call<DeviceEntity>?, t: Throwable?) {
                    alert(activity, "No Device Found","There is no device with that id. Please try again!")
                }
            })
    }

    private fun getRemoteDeviceList(userId: Int) {
        var deviceList = ArrayList<DeviceEntity>()
        apiInterface.getDevices(userId).enqueue( object : Callback<List<DeviceEntity>> {
            override fun onResponse(call: Call<List<DeviceEntity>>?, response: Response<List<DeviceEntity>>?) {
                if(response?.body() != null) {
                    deviceList = response.body() as ArrayList<DeviceEntity>
                    recyclerAdapter.setData(deviceList)
                    recyclerAdapter.notifyDataSetChanged()
                } else {
                    if (response != null) {
                        alert(activity, "ERROR", "Something went wrong! " + response.errorBody())
                    }
                }
            }
            override fun onFailure(call: Call<List<DeviceEntity>>?, t: Throwable?) {
                alert(activity, "No Devices Found", "You haven't paired any devices yet.")
            }
        })
    }
*/
    //endregion
}