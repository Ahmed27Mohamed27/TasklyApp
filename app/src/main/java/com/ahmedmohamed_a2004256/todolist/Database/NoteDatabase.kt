package com.ahmedmohamed_a2004256.todolist.Database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.ahmedmohamed_a2004256.todolist.Dao.Dao
import com.ahmedmohamed_a2004256.todolist.Models.Data

@Database(entities = [Data::class], version = 1, exportSchema = false)
abstract class NoteDatabase : RoomDatabase() {
    abstract fun userDao(): Dao

    companion object {
        @Volatile
        private var INSTANCE: NoteDatabase? = null

        fun getDatabase(context: Context): NoteDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context,
                    NoteDatabase::class.java,
                    "table_name"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

//    companion object {
//        @Volatile
//        private var INSTANCE: NoteDatabase? = null
//
//        @Synchronized
//        fun getDatabase(context: Context): NoteDatabase {
//            if (INSTANCE == null) {
//                INSTANCE = Room.databaseBuilder(context.applicationContext, NoteDatabase::class.java, "table_name")
//                    .fallbackToDestructiveMigration()
//                    .build()
//            }
//            return INSTANCE!!
//        }
//    }

}