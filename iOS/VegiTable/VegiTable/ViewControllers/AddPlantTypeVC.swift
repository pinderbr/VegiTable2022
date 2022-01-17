//
//  AddPlant.swift
//  VegiTable
//
//

import UIKit

class AddPlantTypeVC: UIViewController, UITextFieldDelegate {
    
    //creating objects for each input element
    @IBOutlet weak var plantTypeContainer: UIStackView!
    @IBOutlet weak var plantTypeLabel: UILabel!
    @IBOutlet weak var plantTypeInput: UITextField!
    var plantTypeAssembly: Assembly!
    
    @IBOutlet weak var pHMinContainer: UIStackView!
    @IBOutlet weak var pHMinLabel: UILabel!
    @IBOutlet weak var pHMinInput: UITextField!
    var pHMinAssembly: Assembly!
    
    @IBOutlet weak var pHMaxContainer: UIStackView!
    @IBOutlet weak var pHMaxLabel: UILabel!
    @IBOutlet weak var pHMaxInput: UITextField!
    var pHMaxAssembly: Assembly!
    
    @IBOutlet weak var ppmMinContainer: UIStackView!
    @IBOutlet weak var ppmMinLabel: UILabel!
    @IBOutlet weak var ppmMinInput: UITextField!
    var ppmMinAssembly: Assembly!
    
    @IBOutlet weak var ppmMaxContainer: UIStackView!
    @IBOutlet weak var ppmMaxLabel: UILabel!
    @IBOutlet weak var ppmMaxInput: UITextField!
    var ppmMaxAssembly: Assembly!
    
    @IBOutlet weak var lightMinContainer: UIStackView!
    @IBOutlet weak var lightMinLabel: UILabel!
    @IBOutlet weak var lightMinInput: UITextField!
    var lightMinAssembly: Assembly!
    
    @IBOutlet weak var lightMaxContainer: UIStackView!
    @IBOutlet weak var lightMaxLabel: UILabel!
    @IBOutlet weak var lightMaxInput: UITextField!
    var lightMaxAssembly: Assembly!
    
    @IBOutlet weak var humidityMinContainer: UIStackView!
    @IBOutlet weak var humidityMinLabel: UILabel!
    @IBOutlet weak var humidityMinInput: UITextField!
    var humidityMinAssembly: Assembly!
    
    @IBOutlet weak var humidityMaxContainer: UIStackView!
    @IBOutlet weak var humidityMaxLabel: UILabel!
    @IBOutlet weak var humidityMaxInput: UITextField!
    var humidityMaxAssembly: Assembly!
    
    @IBOutlet weak var tempMinContainer: UIStackView!
    @IBOutlet weak var tempMinLabel: UILabel!
    @IBOutlet weak var tempMinInput: UITextField!
    var tempMinAssembly: Assembly!
    
    @IBOutlet weak var tempMaxContainer: UIStackView!
    @IBOutlet weak var tempMaxLabel: UILabel!
    @IBOutlet weak var tempMaxInput: UITextField!
    var tempMaxAssembly: Assembly!
    
    @IBOutlet weak var growthPhaseContainer: UIStackView!
    @IBOutlet weak var growthPhaseLabel: UILabel!
    @IBOutlet weak var growthPhaseInput: UITextField!
    var growthPhaseAssembly: Assembly!
    
    @IBOutlet weak var editBucketButton: UIButton!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        hideKeyboard()

