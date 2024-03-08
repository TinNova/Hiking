package com.tinnovakovic.hiking.data.photo

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.tinnovakovic.hiking.data.photo.models.HikingPhotoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HikingPhotoDao {
    @Query("SELECT * FROM hikingPhotos_table") // do we need to order then or they are order by when they were added already?
    fun getAll(): Flow<List<HikingPhotoEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertHikingPhoto(vararg hikingPhoto: HikingPhotoEntity): Array<Long>

    @Query("DELETE FROM hikingPhotos_table")
    suspend fun deleteAll()
}