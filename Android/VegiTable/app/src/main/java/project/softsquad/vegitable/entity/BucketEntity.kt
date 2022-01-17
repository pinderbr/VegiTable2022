package project.softsquad.vegitable.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date


@Entity(tableName = "Buckets")
data class BucketEntity (
    @PrimaryKey(autoGenerate = true)
    var bucketId: Long,
    @ColumnInfo(name="bucketName")
    var bucketName: String,
    @ColumnInfo(name="temperatureMin")
    var temperatureMin: Double,
    @ColumnInfo(name="temperatureMax")
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
    @ColumnInfo(name="archiveDateTime")
    var archiveDateTime: String?,
    @ColumnInfo(name="imageURL")
    var imageURL: String,
    @ColumnInfo(name="userId_fk")
    var userId_fk: Int,
    @ColumnInfo(name="deviceId_fk")
    var deviceId_fk: Int
)