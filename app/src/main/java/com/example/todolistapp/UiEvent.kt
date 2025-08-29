package com.example.todolistapp

sealed class UiEvent {
    data class ShowToast(val message : String): UiEvent()
    object DismissDialog : UiEvent()
}