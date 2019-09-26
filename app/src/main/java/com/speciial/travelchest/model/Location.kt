package com.speciial.travelchest.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Location (
    @PrimaryKey(autoGenerate = true)
    val uid:Long,
    val lat:Double,
    val long:Double
)