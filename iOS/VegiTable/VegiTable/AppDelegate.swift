//
//  AppDelegate.swift
//  VegiTable
//
//  Created by  on 3/23/21.
//

import UIKit
import CoreData 

@main
class AppDelegate: UIResponder, UIApplicationDelegate {

//Objects/arrays of objects to hold data from either the local db or the API response
    
    var user : UserModel = UserModel.init()
    //var device : Device = Device.init()
    //var plants
    
    //make API request to login
    func userLogin(){
        //1. make call to the API to log the user in, returning a user in the response
        
        //if found:
        //2.populate the user object with the response object
        
        //user.initializeUserObj(id: <#T##Int#>, email: <#T##String#>, password: <#T##String#>, firstName: <#T##String#>, lastName: <#T##String#>, image: <#T##String#>, createTime: <#T##String#>, updateTime: <#T##String#>, archiveTime: <#T##String#>)
        
        //if not: return error
        
        //********FOR NOW THIS ISNT IMPLEMENTED, USER ID WILL BE HARDCODED*****
        
    }
    
    //function to check if the lastUpdateDateTime field in local db is the same as remote
    //for now has it as  string
    //if returns true, wherever this method was called will have else -> make API call via one of the methods below
    func tableNeedsUpdate(time: String) -> Bool{
        //make call to return lastupdatetime of remote db
        let remoteDatabaseTime = "response value for time"

        if remoteDatabaseTime != time {
            return false
        }
        
        return true
    }
    
    func populateUserObject(userResponse : User){
        //pass in the response from API with user data and set into User object
        user.userId = userResponse.userId
        user.userPassword = userResponse.userPassword
        user.userFirstName = userResponse.userFirstName
        user.userLastName = userResponse.userLastName
        user.userEmail = userResponse.userEmail
        user.imageURL = userResponse.imageUrl
        user.createDateTime = userResponse.createDateTime
        user.lastUpdateDateTime = userResponse.lastUpdateDateTime
        user.archiveDateTime = userResponse.archiveDateTime
    }
    
    
    //method to make a request to the API if the local db is not up to date
    func refreshUserData(userId: Int){
        
        //1. make call to API to get user data
        user.getUser(id: userId, url: ApiCalls.getUser)
        
        //2. update local db
        
        //3. update user object values
        //populateUserObject(userResponse: retrievedUser)
    }
    
    
    

    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?) -> Bool {
        // Override point for customization after application launch.
        return true
    }

    // MARK: UISceneSession Lifecycle

    func application(_ application: UIApplication, configurationForConnecting connectingSceneSession: UISceneSession, options: UIScene.ConnectionOptions) -> UISceneConfiguration {
        // Called when a new scene session is being created.
        // Use this method to select a configuration to create the new scene with.
        return UISceneConfiguration(name: "Default Configuration", sessionRole: connectingSceneSession.role)
    }

    func application(_ application: UIApplication, didDiscardSceneSessions sceneSessions: Set<UISceneSession>) {
        // Called when the user discards a scene session.
        // If any sessions were discarded while the application was not running, this will be called shortly after application:didFinishLaunchingWithOptions.
        // Use this method to release any resources that were specific to the discarded scenes, as they will not return.
    }

    // MARK: - Core Data stack

    lazy var persistentContainer: NSPersistentContainer = {
        /*
         The persistent container for the application. This implementation
         creates and returns a container, having loaded the store for the
         application to it. This property is optional since there are legitimate
         error conditions that could cause the creation of the store to fail.
        */
        let container = NSPersistentContainer(name: "VegiTable")
        container.loadPersistentStores(completionHandler: { (storeDescription, error) in
            if let error = error as NSError? {
                // Replace this implementation with code to handle the error appropriately.
                // fatalError() causes the application to generate a crash log and terminate. You should not use this function in a shipping application, although it may be useful during development.
                 
                /*
                 Typical reasons for an error here include:
                 * The parent directory does not exist, cannot be created, or disallows writing.
                 * The persistent store is not accessible, due to permissions or data protection when the device is locked.
                 * The device is out of space.
                 * The store could not be migrated to the current model version.
                 Check the error message to determine what the actual problem was.
                 */
                fatalError("Unresolved error \(error), \(error.userInfo)")
            }
        })
        return container
    }()

    // MARK: - Core Data Saving support

    func saveContext () {
        let context = persistentContainer.viewContext
        if context.hasChanges {
            do {
                try context.save()
            } catch {
                // Replace this implementation with code to handle the error appropriately.
                // fatalError() causes the application to generate a crash log and terminate. You should not use this function in a shipping application, although it may be useful during development.
                let nserror = error as NSError
                fatalError("Unresolved error \(nserror), \(nserror.userInfo)")
            }
        }
    }

}

