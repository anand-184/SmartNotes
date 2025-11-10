package com.anand.smartnotes.viewmodel

import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anand.smartnotes.data.dataclasses.Note
import com.anand.smartnotes.data.repositories.NotesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

class NotesViewModel: ViewModel() {
    private val repository = NotesRepository()
    private val _notes = MutableStateFlow<List<Note>>(emptyList())
    val notes: StateFlow<List<Note>> = _notes

    init {
        loadNotes()
    }

    private fun loadNotes() {
        viewModelScope.launch {
            repository.getNotes().onSuccess { notes ->
                _notes.value = notes
            }.onFailure { exception ->
                Log.e("NotesViewModel", "Failed to load notes", exception)
            }
        }
    }

    fun getTopTopic(notes: List<Note>): String {
        return notes.groupingBy { it.program }.eachCount().maxByOrNull { it.value }?.key ?: "N/A"
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getWeeklyUploads(notes: List<Note>): Int {
        val weekAgo = LocalDate.now().minusDays(7)
        return notes.count {
            val date = Instant.ofEpochMilli(it.timestamp)
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
            date.isAfter(weekAgo)
        }
    }

    fun getOldNotes(notes: List<Note>): String {
        return notes.take(2).joinToString { it.program } // just a demo
    }


}

