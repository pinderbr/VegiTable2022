//
//  CreateAccountVC.swift
//  VegiTable
//
//

import UIKit

class CreateAccountVC: UIViewController, UITextFieldDelegate {
    var bottomBorder = CALayer()
    
    //defining objects for each input container
    @IBOutlet weak var nameContainer: UIStackView!
    @IBOutlet weak var lblName: UILabel!
    @IBOutlet weak var inputName: UITextField!
    var nameAssembly: Assembly!
    
    @IBOutlet weak var emailContainer: UIStackView!
    @IBOutlet weak var lblEmail: UILabel!
    @IBOutlet weak var inputEmail: UITextField!
    var emailAssembly: Assembly!
        
    @IBOutlet weak var passwordContainer: UIView!
    @IBOutlet weak var btnTogglePassword: UIButton!
    @IBOutlet weak var lblPassword: UILabel!
    @IBOutlet weak var inputPassword: UITextField!
    var showPassword = false
    var passwordAssembly: PasswordAssembly!
    
    @IBOutlet weak var confirmPswdContainer: UIView!
    @IBOutlet weak var btnToggleConfirmPswd: UIButton!
    @IBOutlet weak var lblConfirmPswd: UILabel!
    @IBOutlet weak var inputConfirmPswd: UITextField!
    var showConfirmPassword = false
    var confirmPswdAssembly: PasswordAssembly!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        hideKeyboard()
        
        let inputArr: [UITextField] = [inputName, inputEmail, inputPassword, inputConfirmPswd]
        for input: UITextField in inputArr {
            // add delegate so we can use the textFieldDidStartEditing() & textFieldDidEndEditing()
            input.delegate = self
            // update placeholders to change color of the text
            input.attributedPlaceholder = NSAttributedString(string: input.placeholder!, attributes: [NSAttributedString.Key.foregroundColor: UIColor.init(named: "VT_InputPlaceholder")!])
        }
        //initializing container objects to easily pass to textField methods
        nameAssembly = Assembly(container: nameContainer, label: lblName, input: inputName, width: 300)
        emailAssembly = Assembly(container: emailContainer, label: lblEmail, input: inputEmail, width: 300)
        passwordAssembly = PasswordAssembly(container: passwordContainer, label: lblPassword, input: inputPassword, width: 300, button: btnTogglePassword)
        confirmPswdAssembly = PasswordAssembly(container: confirmPswdContainer, label: lblConfirmPswd, input: inputConfirmPswd, width: 300, button: btnToggleConfirmPswd)
        
    }
    
    //changes style elements when clicking on input fields
    func textFieldDidBeginEditing(_ textField: UITextField) {
        // show the input title and lighten the bg color
        switch (textField) {
            case inputName:
                onInputClick(nameAssembly)
            case inputEmail:
                onInputClick(emailAssembly)
            case inputPassword:
                onPasswordClick(passwordAssembly)
            case inputConfirmPswd:
                onPasswordClick(confirmPswdAssembly)
        default:
            print("Unhandled input statement being edited")
        }
    }
    
    //changes style elements back after exiting input
    func textFieldDidEndEditing(_ textField: UITextField) {
        // remove the purple line at bottom of input
        bottomBorder.removeFromSuperlayer()
        
        // hide the input title and darken the bg color
        switch (textField) {
            case inputName:
                onInputExit(nameAssembly)
            case inputEmail:
                onInputExit(emailAssembly)
            case inputPassword:
                onPasswordExit(passwordAssembly)
            case inputConfirmPswd:
                onPasswordExit(confirmPswdAssembly)
            default:
                print("Unhandled input stopped being edited")
        }
    }
    
    @IBAction func togglePassword() {
        if(showPassword) { // currently showing password
            showPassword = false
            onPasswordToggleOff(passwordAssembly)
        } else { // currently hiding password
            showPassword = true
            onPasswordToggleOn(passwordAssembly)
        }
    }
    
    @IBAction func toggleConfirmPassword() {
        if(showPassword) { // currently showing password
            showPassword = false
            onPasswordToggleOff(confirmPswdAssembly)
        } else { // currently hiding password
            showPassword = true
            onPasswordToggleOn(confirmPswdAssembly)
        }
    }
}

// method to hide keyboard when users is finished with it
extension CreateAccountVC {
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
