//
//  Plant.swift
//  VegiTable
//
//  Created by  on 4/7/21.
//

import Foundation

enum PlantColNames: String {
    case plantId = "plantId"
    case plantType = "plantType"
    case plantName = "plantName"
    case plantPhase = "plantPhase"
    
    case temperatureMax = "temperatureMax"
    case temperatureMin = "temperatureMin"
    case phMax = "phMax"
    case phMin = "phMin"
    case ppmMax = "ppmMax"
    case ppmMin = "ppmMin"
    case lightMax = "lightMax"
    case lightMin = "lightMin"
    case humidityMax = "humidityMax"
    case humidityMin = "humidityMin"
    
    case createDateTime = "createDateTime"
    case lastUpdateDateTime = "lastUpdateDateTime"
    case archiveDateTime = "archiveDateTime"
    case imageURL = "imageURL"
    
    case bucketId_fk = "bucketId_fk"
}

struct Plant: Decodable {
    var plantId: Int
    var bucketId_fk: Int
    var plantName: String
    var plantType: String
    var plantPhase: String
    var temperatureMin: Double
    var temperatureMax: Double
    var phMax: Double
    var phMin: Double
    var ppmMax: Double
    var ppmMin: Double
    var lightMax: Double
    var lightMin: Double
    var humidityMax: Double
    var humidityMin: Double
    var imageURL: String? // nullable
    var createDateTime: String? // nullable, default: CURRENT_TIMESTAMP
    var lastUpdateDateTime: String // default: CURRENT_TIMESTAMP
    var archiveDateTime: String? // nullable,
}

class PlantsModel {
    // this is from the Downloadable protocol in the Netowrk.swift file
    // has to be nil bc its possible th server returns nothing
    weak var delegate: Downloadable?
    let networkModel = Network()
    
    /*
     * cretes a URLSession request and then passed the response method from the Network API object
     */
    func getPlantsByBucket(id: Int, url: String) {
        let fullUrl = url + String(id) + "/plants"
        let request = networkModel.getRequest(url: fullUrl)
        networkModel.response(request: request) { (data) in
            // response is being parsed and decoded into a appropriate object
            let plantList = try! JSONDecoder().decode([Plant]?.self, from: data) as [Plant]?
            // protocol delegation is used to invoke didReceiveData() method from Downloadable protocol in the Network.swift file
            self.delegate?.didReceiveData(data: plantList! as [Plant])
        }
    }
    
    func createPlant(newPlant: [String: Any], url: String) {
        let request = networkModel.postRequest(parameters: newPlant, url: url)
        networkModel.response(request: request) { (data) in
            // response is being parsed and decoded into a TestData object
            let model = try! JSONDecoder().decode(Plant?.self, from: data) as Plant?
            // protocol delegation is used to invoke didReceiveData() method from Downloadable protocol in the Network.swift file
            self.delegate?.didReceiveData(data: model! as Plant)
        }
    }
}
