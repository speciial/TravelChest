package com.speciial.travelchest.model

import androidx.room.*

@Entity(foreignKeys = [(ForeignKey(
    entity = Type::class,
    parentColumns = ["type_id"],
    childColumns = ["type"]))])
data class File (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "file_id")
    val uid:Long,
    val type:Int,
    val path:String,
    @Embedded
    val location:Location
){
    override fun toString(): String {
        return "Type : ${this.type} . Path : ${this.path}"
    }
}