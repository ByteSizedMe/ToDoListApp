package com.example.todolistapp

sealed class MenuAction {
    object AddTask : MenuAction()
    object SortByTime : MenuAction()
    object SortByName : MenuAction()
    object SortByDeadline : MenuAction()
    data class Unknown(val id : Int): MenuAction()
}