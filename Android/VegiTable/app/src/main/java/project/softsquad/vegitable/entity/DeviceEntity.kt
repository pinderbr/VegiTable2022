package project.softsquad.vegitable.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "Devices")
data class DeviceEntity (
    @PrimaryKey(autoGenerate = true)
    var deviceId: Long,
    @ColumnInfo(name="deviceName")
    var deviceName: String,
    @ColumnInfo(name="createDateTime")
    var createDateTime: String?,
    @ColumnInfo(name="lastUpdateDateTime")
    var lastUpdateDateTime: String?,
    @ColumnInfo(name="archiveDateTime")
    var archiveDateTime: String?,
    @ColumnInfo(name="userId_fk")
    var userId_fk: Int?
    )