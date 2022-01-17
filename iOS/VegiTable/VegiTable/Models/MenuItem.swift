//
//  MenuItem.swift
//  VegiTable
//
//  Created by  on 3/25/21.
//

import UIKit

struct menuItem {
    let title: String
    let deselectedIcon: UIImage
    let selectedIcon: UIImage
    let needSearchIcon: Bool
}

let menuItems: [menuItem] = [
    menuItem(title: "Home", deselectedIcon: UIImage(named: "Home")!, selectedIcon: UIImage(named: "Home-Purple")!, needSearchIcon: false),
    menuItem(title: "Profile", deselectedIcon: UIImage(named: "Profile")!, selectedIcon: UIImage(named: "Profile-Purple")!, needSearchIcon: false),
    menuItem(title: "Greenhouse", deselectedIcon: UIImage(named: "Greenhouse")!, selectedIcon: UIImage(named: "Greenhouse-Purple")!, needSearchIcon: true),
    menuItem(title: "Archived Plants", deselectedIcon: UIImage(named: "ArchivedPlants")!, selectedIcon: UIImage(named: "ArchivedPlants-Purple")!, needSearchIcon: true),
    menuItem(title: "Sensor Management", deselectedIcon: UIImage(named: "SensorManagement")!, selectedIcon: UIImage(named: "SensorManagement-Purple")!, needSearchIcon: true),
    menuItem(title: "About", deselectedIcon: UIImage(named: "About")!, selectedIcon: UIImage(named: "About-Purple")!, needSearchIcon: false),
    menuItem(title: "Signout", deselectedIcon: UIImage(named: "Signout")!, selectedIcon: UIImage(named: "Signout-Purple")!, needSearchIcon: false)
]
