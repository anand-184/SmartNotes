import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anand.smartnotes.data.cloud.ImageUploader
import com.anand.smartnotes.data.dataclasses.Note
import com.anand.smartnotes.data.dataclasses.User
import com.anand.smartnotes.data.repositories.AIResponse
import com.anand.smartnotes.data.repositories.AuthRepository
import com.anand.smartnotes.data.repositories.GeminiRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

sealed class GeminiUiState {
    object Idle : GeminiUiState()
    data class Processing(val message: String) : GeminiUiState()
    data class Success(val aiResponse: AIResponse) : GeminiUiState()
    data class ImageUploaded(val imageUrl: String) : GeminiUiState()
    data class Error(val message: String) : GeminiUiState()
}

class SearchViewModel : ViewModel() {

    private val geminiRepository = GeminiRepository()
    private val firestore = FirebaseFirestore.getInstance()
    private val authRepository = AuthRepository()

    private val _uploadState = MutableStateFlow<GeminiUiState>(GeminiUiState.Idle)
    val uploadState: StateFlow<GeminiUiState> = _uploadState.asStateFlow()

    private val _isImageUploaded = MutableStateFlow(false)
    val isImageUploaded: StateFlow<Boolean> = _isImageUploaded.asStateFlow()

    private val _aiResult = MutableStateFlow("")
    val aiResult: StateFlow<String> = _aiResult.asStateFlow()

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()


    fun uploadAndAnalyzeNote(
        userId: String,
        userName: String,
        university: String,
        program: String,
        semester: String,
        batch: String,
        imageUri: Uri,
        context: Context
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {

                _uploadState.value = GeminiUiState.Processing("Uploading image...")

                var imageUrlReturned: String? = null
                // --- CLOUDINARY UPLOAD (single call, no double upload)
                suspendCancellableCoroutine<Unit> { cont ->
                    ImageUploader.uploadImage(
                        imageUri,
                        onSuccess = { url ->
                            imageUrlReturned = url
                            _isImageUploaded.value = true // checkbox update
                            _uploadState.value = GeminiUiState.ImageUploaded(url)
                            cont.resume(Unit) {}
                        },
                        onError = { throwable ->
                            _uploadState.value =
                                GeminiUiState.Error(throwable.message ?: "Upload failed")
                            _isImageUploaded.value = false
                            cont.resume(Unit) {}
                        }
                    )
                }
                val imageUrl = imageUrlReturned ?: throw Exception("Image upload failed")

                _uploadState.value = GeminiUiState.Processing("Extracting text...")
                val extractedText = extractTextFromImage(imageUri, context)
                if (extractedText.isBlank()) throw Exception("No text found in image")

                Log.d("extractedText", extractedText)


                _uploadState.value = GeminiUiState.Processing("Analyzing with AI...")
                geminiRepository.generateSummaryAndQuestions(
                    extractedText, university, program, semester, batch
                ).onSuccess { aiResponse ->
                    val note = createNoteFromAIResponse(
                        aiResponse,
                        userId,
                        userName,
                        university,
                        program,
                        semester,
                        batch,
                        imageUrl,
                        extractedText
                    )
                    _uploadState.value = GeminiUiState.Processing("Saving note...")
                    val docRef = firestore.collection("notes").add(note).await()
                    firestore.collection("notes").document(docRef.id).update("id", docRef.id)
                        .await()
                    _aiResult.value = buildString {
                        append("Summary:\n")
                        aiResponse.summary.forEach { append("• $it\n") }
                        append("\nQuestions & Answers:\n")
                        aiResponse.questions.forEachIndexed { i, qa ->
                            append("${i + 1}. Q: ${qa.question}\n")
                            append("   A: ${qa.answer}\n\n")
                        }
                    }
                    _uploadState.value = GeminiUiState.Success(aiResponse)
                }.onFailure { e ->
                    throw e
                }
            } catch (e: Exception) {
                _uploadState.value = GeminiUiState.Error("Error: ${e.message}")
                _isImageUploaded.value = false
                _aiResult.value = ""
            }
        }
    }

    private fun createNoteFromAIResponse(
        aiResponse: AIResponse,
        userId: String,
        userName: String,
        university: String,
        program: String,
        semester: String,
        imageUrl: String,
        batch: String,
        extractedText: String
    ): Note {
        return Note(
            id = "",
            userId = userId,
            userName = userName,
            university = university,
            program = program,
            semester = semester,
            batch = batch,
            imageUrl = imageUrl,
            extractedText = extractedText,
            summary = aiResponse.summary,
            questions = aiResponse.questions,
            answers = aiResponse.questions.map { it.answer },
            topic = aiResponse.topic,
            syllabusChapter = aiResponse.syllabusChapter,
            timestamp = System.currentTimeMillis()
        )
    }

    fun fetchCurrentUser() {
        viewModelScope.launch {
            val user = authRepository.getCurrentUserData() // Your suspend fun as above
            _currentUser.value = user
        }
    }

    private suspend fun extractTextFromImage(imageUri: Uri, context: Context): String =
        withContext(Dispatchers.IO) {
            val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
            val inputImage = InputImage.fromFilePath(context, imageUri)
            recognizer.process(inputImage).await().text
        }

    fun resetState() {
        _uploadState.value = GeminiUiState.Idle
        _isImageUploaded.value = false
        _aiResult.value = ""
    }
}
