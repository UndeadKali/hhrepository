package com.example.mynotes.fragments

import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.mynotes.R
import com.example.mynotes.activities.MainActivity
import com.example.mynotes.adapters.RecyclerViewNotesAdapter
import com.example.mynotes.databinding.FragmentNoteBinding
import com.example.mynotes.utils.SwipeToDelete
import com.example.mynotes.utils.hideKeyboard
import com.example.mynotes.viewModel.NoteActivityViewModel
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.MaterialElevationScale
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Collections
import java.util.concurrent.TimeUnit

class NoteFragment : Fragment(R.layout.fragment_note) {

    private lateinit var noteFragmentBinding: FragmentNoteBinding
    private val noteActivityViewModel: NoteActivityViewModel by activityViewModels()
    private lateinit var recyclerViewAdapter: RecyclerViewNotesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        exitTransition = MaterialElevationScale(false).apply {
            duration = 350
        }

        enterTransition = MaterialElevationScale(true).apply {
            duration = 350
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        noteFragmentBinding = FragmentNoteBinding.bind(view)
        val activity = activity as MainActivity
        val navController = Navigation.findNavController(view)
        requireView().hideKeyboard()
        CoroutineScope(Dispatchers.Main.immediate).launch {
            delay(10)
            activity.window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            activity.window.statusBarColor = Color.parseColor("#9E9D9D")
        }

        noteFragmentBinding.addNote.setOnClickListener {
            noteFragmentBinding.appBarLayout.visibility = View.INVISIBLE
            navController.navigate(NoteFragmentDirections.actionNoteFragmentToSaveAndDeleteFragment())
        }

        noteFragmentBinding.innerFloatingActionButton.setOnClickListener {
            noteFragmentBinding.appBarLayout.visibility = View.INVISIBLE
            navController.navigate(NoteFragmentDirections.actionNoteFragmentToSaveAndDeleteFragment())
        }

        recyclerViewDisplay()
        swipeToDelete(noteFragmentBinding.recyclerViewNote)

        noteFragmentBinding.search.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                noteFragmentBinding.noData.isVisible = false
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0.toString().isNotEmpty()) {
                    val text = p0.toString()
                    val query = "%$text%"
                    if (query.isNotEmpty()) {
                        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                            noteActivityViewModel.searchNote(query).collect {
                                recyclerViewAdapter.submitList(it)
                            }
                        }
                    } else {
                        observeDataChanges()
                    }
                } else {
                    observeDataChanges()
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })

        noteFragmentBinding.search.setOnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                v.clearFocus()
                requireView().hideKeyboard()
            }
            return@setOnEditorActionListener true
        }

        noteFragmentBinding.recyclerViewNote.setOnScrollChangeListener { _, scrollX, scrollY, _, oldScrollY ->

            when {
                scrollY > oldScrollY -> {
                    noteFragmentBinding.chatFloatingActionButtonText.isVisible = false
                }

                scrollX == scrollY -> {
                    noteFragmentBinding.chatFloatingActionButtonText.isVisible = true
                }

                else -> {
                    noteFragmentBinding.chatFloatingActionButtonText.isVisible = true
                }
            }

        }
    }

    private fun swipeToDelete(recyclerViewNote: RecyclerView) {
        val swipeToDeleteCallback = object : SwipeToDelete() {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {

                recyclerViewAdapter.notifyItemMoved(viewHolder.absoluteAdapterPosition, target.absoluteAdapterPosition)
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.absoluteAdapterPosition
                val note = recyclerViewAdapter.currentList[position]
                var actionButtonTapped = false
                noteActivityViewModel.deleteNote(note)
                noteFragmentBinding.search.apply {
                    hideKeyboard()
                    clearFocus()
                }
                if (noteFragmentBinding.search.text.toString().isEmpty()) {
                    observeDataChanges()
                }
                val snackbar = Snackbar.make(
                    requireView(), "Note Deleted", Snackbar.LENGTH_LONG
                ).addCallback(object : BaseTransientBottomBar.BaseCallback<Snackbar>() {

                    override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                        super.onDismissed(transientBottomBar, event)

                    }

                    override fun onShown(transientBottomBar: Snackbar?) {
                        transientBottomBar?.setAction("Undo") {
                            noteActivityViewModel.saveNote(note)
                            actionButtonTapped = true
                            noteFragmentBinding.noData.isVisible = false
                        }
                    }

                }).apply {
                    animationMode = Snackbar.ANIMATION_MODE_FADE
                    setAnchorView(R.id.add_note)
                }
                snackbar.setActionTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.card_green
                    )
                )
                snackbar.show()
            }

        }
        val itemTouchHelper = ItemTouchHelper(swipeToDeleteCallback)
        itemTouchHelper.attachToRecyclerView(recyclerViewNote)

    }

    private fun recyclerViewDisplay() {
        when (resources.configuration.orientation) {
            Configuration.ORIENTATION_PORTRAIT -> setUpRecyclerView(2)
            Configuration.ORIENTATION_LANDSCAPE -> setUpRecyclerView(3)

            Configuration.ORIENTATION_UNDEFINED -> {
                TODO()
            }
            Configuration.ORIENTATION_SQUARE -> {
                TODO()
            }
        }
    }

    private fun observeDataChanges() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            noteActivityViewModel.getAllNotes().collect { list ->
                noteFragmentBinding.noData.isVisible = list.isEmpty()
                recyclerViewAdapter.submitList(list)
            }
        }
    }

    private fun setUpRecyclerView(spanCount: Int) {
        noteFragmentBinding.recyclerViewNote.apply {
            layoutManager =
                StaggeredGridLayoutManager(spanCount, StaggeredGridLayoutManager.VERTICAL)
            recyclerViewAdapter = RecyclerViewNotesAdapter()
            recyclerViewAdapter.stateRestorationPolicy =
                RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
            adapter = recyclerViewAdapter
            postponeEnterTransition(300L, TimeUnit.MILLISECONDS)
            viewTreeObserver.addOnPreDrawListener {
                startPostponedEnterTransition()
                true
            }
        }

        observeDataChanges()
    }

}