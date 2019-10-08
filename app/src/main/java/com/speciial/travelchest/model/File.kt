package com.speciial.travelchest.model

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class File (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "file_id")
    val uid:Long,
    val type:Int,
    @Embedded
    val path:String,
    @Embedded
    val location:Location
){
    override fun toString(): String {
        return "Type : ${this.type} . Path : ${this.path}"
    }
}
