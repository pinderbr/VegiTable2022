//
//  AboutSection.swift
//  VegiTable
//
//  Created by  on 3/31/21.
//
import UIKit

struct aboutSection {
    let title: String
    let description: String
    let subsection: Bool
}

let aboutSections: [aboutSection] = [
    aboutSection(
        title: "Device Care",description: "Please make sure that you keep the upper part of the sensor kit out of the water. ONLY THE BOTTOM HALF CAN GET WET because it should be slightly in the water.", subsection: false),
    aboutSection(title: "FAQs", description: "", subsection: false),
    aboutSection(title: "Sensor serial number?", description: "It is written on the casing for the sensors.", subsection: true),
    aboutSection(title: "Remove plant from a bucket?", description: "Go to your green house, select the bucket the plant is in, and then press the button below the edit/pencil button. ", subsection: true),
    aboutSection(title: "Terms and Conditions",
         description: "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.",
         subsection: false),
    aboutSection(title: "Privacy Policy",
         description: "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.",
         subsection: false)
]
