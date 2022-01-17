//
//  PairSensorView.swift
//  VegiTable
//
//  Created by  on 4/3/21.
//

import UIKit

extension PairSensorView: Downloadable{
    func didReceiveData(data: Any) {
        DispatchQueue.main.sync{
            
        }
    }
}

class PairSensorView: UIView, UITextFieldDelegate {

    var userId = 7
    @IBOutlet weak var pairBtn: UIButton!
    @IBOutlet weak var lblSerialNumber: UILabel!
    @IBOutlet weak var inputSerialNumber: UITextField!
    @IBOutlet weak var containerSerialNumber: UIStackView!
    var serialNumberAssembly: Assembly!
    
    let model = DevicesModel()
    
    override func awakeFromNib() {
        super.awakeFromNib()
        // add delegate so we can use the textFieldDidStartEditing() & textFieldDidEndEditing()
        inputSerialNumber.delegate = self
        // update placeholders to change color of the text
        inputSerialNumber.attributedPlaceholder = NSAttributedString(string: inputSerialNumber.placeholder!, attributes: [NSAttributedString.Key.foregroundColor: UIColor.init(named: "VT_InputPlaceholder")!])
        
        model.delegate = self
        
        serialNumberAssembly = Assembly(container: containerSerialNumber, label: lblSerialNumber, input: inputSerialNumber, width: 300)
        
        // add swipe gesture to close app when user swipes up
        let onSwipeGesture = UISwipeGestureRecognizer(target: self, action: #selector(closeView(_:)))
        onSwipeGesture.direction = .up
        onSwipeGesture.numberOfTouchesRequired = 1
        self.addGestureRecognizer(onSwipeGesture)
        self.isUserInteractionEnabled = true
        pairBtn.addTarget(self, action: #selector(self.pairSensor), for: .touchUpInside)
    }
    
    @IBAction private func pairSensor() {
        let addingDevice = Int(inputSerialNumber.text!)
        model.addDeviceToUser(deviceId: addingDevice!, url: ApiCalls.addDevice)
        
        self.isHidden = true // hide popup
    }
    
    @objc func closeView(_ gesture: UISwipeGestureRecognizer) {
        self.isHidden = true // hide popup
    }
    
    func textFieldDidBeginEditing(_ textField: UITextField) {
        // update styling for input on click
        onInputClick(serialNumberAssembly)
    }
    
    func textFieldDidEndEditing(_ textField: UITextField) {
        // update styling for input off click
        bottomBorder.removeFromSuperlayer()
        onInputExit(serialNumberAssembly)
    }

}
