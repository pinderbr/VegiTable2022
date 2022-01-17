package project.softsquad.vegitable.ui.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.google.android.material.textfield.TextInputLayout
import project.softsquad.vegitable.R
import project.softsquad.vegitable.entity.DeviceEntity
import java.lang.ClassCastException

class EditDeviceDialogFragment(var device: DeviceEntity) : DialogFragment() {

    lateinit var saveListener: SaveDeviceDialogListener

    // https://stackoverflow.com/questions/28620026/implement-dialogfragment-interface-in-onclicklistener
    interface SaveDeviceDialogListener {
        fun onSaveDialogPositiveClick(dialog: DialogFragment, deviceName: String, currentDevice: DeviceEntity)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            saveListener = parentFragment as SaveDeviceDialogListener
        } catch (e: ClassCastException) {
            throw ClassCastException("$parentFragment must implement SaveDeviceDialogListener")
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater;
            var view = inflater.inflate(R.layout.fragment_edit_device_dialog, null)
            view.findViewById<TextInputLayout>(R.id.deviceName).editText?.setText(device.deviceName)

            builder.setView(view)
                .setPositiveButton("Save",
                    DialogInterface.OnClickListener { dialog, id ->
                        saveListener.onSaveDialogPositiveClick(this, view.findViewById<TextInputLayout>(R.id.deviceName).editText?.text.toString(), device)
                    })
                .setNegativeButton(
                    R.string.cancel,
                    DialogInterface.OnClickListener { dialog, id ->
                        getDialog()?.cancel()
                    })
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}