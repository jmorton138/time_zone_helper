package com.authenticator.timezonehelper

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [City::class], version = 2, exportSchema = true)
abstract class AppDatabase:RoomDatabase() {
    abstract fun cityDao(): CityDao

    companion object {

        @Volatile private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {

            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, "world-cities.db")
                    .createFromAsset("database/simplemaps-world-cities.db")
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }

//        private fun buildDatabase(context: Context) =
//            Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, "world-cities.db")
//                .createFromAsset("database/simplemaps-world-cities.db")
//                .build()
//
////        val PREPOPULATE_DATA = listOf(Data("1", "val"), Data("2", "val 2"))
    }
}