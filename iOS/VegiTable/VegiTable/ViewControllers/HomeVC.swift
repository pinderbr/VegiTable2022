//
//  HomeVC.swift
//  VegiTable
//

import UIKit
import SideMenu

class HomeVC: UIViewController {

    @IBOutlet weak var notificationLbl: UILabel!
    @IBOutlet weak var dateLbl: UILabel!
    @IBOutlet weak var typeLbl: UILabel!
    @IBOutlet weak var phValueLbl: UILabel!
    @IBOutlet weak var lightValueLbl: UILabel!
    @IBOutlet weak var waterLevelLbl: UILabel!
    @IBOutlet weak var temperatureValueLbl: UILabel!
    @IBOutlet weak var greenhouseImage: UIImageView!
    @IBOutlet weak var ppmValueLbl: UILabel!
    @IBOutlet weak var notificationContainer: UIStackView!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        notificationContainer.layer.borderWidth = 2
        notificationContainer.layer.borderColor = UIColor.init(named: "VT_InputPlaceholder")!.cgColor
    }

}
