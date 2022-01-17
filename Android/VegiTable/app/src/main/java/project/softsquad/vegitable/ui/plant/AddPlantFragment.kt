package project.softsquad.vegitable.ui.plant

import android.app.Application
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
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.afollestad.vvalidator.form
import project.softsquad.vegitable.ApiInterface
import project.softsquad.vegitable.R
import project.softsquad.vegitable.alert
import project.softsquad.vegitable.databinding.FragmentAddPlantBinding
import project.softsquad.vegitable.entity.PlantTypeEntity
import project.softsquad.vegitable.entity.PlantsEntity
import project.softsquad.vegitable.viewmodel.bucket.BucketViewModel
import project.softsquad.vegitable.getCurrentDateTime
import project.softsquad.vegitable.viewmodel.PlantTypeViewModel
import project.softsquad.vegitable.viewmodel.plant.PlantViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import project.softsquad.vegitable.checkThresholds

/**
 * Author: Martha Czerwik
 * DESC: Fragment to hold information to be displayed on the add plant page.
 */

class AddPlantFragment : Fragment() {

    private val navigationArgs: AddPlantFragmentArgs by navArgs()

    //view models
    private val plantViewModel: PlantViewModel by activityViewModels()
    private val bucketViewModel: BucketViewModel by activityViewModels()
    private val plantTypeViewModel: PlantTypeViewModel by activityViewModels()

    //view binding
    private var _binding: FragmentAddPlantBinding? = null
    private val binding get() = _binding!!

    //for dropdown items
    var plantTypes = ArrayList<PlantTypeEntity>()
    private val bucketList: MutableList<String> = ArrayList()
    private val plantTypeList: MutableList<String> = ArrayList()

    //required for api calls - to be removed
    private lateinit var apiInterface: ApiInterface

