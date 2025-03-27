package com.ahmedmohamed_a2004256.todolist.Ui

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.ahmedmohamed_a2004256.todolist.Database.NoteDatabase
import com.ahmedmohamed_a2004256.todolist.R
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EditNoteActivity : AppCompatActivity() {

    private lateinit var time: Button

    @SuppressLint("WrongViewCast", "MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_note)

        time = findViewById<Button>(R.id.time)
        val fabDone = findViewById<FloatingActionButton>(R.id.fabDone)
        val materialToolbar = findViewById<MaterialToolbar>(R.id.materialToolbar)
        val titleET = findViewById<EditText>(R.id.titleET)
        val timeTV = findViewById<TextView>(R.id.timeTV)
        val db = NoteDatabase.getDatabase(this)
        var selectedTime: String? = null
        var hours: Int = 0
        var minutes: Int = 0

        var taskId = intent.getIntExtra("id", -1).takeIf { it != -1 }

        taskId?.let { id ->
            lifecycleScope.launch(Dispatchers.IO) {
                val task = db.userDao().getTaskById(id)
                task?.let {
                    titleET.setText(it.title)
                    hours = it.hour
                    minutes = it.minute
                    val formattedHour = if (hours!! % 12 == 0) 12 else hours!! % 12
                    val amPm = if (hours!! >= 12) getString(R.string.pm) else getString(R.string.am)
                    selectedTime = String.format("%02d:%02d %s", formattedHour, minutes, amPm)
                    timeTV.text = selectedTime
                }
            }
        }

        materialToolbar.setNavigationOnClickListener {
            finish()
        }

        time.setOnClickListener {
            val picker = MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_12H)
                .setTitleText(R.string.choose_time)
                .setTheme(R.style.CustomMaterialTimePicker)
                .build()

            picker.show(supportFragmentManager, "TimePicker")

            picker.addOnPositiveButtonClickListener {
                val hour = picker.hour
                val minute = picker.minute

                val amPm: String = if (hour >= 12) getString(R.string.pm) else getString(R.string.am)
                val formattedHour = if (hour % 12 == 0) 12 else hour % 12

                hours = hour
                minutes = minute
                selectedTime = String.format("%02d:%02d %s", formattedHour, minute, amPm)
                timeTV.text = selectedTime
            }
        }

        fabDone.setOnClickListener {
            if (titleET.text.isEmpty()) {
                titleET.error = getString(R.string.error_name_task)
            }
            else if (timeTV.text.toString().isEmpty()) {
                Toast.makeText(this@EditNoteActivity, R.string.error_choose_time, Toast.LENGTH_SHORT).show()
            }
            else {
                val taskTitle = titleET.text.toString()
                lifecycleScope.launch(Dispatchers.IO) {
                    taskId?.let { id ->
                        db.userDao().updateData(id, taskTitle, hours, minutes)
                    }

                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@EditNoteActivity, "تم التعديل علي التاسك بنجاح", Toast.LENGTH_SHORT).show()
                        finish()
                    }

                }
            }
        }

    }

}