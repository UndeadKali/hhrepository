package com.example.mynotes.utils

import androidx.recyclerview.widget.ItemTouchHelper

abstract class SwipeToDelete: ItemTouchHelper.SimpleCallback(
    ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.START or ItemTouchHelper.END or ItemTouchHelper.ACTION_STATE_DRAG,
    ItemTouchHelper.START or ItemTouchHelper.END
) {

}