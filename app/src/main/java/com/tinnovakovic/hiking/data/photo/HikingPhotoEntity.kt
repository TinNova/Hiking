package com.tinnovakovic.hiking.data.photo

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "hikingPhotos_table")
data class HikingPhotoEntity(
    @PrimaryKey val photo: String,
)
