package project.softsquad.vegitable.ui.plant

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.*
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.afollestad.vvalidator.form
import project.softsquad.vegitable.*
import project.softsquad.vegitable.dao.PlantsDao
import project.softsquad.vegitable.databinding.FragmentUpdatePlantBinding
import project.softsquad.vegitable.entity.PlantTypeEntity
import project.softsquad.vegitable.entity.PlantsEntity
import project.softsquad.vegitable.viewmodel.*
import project.softsquad.vegitable.viewmodel.bucket.BucketViewModel
import project.softsquad.vegitable.viewmodel.plant.PlantViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


/**
 * Author: Martha Czerwik
 * DESC: Fragment to hold information to be displayed on the update plant page.
 */

class UpdatePlantFragment : Fragment() {

    //TODO: this is a temp value - need to pass in plant id from previous fragment (when user clicks on the card for certain plant)
    //var plantId: Long = 1

    //to receive values from previous fragment
    private val navigationArgs: UpdatePlantFragmentArgs by navArgs()

    //view models
    private val plantViewModel: PlantViewModel by activityViewModels()
    private val bucketViewModel: BucketViewModel by activityViewModels()
    private val plantTypeViewModel: PlantTypeViewModel by activityViewModels()
    private val userViewModel: UserViewModel by activityViewModels()
    private val readingViewModel: DeviceReadingViewModel by activityViewModels()

    //view binding
    private var _binding: FragmentUpdatePlantBinding? = null
    private val binding get() = _binding!!

    //for dropdown item
    var plantTypes = ArrayList<PlantTypeEntity>()
    private val bucketList: MutableList<String> = ArrayList()
    private val plantTypeList: MutableList<String> = ArrayList()

    //required for api call
    lateinit var apiInterface: ApiInterface

