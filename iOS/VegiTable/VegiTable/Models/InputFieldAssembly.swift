//
//  InputFieldAssembly.swift
//  VegiTable
//
//

import Foundation
import UIKit

var bottomBorder = CALayer()

let thickness: CGFloat = 2.0

//creating struct to easily pass input containers to onInputClick and onExit methods for textFields
struct Assembly{
    let container: UIStackView!
    let label: UILabel!
    let input: UITextField!
    let width: CGFloat
}

//changes style elements of TextField and TextViews within an input container when clicking
func onInputClick(_ assembly: Assembly){
    bottomBorder.backgroundColor = UIColor.init(named: "VT_Purple")?.cgColor
    
    bottomBorder.frame = CGRect(x: 0, y: 70, width: assembly.width, height: thickness)
    assembly.label.isHidden = false
    assembly.container.backgroundColor = UIColor.init(named: "VT_InputOnFocus")
    assembly.container.layer.addSublayer(bottomBorder)
}

//changes style elements when exiting input
func onInputExit(_ assembly: Assembly){
    assembly.label.isHidden = true
    assembly.container.backgroundColor = UIColor.init(named: "VT_InputOffFocus")
}

//struct to easily pass data to onPassword methods
struct PasswordAssembly {
    let container: UIView!
    let label: UILabel!
    let input: UITextField!
    let width: CGFloat
    let button: UIButton!
}

func onPasswordClick(_ passwordAssembly: PasswordAssembly) {
    passwordAssembly.label.isHidden = false
    passwordAssembly.container.backgroundColor = UIColor.init(named: "VT_InputOnFocus")
    passwordAssembly.container.layer.addSublayer(bottomBorder)
}

func onPasswordExit(_ passwordAssembly: PasswordAssembly) {
    passwordAssembly.label.isHidden = true
    passwordAssembly.container.backgroundColor = UIColor.init(named: "VT_InputOffFocus")
}

func onPasswordToggleOn(_ passwordAssembly: PasswordAssembly) {
    passwordAssembly.input.isSecureTextEntry = false
    passwordAssembly.button.setBackgroundImage(UIImage(named: "PasswordEyeVisible"), for: UIControl.State.normal)
}

func onPasswordToggleOff(_ passwordAssembly: PasswordAssembly) {
    passwordAssembly.input.isSecureTextEntry = true
    passwordAssembly.button.setBackgroundImage(UIImage(named: "PasswordEyeInvisible"), for: UIControl.State.normal)
}
