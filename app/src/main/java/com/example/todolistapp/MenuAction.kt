package com.example.todolistapp

// A sealed class to represent different possible actions from the app menu
// Sealed class ensures all cases are known and can be exhaustively handled in 'when' statements
sealed class MenuAction {

    // User chooses to add a new task
    object AddTask : MenuAction()

    // User chooses to sort tasks by the time they were created
    object SortByTime : MenuAction()

    // User chooses to sort tasks by task name
    object SortByName : MenuAction()

    // User chooses to sort tasks by deadline
    object SortByDeadline : MenuAction()

    // Fallback for unexpected/unknown menu action (with the raw menu item id)
    data class Unknown(val id : Int): MenuAction()
}
