//
//  EditPlantVC.swift
//  VegiTable
//

import UIKit

class EditPlantVC: UIViewController, UITextFieldDelegate {

    @IBOutlet weak var humidityMinContainer: UIStackView!
    @IBOutlet weak var lblHumidityMin: UILabel!
    @IBOutlet weak var inputHumidityMin: UITextField!
    var humidityMinAssembly: Assembly!
    
    @IBOutlet weak var humidityMaxContainer: UIStackView!
    @IBOutlet weak var lblHumidityMax: UILabel!
    @IBOutlet weak var inputHumidityMax: UITextField!
    var humidityMaxAssembly: Assembly!
    
    @IBOutlet weak var temperatureMinContainer: UIStackView!
    @IBOutlet weak var lblTemperatureMin: UILabel!
    @IBOutlet weak var inputTemperatureMin: UITextField!
    var temperatureMinAssembly: Assembly!
    
    @IBOutlet weak var temperatureMaxContainer: UIStackView!
    @IBOutlet weak var lblTemperatureMax: UILabel!
    @IBOutlet weak var inputTemperatureMax: UITextField!
    var temperatureMaxAssembly: Assembly!
    
    @IBOutlet weak var phMinContainer: UIStackView!
    @IBOutlet weak var lblPHMin: UILabel!
    @IBOutlet weak var inputPHMin: UITextField!
    var phMinAssembly: Assembly!
    
    @IBOutlet weak var phMaxContainer: UIStackView!
    @IBOutlet weak var lblPHMax: UILabel!
    @IBOutlet weak var inputPHMax: UITextField!
    var phMaxAssembly: Assembly!
    
    @IBOutlet weak var ppmMinContainer: UIStackView!
    @IBOutlet weak var lblPPMMin: UILabel!
    @IBOutlet weak var inputPPMMin: UITextField!
    var ppmMinAssembly: Assembly!
    
    @IBOutlet weak var ppmMaxContainer: UIStackView!
    @IBOutlet weak var lblPPMMax: UILabel!
    @IBOutlet weak var inputPPMMax: UITextField!
    var ppmMaxAssembly: Assembly!
    
    @IBOutlet weak var lightMinContainer: UIStackView!
    @IBOutlet weak var lblLightMin: UILabel!
    @IBOutlet weak var inputLightMin: UITextField!
    var lightMinAssembly: Assembly!
    
    @IBOutlet weak var lightMaxContainer: UIStackView!
    @IBOutlet weak var lblLightMax: UILabel!
    @IBOutlet weak var inputLightMax: UITextField!
    var lightMaxAssembly: Assembly!
    
    @IBOutlet weak var plantNameContainer: UIStackView!
    @IBOutlet weak var lblPlantName: UILabel!
    @IBOutlet weak var inputPlantName: UITextField!
    var plantNameAssembly: Assembly!
    
    @IBOutlet weak var growthPhaseContainer: UIStackView!
    @IBOutlet weak var lblGrowthPhase: UILabel!
    @IBOutlet weak var inputGrowthPhase: UITextField!
    var growthPhaseAssembly: Assembly!
    
    @IBOutlet weak var bucketContainer: UIStackView!
    @IBOutlet weak var lblBucket: UILabel!
    @IBOutlet weak var inputBucket: UITextField!
    var bucketAssembly: Assembly!
    
    @IBOutlet weak var plantTypeContainer: UIStackView!
    @IBOutlet weak var lblPlantType: UILabel!
    @IBOutlet weak var inputPlantType: UITextField!
    var plantTypeAssembly: Assembly!
    
    @IBOutlet weak var imageContainer: UIStackView!
    @IBOutlet weak var lblImageUrl: UILabel!
    @IBOutlet weak var inputImageUrl: UITextField!
    var imageAssembly: Assembly!
    
