package com.example.todolistapp

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface DataDao {
    @Upsert
    suspend fun upsertTask(task : Data)

    @Delete
    suspend fun deleteTask(task : Data)

    @Query("SELECT * FROM Data")
    fun showTasks() : Flow<List<Data>>

    @Query("SELECT * FROM Data ORDER BY createdAt ASC")
    fun sortByCreatedAt(): Flow<List<Data>>

    @Query("SELECT * FROM Data ORDER BY taskName ASC")
    fun sortByTaskName() : Flow<List<Data>>

    @Query("SELECT * FROM Data ORDER BY deadline ASC")
    fun sortByDeadline(): Flow<List<Data>>
}