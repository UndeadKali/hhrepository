package com.example.mynotes.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.mynotes.databinding.ActivityMainBinding
import com.example.mynotes.db.NoteDatabase
import com.example.mynotes.repository.NoteRepository
import com.example.mynotes.viewModel.NoteActivityViewModel
import com.example.mynotes.viewModel.NoteActivityViewModelFactory

class MainActivity : AppCompatActivity() {

    private lateinit var noteActivityViewModel: NoteActivityViewModel
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val noteRepository = NoteRepository(NoteDatabase.createDatabase(this))
        val noteActivityViewModelFactory = NoteActivityViewModelFactory(noteRepository)

        noteActivityViewModel = ViewModelProvider(
            this,
            noteActivityViewModelFactory
        )[NoteActivityViewModel::class.java]

    }
}