package com.example.mynotes.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity
@Parcelize
data class Note(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo var id: Int = 0,
    @ColumnInfo val title: String,
    @ColumnInfo val content: String,
    @ColumnInfo val date: String,
    @ColumnInfo val color: Int = -1,

    ) : Parcelable
