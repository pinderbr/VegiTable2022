//
//  CustomDelegate.swift
//  VegiTable
//
//  Created by  on 4/3/21.
//

import Foundation
import UIKit

protocol MenuControllerDelegate {
    func updateView(index: Int)
}

protocol ContainerDelegate {
    func displayNewView(storyboardID: String, viewTitle: String, data: Any?)
}

protocol ParentChildDelegate {
    func getData(id: Int)
}
