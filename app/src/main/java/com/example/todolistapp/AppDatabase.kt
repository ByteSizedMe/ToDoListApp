package com.example.todolistapp

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// Room database setup with Data entity and versioning
@Database(
    entities = [Data::class], // list of all entities (tables) in the database
    version = 5               // schema version (increment when structure changes)
)
abstract class AppDatabase : RoomDatabase() {

    // Abstract DAO to access database operations (insert, delete, update, query)
    abstract val dao: DataDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null  // Singleton instance of database

        // Function to get the database instance (singleton)
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {  // Thread-safe initialization
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "my_database" // database name
                )
                    // Deletes and rebuilds the DB if migration is missing
                    // (simple for now, but destroys old data on schema changes)
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}
