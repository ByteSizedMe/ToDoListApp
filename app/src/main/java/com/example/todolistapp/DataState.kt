package com.example.todolistapp

// Represents the current state of the UI (used in ViewModel + StateFlow)
// All fields have default values so a fresh state can be easily created.
data class DataState(
    // List of all tasks currently loaded from the database
    val tasks: List<Data> = emptyList(),

    // Whether the "Add/Edit Task" dialog is currently open
    val isAdding: Boolean = false,

    // Current input value for the task name field
    val currentTask: String = "",

    // Current input value for the description field
    val currentDescription: String = "",

    // Current input value for the deadline field (nullable: null = no deadline)
    val currentDeadLine: Long? = null,

    // Current sorting type (default: sort by task name)
    val sortType: SortBy = SortBy.TASK_NAME
)
