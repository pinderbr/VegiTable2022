//
//  Notification.swift
//  VegiTable
//
//  Created by  on 4/7/21.
//

import Foundation

struct Notification: Decodable {
    var notificationId: Int
    var userId_fk: Int
    var dailyNotification: Bool
    var dailyNotificationTime: String
    var alertNotification: Bool
    var sensorNotification: Bool
    var createDateTime: String
    var lastUpdateDateTime: String
    var archiveDateTime: String?
}
