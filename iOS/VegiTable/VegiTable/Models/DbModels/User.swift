//
//  User.swift
//  VegiTable
//
//  Created by  on 4/7/21.
//

import Foundation

struct User: Decodable {
    var userId: Int
    var userEmail: String
    var userPassword: String
    var userFirstName: String
    var userLastName: String
    var imageUrl: String? // nullable
    var createDateTime: String? // default: CURRENT_TIMESTAMP //MARTHA: MAY BE CHANGED TO TIMESTAMP
    var lastUpdateDateTime: String // default: CURRENT_TIMESTAMP //MARTHA: MAY BE CHANGED TO TIMESTAMP
    var archiveDateTime: String? // nullable
}

class UserModel {
    // this is from the Downloadable protocol in the Netowrk.swift file
    // has to be nil bc its possible the server returns nothing
    weak var delegate: Downloadable?
    let networkModel = Network()
    
    var userId: Int?
    var userEmail: String?
    var userPassword: String?
    var userFirstName: String?
    var userLastName: String?
    var imageURL: String?
    var createDateTime: String?
    var lastUpdateDateTime: String?
    var archiveDateTime: String?
    
    func initializeUserObj( id i: Int, email e: String, password p: String, firstName fn: String, lastName ln: String, image img: String, createTime cTime: String, updateTime uTime: String, archiveTime aTime: String){
        userId = i
        userEmail = e
        userPassword = p
        userFirstName = fn
        userLastName = ln
        imageURL = img
        createDateTime = cTime
        lastUpdateDateTime = uTime
        archiveDateTime = aTime
    }
    
    /*
     * creates a URLSession request basedon user's ID and then passes the response method from the Network API object
     */
    func getUser(id: Int, url: String) {
        let fullUrl = url + String(id)
        let request = networkModel.getRequest(url: fullUrl)
        networkModel.response(request: request) { (data) in
            // response is being parsed and decoded into a User object
            let model = try! JSONDecoder().decode(User?.self, from: data) as User?
            // protocol delegation is used to invoke didReceiveData() method from Downloadable protocol in the Network.swift file
            self.delegate?.didReceiveData(data: model! as User)
        }
    }
    
    /*
     * creates a URLSession request based on login credentials and then passes the response method from the Network API object
     */
    func logUserIn(url: String, email: String, password: String) {
        let fullUrl = "\(url)?email=\(email)&password=\(password)"
        print("url request to login: \(fullUrl)")
        let request = networkModel.getRequest(url: fullUrl)
        networkModel.response(request: request) { (data) in
            // response is being parsed and decoded into a User object
            let model = try! JSONDecoder().decode(User?.self, from: data) as User?
            // protocol delegation is used to invoke didReceiveData() method from Downloadable protocol in the Network.swift file
            self.delegate?.didReceiveData(data: model! as User)
        }    }
    
    
}

//MARTHA: to be renamed if used, but this is a User object that will save the user's information so it can be used throughout the application
//will either be pulled fromthe local db, or the API response (determined in an app delegate method)
/*class User2: NSObject {
    var userId: Int?
    var userEmail: String?
    var userPassword: String?
    var userFirstName: String?
    var userLastName: String?
    var imageUrl: String?
    var createDateTime: String?
    var lastUpdateDateTime: String?
    var archiveDateTime: String?
    
    func initializeUserObj( id i: Int, email e: String, password p: String, firstName fn: String, lastName ln: String, image img: String, createTime cTime: String, updateTime uTime: String, archiveTime aTime: String){
        userId = i
        userEmail = e
        userPassword = p
        userFirstName = fn
        userLastName = ln
        imageUrl = img
        createDateTime = cTime
        lastUpdateDateTime = uTime
        archiveDateTime = aTime
    }
    
}
*/
