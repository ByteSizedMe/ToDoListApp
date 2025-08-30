package com.example.todolistapp

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Data(
    @PrimaryKey(autoGenerate=true)
    val id : Int = 0,
    val taskName : String,
    val taskDescription : String,
    val createdAt: Long = System.currentTimeMillis(),
    val deadline : Long?,
    val isCompleted : Boolean = false
)
