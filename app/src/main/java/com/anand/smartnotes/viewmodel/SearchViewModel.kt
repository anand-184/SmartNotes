package com.anand.smartnotes.ui.viewmodel

import android.content.Context
import android.net.Uri
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anand.smartnotes.data.dataclasses.Note
import com.anand.smartnotes.data.repositories.AIResponse
import com.anand.smartnotes.data.repositories.GeminiRepository
import com.anand.smartnotes.data.repositories.MLKitRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

sealed class GeminiUiState {
    object Idle : GeminiUiState()
    data class Processing(val message: String) : GeminiUiState()
    data class Success(val message: String) : GeminiUiState()
    data class Error(val message: String) : GeminiUiState()
}

class NotesViewModel : ViewModel() {

    private val geminiRepository = GeminiRepository()
    private val firestore = FirebaseFirestore.getInstance()

    private val _uploadState = MutableStateFlow<GeminiUiState>(GeminiUiState.Idle)
    val uploadState: StateFlow<GeminiUiState> = _uploadState.asStateFlow()

    fun uploadAndAnalyzeNote(
        userId: String,
        userName: String,
        university: String,
        program: String,
        semester: String,
        imageUri: Uri,
        context: android.content.Context
    ) {
        viewModelScope.launch {
            try {
                // Step 1: Upload image
                _uploadState.value = GeminiUiState.Processing("Uploading image...")
                val imageUrl = uploadImageToStorage(userId, imageUri)

                // Step 2: Extract text
                _uploadState.value = GeminiUiState.Processing("Extracting text...")
                val extractedText = extractTextFromImage(imageUri, context)

                if (extractedText.isBlank()) {
                    throw Exception("No text found in image")
                }

                // Step 3: Get syllabus
                _uploadState.value = GeminiUiState.Processing("Fetching syllabus...")
                val syllabusKey = "${university}_${program.replace(" ", "")}_${semester.replace(" ", "")}"
                val syllabusDoc = firestore.collection("syllabusMapping")
                    .document(syllabusKey)
                    .get()
                    .await()

                val syllabusUrl = syllabusDoc.getString("syllabusUrl")
                    ?: throw Exception("Syllabus not found")

                // Step 4: AI Analysis
                _uploadState.value = GeminiUiState.Processing("Analyzing with AI...")

                geminiRepository.generateSummaryAndQuestions(
                    extractedText = extractedText,
                    syllabusUrl = syllabusUrl,
                    university = university,
                    program = program,
                    semester = semester
                ).onSuccess { aiResponse ->

                    // Step 5: Create Note from AIResponse
                    val note = createNoteFromAIResponse(
                        aiResponse = aiResponse,
                        userId = userId,
                        userName = userName,
                        university = university,
                        program = program,
                        semester = semester,
                        imageUrl = imageUrl,
                        extractedText = extractedText
                    )

                    // Step 6: Save to Firestore
                    _uploadState.value = GeminiUiState.Processing("Saving note...")

                    val docRef = firestore.collection("notes")
                        .add(note)
                        .await()

                    firestore.collection("notes")
                        .document(docRef.id)
                        .update("id", docRef.id)
                        .await()

                    _uploadState.value = GeminiUiState.Success("Note saved successfully!")

                }.onFailure { exception ->
                    throw exception
                }

            } catch (e: Exception) {
                _uploadState.value = GeminiUiState.Error(e.message ?: "Error occurred")
            }
        }
    }

    // ✅ Helper function to create Note from AIResponse
    private fun createNoteFromAIResponse(
        aiResponse: AIResponse,
        userId: String,
        userName: String,
        university: String,
        program: String,
        semester: String,
        imageUrl: String,
        extractedText: String
    ): Note {
        return Note(
            id = "",
            userId = userId,
            userName = userName,
            university = university,
            program = program,
            semester = semester,
            imageUrl = imageUrl,
            extractedText = extractedText,
            summary = aiResponse.summary,  // List<String>
            questions = aiResponse.questions,  // List<QuestionAnswer>
            answers = aiResponse.questions.map { it.answer },  // List<String>
            topic = aiResponse.topic,  // String
            syllabusChapter = aiResponse.syllabusChapter,  // String
            timestamp = System.currentTimeMillis()
        )
    }

    private suspend fun uploadImageToStorage(userId: String, imageUri: Uri): String =
        withContext(Dispatchers.IO) {
            val storageRef = FirebaseStorage.getInstance().reference
            val fileName = "notes_${userId}_${System.currentTimeMillis()}.jpg"
            val imageRef = storageRef.child("user_notes/$userId/$fileName")

            imageRef.putFile(imageUri).await()
            imageRef.downloadUrl.await().toString()
        }

    private suspend fun extractTextFromImage(imageUri: Uri, context: android.content.Context): String =
        withContext(Dispatchers.IO) {
            val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
            val inputImage = InputImage.fromFilePath(context, imageUri)

            val result = recognizer.process(inputImage).await()
            result.text
        }

    fun resetState() {
        _uploadState.value = GeminiUiState.Idle
    }

}
