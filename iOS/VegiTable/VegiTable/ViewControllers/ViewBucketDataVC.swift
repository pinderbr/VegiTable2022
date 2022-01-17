//
//  ViewBucketDataVC.swift
//  VegiTable
//

import UIKit

extension ViewBucketDataVC: Downloadable {
    func didReceiveData(data: Any) {
        DispatchQueue.main.sync {
            //let temp = data as? BucketFullData
            let reading = data as! DeviceReading

            //setSliderValues(bucket: temp?.bucket, sensor: temp?.reading)
            setSliderValues(sensor: reading)
            
        }
    }
    
    func setSliderValues(sensor: DeviceReading?){
        //func setSliderValues(bucket: BucketsTemp?, sensor: DeviceReading?){

        guard let _ = sensor else {
            return
        }
        //update and set all values
        //phMinLbl.text = String((bucket?.phMax)!)
        //phMaxLbl.text = String(bucket!.phMax!)
        //phSlider.minimumValue = Float(bucket!.phMin!)
        //phSlider.maximumValue = Float(bucket!.phMax!)
        //phSlider.setValue(Float(phSlider.minimumValue + 2.2), animated: true)
        phSlider.setValue(Float(sensor!.phValue), animated: true)

        //ppmMinLbl.text = String(bucket!.ppmMin!)
        //ppmMaxLbl.text = String(bucket!.ppmMax!)
        //ppmSlider.minimumValue = Float(bucket!.ppmMin!)
        //ppmSlider.maximumValue = Float(bucket!.ppmMax!)
        ppmSlider.setValue(Float(sensor!.ppmValue), animated: true)
        
        //lumenMinLbl.text = String(bucket!.lightMin!)
        //lumenMaxLbl.text = String(bucket!.lightMax!)
        //lumenSlider.minimumValue = Float(bucket!.lightMin!)
        //lumenSlider.maximumValue = Float(bucket!.lightMax!)
        lumenSlider.setValue(Float(sensor!.lightValue), animated: true)
        
        //tempMinLbl.text = String(bucket!.temperatureMin!)
       // tempMaxLbl.text = String(bucket!.temperatureMax!)
        //temperatureSlider.minimumValue = Float(bucket!.temperatureMin!)
       // temperatureSlider.maximumValue = Float(bucket!.temperatureMax!)
        temperatureSlider.setValue(Float(sensor!.temperatureValue), animated: true)
        
       // humMinLbl.text = String(bucket!.humidityMin!)
       // humMaxLbl.text = String(bucket!.humidityMax!)
       // humiditySlider.minimumValue = Float(bucket!.humidityMin!)
       // humiditySlider.maximumValue = Float(bucket!.humidityMax!)
        humiditySlider.setValue(Float(sensor!.humidityValue), animated: true)
        
    }
}

class ViewBucketDataVC: UIViewController {

    @IBOutlet var bucketVC: UIView!
    @IBOutlet weak var bucketNameLbl: UILabel!
    @IBOutlet weak var bucketImgIcon: UIImageView!
    @IBOutlet weak var bucketImg: UIImageView!
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
    
   // let bucketModel = BucketsModel()
    let model = DeviceReadingsModel()
    //var currentBucketId: Int = 0

    override func viewDidLoad() {
        super.viewDidLoad()
        model.delegate = self
        
        //bucketModel.getBucketById(id: 1, url: ApiCalls.getBucketById, deviceId: 2, deviceUrl: ApiCalls.getDeviceReadingCurrent)
        
        model.getLatestReading(id: 1, url: ApiCalls.getDeviceReadingCurrent)
    }

}