    @IBOutlet weak var btnImage: UIButton!
    @IBOutlet weak var btnAddType: UIButton!
    @IBOutlet weak var btnAddPlant: UIButton!
    
    
    override func viewDidLoad() {
        super.viewDidLoad()
        hideKeyboard()

        plantTypeAssembly = Assembly(container: plantTypeContainer, label: lblPlantType, input: inputPlantType, width: 300)
        phMinAssembly = Assembly(container: phMinContainer, label: lblPHMin, input: inputPHMin, width: 140)
        phMaxAssembly = Assembly(container: phMaxContainer, label: lblPHMax, input: inputPHMax, width: 140)
        ppmMinAssembly = Assembly(container: ppmMinContainer, label: lblPPMMin, input: inputPPMMin, width: 140)
        ppmMaxAssembly = Assembly(container: ppmMaxContainer, label: lblPPMMax, input: inputPPMMax, width: 140)
        lightMinAssembly = Assembly(container: lightMinContainer, label: lblLightMin, input: inputLightMin, width: 140)
        lightMaxAssembly = Assembly(container: lightMaxContainer, label: lblLightMax, input: inputLightMax, width: 140)
        humidityMinAssembly = Assembly(container: humidityMinContainer, label: lblHumidityMin, input: inputHumidityMin, width: 140)
        humidityMaxAssembly = Assembly(container: humidityMaxContainer, label: lblHumidityMax, input: inputHumidityMax, width: 140)
        temperatureMinAssembly = Assembly(container: temperatureMinContainer, label: lblTemperatureMin, input: inputTemperatureMin, width: 140)
        temperatureMaxAssembly = Assembly(container: temperatureMaxContainer, label: lblTemperatureMax, input: inputTemperatureMax, width: 140)
        growthPhaseAssembly = Assembly(container: growthPhaseContainer, label: lblGrowthPhase, input: inputGrowthPhase, width: 300)
        plantNameAssembly = Assembly(container: plantNameContainer, label: lblPlantName, input: inputPlantName, width: 300)
        bucketAssembly = Assembly(container: bucketContainer, label: lblBucket, input: inputBucket, width: 200)
        imageAssembly = Assembly(container: imageContainer, label: lblImageUrl, input: inputImageUrl, width: 200)
        
        let inputArr: [UITextField] = [inputPlantName, inputBucket, inputPlantType, inputPHMin, inputPHMax, inputPPMMin, inputPPMMax, inputHumidityMin, inputHumidityMax, inputLightMin, inputLightMax, inputTemperatureMin, inputTemperatureMax, inputGrowthPhase, inputImageUrl]
        
        for input: UITextField in inputArr{
            // add delegate so we can use the textFieldDidStartEditing() & textFieldDidEndEditing()
            input.delegate = self
            // update placeholders to change color of the text
            input.attributedPlaceholder = NSAttributedString(string: input.placeholder!, attributes: [NSAttributedString.Key.foregroundColor: UIColor.init(named: "VT_InputPlaceholder")!])
        }
    }
    
    func textFieldDidBeginEditing(_ textField: UITextField) {
        /*
        // show the input title and lighten the bg color
        switch (textField) {
            case inputPlantName:
                onInputClick(plantNameAssembly)
            case inputBucket:
                onInputClick(bucketAssembly)
            case inputPlantType:
                onInputClick(plantTypeAssembly)
            case inputPHMin:
                onInputClick(phMinAssembly)
            case inputPHMax:
                onInputClick(phMaxAssembly)
            case inputPPMMin:
                onInputClick(ppmMinAssembly)
            case inputPPMMax:
                onInputClick(ppmMaxAssembly)
            case inputLightMin:
                onInputClick(lightMinAssembly)
            case inputLightMax:
                onInputClick(lightMaxAssembly)
            case inputHumidityMin:
                onInputClick(humidityMinAssembly)
            case inputHumidityMax:
                onInputClick(humidityMaxAssembly)
            case inputTemperatureMin:
                onInputClick(temperatureMinAssembly)
            case inputTemperatureMax:
                onInputClick(temperatureMaxAssembly)
            case inputGrowthPhase:
                onInputClick(growthPhaseAssembly)
            case inputImageUrl:
                onInputClick(imageAssembly)
            default:
                print("Unhandled input statement being edited")
        }
 */
    }
    

    func textFieldDidEndEditing(_ textField: UITextField) {
        // remove the purple line at bottom of input
        bottomBorder.removeFromSuperlayer()
        /*
        // hide the input title and darken the bg color
        switch (textField) {
            case inputPlantType:
                onInputExit(plantTypeAssembly)
            case inputBucket:
                onInputClick(bucketAssembly)
            case inputPlantName:
                onInputClick(plantNameAssembly)
            case inputPHMin:
                onInputExit(phMinAssembly)
            case inputPHMax:
                onInputExit(phMaxAssembly)
            case inputPPMMin:
                onInputExit(ppmMinAssembly)
            case inputPPMMax:
                onInputExit(ppmMaxAssembly)
            case inputLightMin:
                onInputExit(lightMinAssembly)
            case inputLightMax:
                onInputExit(lightMaxAssembly)
            case inputHumidityMin:
                onInputExit(humidityMinAssembly)
            case inputHumidityMax:
                onInputExit(humidityMaxAssembly)
            case inputTemperatureMin:
                onInputExit(temperatureMinAssembly)
            case inputTemperatureMax:
                onInputExit(temperatureMaxAssembly)
            case inputGrowthPhase:
                onInputExit(growthPhaseAssembly)
            case inputImageUrl:
                onInputClick(imageAssembly)
            default:
                print("Unhandled input stopped being edited")
        } */
    }
}

// method to hide keyboard when users is finished with it
extension EditPlantVC {
    //ensuring that tapping on the return key will hide the keyboard when entering input
    func textFieldShouldReturn(_ textField: UITextField) -> Bool {
        return textField.resignFirstResponder()
    }
    
    //function to hide the keyboard when user taps on the screen outside of the keyboard
    func hideKeyboard(){
        self.view.addGestureRecognizer(self.endEditingRecognizer())
    }
    
    //function to help hide keyboard when user taps on screen outside of the keyboard
    func endEditingRecognizer() ->UIGestureRecognizer {
        let tap = UITapGestureRecognizer(target: self.view, action:#selector(self.view.endEditing(_:)))
        tap.cancelsTouchesInView = false
        return tap
    }
}
