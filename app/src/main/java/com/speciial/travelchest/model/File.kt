package com.speciial.travelchest.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(foreignKeys = [(ForeignKey(
    entity = Type::class,
    parentColumns = ["uid"],
    childColumns = ["type"]))])
data class File (
    @PrimaryKey(autoGenerate = true)
    val uid:Long,
    val type:Long,
    val path:String,
    val location:Location
)