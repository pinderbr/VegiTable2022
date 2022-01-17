//
//  ArchivedPlantsCollectionViewCell.swift
//  VegiTable
//
//  Created by  on 3/31/21.
//

import UIKit

class ArchivedPlantsCollectionViewCell: UICollectionViewCell {
    

    @IBOutlet weak var lblPlantNameType: UILabel!
    @IBOutlet weak var lblPhValues: UILabel!
    @IBOutlet weak var lblTemperatureValues: UILabel!
    @IBOutlet weak var lblPPMValues: UILabel!
    @IBOutlet weak var lblLightValues: UILabel!
    
    func cellSetup() {
        lblPlantNameType.text = "Plant name - Type"
        lblPhValues.text = "pH: min - max"
        lblPPMValues.text = "PPM: min - max"
        lblTemperatureValues.text = "Temp.: min - max"
        lblLightValues.text = "Light: min - max"
    }
    
}
