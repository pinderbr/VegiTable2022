package project.softsquad.vegitable.ui.planttype
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.NavigationUI.navigateUp
import com.afollestad.vvalidator.form
import project.softsquad.vegitable.databinding.FragmentCreatePlantTypeBinding
import project.softsquad.vegitable.entity.PlantTypeEntity
import project.softsquad.vegitable.entity.PlantsEntity
import project.softsquad.vegitable.getCurrentDateTime
import project.softsquad.vegitable.ui.plant.AddPlantFragmentArgs
import project.softsquad.vegitable.viewmodel.PlantTypeViewModel
import project.softsquad.vegitable.viewmodel.UserViewModel
import project.softsquad.vegitable.viewmodel.plant.PlantViewModel

/**
 * Author: Martha Czerwik
 * Modified By:
 * DESC: Fragment to hold information to be displayed on the add plant type page. There will be a pre-populated list of plant types (pulled from the API from DefaultPlantTypes table + custom plant types previously made by the user).
 * The user will also have the option to create a new plant type from scratch - this will be handled by this fragment.
 * Once the form is filled out and validated, the new plant type will be added to the list of custom plant types going forward.
 */

class CreatePlantTypeFragment : Fragment() {

    private val userViewModel: UserViewModel by activityViewModels()


    //plant type view model
    private lateinit var plantTypeViewModel: PlantTypeViewModel

    //view binding
    private var _binding: FragmentCreatePlantTypeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentCreatePlantTypeBinding.inflate(inflater, container, false)

        //initialize view model
        plantTypeViewModel = ViewModelProvider(this).get(PlantTypeViewModel::class.java)

        //form validation - commented out temporarily for testing purposes - TODO: uncomment when ready for use
        form {
/*
            inputLayout(binding.addTypeName) {
                isNotEmpty().description(getString(R.string.name_required))
            }
            inputLayout(binding.addTypePhMinInput) {
                isNotEmpty().description(getString(R.string.value_required))
            }
            inputLayout(binding.addTypePhMaxInput) {
                isNotEmpty().description(getString(R.string.value_required))
            }
            inputLayout(binding.addTypePPMMinInput) {
                isNotEmpty().description(getString(R.string.value_required))
            }
            inputLayout(binding.addTypePPMMaxInput) {
                isNotEmpty().description(getString(R.string.value_required))
            }
            inputLayout(binding.addTypeLightMinInput) {
                isNotEmpty().description(getString(R.string.value_required))
            }
            inputLayout(binding.addTypeLightMaxInput) {
                isNotEmpty().description(getString(R.string.value_required))
            }
            inputLayout(binding.addTypeHumidityMinInput) {
                isNotEmpty().description(getString(R.string.value_required))
            }
            inputLayout(binding.addTypeHumidityMaxInput) {
                isNotEmpty().description(getString(R.string.value_required))
            }
            inputLayout(binding.addTypeTempMinInput) {
                isNotEmpty().description(getString(R.string.value_required))
            }
            inputLayout(binding.addTypeTempMaxInput) {
                isNotEmpty().description(getString(R.string.value_required))
            }
*/
            //if the form is valid:
            submitWith(binding.addTypeBtn) {

                userViewModel.readAllData.observe(viewLifecycleOwner, Observer{
                    //call add plant type method within the view model
                    val currentTime = getCurrentDateTime()


                    val newPlantType = PlantTypeEntity(
                        0,
                        binding.addTypeName.editText?.text.toString(),
                        binding.addTypeTempMinInput.editText?.text.toString().toDouble(),
                        binding.addTypeTempMaxInput.editText?.text.toString().toDouble(),
                        binding.addTypePhMinInput.editText?.text.toString().toDouble(),
                        binding.addTypePhMaxInput.editText?.text.toString().toDouble(),
                        binding.addTypePPMMinInput.editText?.text.toString().toDouble(),
                        binding.addTypePPMMaxInput.editText?.text.toString().toDouble(),
                        binding.addTypeLightMinInput.editText?.text.toString().toDouble(),
                        binding.addTypeLightMaxInput.editText?.text.toString().toDouble(),
                        binding.addTypeHumidityMinInput.editText?.text.toString().toDouble(),
                        binding.addTypeHumidityMaxInput.editText?.text.toString().toDouble(),
                        currentTime,
                        currentTime,
                        it.userId.toInt()

                    )

                    plantTypeViewModel.addPlantType(newPlantType)


                    //nav back to the plant
                    findNavController().navigateUp()
                })

            }
        }

        return binding.root
    }
}