package project.softsquad.vegitable.ui.dialog
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.textfield.TextInputLayout
import project.softsquad.vegitable.R
import project.softsquad.vegitable.entity.DeviceEntity
import project.softsquad.vegitable.entity.PlantsEntity
import project.softsquad.vegitable.viewmodel.DeviceViewModel
import java.lang.ClassCastException

/**
 * Author: Martha Czerwik
 * Modified By: Brianna McBurney
 * DESC: Fragment to hold the dialog that will be displayed when the user clicks on the copy icon - this will create a duplicated entry in the db and will be added to the user's list of plants (active plants).
 * The user can copy plants from their current list of plants (active), or from their list of archived plants
 *
 */

public class CopyPlantDialogFragment(var bucketList: MutableMap<String, Long>, var currentPlant: PlantsEntity) : DialogFragment() {

    lateinit var copyPlantListener: CopyPlantDialogListener
    private val bucketNameList: MutableList<String> = ArrayList()
    private var selectedBucketName: String = ""

    interface CopyPlantDialogListener {
        fun onCopyPlantDialogPositiveClick(dialog: DialogFragment, bucketId: Int, plant: PlantsEntity)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        try {
            copyPlantListener = parentFragment as CopyPlantDialogListener
        } catch (e: ClassCastException) {
            throw ClassCastException("$parentFragment must implement PairingDialogListener")
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater;
            var view = inflater.inflate(R.layout.fragment_copy_plant_dialog, null)

            for ((name, id) in bucketList) {
                bucketNameList.add(name)
            }
            selectedBucketName = bucketNameList.first()

            val bucketDDM = view.findViewById<AutoCompleteTextView>(R.id.copyToBucketDropdown)
            val deviceAdapter = ArrayAdapter(requireContext(), R.layout.bucket_dropdown, bucketNameList)
            bucketDDM.setAdapter(deviceAdapter)
            bucketDDM.setText(selectedBucketName, false)
            bucketDDM.setOnItemClickListener { parent, view, position, id ->
                selectedBucketName = bucketNameList[position]
            }

            builder.setView(view)
                .setPositiveButton("Copy",
                    DialogInterface.OnClickListener { _, _ ->
                        var selectedBucket = selectedBucketName
                        var bucketId = bucketList[selectedBucket]
                        if (bucketId != null) {
                            copyPlantListener.onCopyPlantDialogPositiveClick(this, bucketId.toInt(), currentPlant)
                        }
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