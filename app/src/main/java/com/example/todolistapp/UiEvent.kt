package com.example.todolistapp

// Sealed class representing one-time UI events triggered by the ViewModel.
// These are not part of the state (DataState) but represent transient actions
// like showing a toast or closing a dialog.
sealed class UiEvent {

    // Show a short toast message on the screen
    data class ShowToast(val message : String) : UiEvent()

    // Dismiss/close any currently shown dialog
    object DismissDialog : UiEvent()
}
