package project.softsquad.vegitable.ui.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.google.android.material.textfield.TextInputLayout
import project.softsquad.vegitable.R
import java.lang.ClassCastException

/**
 * Author: Brianna McBurney
 * Description:
 */
class PairDeviceDialogFragment : DialogFragment() {

    lateinit var pairingListener: PairingDialogListener

    // https://stackoverflow.com/questions/28620026/implement-dialogfragment-interface-in-onclicklistener
    interface PairingDialogListener {
        fun onPairDialogPositiveClick(dialog: DialogFragment, deviceIdInput: String)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        try {
            pairingListener = parentFragment as PairingDialogListener
        } catch (e: ClassCastException) {
            throw ClassCastException("$parentFragment must implement PairingDialogListener")
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater;
            var view = inflater.inflate(R.layout.fragment_pair_device_dialog, null)

            builder.setView(view)
                .setPositiveButton("Pair",
                    DialogInterface.OnClickListener { dialog, id ->
                        pairingListener.onPairDialogPositiveClick(this, view.findViewById<TextInputLayout>(R.id.newDeviceId).editText?.text.toString())
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