package project.softsquad.vegitable.entity

import androidx.room.PrimaryKey
import androidx.room.Entity
import androidx.room.ColumnInfo
import java.util.Date

@Entity(tableName = "Users")
data class UsersEntity (
        @PrimaryKey(autoGenerate = true)
        var userId: Long,
        @ColumnInfo(name="userEmail")
        var userEmail: String,
        @ColumnInfo(name="userPassword")
        var userPassword: String,
        @ColumnInfo(name="userFirstName")
        var userFirstName: String,
        @ColumnInfo(name="userLastName")
        var userLastName: String,
        @ColumnInfo(name="createDateTime")
        var createDateTime: String?,
        @ColumnInfo(name="lastUpdateDateTime")
        var lastUpdateDateTime: String?,
        @ColumnInfo(name="archiveDateTime")
        var archiveDateTime: String?,
        @ColumnInfo(name="imageURL")
        var imageURL: String?
        )