        //initializing "assembly" structs to easily pass data to onClick methods
        plantTypeAssembly = Assembly(container: plantTypeContainer, label: plantTypeLabel, input: plantTypeInput, width: 300)
        pHMinAssembly = Assembly(container: pHMinContainer, label: pHMinLabel, input: pHMinInput, width: 140)
        pHMaxAssembly = Assembly(container: pHMaxContainer, label: pHMaxLabel, input: pHMaxInput, width: 140)
        ppmMinAssembly = Assembly(container: ppmMinContainer, label: ppmMinLabel, input: ppmMinInput, width: 140)
        ppmMaxAssembly = Assembly(container: ppmMaxContainer, label: ppmMaxLabel, input: ppmMaxInput, width: 140)
        lightMinAssembly = Assembly(container: lightMinContainer, label: lightMinLabel, input: lightMinInput, width: 140)
        lightMaxAssembly = Assembly(container: lightMaxContainer, label: lightMaxLabel, input: lightMaxInput, width: 140)
        humidityMinAssembly = Assembly(container: humidityMinContainer, label: humidityMinLabel, input: humidityMinInput, width: 140)
        humidityMaxAssembly = Assembly(container: humidityMaxContainer, label: humidityMinLabel, input: humidityMinInput, width: 140)
        tempMinAssembly = Assembly(container: tempMinContainer, label: tempMinLabel, input: tempMinInput, width: 140)
        tempMaxAssembly = Assembly(container: tempMaxContainer, label: tempMaxLabel, input: tempMaxInput, width: 140)
        growthPhaseAssembly = Assembly(container: growthPhaseContainer, label: growthPhaseLabel, input: growthPhaseInput, width: 300)
        
        let inputArr: [UITextField] = [plantTypeInput, pHMinInput, pHMaxInput, ppmMinInput, ppmMaxInput, humidityMinInput, humidityMaxInput, lightMinInput, lightMaxInput, tempMinInput, tempMaxInput, growthPhaseInput]
        
        for input: UITextField in inputArr {
            // add delegate so we can use the textFieldDidStartEditing() & textFieldDidEndEditing()
            input.delegate = self
            // update placeholders to change color of the text
            input.attributedPlaceholder = NSAttributedString(string: input.placeholder!, attributes: [NSAttributedString.Key.foregroundColor: UIColor.init(named: "VT_InputPlaceholder")!])
        }
    }
    
    //function changes visual elements of each input container
    func textFieldDidBeginEditing(_ textField: UITextField) {
        
        // show the input title and lighten the bg color
        switch (textField) {
            case plantTypeInput:
                onInputClick(plantTypeAssembly)
            case pHMinInput:
                onInputClick(pHMinAssembly)
            case pHMaxInput:
                onInputClick(pHMaxAssembly)
            case ppmMinInput:
                onInputClick(ppmMinAssembly)
            case ppmMaxInput:
                onInputClick(ppmMaxAssembly)
            case lightMinInput:
                onInputClick(lightMinAssembly)
            case lightMaxInput:
                onInputClick(lightMaxAssembly)
            case humidityMinInput:
                onInputClick(humidityMinAssembly)
            case humidityMaxInput:
                onInputClick(humidityMaxAssembly)
            case tempMinInput:
                onInputClick(tempMinAssembly)
            case tempMaxInput:
                onInputClick(tempMaxAssembly)
            case growthPhaseInput:
                onInputClick(growthPhaseAssembly)
            default:
                print("Unhandled input statement being edited")
        }
    }
    
    //changes visual elements back when exiting input
    func textFieldDidEndEditing(_ textField: UITextField) {
        // remove the purple line at bottom of input
        bottomBorder.removeFromSuperlayer()
        
        // hide the input title and darken the bg color
        switch (textField) {
            case plantTypeInput:
                onInputExit(plantTypeAssembly)
            case pHMinInput:
                onInputExit(pHMinAssembly)
            case pHMaxInput:
                onInputExit(pHMaxAssembly)
            case ppmMinInput:
                onInputExit(ppmMinAssembly)
            case ppmMaxInput:
                onInputExit(ppmMaxAssembly)
            case lightMinInput:
                onInputExit(lightMinAssembly)
            case lightMaxInput:
                onInputExit(lightMaxAssembly)
            case humidityMinInput:
                onInputExit(humidityMinAssembly)
            case humidityMaxInput:
                onInputExit(humidityMaxAssembly)
            case tempMinInput:
                onInputExit(tempMinAssembly)
            case tempMaxInput:
                onInputExit(tempMaxAssembly)
            case growthPhaseInput:
                onInputExit(growthPhaseAssembly)
            default:
                print("Unhandled input stopped being edited")
        }
    }

}
// method to hide keyboard when users is finished with it
extension AddPlantTypeVC {
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
