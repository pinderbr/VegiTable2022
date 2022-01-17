//
//  LoginVC.swift
//  VegiTable
//

import UIKit

// implemented to conform to the Downloadable protocol

extension LoginVC: Downloadable {
    func didReceiveData(data: Any) {
        DispatchQueue.main.sync {
            printUserValues(currentUser: data as? User) //jsut for testing
            //here could be code to set global variables to hold userobject, so it can be used throughout the app
            
        }
    }

    func printUserValues(currentUser: User?) {
        guard let _ = currentUser else {
            return
        }
        print("User logged in was: \(currentUser?.userFirstName) \(currentUser?.userLastName)")
    }
}

class LoginVC: UIViewController, UITextFieldDelegate {
    
    // used for api functions
    let model = UserModel()
    
    // botom border to add to inputs
    var bottomBorder = CALayer()
    
    // all components for email input object
    @IBOutlet weak var inputEmail: UITextField!
    @IBOutlet weak var lblEmail: UILabel!
    @IBOutlet weak var emailContainer: UIStackView!
    var emailAssembly: Assembly!
    
    // all components for password input object
    @IBOutlet weak var inputPassword: UITextField!
    @IBOutlet weak var lblPassword: UILabel!
    @IBOutlet weak var passwordContainer: UIView!
    var passwordAssembly: PasswordAssembly!
    
    @IBOutlet weak var btnTogglePassword: UIButton!
    var showPassword = false
    
    @IBOutlet weak var rememberMeContainer: UIView!
    @IBOutlet weak var chkBoxRememberMe: UIImageView!
    var rememberMe = false
    
    override func viewDidLoad() {
        super.viewDidLoad()
        hideKeyboard()
        
        // for API queries
        model.delegate = self
        
        inputEmail.delegate = self
        inputPassword.delegate = self
        
        // make the placeholder text visible
        inputEmail.attributedPlaceholder = NSAttributedString(string: "Email", attributes: [NSAttributedString.Key.foregroundColor: UIColor.init(named: "VT_InputPlaceholder")!])
        inputPassword.attributedPlaceholder = NSAttributedString(string: "Password", attributes: [NSAttributedString.Key.foregroundColor: UIColor.init(named: "VT_InputPlaceholder")!])
        
        // add tap gesture to remember me View
        let rememberMeTap = UITapGestureRecognizer(target: self, action: #selector(self.onRememberMeClick(_:)))
        rememberMeContainer.isUserInteractionEnabled = true
        rememberMeContainer.addGestureRecognizer(rememberMeTap)
        
        // compile input components into an Assembly object
        emailAssembly = Assembly(container: emailContainer, label: lblEmail, input: inputEmail, width: 300)
        passwordAssembly = PasswordAssembly(container: passwordContainer, label: lblPassword, input: inputPassword, width: 300, button: btnTogglePassword)
        
    }
    
    //on clicking log in button, log the user in and retrive their data
    @IBAction func logIn(sender: UIButton){
        model.logUserIn(url: ApiCalls.loginUser, email: inputEmail.text!, password: inputPassword.text!)
        performSegue(withIdentifier: "LoginToHome", sender: nil)

    }
    
    func textFieldDidBeginEditing(_ textField: UITextField) {
        // show the input title, lighten the bg color, and add bottom border to specified input
        if (textField == inputEmail) {
            onInputClick(emailAssembly)
        } else if (textField == inputPassword) {
            onPasswordClick(passwordAssembly)
        }
    }
    
    func textFieldDidEndEditing(_ textField: UITextField) {
        // remove the purple line at bottom of input
        bottomBorder.removeFromSuperlayer()
        
        // hide the input title and darken the bg color
        if (textField == inputEmail) {
            onInputExit(emailAssembly)
        } else if (textField == inputPassword) {
            onPasswordExit(passwordAssembly)
        }
    }
    
    @IBAction func onPasswordToggle() {
        if(showPassword) { // currently showing password
            showPassword = false
            // hide password and change eye color
            onPasswordToggleOff(passwordAssembly)
        } else { // currently hiding password
            showPassword = true
            // show password and change eye color
            onPasswordToggleOn(passwordAssembly)
        }
    }
    
    
    @objc func onRememberMeClick(_ sender: UITapGestureRecognizer) {
        if(rememberMe) {// currently, remember me
            rememberMe = false
            chkBoxRememberMe.image = UIImage(named: "CheckBoxDeselected")
        } else { // current, don't remember me
            rememberMe = true
            chkBoxRememberMe.image = UIImage(named: "CheckBoxSelected")
        }
    }

}

// method to hide keyboard when users is finished with it
extension LoginVC {
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
