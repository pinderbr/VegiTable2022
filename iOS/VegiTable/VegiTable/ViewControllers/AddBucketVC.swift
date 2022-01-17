//
//  AddBucketVC.swift
//  VegiTable
//
//

import UIKit

// implemented to conform to the Downloadable protocol
extension AddBucketVC: Downloadable {
    func didReceiveData(data: Any) {
        DispatchQueue.main.sync {
            if(data is Array<Any>){
            deviceList = data as! [Device]
            }
            sensorPicker.reloadAllComponents()
            // bucket returned after its been added
            print(data)
        }
    }
    
    @IBAction func AddBucket() {
        // do form validation
        let selectedValue = deviceList[sensorPicker.selectedRow(inComponent: 0)].deviceId
        let imageUrl = imageInput.text!
        let newBucket: [String: Any] = [
            BucketColNames.userId_fk.rawValue : 7,
            BucketColNames.deviceId_fk.rawValue : Int(selectedValue),
            BucketColNames.bucketName.rawValue : bucketInput.text!,
            BucketColNames.imageURL.rawValue : imageUrl
        ]
        model.addBucket(newBucket: newBucket, url: ApiCalls.addBucket)
    }
}

class AddBucketVC: UIViewController, UIPickerViewDelegate, UIPickerViewDataSource, UITextFieldDelegate{
    
    // used for api functions
    let model = BucketsModel()
    let devices = DevicesModel()

    var bottomBorder = CALayer()
    //creating objects for each element
    @IBOutlet weak var bucketContainer: UIStackView!
    @IBOutlet weak var bucketLabel: UILabel!
    @IBOutlet weak var bucketInput: UITextField!
    
    @IBOutlet weak var imageContainer: UIStackView!
    @IBOutlet weak var imageLabel: UILabel!
    @IBOutlet weak var imageInput: UITextField!
    
    @IBOutlet weak var addImage: UIButton!
    @IBOutlet weak var addBucket: UIButton!
    @IBOutlet weak var sensorPicker: UIPickerView!
    
    
    var deviceList: [Device] = []
    
    override func viewDidLoad() {
        super.viewDidLoad()
        hideKeyboard()
        
        // used for api functions
        model.delegate = self
        devices.delegate = self
        
        devices.getDevicesByUser(id: 7, url: ApiCalls.getDevices)

        // Do any additional setup after loading the view.
        let inputArr: [UITextField] = [bucketInput, imageInput]
        for input: UITextField in inputArr {
            // add delegate so we can use the textFieldDidStartEditing() & textFieldDidEndEditing()
            input.delegate = self
            // update placeholders to change color of the text
            input.attributedPlaceholder = NSAttributedString(string: input.placeholder!, attributes: [NSAttributedString.Key.foregroundColor: UIColor.init(named: "VT_InputPlaceholder")!])
        }
        self.sensorPicker.delegate=self
        self.sensorPicker.dataSource=self
    }
    
    @IBAction func refreshData() {
        devices.getDevicesByUser(id: 7, url: ApiCalls.getDevices)
        sensorPicker.reloadAllComponents()
    }
    
    func textFieldDidBeginEditing(_ textField: UITextField) {
        // Add purple border to bottom of the input
        let thickness: CGFloat = 2.0
        bottomBorder.backgroundColor = UIColor.init(named: "VT_Purple")?.cgColor
        
        // show the input title and lighten the bg color
        switch (textField) {
            case bucketInput:
                bottomBorder.frame = CGRect(x: 0, y: 70, width: 300, height: thickness)
                bucketLabel.isHidden = false
                bucketContainer.backgroundColor = UIColor.init(named: "VT_InputOnFocus")
                bucketContainer.layer.addSublayer(bottomBorder)
        case imageInput:
            bottomBorder.frame = CGRect(x: 0, y: 70, width: 180, height: thickness)
            imageLabel.isHidden = false
            imageContainer.backgroundColor = UIColor.init(named: "VT_InputOnFocus")
            imageContainer.layer.addSublayer(bottomBorder)
        default:
            print("Unhandled input statement being edited")
                
        }
    }
    
    
    
    func textFieldDidEndEditing(_ textField: UITextField) {
        // remove the purple line at bottom of input
        bottomBorder.removeFromSuperlayer()
        
        // hide the input title and darken the bg color
        switch (textField) {
            case bucketInput:
                bucketLabel.isHidden = true
                bucketContainer.backgroundColor = UIColor.init(named: "VT_InputOffFocus")
            case imageInput:
                imageLabel.isHidden = true
                imageContainer.backgroundColor = UIColor.init(named: "VT_InputOffFocus")
            
            default:
                print("Unhandled input stopped being edited")
        }
    }
    
    //number of columns in PickerView
    func numberOfComponents(in pickerView: UIPickerView) -> Int {
        return 1
    }
    
    //number of rows in PickerView based on count of sensorPickerData array
    func pickerView(_ pickerView: UIPickerView, numberOfRowsInComponent component: Int) -> Int {
        return deviceList.count
    }
    
    //each element value in Picker view
    func pickerView(_ pickerView: UIPickerView, titleForRow row: Int, forComponent component: Int) -> String? {
        var rowDevice = 0
        if(deviceList.count > 0){
            rowDevice = deviceList[row].deviceId
        }
        return String(rowDevice)
    }
}
// method to hide keyboard when users is finished with it
extension AddBucketVC {
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
