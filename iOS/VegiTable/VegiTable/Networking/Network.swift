import Foundation

/*
  * This protocol is in charge of passing the data downloaded to other models/view controllers that are implementing the protocol - This is called Protocol Delegation
 */
protocol Downloadable: class {
    func didReceiveData(data: Any)
}

/*
 * URL end points to the API
 */
enum ApiCalls {
    
    static let apiString: String = "http://vegitableapi-env.eba-pyj572qp.ca-central-1.elasticbeanstalk.com"
    //USER
    static let getUser: String = apiString + "/api/user/"
    static let createUser: String = apiString +  "/api/user"
    static let updateUser: String = apiString + "/api/user/"
    static let loginUser: String = apiString + "/api/user/login"

    
    //BUCKET
    // getBucketByUser: ...api/user/userId/buckets
    static let getBucketByUser: String = apiString + "/api/user/"
    static let getBucketById: String = apiString + "/api/bucket/"
    static let addBucket: String = apiString + "/api/bucket"
    //editBucket: ...api/bucket/bucketId
    static let editBucket: String = apiString + "/api/bucket/"
    
    //PLANT
    //get plant by id
    static let getPlant: String = apiString + "/api/plant/"
    //get plants from specific bucket id: Append -> bucketId/plants
    static let getPlants: String = apiString + "/api/bucket/"
    static let createPlant: String = apiString + "/api/plant"
    static let updatePlant: String = apiString + "/api/plant/"
    
    //DEVICE READINGS
    static let getDeviceReadingCurrent: String = apiString + "/api/deviceReading/"

    //NOTIFICATIONS
    static let getNotificationSettings: String = apiString + "/api/notification/"
    static let createNotificationSettings: String = apiString +  "/api/notification"
    static let updateNotificationSettings: String = apiString +  "/api/notification/"
    
    //DEVICE
    static let getDevices: String = apiString + "/api/devices/"
    static let removeDevice: String = apiString + "/api/device/"
    static let addDevice: String = apiString + "/api/device?device="
    
}

/*
 * Request and response methods that will handle the communication between the app and the API
 */
class Network{
    
    func getRequest(url: String) -> URLRequest {
        var request = URLRequest(url: URL(string: url)!)
        request.setValue("application/x-www-form-urlencoded", forHTTPHeaderField: "Content-Type")
        request.httpMethod = "GET"
        
        return request
    }
    
    func postRequest(parameters: [String: Any], url: String) -> URLRequest {
        var request = URLRequest(url: URL(string: url)!)
        request.setValue("application/x-www-form-urlencoded", forHTTPHeaderField: "Content-Type")
        request.httpMethod = "POST"
        
        request.httpBody = parameters.percentEscaped().data(using: .utf8)
        print("Request: \(request)");
        return request
    }
    
    func putRequest(parameters: [String: Any], url: String) -> URLRequest {
        var request = URLRequest(url: URL(string: url)!)
        request.setValue("application/x-www-form-urlencoded", forHTTPHeaderField: "Content-Type")
        request.httpMethod = "PUT"
        
        request.httpBody = parameters.percentEscaped().data(using: .utf8)
        return request
    }
    
    // takes the request and a completion block as parameters
    // completion block will make sure the data is available when needed
    func response(request: URLRequest, completionBlock: @escaping (Data) -> Void) -> Void {
        let task = URLSession.shared.dataTask(with: request) {
            data, response, error in
            guard let data = data,
                let response = response as? HTTPURLResponse,
                error == nil else {   // check for fundamental networking error
                    print("error", error ?? "Unknown error")
                    return
            }
            guard (200 ... 299) ~= response.statusCode else { //check for http errors
                print("statusCode should be 2xx, but is \(response.statusCode)")
                print("response = \(response)")
                return
            }
            // data will be available for other models that implements the block
            completionBlock(data);
        }
      task.resume()
    }
}

// providing escape and parsing functionality to the URL Session
extension Dictionary {
    func percentEscaped() -> String {
        return map { (key, value) in
            let escapedKey = "\(key)".addingPercentEncoding(withAllowedCharacters: .urlQueryValueAllowed) ?? ""
            let escapedValue = "\(value)".addingPercentEncoding(withAllowedCharacters: .urlQueryValueAllowed) ?? ""
            return escapedKey + "=" + escapedValue
            }
            .joined(separator: "&")
    }
}

// providing escape and parsing functionality to the URL Session
extension CharacterSet {
    static let urlQueryValueAllowed: CharacterSet = {
        let generalDelimitersToEncode = ":#[]@" // does not include "?" or "/" due to RFC 3986 - Section 3.4
        let subDelimitersToEncode = "!$&'()*+,;="
        
        var allowed = CharacterSet.urlQueryAllowed
        allowed.remove(charactersIn: "\(generalDelimitersToEncode)\(subDelimitersToEncode)")
        return allowed
    }()
}
