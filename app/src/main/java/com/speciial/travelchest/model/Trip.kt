package com.speciial.travelchest.model

import androidx.room.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@Entity
data class Trip(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "trip_id")
    val uid:Long,
    val name:String,
    val tripCiy:String,
    @Embedded
    val location:Location,
    var pathThumbnail:String,
    val startDate:String,
    val endDate:String,
    val fileList:ArrayList<File> = ArrayList()
) {
    fun getFilesByType(type:Int):List<File>{
        return fileList.filter { it.type == type }
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