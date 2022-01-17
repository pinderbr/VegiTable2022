package project.softsquad.vegitable.recyclerview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import project.softsquad.vegitable.ApiInterface
import project.softsquad.vegitable.R
import project.softsquad.vegitable.entity.DeviceEntity
import java.lang.ClassCastException


/**
 * Author: Brianna McBurney
 * Description:
 */
class DeviceListRecyclerView(var devicesFragment: Fragment): RecyclerView.Adapter<DeviceListRecyclerView.DeviceViewHolder>() {

    var deviceList: List<DeviceEntity> = listOf()
    lateinit var apiInterface: ApiInterface

    lateinit var editListener: EditDeviceDialogListener
    lateinit var unpairListener: UnpairDeviceDialogListener

    // https://stackoverflow.com/questions/28620026/implement-dialogfragment-interface-in-onclicklistener
    interface EditDeviceDialogListener {
        fun onEditDialogPositiveClick(holder: DeviceViewHolder, currentDevice: DeviceEntity)
    }
    interface UnpairDeviceDialogListener {
        fun onUnpairDialogPositiveClick(holder: DeviceViewHolder, currentDevice: DeviceEntity)
    }

    class DeviceViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var deviceName = itemView.findViewById<TextView>(R.id.device_name)
        var deviceNumber = itemView.findViewById<TextView>(R.id.device_number)
        var removeDeviceButton = itemView.findViewById<ImageButton>(R.id.delete_device_btn)
        var editDeviceButton = itemView.findViewById<ImageButton>(R.id.edit_device_btn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.device_list_card, parent, false)
        apiInterface = ApiInterface.create()
        try {
            editListener = devicesFragment as EditDeviceDialogListener
        } catch (e: ClassCastException) {
            throw ClassCastException("ERROR -> $devicesFragment must implement EditDeviceDialogListener")
        }
        try {
            unpairListener = devicesFragment as UnpairDeviceDialogListener
        } catch (e: ClassCastException) {
            throw ClassCastException("ERROR -> $devicesFragment must implement UnpairDeviceDialogListener")
        }
        return DeviceViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: DeviceViewHolder, position: Int) {
        val currentDevice = deviceList[position]

        holder.deviceNumber.text = "Serial Number: " + currentDevice.deviceId.toString()
        holder.deviceName.text = currentDevice.deviceName
        holder.removeDeviceButton.setOnClickListener {
            unpairListener.onUnpairDialogPositiveClick(holder, currentDevice)
        }
        holder.editDeviceButton.setOnClickListener {
            editListener.onEditDialogPositiveClick(holder, currentDevice)
        }
    }

    fun setData(deviceEntity: List<DeviceEntity>) {
        this.deviceList = deviceEntity
        notifyDataSetChanged()
    }

    override fun getItemCount() = deviceList.size

}