//
//  Bucket.swift
//  VegiTable
//
//  Created by  on 4/7/21.
//

import Foundation

enum BucketColNames: String {
    case bucketId = "bucketId"
    case userId_fk = "userId_fk"
    case deviceId_fk = "deviceId_fk"
    case bucketName = "bucketName"
    
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
}

class Bucket {
    var bucketId: Int? // nullable so we dont include it when adding a new bucket
    var userId_fk: Int
    var deviceId_fk: Int
    var bucketName: String
    
    var temperatureMin: Double?// nullable
    var temperatureMax: Double?// nullable
    var phMax: Double?// nullable
    var phMin: Double?// nullable
    var ppmMax: Double?// nullable
    var ppmMin: Double?// nullable
    var lightMax: Double?// nullable
    var lightMin: Double?// nullable
    var humidityMax: Double?// nullable
    var humidityMin: Double?// nullable
    
    var createDateTime: String // default: CURRENT_TIMESTAMP
    var lastUpdateDateTime: String // default: CURRENT_TIMESTAMP
    var archiveDateTime: String?// nullable
    var imageURL: String?// nullable
    
    internal init(bucketId: Int? = nil, userId_fk: Int, deviceId_fk: Int, bucketName: String, temperatureMin: Double? = nil, temperatureMax: Double? = nil, phMax: Double? = nil, phMin: Double? = nil, ppmMax: Double? = nil, ppmMin: Double? = nil, lightMax: Double? = nil, lightMin: Double? = nil, humidityMax: Double? = nil, humidityMin: Double? = nil, createDateTime: String, lastUpdateDateTime: String, archiveDateTime: String? = nil, imageUrl: String? = nil) {
        self.bucketId = bucketId
        self.userId_fk = userId_fk
        self.deviceId_fk = deviceId_fk
        self.bucketName = bucketName
        self.temperatureMin = temperatureMin
        self.temperatureMax = temperatureMax
        self.phMax = phMax
        self.phMin = phMin
        self.ppmMax = ppmMax
        self.ppmMin = ppmMin
        self.lightMax = lightMax
        self.lightMin = lightMin
        self.humidityMax = humidityMax
        self.humidityMin = humidityMin
        self.createDateTime = createDateTime
        self.lastUpdateDateTime = lastUpdateDateTime
        self.archiveDateTime = archiveDateTime
        self.imageURL = imageUrl
    }
}

struct BucketFullData: Decodable {
    var bucket: BucketsTemp
    var reading: DeviceReading
}


struct BucketsTemp: Decodable { // x18 cols
    var bucketId: Int
    var userId_fk: Int
    var deviceId_fk: Int
    var bucketName: String
    var temperatureMin: Double?// nullable
    var temperatureMax: Double?// nullable
    var phMax: Double?// nullable
    var phMin: Double?// nullable
    var ppmMax: Double?// nullable
    var ppmMin: Double?// nullable
    var lightMax: Double?// nullable
    var lightMin: Double?// nullable
    var humidityMax: Double?// nullable
    var humidityMin: Double?// nullable
    var createDateTime: String // default: CURRENT_TIMESTAMP
    var lastUpdateDateTime: String // default: CURRENT_TIMESTAMP
    var archiveDateTime: String?// nullable
    var imageUrl: String?// nullable
}

struct BucketPlantsSet {
    var bucket: BucketsTemp
    var plantList: [Plant]?
}

class BucketsModel {
    // this is from the Downloadable protocol in the Netowrk.swift file
    // has to be nil bc its possible th server returns nothing
    weak var delegate: Downloadable?
    let networkModel = Network()
    
    /*
     * cretes a URLSession request and then passed the response method from the Network API object
     */
    func getBucketByUser(id: Int, url: String) {
        let fullUrl = url + String(id) + "/buckets"
        let request = networkModel.getRequest(url: fullUrl)
        networkModel.response(request: request) { (data) in
            // response is being parsed and decoded into a TestData object
            let bucketList = try! JSONDecoder().decode([BucketsTemp]?.self, from: data) as [BucketsTemp]?            
            // protocol delegation is used to invoke didReceiveData() method from Downloadable protocol in the Network.swift file
            self.delegate?.didReceiveData(data: bucketList! as [BucketsTemp])
        }
    }
    /*
    func getBucketById(id: Int, url: String, deviceId: Int, deviceUrl: String) {
        var bucketList : BucketsTemp?
        var deviceReading: DeviceReading?
        let fullUrl = url + String(id)
        let request = networkModel.getRequest(url: fullUrl)
        networkModel.response(request: request) { (data) in
            // response is being parsed and decoded into a TestData object
            bucketList = try! (JSONDecoder().decode(BucketsTemp?.self, from: data) as BucketsTemp?)!
            // protocol delegation is used to invoke didReceiveData() method from Downloadable protocol in the Network.swift file
            //self.delegate?.didReceiveData(data: bucketList! as [BucketsTemp])
            
        }
        let secondUrl = deviceUrl + String(deviceId)
        let request2 = networkModel.getRequest(url: secondUrl)
        networkModel.response(request: request2) { (data) in
            deviceReading = try! (JSONDecoder().decode(DeviceReading?.self, from: data) as DeviceReading?)!
        }
        let fullBucketData = BucketFullData (bucket: bucketList!, reading: deviceReading!)
        self.delegate?.didReceiveData(data: fullBucketData as Any)
        
        
    }*/
    
    func addBucket(newBucket: [String: Any], url: String) {
        let request = networkModel.postRequest(parameters: newBucket, url: url)
        networkModel.response(request: request) { (data) in
            // response is being parsed and decoded into a TestData object
            let model = try! JSONDecoder().decode(BucketsTemp?.self, from: data) as BucketsTemp?
            // protocol delegation is used to invoke didReceiveData() method from Downloadable protocol in the Network.swift file
            self.delegate?.didReceiveData(data: model! as BucketsTemp)
        }
    }
    
    func updateBucket(newBucket: [String: String], url: String) {
        
        let fullUrl = url + newBucket[BucketColNames.bucketId.rawValue]!
        
        let request = networkModel.putRequest(parameters: newBucket, url: fullUrl)
        networkModel.response(request: request) { (data) in
            // response is being parsed and decoded into a BucketsTemp object
            let model = try! JSONDecoder().decode(BucketsTemp?.self, from: data) as BucketsTemp?
            // protocol delegation is used to invoke didReceiveData() method from Downloadable protocol in the Network.swift file
            self.delegate?.didReceiveData(data: model! as BucketsTemp)
        }
    }
}

