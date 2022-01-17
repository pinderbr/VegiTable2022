package project.softsquad.vegitable.entity

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey
import androidx.room.Entity
import java.util.Date

@Entity(tableName="DeviceReadings")
data class DeviceReadingsEntity (
    @PrimaryKey(autoGenerate = true)
    var deviceReadingId: Long,
    @ColumnInfo(name="currentDateTime")
    var currentDateTimeStr: String,
    @ColumnInfo(name="errorReading")
    var errorReading: String?,
    @ColumnInfo(name="phValue")
    var phValue: Double?,
    @ColumnInfo(name="temperatureValue")
    var temperatureValue: Double?,
    @ColumnInfo(name="ppmValue")
    var ppmValue: Double?,
    @ColumnInfo(name="waterValue")
    var waterValue: Double?,
    @ColumnInfo(name="humidityValue")
    var humidityValue: Double?,
    @ColumnInfo(name="lightValue")
    var lightValue: Double?,
    @ColumnInfo(name="deviceId_fk")
    var deviceId_fk: Int
)