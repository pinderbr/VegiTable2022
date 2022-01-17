//
//  Device.swift
//  VegiTable
//
//  Created by  on 4/7/21.
//

import Foundation

enum DeviceColNames: String {
    case deviceId =  "deviceId"// serial number
    case deviceName = "deviceName"
    case createDateTime = "createDateTime" // default: CURRENT_TIMESTAMP
    case lastUpdateDateTime = "lastUpdateDateTime" // default: CURRENT_TIMESTAMP
    case archiveDateTime = "archiveDateTime" // nullable
    case userId_fk = "userId_fk"
}

struct Device: Decodable {
    var deviceId: Int // serial number
    var deviceName: String
    var createDateTime: String // default: CURRENT_TIMESTAMP
    var lastUpdateDateTime: String // default: CURRENT_TIMESTAMP
    var archiveDateTime: String? // nullable
    var userId_fk: Int?
}

class DevicesModel{
    
    weak var delegate: Downloadable?
    let networkModel = Network()
    
    func getDevicesByUser(id: Int, url: String){
        let fullUrl = url + String(id)
        
        let request = networkModel.getRequest(url: fullUrl)
        networkModel.response(request: request){
            (data) in
            
            let deviceList = try!
                JSONDecoder().decode([Device]?.self, from: data) as [Device]?
            
            self.delegate?.didReceiveData(data: deviceList! as [Device])
        }
    }
    
    func removeDeviceFromUser(currentDevice: [String : Any], url: String){
        let fullUrl = url + String(describing: currentDevice[DeviceColNames.deviceId.rawValue]!)
        let empty: [String : Any] = [:]
        //let fullUrl = url + currentDevice[DeviceColNames.deviceId.rawValue]!
        
        let request = networkModel.putRequest(parameters: empty, url: fullUrl)
        networkModel.response(request: request) { (data) in
        
            let model = try! JSONDecoder().decode(Device?.self, from: data) as Device?
            
            self.delegate?.didReceiveData(data: model! as Device)
        }
    }
    
    func addDeviceToUser(deviceId: Int, url: String){
        let fullUrl = url + String(deviceId) + "&user=7"
        
        let empty: [String : Any] = [:]
        
        let request = networkModel.putRequest(parameters: empty, url: fullUrl)
        networkModel.response(request: request) { (data) in
            let model = try! JSONDecoder().decode(Device?.self, from: data) as Device?
            
            self.delegate?.didReceiveData(data: model! as Device)
        }
    }
}
