package com.example.todolistapp

// Enum class to define the different ways tasks can be sorted in the app
enum class SortBy {

    // Sort tasks by the time they were created (default order: oldest to newest)
    CREATED_AT,

    // Sort tasks alphabetically by task name (A → Z)
    TASK_NAME,

    // Sort tasks by their deadline (earliest deadline first)
    DEADLINE
}
