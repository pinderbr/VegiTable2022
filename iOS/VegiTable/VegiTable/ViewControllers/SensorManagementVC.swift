//
//  SensorManagementVC.swift
//  VegiTable
//

import UIKit


extension SensorManagementVC: Downloadable{
    func didReceiveData(data: Any) {
        DispatchQueue.main.sync{
            if(data is Array<Any>){
            deviceList = data as! [Device]
            deviceCollection.reloadData()
            }
        }
    }
}

class SensorManagementVC: UIViewController, UICollectionViewDataSource, UICollectionViewDelegateFlowLayout{

    @IBOutlet weak var deviceCollection: UICollectionView!
    
    let cellReuseId = "SensorCell"
    let model = DevicesModel()
    let pairPopup = Bundle.main.loadNibNamed("PairSensorView", owner: self, options: nil)?.first as! PairSensorView
    var deviceList: [Device] = []
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        model.delegate = self
        // get buckets for current user
        model.getDevicesByUser(id: 7, url: ApiCalls.getDevices)
        
        deviceCollection.dataSource=self
        deviceCollection.delegate=self
        
        let layout = UICollectionViewFlowLayout()
        
        layout.minimumLineSpacing = 30
        layout.minimumInteritemSpacing = 30

        deviceCollection.collectionViewLayout = layout
        
        deviceCollection.backgroundColor=UIColor.init(named: "VT_BgColor")
        
        // load in popup and hide it
        view.addSubview(pairPopup)
        pairPopup.isHidden = true
        
    }
    
    @IBAction func refreshData() {
        model.getDevicesByUser(id: 7, url: ApiCalls.getDevices)
    }
    
    @IBAction func openPairingPopup() {
        pairPopup.isHidden = false
    }
    
    //number of sections in collectionview
    func numberOfSections(in collectionView: UICollectionView) -> Int {
        return 1
    }
    
    //number of items in collection view
    func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        return deviceList.count
    }
    
    //creates collection view cell
    func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
        let cell = deviceCollection.dequeueReusableCell(withReuseIdentifier: cellReuseId, for: indexPath) as! SensorCollectionViewCell
        cell.layer.borderWidth = 2
        cell.layer.borderColor = UIColor.init(named: "VT_InputPlaceholder")!.cgColor
        
        let index = indexPath.row
        
        if(deviceList.count > 0) {
            cell.deviceName.text = deviceList[index].deviceName
            cell.deleteButton.tag = index
            cell.deleteButton.addTarget(self, action: #selector(deleteDevice), for: .touchUpInside)
        }
        
        return cell
    }
    
    //creates layout size of collection view
    func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, sizeForItemAt indexPath: IndexPath) -> CGSize {
        return CGSize(width: 320, height: 100)
    }

    @objc func deleteDevice(sender: UIButton){
        
        let currentDevice: Device = deviceList[sender.tag]
        
        let formattedDevice: [String : Any] = [
            DeviceColNames.deviceId.rawValue: currentDevice.deviceId,
            DeviceColNames.deviceName.rawValue: currentDevice.deviceName,
            DeviceColNames.createDateTime.rawValue: currentDevice.createDateTime,
            DeviceColNames.lastUpdateDateTime.rawValue: currentDevice.lastUpdateDateTime,
            DeviceColNames.archiveDateTime.rawValue: currentDevice.archiveDateTime,
            DeviceColNames.userId_fk.rawValue: currentDevice.userId_fk
        ]
        model.removeDeviceFromUser(currentDevice: formattedDevice, url: ApiCalls.removeDevice)
    }
    

}
