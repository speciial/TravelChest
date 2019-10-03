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
) {
    companion object {
        const val PICTURE = 1
        const val VIDEO = 2
        const val SOUND = 3
        const val PICTURE_STRING = "picture"
        const val VIDEO_STRING = "movie"
        const val SOUND_STRING = "sound"
    }
}

object DataType{
    val populateData = listOf(
        Type(1,Type.PICTURE_STRING),
        Type(2,Type.VIDEO_STRING),
        Type(3,Type.SOUND_STRING)
    )
}