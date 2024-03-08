package com.tinnovakovic.hiking.data.photo

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.tinnovakovic.hiking.data.photo.models.HikingPhotoEntity

// Annotates class to be a Room Database with a table (entity) of the Word class
@Database(entities = arrayOf(HikingPhotoEntity::class), version = 1, exportSchema = false)
abstract class HikingDatabase : RoomDatabase() {

    abstract fun hikingPhotoDao(): HikingPhotoDao

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: HikingDatabase? = null

        fun getDatabase(context: Context): HikingDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    HikingDatabase::class.java,
                    "hiking_database"
                ).build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}
