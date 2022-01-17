//
//  ContainerVC.swift
//  VegiTable
//

import UIKit
import SideMenu

class ContainerVC: UIViewController, MenuControllerDelegate, ContainerDelegate {
    
    private var sideMenu: SideMenuNavigationController?
    var rightBarButton = UIBarButtonItem(image: UIImage(systemName: "arrow.backward"), style: .plain, target: .none ,action: #selector(backButtonClicked))
    
    var menuControllers: [UIViewController] = []
    var childControllers: [UIViewController] = []
    @IBOutlet weak var container: UIView!
    var currentChildIndex = 0 // set current as the home controller
    var prevChildIndex = -1
    
    public var delegate: ParentChildDelegate?
    
    override func viewDidLoad() {
        super.viewDidLoad()
        rightBarButton.target = self
        
        // initialize all menu item view controllers
        let homeVC = storyboard?.instantiateViewController(identifier: "HomeVC") as! HomeVC
        let profileVC = storyboard?.instantiateViewController(identifier: "ProfileVC") as! ProfileVC
        let greenhouseVC = storyboard?.instantiateViewController(identifier: "GreenhouseVC") as! GreenhouseVC
        let archivedPlantsVC = storyboard?.instantiateViewController(identifier: "ArchivedPlantsVC") as! ArchivedPlantsVC
        let sensorManagementVC = storyboard?.instantiateViewController(identifier: "SensorManagementVC") as! SensorManagementVC
        let aboutVC = storyboard?.instantiateViewController(identifier: "AboutVC") as! AboutVC
        
        menuControllers = [ homeVC, profileVC, greenhouseVC, archivedPlantsVC, sensorManagementVC, aboutVC ]
        
        // initialize all non-menu view controllers
        let addBucketVC = storyboard?.instantiateViewController(identifier: "AddBucketVC") as! AddBucketVC
        let editBucketVC = storyboard?.instantiateViewController(identifier: "EditBucketVC") as! EditBucketVC
        let viewBucketVC = storyboard?.instantiateViewController(identifier: "ViewBucketVC") as! ViewBucketVC
        let addPlantVC = storyboard?.instantiateViewController(identifier: "AddPlantVC") as! AddPlantVC
        let viewPlantVC = storyboard?.instantiateViewController(identifier: "ViewPlantVC") as! ViewPlantVC
        let viewBucketDataVC = storyboard?.instantiateViewController(identifier: "ViewBucketDataVC") as! ViewBucketDataVC
        let editPlantVC = storyboard?.instantiateViewController(identifier: "EditPlantVC") as! EditPlantVC
        let addPlantTypeVC = storyboard?.instantiateViewController(identifier: "AddPlantTypeVC") as! AddPlantTypeVC
        
        // set delegate so we can access displayNewView() from each viewcontroller
        // need this so we can use buttons in child views (listed below) to change the displayed child view in containerVC
        greenhouseVC.containerDelegate = self
        viewBucketVC.containerDelegate = self

        let menu = MenuController()
        sideMenu = SideMenuNavigationController(rootViewController: menu)
        menu.delegate = self
        sideMenu?.leftSide = true
        SideMenuManager.default.leftMenuNavigationController = sideMenu
        SideMenuManager.default.addPanGestureToPresent(toView: view)
        
        sideMenu?.navigationBar.isHidden = true
        
        childControllers = [ homeVC, profileVC, greenhouseVC, archivedPlantsVC, sensorManagementVC, aboutVC, addBucketVC, editBucketVC, viewBucketVC, addPlantVC, viewPlantVC, viewBucketDataVC, editPlantVC, addPlantTypeVC ]

        addChildControllers()
        updateView(index: 0) // show HomeVC
    }
    
    @objc func backButtonClicked(sender: UIBarButtonItem) {
        // get story baord id of the previous controller
        let storyboardID = childControllers[currentChildIndex].restorationIdentifier
        switch storyboardID {
        case "AddBucketVC", "EditBucketVC", "ViewBucketVC":
            updateView(index: 2) // greenhouse
        case "ViewBucketDataVC", "AddPlantVC", "EditPlantVC", "ViewPlantVC":
            displayNewView(storyboardID: "ViewBucketVC", viewTitle: "View Bucket", data: nil)
        case "AddPlantTypeVC":
            updateView(index: prevChildIndex) // either EditPlant or AddPlant
        default:
            print("missing a acase")
        }
        
    }
    
    @IBAction func onMenuClick() {
        present(sideMenu!, animated: true)
    }
    
    private func addChildControllers() {
        // add each controller as a child
        for controller in childControllers {
            // add the views for each controller
            addChild(controller)

            // set the frames for each controller to fill the entire screen
            view.addSubview(controller.view)

            // call didmove for each controller to specify the life cycle
            // set the controller to be a child of the parent (which is set as itself)
            // this are very important, leading to the viewDidLoad() in individual controllers
            controller.didMove(toParent: self)

            // hide the views
            controller.view.isHidden = true
        }
    }
    
    // for navigation using the side menu
    func updateView(index: Int) {
        // hide the side menu
        sideMenu?.dismiss(animated: true, completion: nil)
        
        if index == -1 {
            // signout
            self.dismiss(animated: true, completion: nil)
            let loginVC = (storyboard?.instantiateViewController(identifier: "LoginNav"))!
            loginVC.modalPresentationStyle = .fullScreen
            self.present(loginVC, animated: true, completion: nil)
        } else {
            self.navigationItem.rightBarButtonItem = nil
            // loop through all controllers and show the selected on and hide the rest
            var i = 0;
            for item in childControllers {
                if i == index {
                    item.view.isHidden = false
                    self.title = menuItems[index].title
                    currentChildIndex = i
                } else {
                    item.view.isHidden = true
                }
                i += 1
            }
        }
    }
    
    // for navigaiton within a menu option (mostly within greenhouse)
    func displayNewView(storyboardID: String, viewTitle: String, data: Any?) {
        prevChildIndex = currentChildIndex
        
        // find the correct view and show it, then hide all the rest
        var index = 0
        for vc in childControllers {
            // show the requested view and hide all others
            if(vc.restorationIdentifier == storyboardID) {
                vc.view.isHidden = false
                self.title = viewTitle
                currentChildIndex = index
            } else {
                vc.view.isHidden = true
            }
            
            var found: Bool = false
            // check if its a menu controller
            for item in menuControllers {
                // hide the back button
                self.navigationItem.rightBarButtonItem = nil
                if(item.restorationIdentifier == storyboardID) {
                    found = true
                }
            }
            if !found {
                // navigating not to a manu item so add a back button
                self.navigationItem.rightBarButtonItem = rightBarButton
            }
            
            // add the search icon if requried
            if (storyboardID == "ViewBucketVC" && vc.restorationIdentifier == storyboardID) {
                if data != nil {
                    let viewBucketVC = vc as! ViewBucketVC
                    viewBucketVC.currentBucketId = data as! Int
                }
                //delegate?.getData(id: data as! Int)
            }
            
            index += 1
        }
    }
}
