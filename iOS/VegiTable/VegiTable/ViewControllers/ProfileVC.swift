//
//  ProfileVC.swift
//  VegiTable
//
//  Created by  on 3/27/21.
//

import UIKit
import SideMenu

//MARTHA: to accessapp delegate
let mainDelegate = UIApplication.shared.delegate as! AppDelegate


// implemented to conform to the Downloadable protocol

extension ProfileVC: Downloadable {
    func didReceiveData(data: Any) {
        DispatchQueue.main.sync {
            setUserValues(currentUser: data as? User)
        }
    }
    
    // Once API has returned the users' data fill out the form
    //MARTHA: not sure,but maybe here is where we call on a method in app delegate to save these values to the object
    func setUserValues(currentUser: User?) {
        guard let _ = currentUser else {
            return
        }

        //MARTHA: MOVE THIS INTO VIEWDIDLOAD and take values from user object not currentUser
        // set text in the profile up top
        profileName.text = currentUser!.userFirstName + " " + currentUser!.userLastName
        profileEmail.text = currentUser!.userEmail
        
        // set values in textfields
        inputName.text = currentUser!.userFirstName + " " + currentUser!.userLastName
        inputEmail.text = currentUser!.userEmail
        inputPassword.text = currentUser!.userPassword
        inputConfirmPswd.text = currentUser!.userPassword
    }
}

class ProfileVC: UIViewController, UITextFieldDelegate {
    
    // used for api functions
    let model = UserModel()
    
    var bottomBorder = CALayer()
    
    @IBOutlet weak var profileName: UILabel!
    @IBOutlet weak var profileEmail: UILabel!
    
    
    //creating objects for each element
    @IBOutlet weak var nameContainer: UIStackView!
    @IBOutlet weak var lblName: UILabel!
    @IBOutlet weak var inputName: UITextField!
    
    @IBOutlet weak var emailContainer: UIStackView!
    @IBOutlet weak var lblEmail: UILabel!
    @IBOutlet weak var inputEmail: UITextField!
        
    @IBOutlet weak var passwordContainer: UIView!
    @IBOutlet weak var btnTogglePassword: UIButton!
    @IBOutlet weak var lblPassword: UILabel!
    @IBOutlet weak var inputPassword: UITextField!
    var showPassword = false
    
    @IBOutlet weak var confirmPswdContainer: UIView!
    @IBOutlet weak var btnToggleConfirmPswd: UIButton!
    @IBOutlet weak var lblConfirmPswd: UILabel!
    @IBOutlet weak var inputConfirmPswd: UITextField!
    var showConfirmPassword = false

    override func viewDidLoad() {
        super.viewDidLoad()
        hideKeyboard()
        
        // for API queries
        model.delegate = self
        
        let inputArr: [UITextField] = [inputName, inputEmail, inputPassword, inputConfirmPswd]
        for input: UITextField in inputArr {
            // add delegate so we can use the textFieldDidStartEditing() & textFieldDidEndEditing()
            input.delegate = self
            // update placeholders to change color of the text
            input.attributedPlaceholder = NSAttributedString(string: input.placeholder!, attributes: [NSAttributedString.Key.foregroundColor: UIColor.init(named: "VT_InputPlaceholder")!])
        }
        
        // get data for current user
        model.getUser(id: 7, url: ApiCalls.getUser)
        
    }
    

    //function changes visual elements of each input container
    func textFieldDidBeginEditing(_ textField: UITextField) {
        // Add purple border to bottom of the input
        let thickness: CGFloat = 2.0
        bottomBorder.frame = CGRect(x: 0, y: 70, width: 300, height: thickness)
        bottomBorder.backgroundColor = UIColor.init(named: "VT_Purple")?.cgColor
        
        // show the input title and lighten the bg color
        switch (textField) {
            case inputName:
                lblName.isHidden = false
                nameContainer.backgroundColor = UIColor.init(named: "VT_InputOnFocus")
                nameContainer.layer.addSublayer(bottomBorder)
            case inputEmail:
                lblEmail.isHidden = false
                emailContainer.backgroundColor = UIColor.init(named: "VT_InputOnFocus")
                emailContainer.layer.addSublayer(bottomBorder)
            case inputPassword:
                lblPassword.isHidden = false
                passwordContainer.backgroundColor = UIColor.init(named: "VT_InputOnFocus")
                passwordContainer.layer.addSublayer(bottomBorder)
            case inputConfirmPswd:
                lblConfirmPswd.isHidden = false
                confirmPswdContainer.backgroundColor = UIColor.init(named: "VT_InputOnFocus")
                confirmPswdContainer.layer.addSublayer(bottomBorder)
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
            case inputName:
                lblName.isHidden = true
                nameContainer.backgroundColor = UIColor.init(named: "VT_InputOffFocus")
            case inputEmail:
                lblEmail.isHidden = true
                emailContainer.backgroundColor = UIColor.init(named: "VT_InputOffFocus")
            case inputPassword:
                lblPassword.isHidden = true
                passwordContainer.backgroundColor = UIColor.init(named: "VT_InputOffFocus")
            case inputConfirmPswd:
                lblConfirmPswd.isHidden = true
                confirmPswdContainer.backgroundColor = UIColor.init(named: "VT_InputOffFocus")
            default:
                print("Unhandled input stopped being edited")
        }
    }
    
    @IBAction func togglePassword() {
        showPassword = onPasswordToggle(showPswd: showPassword, input: inputPassword, btnToggle: btnTogglePassword)
    }
    @IBAction func toggleConfirmPassword() {
        showConfirmPassword = onPasswordToggle(showPswd: showConfirmPassword, input: inputConfirmPswd, btnToggle: btnToggleConfirmPswd)
    }
    
    func onPasswordToggle(showPswd: Bool, input: UITextField, btnToggle: UIButton) -> Bool {
        if(showPswd) { // currently showing password
            input.isSecureTextEntry = true
            btnToggle.setBackgroundImage(UIImage(named: "PasswordEyeInvisible"), for: UIControl.State.normal)
            return false
        } else { // currently hiding password
            input.isSecureTextEntry = false
            btnToggle.setBackgroundImage(UIImage(named: "PasswordEyeVisible"), for: UIControl.State.normal)
            return true
        }
    }
}

// method to hide keyboard when users is finished with it
extension ProfileVC {
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
