package com.example.todolistapp

sealed interface DataEvent {
    object showDialog : DataEvent
    object hideDialog : DataEvent
    data class setTask(val task : String) : DataEvent
    data class setDescription(val description : String) : DataEvent
    data class setDeadline(val deadline : Long?): DataEvent
    object saveData : DataEvent
    data class deleteData(val data : Data) : DataEvent
    data class sortBy(val sortType : SortBy): DataEvent
    data class taskCompleted(val data : Data): DataEvent
    data class taskNotCompleted(val data : Data): DataEvent
}