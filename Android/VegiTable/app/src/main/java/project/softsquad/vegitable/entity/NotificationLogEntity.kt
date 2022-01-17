package project.softsquad.vegitable.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "NotificationLogs")
data class NotificationLogEntity (
    @PrimaryKey(autoGenerate = true)
    var notificationLogId: Long,
    @ColumnInfo(name="notificationType")
    var notificationType: String,
    @ColumnInfo(name="notificationTime")
    var notificationTime: String,
    @ColumnInfo(name="createDateTime")
    var createDateTime: String?,
    @ColumnInfo(name="lastUpdateDateTime")
    var lastUpdateDateTime: String?,
    @ColumnInfo(name="notificationMessage")
    var notificationMessage: String,
    @ColumnInfo(name="bucketId_fk")
    var bucketId_fk: Int,
    @ColumnInfo(name="userId_fk")
    var userId_fk: Int
)