    override fun onResume() {
        super.onResume()

        //populate bucket and plant type dropdown
        //getBuckets()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentAddPlantBinding.inflate(inflater, container, false)

        //initialize interface - to be removed
        apiInterface = ApiInterface.create()

        getPlantTypes()
        getDefaultPlantTypes()

        binding.addPlantTypeDropdownItem.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                plantTypeViewModel.getPlantTypeByName(s.toString()).observe(viewLifecycleOwner, Observer { type ->
                    if (type != null) {
                        binding.addTempMinInput.editText?.setText(type?.temperatureMin.toString())
                        binding.addTempMaxInput.editText?.setText(type?.temperatureMax.toString())
                        binding.addPhMinInput.editText?.setText(type?.phMin.toString())
                        binding.addPhMaxInput.editText?.setText(type?.phMax.toString())
                        binding.addPPMMinInput.editText?.setText(type?.ppmMin.toString())
                        binding.addPPMMaxInput.editText?.setText(type?.ppmMax.toString())
                        binding.addLightMinInput.editText?.setText(type?.lightMin.toString())
                        binding.addLightMaxInput.editText?.setText(type?.lightMax.toString())
                        binding.addHumidityMinInput.editText?.setText(type?.humidityMin.toString())
                        binding.addHumidityMaxInput.editText?.setText(type?.humidityMax.toString())
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

        //form validation -- commented out for testing
        form {

            inputLayout(binding.addPlantNickname) {
                isNotEmpty().description(getString(R.string.name_required))
            }
            //inputLayout(binding.addBucketDropdown) {
            //    isNotEmpty().description(getString(R.string.bucket_required))
            //}
            inputLayout(binding.addPhMinInput) {
                isNotEmpty().description(getString(R.string.value_required))
            }
            inputLayout(binding.addPhMaxInput) {
                isNotEmpty().description(getString(R.string.value_required))
            }
            inputLayout(binding.addPPMMinInput) {
                isNotEmpty().description(getString(R.string.value_required))
            }
            inputLayout(binding.addPPMMaxInput) {
                isNotEmpty().description(getString(R.string.value_required))
            }
            inputLayout(binding.addLightMinInput) {
                isNotEmpty().description(getString(R.string.value_required))
            }
            inputLayout(binding.addLightMaxInput) {
                isNotEmpty().description(getString(R.string.value_required))
            }
            inputLayout(binding.addHumidityMinInput) {
                isNotEmpty().description(getString(R.string.value_required))
            }
            inputLayout(binding.addHumidityMaxInput) {
                isNotEmpty().description(getString(R.string.value_required))
            }
            inputLayout(binding.addTempMinInput) {
                isNotEmpty().description(getString(R.string.value_required))
            }
            inputLayout(binding.addTempMaxInput) {
                isNotEmpty().description(getString(R.string.value_required))
            }


            //if the form is valid:
            submitWith(binding.addPlantBtn) {
                var currentTime = getCurrentDateTime()

                //call add plant method within the view model
                val newPlant = PlantsEntity(
                    0,
                    binding.addPlantTypeDropdown.editText?.text.toString(),
                    binding.addPlantNickname.editText?.text.toString(),
                    binding.addTempMinInput.editText?.text.toString().toDouble(),
                    binding.addTempMaxInput.editText?.text.toString().toDouble(),
                    binding.addPhMinInput.editText?.text.toString().toDouble(),
                    binding.addPhMaxInput.editText?.text.toString().toDouble(),
                    binding.addPPMMinInput.editText?.text.toString().toDouble(),
                    binding.addPPMMaxInput.editText?.text.toString().toDouble(),
                    binding.addLightMinInput.editText?.text.toString().toDouble(),
                    binding.addLightMaxInput.editText?.text.toString().toDouble(),
                    binding.addHumidityMinInput.editText?.text.toString().toDouble(),
                    binding.addHumidityMaxInput.editText?.text.toString().toDouble(),
                    binding.addPlantPhase.editText?.text.toString(),
                    currentTime,
                    currentTime,
                    null,
                    binding.addImageName.editText?.text.toString(),
                    navigationArgs.bucketId.toInt(),
                    navigationArgs.userId.toInt()
                )

                plantViewModel.setCurrentPlant(newPlant)
                bucketViewModel.getBucket(navigationArgs.bucketId).observe(viewLifecycleOwner, Observer { bucket ->
                        plantViewModel.setCurrentBucket(bucket)
                        plantViewModel.getPlants(bucket.bucketId).observe(viewLifecycleOwner, Observer { plants ->
                                plantViewModel.setcurrentPlantList(plants)

                                if (plantViewModel.currentPlantList.value != null) {
                                    var count = plantViewModel.currentPlantList.value!!.count()
                                    if (count <= 5) {
                                        if (checkThresholds(bucket, count, newPlant)) {
                                            plantViewModel.addPlant(
                                                newPlant,
                                                bucket,
                                                plantViewModel.currentPlantList.value!!
                                            )
                                            // action_addPlantFragment_to_viewPlantListFragment
                                            val action =
                                                AddPlantFragmentDirections.actionAddPlantFragmentToViewPlantListFragment(
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

                                    } else {
                                        alert(
                                            activity,
                                            "Error",
                                            "This bucket is already full. Please put this plant into a new bucket or remove any old plants."
                                        )
                                    }
                                }
                            })
                    })
            }
        }

        binding.addImgBtn.setOnClickListener {
            //TODO: insert code here to upload an image from the user's local storage
        }

        binding.addPlantTypeBtn.setOnClickListener {
            //nav to add plant type fragment
            findNavController().navigate(R.id.action_addPlantFragment_to_createPlantTypeFragment)
        }

        return binding.root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /**
     * Method to check if the plant being added/updated willworkwith the bucket's threshold

    fun checkThresholds(bucket: BucketEntity, numPlants: Int, plant: PlantsEntity) : Boolean {
    if (numPlants == 0){
    //return true if bucket has no plants - no errors can be caused
    return true
    }
    //return false if any min/max values will conflict
    return !(plant.phMin >= bucket.phMax ||
    plant.phMax <= bucket.phMin ||
    plant.ppmMin >= bucket.ppmMax ||
    plant.ppmMax <= bucket.ppmMin ||
    plant.temperatureMin >= bucket.temperatureMax ||
    plant.temperatureMax <= bucket.temperatureMin ||
    plant.lightMin >= bucket.lightMax ||
    plant.lightMax <= bucket.lightMin ||
    plant.humidityMin >= bucket.humidityMax ||
    plant.humidityMax <= bucket.humidityMin)

    //otherwise return true, no conflicts
    return true
    }*/

    private fun getPlantTypes() {
        plantTypeViewModel.readAllData.observe(viewLifecycleOwner, Observer { types ->
            plantTypeList.clear()
            for (type in types) {
                plantTypeList.add(type.plantTypeName)
            }
        })

        val plantTypeAdapter =
            ArrayAdapter(requireContext(), R.layout.plant_type_dropdown, plantTypeList)
        binding.addPlantTypeDropdownItem.setAdapter(plantTypeAdapter)
    }


    //keeping default plant types as an API call, as this can be a long list of items eventually
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
                    val plantTypeAdapter =
                        ArrayAdapter(requireContext(), R.layout.plant_type_dropdown, plantTypeList)
                    binding.addPlantTypeDropdownItem.setAdapter(plantTypeAdapter)
                } else {
                    Log.i("GET FAILURE", "No plant types exist")
                }
            }

            override fun onFailure(call: Call<List<PlantTypeEntity>>?, t: Throwable?) {
                Log.i("GET ERROR", "Something went wrong")

            }
        })
    }

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
                    binding.addTempMinInput.editText?.setText(plant?.temperatureMin.toString())
                    binding.addTempMaxInput.editText?.setText(plant?.temperatureMax.toString())
                    binding.addPhMinInput.editText?.setText(plant?.phMin.toString())
                    binding.addPhMaxInput.editText?.setText(plant?.phMax.toString())
                    binding.addPPMMinInput.editText?.setText(plant?.ppmMin.toString())
                    binding.addPPMMaxInput.editText?.setText(plant?.ppmMax.toString())
                    binding.addLightMinInput.editText?.setText(plant?.lightMin.toString())
                    binding.addLightMaxInput.editText?.setText(plant?.lightMax.toString())
                    binding.addHumidityMinInput.editText?.setText(plant?.humidityMin.toString())
                    binding.addHumidityMaxInput.editText?.setText(plant?.humidityMax.toString())
                } else {
                    Log.i("GET FAILURE", "Plant type does not exist")
                }
            }

            override fun onFailure(call: Call<PlantTypeEntity>?, t: Throwable?) {
                Log.i("GET ERROR", "Something went wrong")

            }
        })
    }
}