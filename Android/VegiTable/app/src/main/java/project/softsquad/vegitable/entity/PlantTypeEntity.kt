package project.softsquad.vegitable.entity

import androidx.room.PrimaryKey
import androidx.room.Entity
import androidx.room.ColumnInfo

@Entity(tableName = "PlantTypes")
data class PlantTypeEntity(
    @PrimaryKey(autoGenerate = true)
    var plantTypeId: Long,
    @ColumnInfo(name="plantTypeName")
    var plantTypeName: String,
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
    @ColumnInfo(name="createDateTime")
    var createDateTime: String?,
    @ColumnInfo(name="lastUpdateDateTime")
    var lastUpdateDateTime: String?,
    @ColumnInfo(name="userId_fk")
    var userId_fk: Int
)
