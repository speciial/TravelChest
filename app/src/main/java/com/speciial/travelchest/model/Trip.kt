package com.speciial.travelchest.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity
data class Trip(
    @PrimaryKey(autoGenerate = true)
    val uid:Long,
    val name:String,
    @Relation(parentColumn = "uid", entityColumn = "uid")
    val locList:List<Location>? = null,
    @Relation(parentColumn = "uid", entityColumn = "uid")
    val fileList:List<File>?= null
)