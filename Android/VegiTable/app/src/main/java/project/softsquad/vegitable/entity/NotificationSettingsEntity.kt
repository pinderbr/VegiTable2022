package project.softsquad.vegitable.entity

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey
import androidx.room.Entity
import java.util.Date

@Entity(tableName = "NotificationSettings")
data class NotificationSettingsEntity (
    @PrimaryKey(autoGenerate = true)
    var notificationSettingsId: Long,
    @ColumnInfo(name="dailyNotification")
    var dailyNotification: Boolean,
    @ColumnInfo(name="dailyNotificationTime")
    var dailyNotificationTime: String,
    @ColumnInfo(name="alertNotification")
    var alertNotification: Boolean,
    @ColumnInfo(name="deviceNotification")
    var deviceNotification: Boolean,
    @ColumnInfo(name="createDateTime")
    var createDateTime: String?,
    @ColumnInfo(name="lastUpdateDateTime")
    var lastUpdateDateTime: String?,
    @ColumnInfo(name="archiveDateTime")
    var archiveDateTime: String?,
    @ColumnInfo(name="userId_fk")
    var userId_fk: Int
    )