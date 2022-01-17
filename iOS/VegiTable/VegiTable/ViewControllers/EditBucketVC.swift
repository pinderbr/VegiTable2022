//
//  EditBucket.swift
//  VegiTable
//
//  Created by  on 4/2/21.
//

import UIKit

// implemented to conform to the Downloadable protocol
extension EditBucketVC: Downloadable {
    func didReceiveData(data: Any) {
        DispatchQueue.main.sync {
            // user returned after its been added
            print(data)
        }
    }
    
    @IBAction func updateBucket() {
        let newBucket: [String: String] = [
            BucketColNames.bucketId.rawValue : "5",
            BucketColNames.userId_fk.rawValue : "1",
            BucketColNames.deviceId_fk.rawValue : "2",
            BucketColNames.bucketName.rawValue : "Lettuce Bucket 11",
            BucketColNames.imageURL.rawValue : ""
        ]
        
        model.updateBucket(newBucket: newBucket, url: ApiCalls.editBucket)
        
    }
}

class EditBucketVC: UIViewController, UIPickerViewDelegate, UIPickerViewDataSource, UITextFieldDelegate{
    
    // used for api functions
    let model = BucketsModel()
    
    var bottomBorder = CALayer()
    //creating objects for each element
    @IBOutlet weak var bucketContainer: UIStackView!
    @IBOutlet weak var bucketLabel: UILabel!
    @IBOutlet weak var bucketInput: UITextField!
    var bucketAssembly: Assembly!
    
    @IBOutlet weak var imageContainer: UIStackView!
    @IBOutlet weak var imageLabel: UILabel!
    @IBOutlet weak var imageInput: UITextField!
    var imageAssembly: Assembly!
    
    
    @IBOutlet weak var addImage: UIButton!
    
    @IBOutlet weak var editBucket: UIButton!
    
    @IBOutlet weak var sensorPicker: UIPickerView!
    
    var sensorPickerData: [String] = [String]()
    
    override func viewDidLoad() {
        super.viewDidLoad()
        hideKeyboard()
        
        //initializing "assembly" structs to easily pass data to onClick methods
        bucketAssembly = Assembly(container: bucketContainer, label: bucketLabel, input: bucketInput, width: 300)
        imageAssembly = Assembly(container: imageContainer, label: imageLabel, input: imageInput, width: 180)
        
        let inputArr: [UITextField] = [bucketInput, imageInput]
        for input: UITextField in inputArr {
            // add delegate so we can use the textFieldDidStartEditing() & textFieldDidEndEditing()
            input.delegate = self
            // update placeholders to change color of the text
            input.attributedPlaceholder = NSAttributedString(string: input.placeholder!, attributes: [NSAttributedString.Key.foregroundColor: UIColor.init(named: "VT_InputPlaceholder")!])
        }
        self.sensorPicker.delegate=self
        self.sensorPicker.dataSource=self
        // used for api functions
        model.delegate = self
        
        sensorPickerData = ["Sensor 1", "Sensor 2"]
        
    }
    
    //function changes visual elements of each input container
    func textFieldDidBeginEditing(_ textField: UITextField) {
        
        // show the input title and lighten the bg color
        switch (textField) {
            case bucketInput:
                onInputClick(bucketAssembly)
            case imageInput:
                onInputClick(imageAssembly)
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
            case bucketInput:
                onInputExit(bucketAssembly)
            case imageInput:
                onInputExit(imageAssembly)
            
            default:
                print("Unhandled input stopped being edited")
        }
    }
    
    //necessary PickerView methods
    func numberOfComponents(in pickerView: UIPickerView) -> Int {
        return 1
    }
    
    func pickerView(_ pickerView: UIPickerView, numberOfRowsInComponent component: Int) -> Int {
        return sensorPickerData.count
    }
    
    func pickerView(_ pickerView: UIPickerView, titleForRow row: Int, forComponent component: Int) -> String? {
        return sensorPickerData[row]
    }

}
// method to hide keyboard when users is finished with it
extension EditBucketVC {
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
