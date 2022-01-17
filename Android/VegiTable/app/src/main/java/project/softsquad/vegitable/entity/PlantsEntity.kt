package project.softsquad.vegitable.entity

import androidx.room.PrimaryKey
import androidx.room.Entity
import androidx.room.ColumnInfo
import project.softsquad.vegitable.getCurrentDateTime
import java.util.Date

@Entity(tableName = "Plants")
data class PlantsEntity(
        @PrimaryKey(autoGenerate = true)
        var plantId: Long,
        @ColumnInfo(name="plantType")
        var plantType: String,
        @ColumnInfo(name="plantName")
        var plantName: String,
        @ColumnInfo(name="temperatureMin")
        var temperatureMin: Double,
        @ColumnInfo(name="TemperatureMax")
        var temperatureMax: Double,
        @ColumnInfo(name="phMin")
        var phMin: Double,
        @ColumnInfo(name="phMax")
        var phMax: Double,
        @ColumnInfo(name="ppmMin")
        var ppmMin: Double,
        @ColumnInfo(name="ppmMax")
        var ppmMax: Double,
        @ColumnInfo(name="lightMin")
        var lightMin: Double,
        @ColumnInfo(name="lightMax")
        var lightMax: Double,
        @ColumnInfo(name="humidityMin")
        var humidityMin: Double,
        @ColumnInfo(name="humidityMax")
        var humidityMax: Double,
        @ColumnInfo(name="plantPhase")
        var plantPhase: String,
        @ColumnInfo(name="createDateTime")
        var createDateTime: String?,
        @ColumnInfo(name="lastUpdateDateTime")
        var lastUpdateDateTime: String?,
        @ColumnInfo(name="archiveDateTime")
        var archiveDateTime: String?,
        @ColumnInfo(name="imageURL")
        var imageURL: String,
        @ColumnInfo(name="bucketID_fk")
        var bucketId_fk: Int,
        @ColumnInfo(name="userId_fk")
        var userId_fk: Int
        )
