package com.ahmedmohamed_a2004256.todolist.Ui

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ahmedmohamed_a2004256.todolist.Adapter.Adapter
import com.ahmedmohamed_a2004256.todolist.Database.NoteDatabase
import com.ahmedmohamed_a2004256.todolist.R
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale


class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var database: NoteDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        val fab = findViewById<FloatingActionButton>(R.id.fab)
        database = NoteDatabase.getDatabase(this)
        val materialToolbar = findViewById<MaterialToolbar>(R.id.materialToolbar)

        materialToolbar.setNavigationOnClickListener {
            AlertDialog.Builder(this)
                .setTitle(getString(R.string.close_app))
                .setMessage(getString(R.string.sub_close_app))
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes,
                    DialogInterface.OnClickListener { arg0, arg1 ->
                        finish()
                    }).create().show()
        }

        materialToolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.transfer -> {
                    showLanguageDialog()
                    true
                }
                else -> false
            }
        }

        lifecycleScope.launch(Dispatchers.IO) {
            val users = database.userDao().getAllData()

            withContext(Dispatchers.Main) {
                recyclerView.adapter = Adapter(users, this@MainActivity, this@MainActivity, database.userDao())
            }

        }

        fab.setOnClickListener {
            val intent = Intent(this, AddNoteActivity::class.java)
            startActivityForResult(intent, 100)
        }

    }

    override fun onResume() {
        super.onResume()
        fetchTasks()
    }

    private fun fetchTasks() {
        lifecycleScope.launch(Dispatchers.IO) {
            val users = database.userDao().getAllData()
            withContext(Dispatchers.Main) {
                recyclerView.adapter = Adapter(users, this@MainActivity, this@MainActivity, database.userDao())
            }
        }
    }

    private fun showLanguageDialog() {
        val languages = arrayOf("العربية", "English")

        AlertDialog.Builder(this)
            .setTitle(getString(R.string.choose_language))
            .setItems(languages) { _, which ->
                val selectedLanguage = if (which == 0) "ar" else "en"

                val sharedPreferences = getSharedPreferences("Settings", MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.putString("App_Lang", selectedLanguage)
                editor.apply()

                setLocale(selectedLanguage)
                recreate()
            }
            .show()
    }

    private fun setLocale(lang: String) {
        val locale = Locale(lang)
        Locale.setDefault(locale)

        val config = resources.configuration
        config.setLocale(locale)

        resources.updateConfiguration(config, resources.displayMetrics)
    }

}