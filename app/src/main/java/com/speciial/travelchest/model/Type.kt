package com.speciial.travelchest.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Type (
    @PrimaryKey
    @ColumnInfo(name = "type_id")
    val uid:Int,
    val name:String
)

object DataType{
    val populateData = listOf(
        Type(1,"Picture"),
        Type(2,"Video"),
        Type(3,"Audio")
    )
}