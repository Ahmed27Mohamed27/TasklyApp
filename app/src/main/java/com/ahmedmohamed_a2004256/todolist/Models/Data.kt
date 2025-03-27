package com.ahmedmohamed_a2004256.todolist.Models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "table_name")
class Data(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val hour: Int,
    val minute: Int,
    var isChecked: Boolean = false
)