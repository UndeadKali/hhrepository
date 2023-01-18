package com.example.mynotes.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mynotes.model.Note
import com.example.mynotes.repository.NoteRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class NoteActivityViewModel(private val repository: NoteRepository) : ViewModel() {

    fun saveNote(newNote: Note) = viewModelScope.launch(Dispatchers.IO) {
        repository.addNote(newNote)
    }

    fun updateNote(existingNote: Note) = viewModelScope.launch(Dispatchers.IO) {
        repository.updateNote(existingNote)
    }

    fun deleteNote(existingNote: Note) = viewModelScope.launch(Dispatchers.IO) {
        repository.deleteNote(existingNote)
    }

    fun getAllNotes(): Flow<List<Note>> = repository.getAllNotes()

    fun searchNote(query: String): Flow<List<Note>> = repository.searchNote(query)

}