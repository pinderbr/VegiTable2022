package project.softsquad.vegitable.ui.bucket

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.annotation.RequiresApi
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.afollestad.vvalidator.form
import project.softsquad.vegitable.ApiInterface
import project.softsquad.vegitable.R
import project.softsquad.vegitable.databinding.FragmentAddBucketBinding
import project.softsquad.vegitable.entity.BucketEntity
import project.softsquad.vegitable.viewmodel.bucket.BucketViewModel
import project.softsquad.vegitable.getCurrentDateTime
import project.softsquad.vegitable.viewmodel.DeviceViewModel
import project.softsquad.vegitable.viewmodel.UserViewModel

/**
 * Author: Martha Czerwik
 * DESC: Fragment to hold information to be displayed on the add bucket page.
 */

class AddBucketFragment : Fragment() {

   // var userId: Long = 1 // TODO replace with actual userid

    //view models
    private lateinit var bucketViewModel: BucketViewModel
    private val deviceViewModel: DeviceViewModel by activityViewModels()
    private val userViewModel: UserViewModel by activityViewModels()

    //private var currentUserId: Long = 15

    //view binding
    private var _binding: FragmentAddBucketBinding? = null
    private val binding get() = _binding!!

    //for dropdown items
    private val deviceList: MutableList<String> = ArrayList()

    //required for api calls - to be removed
    private lateinit var apiInterface: ApiInterface

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onResume() {
        super.onResume()

        //populate device dropdown
        //TODO: pass in user id
        getDevices()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentAddBucketBinding.inflate(inflater, container, false)

        //initialize interface - to be removed
        apiInterface = ApiInterface.create()

        //initialize view models
        bucketViewModel = ViewModelProvider(this).get(BucketViewModel::class.java)
       // deviceViewModel = ViewModelProvider(this).get(DeviceViewModel::class.java)

       // currentUserId = userViewModel.userId.value ?: 15

        //form validation
        form {

            inputLayout(binding.addBucketTextField) {
                isNotEmpty().description(getString(R.string.name_required))
            }
            inputLayout(binding.addBucketPairSensor) {
                isNotEmpty().description(getString(R.string.device_required))
            }

            //if the form is valid:
            submitWith(binding.addBucketFinishButton) {
                //get device id of the selected device name
                bucketViewModel.getSensor(binding.addBucketPairSensor.editText?.text.toString()).observe(viewLifecycleOwner,
                    { id ->
                        var deviceId = id.deviceId.toInt()
                        userViewModel.readAllData.observe(viewLifecycleOwner, {
                            //call add bucket method within the view model
                            //TODO: pass in image url once implemented
                            val bucketToAdd = BucketEntity(
                                0,
                                binding.addBucketTextField.editText?.text.toString(),
                                0.0,
                                0.0,
                                0.0,
                                0.0,
                                0.0,
                                0.0,
                                0.0,
                                0.0,
                                0.0,
                                0.0,
                                getCurrentDateTime(),
                                getCurrentDateTime(),
                                null,
                                "",
                                it.userId.toInt(),
                                deviceId
                            )

                            bucketViewModel.addBucket(bucketToAdd)
                            findNavController().navigate(R.id.action_addBucketFragment_to_bucketListFragment)
                        })

                    //val marthaTestBucket = BucketEntity(0, "Test Bucket 2", 0.0, 100.0, 4.0, 5.0, 2.0, 6.0, 200.0, 300.0, 24.0, 25.0, "2021-10-13 19:43:36", "2021-10-13 19:43:36", null, "placeholder", currentUserId.toInt(), 3000)
                    //bucketViewModel.addBucket(marthaTestBucket)
                    })


              //  val marthaTestBucket = BucketEntity(0, "Test Bucket 2", 0.0, 100.0, 4.0, 5.0, 2.0, 6.0, 200.0, 300.0, 24.0, 25.0, "2021-10-13 19:43:36", "2021-10-13 19:43:36", null, "placeholder", currentUserId.toInt(), 3000)

            }
        }

        binding.addBucketAddImage.setOnClickListener {
            //TODO: insert code here to upload an image from the user's local storage
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getDevices(){
        //userViewModel.userId.value?.let {
        /*
            deviceViewModel.getDeviceList(currentUserId.toInt()).observe(viewLifecycleOwner, { devices ->
                for (device in devices) {
                    deviceList.add(device.deviceName)
                }
                val deviceAdapter =
                    ArrayAdapter(requireContext(), R.layout.bucket_dropdown, deviceList)
                binding.deviceDropdownItem.setAdapter(deviceAdapter)
            })*/

        deviceViewModel.readAllData.observe(viewLifecycleOwner, { devices ->
            for (device in devices) {
                deviceList.add(device.deviceName)
            }
            val deviceAdapter =
                ArrayAdapter(requireContext(), R.layout.bucket_dropdown, deviceList)
            binding.deviceDropdownItem.setAdapter(deviceAdapter)
        })
       // }
    }
}