    override fun onResume() {
        super.onResume()
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentUpdatePlantBinding.inflate(inflater, container, false)
        //initialize interface
        apiInterface = ApiInterface.create()

        getPlantTypes()
        getDefaultPlantTypes()


        //prepouplate fields based on original values
        plantViewModel.singlePlant(navigationArgs.plantId).observe(viewLifecycleOwner, Observer{  plant->
            binding.updatePlantNickname.editText?.setText(plant.plantName)
            binding.updatePlantTypeDropdown.editText?.setText(plant?.plantType)
            binding.updateTempMinInput.editText?.setText(plant?.temperatureMin.toString())
            binding.updateTempMaxInput.editText?.setText(plant?.temperatureMax.toString())
            binding.updatePhMinInput.editText?.setText(plant?.phMin.toString())
            binding.updatePhMaxInput.editText?.setText(plant?.phMax.toString())
            binding.updatePPMMinInput.editText?.setText(plant?.ppmMin.toString())
            binding.updatePPMMaxInput.editText?.setText(plant?.ppmMax.toString())
            binding.updateLightMinInput.editText?.setText(plant?.lightMin.toString())
            binding.updateLightMaxInput.editText?.setText(plant?.lightMax.toString())
            binding.updateHumidityMinInput.editText?.setText(plant?.humidityMin.toString())
            binding.updateHumidityMaxInput.editText?.setText(plant?.humidityMax.toString())
            binding.updatePlantPhase.editText?.setText(plant?.plantPhase)
        })

        binding.updatePlantTypeDropdownItem.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                plantTypeViewModel.getPlantTypeByName(s.toString()).observe(viewLifecycleOwner, Observer { type ->
                    if (type != null) {
                        binding.updateTempMinInput.editText?.setText(type?.temperatureMin.toString())
                        binding.updateTempMaxInput.editText?.setText(type?.temperatureMax.toString())
                        binding.updatePhMinInput.editText?.setText(type?.phMin.toString())
                        binding.updatePhMaxInput.editText?.setText(type?.phMax.toString())
                        binding.updatePPMMinInput.editText?.setText(type?.ppmMin.toString())
                        binding.updatePPMMaxInput.editText?.setText(type?.ppmMax.toString())
                        binding.updateLightMinInput.editText?.setText(type?.lightMin.toString())
                        binding.updateLightMaxInput.editText?.setText(type?.lightMax.toString())
                        binding.updateHumidityMinInput.editText?.setText(type?.humidityMin.toString())
                        binding.updateHumidityMaxInput.editText?.setText(type?.humidityMax.toString())
                    } else {
                        var t: String = s.toString()
                        getDefaultPlantType(t)
                    }
                })
            }

            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                Log.i("TEST",(s.toString()))
            }
        })

        //form validation - commented out for testing
        form {
/*
            inputLayout(binding.updatePlantNickname) {
                isNotEmpty().description(getString(R.string.name_required))
            }
            inputLayout(binding.updateBucketDropdown) {
                isNotEmpty().description(getString(R.string.bucket_required))
            }
            inputLayout(binding.updatePhMinInput) {
                isNotEmpty().description(getString(R.string.value_required))
            }
            inputLayout(binding.updatePhMaxInput) {
                isNotEmpty().description(getString(R.string.value_required))
            }
            inputLayout(binding.updatePPMMinInput) {
                isNotEmpty().description(getString(R.string.value_required))
            }
            inputLayout(binding.updatePPMMaxInput) {
                isNotEmpty().description(getString(R.string.value_required))
            }
            inputLayout(binding.updateLightMinInput) {
                isNotEmpty().description(getString(R.string.value_required))
            }
            inputLayout(binding.updateLightMaxInput) {
                isNotEmpty().description(getString(R.string.value_required))
            }
            inputLayout(binding.updateHumidityMinInput) {
                isNotEmpty().description(getString(R.string.value_required))
            }
            inputLayout(binding.updateHumidityMaxInput) {
                isNotEmpty().description(getString(R.string.value_required))
            }
            inputLayout(binding.updateTempMinInput) {
                isNotEmpty().description(getString(R.string.value_required))
            }
            inputLayout(binding.updateTempMaxInput) {
                isNotEmpty().description(getString(R.string.value_required))
            }
*/
            //if the form is valid:
            submitWith(binding.updatePlantBtn) {
                //call method in viewmodel to update plant
                var currentTime = getCurrentDateTime()


                plantViewModel.singlePlant(navigationArgs.plantId).observe(viewLifecycleOwner, Observer{  plant->
                    plant.plantType = binding.updatePlantTypeDropdown.editText?.text.toString()
                    plant.plantName = binding.updatePlantNickname.editText?.text.toString()
                    plant.temperatureMin = binding.updateTempMinInput.editText?.text.toString().toDouble()
                    plant.temperatureMax = binding.updateTempMaxInput.editText?.text.toString().toDouble()
                    plant.phMin = binding.updatePhMinInput.editText?.text.toString().toDouble()
                    plant.phMax = binding.updatePhMaxInput.editText?.text.toString().toDouble()
                    plant.ppmMin = binding.updatePPMMinInput.editText?.text.toString().toDouble()
                    plant.ppmMax = binding.updatePPMMaxInput.editText?.text.toString().toDouble()
                    plant.lightMin = binding.updateLightMinInput.editText?.text.toString().toDouble()
                    plant.lightMax = binding.updateLightMaxInput.editText?.text.toString().toDouble()
                    plant.humidityMin = binding.updateHumidityMinInput.editText?.text.toString().toDouble()
                    plant.humidityMax = binding.updateHumidityMaxInput.editText?.text.toString().toDouble()
                    plant.plantPhase = binding.updatePlantPhase.editText?.text.toString()
                    plant.lastUpdateDateTime = currentTime
                    plant.imageURL = binding.updateImageName.editText?.text.toString()

                plantViewModel.setCurrentPlant(plant)
                bucketViewModel.getBucket(navigationArgs.bucketId)
                    .observe(viewLifecycleOwner, Observer { bucket ->
                        plantViewModel.setCurrentBucket(bucket)
                        plantViewModel.getPlants(bucket.bucketId)
                            .observe(viewLifecycleOwner, Observer { plants ->
                                plantViewModel.setcurrentPlantList(plants)

                                if (plantViewModel.currentPlantList.value != null) {
                                    var count = plantViewModel.currentPlantList.value!!.count()
                                        if (checkThresholds(bucket, count, plant)) {
                                            plantViewModel.updatePlant(
                                                plant,
                                                bucket,
                                                plantViewModel.currentPlantList.value!!
                                            )
                                            val action =
                                                UpdatePlantFragmentDirections.actionUpdatePlantFragmentToViewPlantListFragment(
                                                    navigationArgs.bucketId,
                                                    navigationArgs.userId
                                                )
                                            findNavController().navigate(action)
                                        } else {
                                            alert(
                                                activity,
                                                "Error",
                                                "This plant will not thrive in this bucket. Please add it to another bucket."
                                            )
                                        }


                                }
                            })
                    })

            })
            }
        }


        binding.updateImgBtn.setOnClickListener {
            //TODO: insert code here to upload an image from the user's local storage - AWS S3
        }

        binding.updatePlantTypeBtn.setOnClickListener {
            //nav to add plant type fragment
            findNavController().navigate(R.id.action_updatePlantFragment_to_createPlantTypeFragment)
        }

        return binding.root
    }

//    private fun getBuckets(){
//        bucketViewModel.readAllData.observe(viewLifecycleOwner, Observer{  buckets->
//            for (bucket in buckets) {
//                bucketList.add(bucket.bucketName)
//            }
//            val bucketAdapter =
//                ArrayAdapter(requireContext(), R.layout.bucket_dropdown, bucketList)
//            binding.updateBucketDropdownItem.setAdapter(bucketAdapter)
//        })
//    }



/*
    private fun getPlant(plantId: Long){
        plantViewModel.getPlant(plantId).observe(viewLifecycleOwner, {
            if (it != null){
                Log.i("TEST", "plant returned: $it")
                //binding.updatePlantNickname.editText?.setText(it.plantName)
            }
        })
    }*/
