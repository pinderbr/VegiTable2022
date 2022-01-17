package project.softsquad.vegitable

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import project.softsquad.vegitable.dao.*
import project.softsquad.vegitable.entity.*

@Database(entities = [BucketEntity::class, DeviceEntity::class, DeviceReadingsEntity::class,
                     NotificationSettingsEntity::class, PlantsEntity::class, UsersEntity::class, PlantTypeEntity::class, NotificationLogEntity::class],
                     version = 1)
@TypeConverters(Converters::class)

abstract class VegiTableDatabase : RoomDatabase(){

    abstract fun BucketDao(): BucketDao
    abstract fun DeviceDao(): DeviceDao
    abstract fun DeviceReadingsDao(): DeviceReadingsDao
    abstract fun NotificationSettingsDao(): NotificationSettingsDao
    abstract fun PlantsDao(): PlantsDao
    abstract fun UsersDao(): UsersDao
    abstract fun PlantTypeDao(): PlantTypeDao
    abstract fun NotificationLogDao(): NotificationLogDao

    companion object{
        @Volatile
        private var INSTANCE: VegiTableDatabase? = null

        fun getInstance(context: Context): VegiTableDatabase {
            if(INSTANCE == null){
                Room.databaseBuilder(
                    context,
                    VegiTableDatabase::class.java,
                    "vegi.db"
                ).build()
                .also { INSTANCE = it }
            }
            return INSTANCE as VegiTableDatabase
        }
    }
}
