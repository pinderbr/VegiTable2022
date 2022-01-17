//
//  DeviceReading.swift
//  VegiTable
//
//  Created by  on 4/7/21.
//

import Foundation

struct DeviceReading: Decodable {
    var deviceReadingId: Int
    var deviceId_fk: Int
    var temperatureValue: Double
    var phValue: Double
    var ppmValue: Double
    var lightValue: Double
    var humidityValue: Double
    var waterValue: Double
    var currentDateTime: String // default: CURRENT_TIMESTAMP
}

class DeviceReadingsModel{
    
    weak var delegate: Downloadable?
    let networkModel = Network()
    
    func getLatestReading(id: Int, url: String){
        let fullUrl = url + String(id)
        
        let request = networkModel.getRequest(url: fullUrl)
        networkModel.response(request: request){
            (data) in
            
            let reading = try!
                JSONDecoder().decode(DeviceReading?.self, from: data) as DeviceReading?
            
            self.delegate?.didReceiveData(data: reading! as DeviceReading)
        }
    }
}
