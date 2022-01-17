//
//  PlantCollectionViewCell.swift
//  VegiTable
//
//  Created by Bryce Pinder on 2021-04-03.
//

import UIKit

class BucketCollectionViewCell: UICollectionViewCell {
    @IBOutlet weak var plantNameLbl: UILabel!
    @IBOutlet weak var plantTypeLbl: UILabel!
    @IBOutlet weak var plantStageLbl: UILabel!
    
    @IBOutlet weak var phValueLbl: UILabel!
    @IBOutlet weak var ppmValueLbl: UILabel!
    @IBOutlet weak var temperatureValueLbl: UILabel!
    @IBOutlet weak var humidityValueLbl: UILabel!
    @IBOutlet weak var lightValueLbl: UILabel!
    
    @IBOutlet weak var editPlantBtn: UIButton!
    @IBOutlet weak var archivePlantBtn: UIButton!
    
}
