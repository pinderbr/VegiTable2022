package project.softsquad.vegitable.ui.bucket

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.afollestad.vvalidator.form
import project.softsquad.vegitable.ApiInterface
import project.softsquad.vegitable.R
import project.softsquad.vegitable.databinding.FragmentUpdateBucketBinding
import project.softsquad.vegitable.viewmodel.bucket.BucketViewModel
import project.softsquad.vegitable.viewmodel.DeviceViewModel
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener

import android.widget.AutoCompleteTextView
import project.softsquad.vegitable.alert


/**
 * Author: Martha Czerwik
 * Modified by: Brianna McBurney
 * DESC: Fragment to hold information to be displayed on the update bucket page. Form fields will be prepopulated based on which bucket is clicked.
 */

class UpdateBucketFragment : Fragment() {

    // passing in bucketID
    private val navigationArgs: UpdateBucketFragmentArgs by navArgs()
    val userId: Long = 1;

    //view binding
    private lateinit var binding: FragmentUpdateBucketBinding

    //items for dropdown
    private val deviceList: MutableList<String> = ArrayList()
    private var selectedIndex: Int = 0

    //view models
    private lateinit var bucketViewModel: BucketViewModel
    private lateinit var deviceViewModel: DeviceViewModel

    //required for api calls - to be removed once not needed
    private lateinit var apiInterface: ApiInterface

    override fun onResume() {
        super.onResume()

        //TODO: pass in user id
        getDevices(userId)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = FragmentUpdateBucketBinding.inflate(inflater, container, false)
        val view = binding.root

        //initialize interface - to be removed
        apiInterface = ApiInterface.create()

        //initialize view model
        bucketViewModel = ViewModelProvider(this).get(BucketViewModel::class.java)
        deviceViewModel = ViewModelProvider(this).get(DeviceViewModel::class.java)

        binding.updateDeviceDropdownItem.setOnItemClickListener { parent, view, position, id ->
            selectedIndex = position
        }
        bucketViewModel.getLiveBucket(navigationArgs.bucketId).observe(viewLifecycleOwner, { bucket ->
            Log.i("BUCKET_ID", navigationArgs.bucketId.toString())
            if (bucket != null) {
                binding.updateBucketTextField.editText?.setText(bucket.bucketName)
                deviceViewModel.readAllData.observe(viewLifecycleOwner, Observer { devices ->
                    var currentDeviceId: Int = bucket.deviceId_fk
                    for (i in 0 until devices.count()) {
                        var deviceId: Int = devices[i].deviceId.toInt()
                        if (currentDeviceId == deviceId) {
                            selectedIndex = i
                           // binding.updateDeviceDropdownItem.setSelection(i)
                            binding.updateBucketPairSensor.editText?.setText(devices[i].deviceName)
                        }
                    }
                })
            } else {
                alert(activity, "NO BUCKET", "Bucket couldn't be found.")
            }
        })

        //form validation
        form {

            inputLayout(binding.updateBucketTextField) {
                isNotEmpty().description(getString(R.string.name_required))
            }
            inputLayout(binding.updateBucketPairSensor) {
                isNotEmpty().description(getString(R.string.device_required))
            }

            //if the form is valid:
            submitWith(binding.updateBucketButton) {

                bucketViewModel.getLiveBucket(navigationArgs.bucketId).observe(viewLifecycleOwner, { bucket ->
                    if (bucket != null) {
                        bucket.deviceId_fk = 0
                        bucket.bucketName = binding.updateBucketTextField.editText?.text.toString()
                        deviceViewModel.readAllData.observe(viewLifecycleOwner, Observer{  devices->
                            for (i in 0..devices.count()) {
                                if(i == selectedIndex) {
                                    bucket.deviceId_fk = devices[i].deviceId.toInt()
                                }
                            }
                        })

                        bucketViewModel.updateBucket(bucket)

                        //nav back to bucket list
                        findNavController().navigate(R.id.action_updateBucketFragment_to_bucketListFragment)
                    } else {
                        alert(activity, "Error", "Your bucket could not be updated at this time. Please try again later.")
                    }
                })
            }
        }

        binding.updateBucketAddImage.setOnClickListener {
            //TODO: insert code here to upload an image from the user's local storage - AWS S3
        }

        return view
    }

    private fun getDevices(userId: Long){
        deviceViewModel.readAllData.observe(viewLifecycleOwner, Observer{  devices->
            for (device in devices) {
                deviceList.add(device.deviceName)
            }
            val deviceAdapter =
                ArrayAdapter(requireContext(), R.layout.bucket_dropdown, deviceList)
            binding.updateDeviceDropdownItem.setAdapter(deviceAdapter)
        })
    }
}