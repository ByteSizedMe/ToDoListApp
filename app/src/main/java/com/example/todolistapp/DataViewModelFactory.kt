package com.example.todolistapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

// Factory class for creating instances of DataViewModel with dependencies injected
// ViewModels should not directly create their own dependencies (like dao),
// so we use this factory to pass in the dao when constructing the ViewModel.
class DataViewModelFactory(
    private val dao: DataDao // The DAO (database access object) dependency
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST") // Suppress warning for type casting below
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // Check if the requested ViewModel class is DataViewModel
        if (modelClass.isAssignableFrom(DataViewModel::class.java)) {
            // If yes, return a new instance of DataViewModel with dao injected
            return DataViewModel(dao) as T
        }
        // If the requested ViewModel class is not recognized, throw an error
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
