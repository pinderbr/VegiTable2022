//
//  ViewPlantVC.swift
//  VegiTable
//

import UIKit

extension ViewPlantVC: Downloadable {
    func didReceiveData(data: Any) {
        DispatchQueue.main.sync {
            setSliderValues(sensor: data as! DeviceReading, plant: data as! Plant)
        }
    }
    
    func setSliderValues(sensor: DeviceReading?, plant: Plant?){
        guard let _ = plant else {
            return
        }
        //update and set all values
        phMinLbl.text = String(plant!.phMax)
        phMaxLbl.text = String(plant!.phMax)
        phSlider.minimumValue = Float(plant!.phMin)
        phSlider.maximumValue = Float(plant!.phMax)
        phSlider.setValue(Float(sensor!.phValue), animated: true)
        
        ppmMinLbl.text = String(plant!.ppmMin)
        ppmMaxLbl.text = String(plant!.ppmMax)
        ppmSlider.minimumValue = Float(plant!.ppmMin)
        ppmSlider.maximumValue = Float(plant!.ppmMax)
        ppmSlider.setValue(Float(sensor!.ppmValue), animated: true)
        
        lumenMinLbl.text = String(plant!.lightMin)
        lumenMaxLbl.text = String(plant!.lightMax)
        lumenSlider.minimumValue = Float(plant!.lightMin)
        lumenSlider.maximumValue = Float(plant!.lightMax)
        lumenSlider.setValue(Float(sensor!.lightValue), animated: true)
        
        tempMinLbl.text = String(plant!.temperatureMin)
        tempMaxLbl.text = String(plant!.temperatureMax)
        temperatureSlider.minimumValue = Float(plant!.temperatureMin)
        temperatureSlider.maximumValue = Float(plant!.temperatureMax)
        temperatureSlider.setValue(Float(sensor!.temperatureValue), animated: true)
        
        humMinLbl.text = String(plant!.humidityMin)
        humMaxLbl.text = String(plant!.humidityMax)
        humiditySlider.minimumValue = Float(plant!.humidityMin)
        humiditySlider.maximumValue = Float(plant!.humidityMax)
        humiditySlider.setValue(Float(sensor!.humidityValue), animated: true)
        
        growthPhaseValue.text = plant!.plantPhase
        
    }
}

class ViewPlantVC: UIViewController {

    @IBOutlet weak var plantNameLbl: UILabel!
    @IBOutlet weak var plantTypeLbl: UILabel!
    @IBOutlet weak var plantImgIcon: UIImageView!
    @IBOutlet weak var plantImg: UIImageView!
    @IBOutlet weak var growthPhaseLbl: UILabel!
    @IBOutlet weak var growthPhaseValue: UILabel!
    @IBOutlet weak var datePlantedLbl: UILabel!
    @IBOutlet weak var datePlantedValue: UILabel!
    @IBOutlet weak var phLbl: UILabel!
    @IBOutlet weak var phMinLbl: UILabel!
    @IBOutlet weak var phMaxLbl: UILabel!
    @IBOutlet weak var phSlider: UISlider!
    @IBOutlet weak var ppmLbl: UILabel!
    @IBOutlet weak var ppmMinLbl: UILabel!
    @IBOutlet weak var ppmMaxLbl: UILabel!
    @IBOutlet weak var ppmSlider: UISlider!
    @IBOutlet weak var lumenLbl: UILabel!
    @IBOutlet weak var lumenMinLbl: UILabel!
    @IBOutlet weak var lumenMaxLbl: UILabel!
    @IBOutlet weak var lumenSlider: UISlider!
    @IBOutlet weak var temperatureLbl: UILabel!
    @IBOutlet weak var tempMinLbl: UILabel!
    @IBOutlet weak var tempMaxLbl: UILabel!
    @IBOutlet weak var temperatureSlider: UISlider!
    @IBOutlet weak var humidityLbl: UILabel!
    @IBOutlet weak var humMinLbl: UILabel!
    @IBOutlet weak var humMaxLbl: UILabel!
    @IBOutlet weak var humiditySlider: UISlider!
    
    override func viewDidLoad() {
        super.viewDidLoad()

        let plantModel = PlantsModel()
        plantModel.delegate = self
        
        plantModel.getPlantsByBucket(id: 35, url: ApiCalls.getPlant)
        
    }
}
