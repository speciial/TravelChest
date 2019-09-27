package com.speciial.travelchest.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@Entity
data class Trip(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "trip_id")
    val uid:Long,
    val name:String,

    val locList:ArrayList<Location>? = null,
    
    val fileList:ArrayList<File>? = null
)

class LocationConverter {

    @TypeConverter
    fun fromLocationList(location: ArrayList<Location>?): String? {
        if (location == null) {
            return null
        }
        val gson = Gson()
        val type = object : TypeToken<ArrayList<Location>>() {

        }.type
        return gson.toJson(location, type)
    }

    @TypeConverter
    fun toLocationlist(locationString: String?): ArrayList<Location>? {
        if (locationString == null) {
            return null
        }
        val gson = Gson()
        val type = object : TypeToken<ArrayList<Location>>() {

        }.type
        return gson.fromJson(locationString, type)
    }
}
class FileConverter {

    @TypeConverter
    fun fromFileList(file: ArrayList<File>?): String? {
        if (file == null) {
            return null
        }
        val gson = Gson()
        val type = object : TypeToken<ArrayList<File>>() {

        }.type
        return gson.toJson(file, type)
    }

    @TypeConverter
    fun toFilelist(fileString: String?): ArrayList<File>? {
        if (fileString == null) {
            return null
        }
        val gson = Gson()
        val type = object : TypeToken<ArrayList<File>>() {

        }.type
        return gson.fromJson(fileString, type)
    }
}