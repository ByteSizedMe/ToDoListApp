package com.example.todolistapp

import androidx.room.Entity
import androidx.room.PrimaryKey

// Represents a table in the Room database
@Entity
data class Data(
    // Primary key with auto-generated IDs for each task
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    // Name/title of the task
    val taskName: String,

    // Optional description/details about the task
    val taskDescription: String,

    // Timestamp for when the task was created (default = current time)
    val createdAt: Long = System.currentTimeMillis(),

    // Deadline for the task (nullable, since not all tasks may have deadlines)
    val deadline: Long?,

    // Completion status of the task (false by default = not completed)
    val isCompleted: Boolean = false
)
