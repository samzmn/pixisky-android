package com.pixisky.app.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Pixi::class], version = 1, exportSchema = false)
abstract class PixiDatabase : RoomDatabase() {

    abstract fun pixiDao(): PixiDao

    companion object {
        @Volatile
        private var Instance:PixiDatabase? = null

        fun getDatabase(context: Context) : PixiDatabase {
            // if the Instance is not null, return it, otherwise create a new database instance.
            return Instance?: synchronized(this) {
                Room.databaseBuilder(context, PixiDatabase::class.java, "pixi_database")
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { Instance = it }
            }
        }
    }
}