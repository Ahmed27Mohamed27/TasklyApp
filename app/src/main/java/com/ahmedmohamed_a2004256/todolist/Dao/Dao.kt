package com.ahmedmohamed_a2004256.todolist.Dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.ahmedmohamed_a2004256.todolist.Models.Data

@Dao
interface Dao {

    @Query("SELECT * FROM table_name")
    fun getAllData(): MutableList<Data>

    @Query("SELECT * FROM table_name ORDER BY id DESC")
    fun getAllData2(): LiveData<List<Data>>

    @Query("SELECT * FROM table_name WHERE id = :taskId LIMIT 1")
    fun getTaskById(taskId: Int): Data?

    @Insert
   fun insertData(data: Data)

    @Query("DELETE FROM table_name WHERE id = :id")
   fun deleteData(id: Int)

    @Query("UPDATE table_name SET title = :title, hour = :hour, minute = :minute WHERE id = :id")
   fun updateData(id: Int, title: String, hour: Int, minute: Int)

    @Query("UPDATE table_name SET isChecked = :isChecked WHERE id = :taskId")
    suspend fun updateTaskChecked(taskId: Int, isChecked: Boolean)

}