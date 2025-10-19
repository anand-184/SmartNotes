package com.anand.smartnotes.data.repositories

import com.anand.smartnotes.data.dataclasses.Note
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await


class NotesRepository {
    private val firestore = FirebaseFirestore.getInstance()

    suspend fun getNotes(): Result<List<Note>> {
        return try {
            // Firestore automatically cache se data dega agar offline hai
            val snapshot = firestore.collection("notes").get().await()
            Result.success(snapshot.toObjects(Note::class.java))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}