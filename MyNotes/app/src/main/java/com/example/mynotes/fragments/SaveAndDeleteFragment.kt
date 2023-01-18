package com.example.mynotes.fragments

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResult
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.example.mynotes.R
import com.example.mynotes.activities.MainActivity
import com.example.mynotes.databinding.BottomSheetLayoutBinding
import com.example.mynotes.databinding.FragmentSaveAndDeleteBinding
import com.example.mynotes.model.Note
import com.example.mynotes.utils.hideKeyboard
import com.example.mynotes.viewModel.NoteActivityViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.transition.MaterialContainerTransform
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class SaveAndDeleteFragment : Fragment(R.layout.fragment_save_and_delete) {

    private lateinit var navController: NavController
    private lateinit var contentBinding: FragmentSaveAndDeleteBinding
    private var note: Note? = null
    private var color = -1
    private lateinit var result: String
    private val formatter = SimpleDateFormat("dd.MM.yyyy 'at' HH:mm:ss", Locale("ru", "RU"))
    private val dateString = formatter.format(Date())
    private val noteActivityViewModel: NoteActivityViewModel by activityViewModels()
    private val job = CoroutineScope(Dispatchers.Main)
    private val args: SaveAndDeleteFragmentArgs by navArgs()
    lateinit var temporaryColorHEX: String
    private val setOfUniqueColors = mutableSetOf<String>()
    private val rnd = Random()
    private var temporaryColorARGB = 0
    private var colorArray: IntArray = intArrayOf()
    private val supportingColorHashSet = hashSetOf<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        while (supportingColorHashSet.size != 20) {
            supportingColorHashSet.add(Random().nextInt(21))
        }

        colorArray = supportingColorHashSet.toIntArray()

        val animation = MaterialContainerTransform().apply {
            drawingViewId = R.id.fragment
            scrimColor = Color.TRANSPARENT
            duration = 300
        }

        sharedElementEnterTransition = animation
        sharedElementReturnTransition = animation
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        contentBinding = FragmentSaveAndDeleteBinding.bind(view)
        navController = Navigation.findNavController(view)
        val activity = activity as MainActivity

        ViewCompat.setTransitionName(
            contentBinding.noteContentFragmentParent,
            "recyclerView_${args.note?.id}"
        )

        contentBinding.backButton.setOnClickListener {
            requireView().hideKeyboard()
            navController.popBackStack()
        }

        contentBinding.lastEdited.text = getString(R.string.edited_on, dateString)

        contentBinding.saveNote.setOnClickListener {
            saveNote()
        }

        try {
            contentBinding.editTextNoteContent.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    contentBinding.bottomBar.visibility = View.VISIBLE
                    contentBinding.floatingActionButtonColorPick.visibility = View.GONE
                    contentBinding.editTextNoteContent.setStylesBar(contentBinding.styleBar)
                } else {
                    contentBinding.bottomBar.visibility = View.GONE
                    contentBinding.floatingActionButtonColorPick.visibility = View.VISIBLE
                }

            }
        } catch (e: java.lang.Exception) {
            Log.d("TAG", e.stackTraceToString())
        }

        setUpUniqueColors()

//        contentBinding.toolbarFragmentNoteContent.background = colorArray[rnd.nextInt(colorArray.size)].toDrawable()

        contentBinding.floatingActionButtonColorPick.setOnClickListener {
            val bottomSheetDialog = BottomSheetDialog(
                requireContext(),
                R.style.BottomSheetDialogTheme
            )

            val bottomSheetView: View = layoutInflater.inflate(
                R.layout.bottom_sheet_layout, null
            )
            with(bottomSheetDialog) {
                setContentView(bottomSheetView)
                show()
            }

            val bottomSheetBinding = BottomSheetLayoutBinding.bind(bottomSheetView)
            bottomSheetBinding.apply {
                colorPicker.apply {
                    this.setColors(colorArray)
                    this.setFixedColumnCount(colorArray.size)
                    setSelectedColor(color)
                    setOnColorSelectedListener { value ->
                        color = value
                        contentBinding.apply {
                            noteContentFragmentParent.setBackgroundColor(color)
                            toolbarFragmentNoteContent.setBackgroundColor(color)
                            bottomBar.setBackgroundColor(color)
                            activity.window.statusBarColor = color
                            bottomSheetContainer.setBackgroundColor(color)
                        }
                        bottomSheetBinding.bottomSheetParent.setCardBackgroundColor(color)
                    }
                }
                bottomSheetParent.setBackgroundColor(color)
            }
            bottomSheetView.post {
                bottomSheetDialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
            }

            setUpUniqueColors()
        }

        setUpNote()
    }

    private fun setUpNote() {
        val note = args.note
        val title = contentBinding.editTextTitle
        val content = contentBinding.editTextNoteContent
        val lastEdited = contentBinding.lastEdited

        if (note == null) {
            contentBinding.lastEdited.text = getString(R.string.edited_on, dateString)
        }

        if (note != null) {
            title.setText(note.title)
            content.renderMD(note.content)
            lastEdited.text = getString(R.string.edited_on,note.date)
            color = note.color
            contentBinding.apply {
                job.launch {
                    delay(10)
                    noteContentFragmentParent.setBackgroundColor(color)
                }
                toolbarFragmentNoteContent.setBackgroundColor(color)
                bottomBar.setBackgroundColor(color)
            }
            activity?.window?.statusBarColor = note.color
        }
    }

    private fun setUpUniqueColors() {
        for (i in colorArray.indices) {
            temporaryColorARGB = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))
            temporaryColorHEX = java.lang.String.format("#%06X", 0xFFFFFF and temporaryColorARGB)
            if (!setOfUniqueColors.add(temporaryColorHEX)) {
                continue
            }
            colorArray[i] = temporaryColorARGB
        }
    }

    private fun saveNote() {
        if (contentBinding.editTextNoteContent.text.toString().isEmpty() ||
            contentBinding.editTextTitle.text.toString().isEmpty()
        ) {
            Toast.makeText(activity, "Content or title is empty", Toast.LENGTH_SHORT).show()
        } else {
            note = args.note
            when (note) {
                null -> {
                    noteActivityViewModel.saveNote(
                        Note(
                            0,
                            contentBinding.editTextTitle.text.toString(),
                            contentBinding.editTextNoteContent.getMD(),
                            dateString,
                            color
                        )
                    )
                    result = "Note saved"
                    setFragmentResult(
                        "key",
                        bundleOf("bundleKey" to result)
                    )

                    navController.navigate(SaveAndDeleteFragmentDirections.actionSaveAndDeleteFragmentToNoteFragment())
                }
                else -> {
                    updateNote()
                    navController.popBackStack()
                }

            }

        }
    }

    private fun updateNote() {
        if (note != null) {
            noteActivityViewModel.updateNote(
                Note(
                    note!!.id,
                    contentBinding.editTextTitle.text.toString(),
                    contentBinding.editTextNoteContent.getMD(),
                    dateString,
                    color
                )
            )
        }
    }

}