/*
    private fun getPlant(plantId: Long){
        lifecycleScope.launch {
            var plant = plantViewModel.getPlant(plantId)
            binding.updatePlantNickname.editText?.setText(plant.plantName)
            binding.updatePlantTypeDropdown.editText?.setText(plantToUpdate?.plantType)
            binding.updateTempMinInput.editText?.setText(plantToUpdate?.temperatureMin.toString())
            binding.updateTempMaxInput.editText?.setText(plantToUpdate?.temperatureMax.toString())
            binding.updatePhMinInput.editText?.setText(plantToUpdate?.phMin.toString())
            binding.updatePhMaxInput.editText?.setText(plantToUpdate?.phMax.toString())
            binding.updatePPMMinInput.editText?.setText(plantToUpdate?.ppmMin.toString())
            binding.updatePPMMaxInput.editText?.setText(plantToUpdate?.ppmMax.toString())
            binding.updateLightMinInput.editText?.setText(plantToUpdate?.lightMin.toString())
            binding.updateLightMaxInput.editText?.setText(plantToUpdate?.lightMax.toString())
            binding.updateHumidityMinInput.editText?.setText(plantToUpdate?.humidityMin.toString())
            binding.updateHumidityMinInput.editText?.setText(plantToUpdate?.humidityMax.toString())
            binding.updatePlantPhase.editText?.setText(plantToUpdate?.plantPhase)
        }


    }
*/


    private fun getPlantTypes(){
        plantTypeViewModel.readAllData.observe(viewLifecycleOwner, Observer{  types->
            plantTypeList.clear()
            for (type in types) {
                plantTypeList.add(type.plantTypeName)
            }
        })

        val plantTypeAdapter =
            ArrayAdapter(requireContext(), R.layout.plant_type_dropdown, plantTypeList)
        binding.updatePlantTypeDropdownItem.setAdapter(plantTypeAdapter)
    }

    //region API methods

    private fun getDefaultPlantType(type : String){
        //call get method from api interface
        apiInterface.getDefaultPlantTypeByName(type).enqueue(object : Callback<PlantTypeEntity> {
            override fun onResponse(
                call: Call<PlantTypeEntity>?,
                response: Response<PlantTypeEntity>?
            ) {
                if (response?.body() != null) {
                    //if response has a list of plant types, populate plant type dropdown array
                    Log.i("GET SUCCESS", "Plant Type has been found")
                    var plant = response.body() as PlantTypeEntity
                    //binding here
                    binding.updateTempMinInput.editText?.setText(plant?.temperatureMin.toString())
                    binding.updateTempMaxInput.editText?.setText(plant?.temperatureMax.toString())
                    binding.updatePhMinInput.editText?.setText(plant?.phMin.toString())
                    binding.updatePhMaxInput.editText?.setText(plant?.phMax.toString())
                    binding.updatePPMMinInput.editText?.setText(plant?.ppmMin.toString())
                    binding.updatePPMMaxInput.editText?.setText(plant?.ppmMax.toString())
                    binding.updateLightMinInput.editText?.setText(plant?.lightMin.toString())
                    binding.updateLightMaxInput.editText?.setText(plant?.lightMax.toString())
                    binding.updateHumidityMinInput.editText?.setText(plant?.humidityMin.toString())
                    binding.updateHumidityMaxInput.editText?.setText(plant?.humidityMax.toString())
                } else {
                    Log.i("GET FAILURE", "Plant type does not exist")
                }
            }

            override fun onFailure(call: Call<PlantTypeEntity>?, t: Throwable?) {
                Log.i("GET ERROR", "Something went wrong")

            }
        })
    }

    private fun getDefaultPlantTypes() {
        //call get method from api interface
        apiInterface.getDefaultPlantTypes().enqueue(object : Callback<List<PlantTypeEntity>> {
            override fun onResponse(
                call: Call<List<PlantTypeEntity>>?,
                response: Response<List<PlantTypeEntity>>?
            ) {
                if (response?.body() != null) {
                    //if response has a list of plant types, populate plant type dropdown array
                    Log.i("GET SUCCESS", "Plant Types have been found")
                    plantTypes = response.body() as ArrayList<PlantTypeEntity>
                    for (type in plantTypes) {
                        plantTypeList.add(type.plantTypeName)
                    }
                    val plantTypeAdapter = ArrayAdapter(requireContext(), R.layout.plant_type_dropdown, plantTypeList)
                    binding.updatePlantTypeDropdownItem.setAdapter(plantTypeAdapter)
                } else {
                    Log.i("GET FAILURE", "No plant types exist")
                }
            }

            override fun onFailure(call: Call<List<PlantTypeEntity>>?, t: Throwable?) {
                Log.i("GET ERROR", "Something went wrong")
                alert(activity, "Error", "Default plant types could not be retrieved - please connect to the internet to select a default plant type.")

            }
        })
    }

    //endregion


}