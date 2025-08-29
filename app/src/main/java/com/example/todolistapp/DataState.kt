package com.example.todolistapp

data class DataState(
    val tasks : List<Data> = emptyList(),
    val isAdding: Boolean = false,
    val currentTask : String = "",
    val currentDescription : String = "",
    val currentDeadLine : Long? = null,
    val sortType : SortBy = SortBy.TASK_NAME
    )
