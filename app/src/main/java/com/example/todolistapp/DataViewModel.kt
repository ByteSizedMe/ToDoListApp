package com.example.todolistapp

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import androidx.lifecycle.viewModelScope
import com.example.todolistapp.UiEvent.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// ViewModel for managing all data-related logic in the ToDo app
class DataViewModel(private val dao : DataDao): ViewModel() {

    // Holds the current UI state (task list, dialog state, etc.)
    private val _state = MutableStateFlow(DataState())

    // Holds the current sorting type (by created date, task name, or deadline)
    private val _sortBy = MutableStateFlow(SortBy.CREATED_AT)

    // Observes database changes depending on selected sort type
    private val _data = _sortBy
        .flatMapLatest { sortType->
            when(sortType){
                SortBy.CREATED_AT -> {
                    dao.sortByCreatedAt()
                }
                SortBy.TASK_NAME -> {
                    dao.sortByTaskName()
                }
                SortBy.DEADLINE -> {
                    dao.sortByDeadline()
                }
            }
        } // Convert to StateFlow so it survives config changes (screen rotations, etc.)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    // Combines UI state + database data + sort type into a single observable state
    val state = combine(_state, _data, _sortBy){state, data, sort ->
        state.copy(
            tasks = data,
            sortType = sort
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DataState())

    // SharedFlow for sending one-time events (toasts, dismiss dialogs, etc.)
    private val _uiEvent = MutableSharedFlow<UiEvent>() // <- use SharedFlow
    val uiEvent = _uiEvent.asSharedFlow()

    // Handles all user actions (events) and updates state or database accordingly
    fun onEvent(event : DataEvent){
        when(event){
            // Delete a task from database
            is DataEvent.deleteData -> {
                viewModelScope.launch{
                    dao.deleteTask(event.data)
                }
            }
            // Hide "Add Task" dialog
            DataEvent.hideDialog -> {
                _state.value = _state.value.copy(isAdding = false)
            }
            // Save a new task after validation
            DataEvent.saveData -> {
                if(_state.value.currentTask.isBlank()){
                    // If task title is missing, show toast
                    viewModelScope.launch {
                        _uiEvent.emit(ShowToast("Enter task"))
                    }
                }
                else if(_state.value.currentDescription.isBlank()){
                    // If description is missing, show toast
                    viewModelScope.launch {
                        _uiEvent.emit(ShowToast("Enter description"))
                    }
                }
                else if(_state.value.currentDeadLine == null){
                    // If deadline not set, show toast
                    viewModelScope.launch {
                        _uiEvent.emit(ShowToast("Pick date"))
                    }
                }
                else{
                    // Create a new Data object with input values
                    val newTask = Data(taskName = _state.value.currentTask, taskDescription = _state.value.currentDescription, deadline = _state.value.currentDeadLine)
                    viewModelScope.launch {
                        // Insert or update task in DB
                        dao.upsertTask(newTask)
                        // Reset input fields and close dialog
                        _state.value = _state.value.copy(
                            isAdding = false,
                            currentTask = "",
                            currentDescription = "",
                            currentDeadLine = null
                        )
                        // Notify UI to dismiss dialog
                        _uiEvent.emit(DismissDialog) // <- tell UI to dismiss
                    }
                }
            }
            // Update current description input
            is DataEvent.setDescription -> {
                _state.value = _state.value.copy(currentDescription = event.description)
            }
            // Update current task name input
            is DataEvent.setTask -> {
                _state.value = _state.value.copy(currentTask = event.task)
            }
            // Show "Add Task" dialog
            DataEvent.showDialog -> {
                _state.value = _state.value.copy(isAdding = true)
            }
            // Change sorting type
            is DataEvent.sortBy -> {
                _sortBy.value = event.sortType
            }
            // Update current deadline input
            is DataEvent.setDeadline -> {
                _state.value = _state.value.copy(currentDeadLine = event.deadline)
            }
            // Mark a task as completed in DB
            is DataEvent.taskCompleted -> {
                viewModelScope.launch {
                    dao.updateCompleted(event.data.id)
                }
            }
            // Mark a task as not completed in DB
            is DataEvent.taskNotCompleted -> {
                viewModelScope.launch {
                    dao.updateNotCompleted(event.data.id)
                }
            }
        }
    }
}