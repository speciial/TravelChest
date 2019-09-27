package com.speciial.travelchest.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.speciial.travelchest.model.*
import java.util.concurrent.Executors


@Database(entities = [(Trip::class),(Location::class),(File::class),(Type::class)], version = 1)
@TypeConverters(FileConverter::class,LocationConverter::class)
abstract class TravelChestDatabase: RoomDatabase() {
    abstract fun fileDao(): FileDao
    abstract fun tripDao(): TripDao
    abstract fun LocationDao(): LocationDao
    abstract fun typeDao(): TypeDao

    /* one and only one instance, similar to static in Java */
    companion object {
        private var sInstance: TravelChestDatabase? = null
        @Synchronized
        fun get(context: Context): TravelChestDatabase {
            if (sInstance == null) {
                sInstance = Room.databaseBuilder(context.applicationContext,
                    TravelChestDatabase::class.java, "TravelChestDatabase.db")
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            Executors.newSingleThreadScheduledExecutor().execute {
                                sInstance!!.typeDao().insertAll(DataType.populateData)
                            }
                        }
                    })
                    .build()
            }

            return sInstance!!
        }
    }

}