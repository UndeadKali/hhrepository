package com.example.mynotes.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.mynotes.model.Note

@Database(entities = [Note::class], version = 1)

abstract class NoteDatabase : RoomDatabase() {

    abstract fun getNoteDao(): DAO

    companion object {
        @Volatile
        private var INSTANCE: NoteDatabase? = null

         fun createDatabase(context: Context): NoteDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context,
                    NoteDatabase::class.java,
                    "note_database")
                    .build()
                INSTANCE = instance

                instance
            }
        }

    }
}