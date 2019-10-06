package com.speciial.travelchest.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.speciial.travelchest.model.File
import com.speciial.travelchest.model.FileConverter
import com.speciial.travelchest.model.Trip


@Database(entities = [(Trip::class),(File::class)], version = 1)
@TypeConverters(FileConverter::class)
abstract class TravelChestDatabase: RoomDatabase() {
    abstract fun fileDao(): FileDao
    abstract fun tripDao(): TripDao

    /* one and only one instance, similar to static in Java */
    companion object {
        private var sInstance: TravelChestDatabase? = null
        @Synchronized
        fun get(context: Context): TravelChestDatabase {
            if (sInstance == null) {
                sInstance = Room.databaseBuilder(context.applicationContext,
                    TravelChestDatabase::class.java, "TravelChestDatabase.db")
                    .build()
            }

            return sInstance!!
        }
    }

}