package com.example.todolistapp

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

// Data Access Object (DAO) for handling all database operations related to "Data"
@Dao
interface DataDao {

    // Insert a new task OR update it if it already exists
    @Upsert
    suspend fun upsertTask(task: Data)

    // Delete a specific task
    @Delete
    suspend fun deleteTask(task: Data)

    // Retrieve all tasks without any specific order
    @Query("SELECT * FROM Data")
    fun showTasks(): Flow<List<Data>>

    // Retrieve tasks sorted by creation time (oldest first)
    @Query("SELECT * FROM Data ORDER BY createdAt ASC")
    fun sortByCreatedAt(): Flow<List<Data>>

    // Retrieve tasks sorted alphabetically by task name
    @Query("SELECT * FROM Data ORDER BY taskName ASC")
    fun sortByTaskName(): Flow<List<Data>>

    // Retrieve tasks sorted by deadline (earliest deadline first)
    @Query("SELECT * FROM Data ORDER BY deadline ASC")
    fun sortByDeadline(): Flow<List<Data>>

    // Mark a specific task as completed (isCompleted = true)
    @Query("UPDATE Data SET isCompleted = 1 WHERE id = :itemId")
    suspend fun updateCompleted(itemId: Int)

    // Mark a specific task as not completed (isCompleted = false)
    @Query("UPDATE Data SET isCompleted = 0 WHERE id = :itemId")
    suspend fun updateNotCompleted(itemId: Int)
}
