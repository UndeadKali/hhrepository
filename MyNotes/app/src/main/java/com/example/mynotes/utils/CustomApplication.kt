package com.example.mynotes.utils

import android.app.Application
import com.example.mynotes.db.NoteDatabase

class CustomApplication : Application() {
    val database: NoteDatabase by lazy { NoteDatabase.createDatabase(this) }
}