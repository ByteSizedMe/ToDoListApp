package com.example.todolistapp

// Sealed interface for representing all possible events (user actions or app triggers)
// Using a sealed interface ensures exhaustive handling in 'when' statements in the ViewModel
sealed interface DataEvent {
    // ----- UI dialog control -----
    // Show the "Add/Edit Task" dialog
    object showDialog : DataEvent

    // Hide the "Add/Edit Task" dialog
    object hideDialog : DataEvent

    // ----- Input field updates -----
    // Set/update the task name
    data class setTask(val task : String) : DataEvent

    // Set/update the task description
    data class setDescription(val description : String) : DataEvent

    // Set/update the task deadline (nullable for "no deadline")
    data class setDeadline(val deadline : Long?): DataEvent

    // ----- Database operations -----
    // Save a new task or update an existing one
    object saveData : DataEvent

    // Delete a given task from the database
    data class deleteData(val data : Data) : DataEvent

    // Sort tasks according to a chosen type (by name, deadline, created date, etc.)
    data class sortBy(val sortType : SortBy): DataEvent

    // ----- Completion state -----
    // Mark a task as completed
    data class taskCompleted(val data : Data): DataEvent

    // Mark a task as not completed
    data class taskNotCompleted(val data : Data): DataEvent
}