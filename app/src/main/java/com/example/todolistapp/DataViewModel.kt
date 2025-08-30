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

class DataViewModel(private val dao : DataDao): ViewModel() {
    private val _state = MutableStateFlow(DataState())
    private val _sortBy = MutableStateFlow(SortBy.CREATED_AT)

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
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    val state = combine(_state, _data, _sortBy){state, data, sort ->
        state.copy(
            tasks = data,
            sortType = sort
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DataState())

    private val _uiEvent = MutableSharedFlow<UiEvent>() // <- use SharedFlow
    val uiEvent = _uiEvent.asSharedFlow()

    fun onEvent(event : DataEvent){
        when(event){
            is DataEvent.deleteData -> {
                viewModelScope.launch{
                    dao.deleteTask(event.data)
                }
            }
            DataEvent.hideDialog -> {
                _state.value = _state.value.copy(isAdding = false)
            }
            DataEvent.saveData -> {
                if(_state.value.currentTask.isBlank()){
                    viewModelScope.launch {
                        _uiEvent.emit(ShowToast("Enter task"))
                    }
                }
                else if(_state.value.currentDescription.isBlank()){
                    viewModelScope.launch {
                        _uiEvent.emit(ShowToast("Enter description"))
                    }
                }
                else if(_state.value.currentDeadLine == null){
                    viewModelScope.launch {
                        _uiEvent.emit(ShowToast("Pick date"))
                    }
                }
                else{
                    val newTask = Data(taskName = _state.value.currentTask, taskDescription = _state.value.currentDescription, deadline = _state.value.currentDeadLine)
                    viewModelScope.launch {
                        dao.upsertTask(newTask)
                        _state.value = _state.value.copy(
                            isAdding = false,
                            currentTask = "",
                            currentDescription = "",
                            currentDeadLine = null
                        )
                        _uiEvent.emit(DismissDialog) // <- tell UI to dismiss
                    }
                }
            }
            is DataEvent.setDescription -> {
                _state.value = _state.value.copy(currentDescription = event.description)
            }
            is DataEvent.setTask -> {
                _state.value = _state.value.copy(currentTask = event.task)
            }
            DataEvent.showDialog -> {
                _state.value = _state.value.copy(isAdding = true)
            }
            is DataEvent.sortBy -> {
                _sortBy.value = event.sortType
            }
            is DataEvent.setDeadline -> {
                _state.value = _state.value.copy(currentDeadLine = event.deadline)
            }

            is DataEvent.taskCompleted -> {
                viewModelScope.launch {
                    dao.updateCompleted(event.data.id)
                }
            }
            is DataEvent.taskNotCompleted -> {
                viewModelScope.launch {
                    dao.updateNotCompleted(event.data.id)
                }
            }
        }
    }
}