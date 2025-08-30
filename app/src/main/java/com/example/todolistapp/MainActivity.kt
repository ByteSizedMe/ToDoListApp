package com.example.todolistapp

import android.app.DatePickerDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todolistapp.DataEvent.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var adapter: Adapter
    private var currentDialog: AlertDialog? = null

    // Get DataViewModel with injected DAO using DataViewModelFactory
    private val viewModel: DataViewModel by viewModels {
        DataViewModelFactory(AppDatabase.getDatabase(this).dao)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Handle system insets (status/navigation bars)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Setup RecyclerView
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        adapter = Adapter(this)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        // Handle task deletion
        adapter.onDeleteClick = { data -> viewModel.onEvent(DataEvent.deleteData(data)) }

        // Handle task completion toggle
        adapter.onCompleteClick = { data ->
            if (data.isCompleted) {
                viewModel.onEvent(DataEvent.taskNotCompleted(data))
            } else {
                viewModel.onEvent(DataEvent.taskCompleted(data))
            }
        }

        // Setup custom Toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar_include)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        // Collect state and events from ViewModel
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                // Collect UI state updates
                launch {
                    viewModel.state.collect { state ->
                        adapter.submitList(state.tasks) // Update RecyclerView
                        if (state.isAdding) showAddTaskDialog() // Show dialog if needed
                    }
                }
                // Collect one-time UI events (toast, dismiss, etc.)
                launch {
                    viewModel.uiEvent.collect { event ->
                        when (event) {
                            is UiEvent.ShowToast ->
                                Toast.makeText(this@MainActivity, event.message, Toast.LENGTH_SHORT).show()
                            is UiEvent.DismissDialog ->
                                currentDialog?.dismiss()
                        }
                    }
                }
            }
        }
    }

    // Inflate menu options (Add, Sort)
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    // Helper to map menu IDs → custom MenuAction type
    private fun menuActionFromId(id: Int): MenuAction {
        return when (id) {
            R.id.addTask -> MenuAction.AddTask
            R.id.sortByTime -> MenuAction.SortByTime
            R.id.sortByName -> MenuAction.SortByName
            R.id.sortByDeadline -> MenuAction.SortByDeadline
            else -> MenuAction.Unknown(id)
        }
    }

    // Handle toolbar menu item clicks
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (val action = menuActionFromId(item.itemId)) {
            MenuAction.AddTask -> {
                viewModel.onEvent(DataEvent.showDialog)
                true
            }
            MenuAction.SortByTime -> {
                viewModel.onEvent(sortBy(SortBy.CREATED_AT))
                true
            }
            MenuAction.SortByDeadline -> {
                viewModel.onEvent(sortBy(SortBy.DEADLINE))
                true
            }
            MenuAction.SortByName -> {
                viewModel.onEvent(sortBy(SortBy.TASK_NAME))
                true
            }
            is MenuAction.Unknown -> super.onOptionsItemSelected(item)
        }
    }

    // Show "Add Task" dialog box
    private fun showAddTaskDialog() {
        if (currentDialog?.isShowing == true) return // Prevent duplicate dialogs

        val dialogView = layoutInflater.inflate(R.layout.dialog_box, null)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()
        currentDialog = dialog
        dialog.setCanceledOnTouchOutside(false)

        val taskInput = dialogView.findViewById<EditText>(R.id.newTask)
        val descInput = dialogView.findViewById<EditText>(R.id.newDesc)
        val deadlineInput = dialogView.findViewById<EditText>(R.id.deadlineInput)

        // Prefill existing state values
        taskInput.setText(viewModel.state.value.currentTask)
        descInput.setText(viewModel.state.value.currentDescription)

        // Setup DatePicker for deadline input
        deadlineInput.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePicker = DatePickerDialog(this, { _, y, m, d ->
                val selectedCal = Calendar.getInstance()
                selectedCal.set(y, m, d)

                // Format date to dd/MM/yyyy
                val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val dateString = sdf.format(selectedCal.time)

                deadlineInput.setText(dateString)

                // Save into ViewModel state (as millis)
                viewModel.onEvent(DataEvent.setDeadline(selectedCal.timeInMillis))
            }, year, month, day)

            // Block past dates
            datePicker.datePicker.minDate = calendar.timeInMillis

            datePicker.show()
        }

        // Setup Save + Cancel buttons
        val saveBtn = dialogView.findViewById<Button>(R.id.saveButton)
        val cancelBtn = dialogView.findViewById<Button>(R.id.cancelButton)

        saveBtn.setOnClickListener {
            // Push user inputs into ViewModel
            viewModel.onEvent(DataEvent.setTask(taskInput.text.toString()))
            viewModel.onEvent(DataEvent.setDescription(descInput.text.toString()))
            viewModel.onEvent(DataEvent.saveData) // Save task
        }

        cancelBtn.setOnClickListener {
            viewModel.onEvent(DataEvent.hideDialog) // Close without saving
            dialog.dismiss()
        }

        // Show dialog with transparent background
        dialog.show()
        dialog.window?.setLayout(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }
}
