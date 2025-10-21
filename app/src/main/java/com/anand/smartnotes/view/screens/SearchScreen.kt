package com.anand.smartnotes.view.screens

import androidx.lifecycle.viewmodel.compose.viewModel
import SearchViewModel
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CheckboxDefaults.colors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.anand.smartnotes.ui.theme.DeepNavy
import com.anand.smartnotes.ui.theme.LightText
import android.Manifest
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import com.anand.smartnotes.data.repositories.AuthRepository
import com.anand.smartnotes.viewmodel.AuthViewModel


@Composable
fun SearchScreen() {
    val authViewModel: AuthViewModel = viewModel()
    val viewModel: SearchViewModel = viewModel()
    val context = LocalContext.current
    val authRepository = AuthRepository()

    val uploadState by viewModel.uploadState.collectAsState()
    val isImageUploaded by viewModel.isImageUploaded.collectAsState()
    val aiResult by viewModel.aiResult.collectAsState()

    var showDialog by remember { mutableStateOf(false) }
    var cameraImageUri by remember { mutableStateOf<Uri?>(null) }
    val currentUser by viewModel.currentUser.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchCurrentUser()
    }

    // ---------------- Launchers ----------------
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            currentUser?.let { user ->
                viewModel.uploadAndAnalyzeNote(
                    userId = user.id,
                    userName = user.userName,
                    university = user.university,
                    program = user.program,
                    semester = user.semester,
                    imageUri = it,
                    context = context

                )
            }

        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            cameraImageUri?.let { uri ->
                currentUser?.let { user ->
                    viewModel.uploadAndAnalyzeNote(
                        userId = user.id,
                        userName = user.userName,
                        university = user.university,
                        program = user.program,
                        semester = user.semester,
                        imageUri = uri,
                        context = context

                    )
                }

           }
        }
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            cameraImageUri = createImageUri(context)
            cameraLauncher.launch(cameraImageUri!!)
        } else {
            Toast.makeText(context, "Camera permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    // ---------------- UI Layout ----------------
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        // Upload row
        Row(verticalAlignment = Alignment.CenterVertically) {
            Button(onClick = { showDialog = true }) {
                Text("📸 Upload Photo")
            }

            Spacer(modifier = Modifier.width(8.dp))

            Checkbox(
                checked = isImageUploaded,
                onCheckedChange = null,
                colors = CheckboxDefaults.colors(checkedColor = DeepNavy)
            )

            Text(
                text = if (isImageUploaded) "Uploaded" else "Not Uploaded",
                color = LightText,
                fontSize = 14.sp
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Status text
        when (uploadState) {
            is GeminiUiState.Idle -> Text("Idle", color = LightText)
            is GeminiUiState.Processing -> Text(
                (uploadState as GeminiUiState.Processing).message,
                color = LightText
            )
            is GeminiUiState.Error -> Text(
                (uploadState as GeminiUiState.Error).message,
                color = LightText
            )

            is GeminiUiState.Success -> {
                val aiResponse = (uploadState as GeminiUiState.Success).aiResponse

                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "✅ AI Analysis Completed",
                    fontSize = 18.sp,
                    color = DeepNavy,
                    fontFamily = FontFamily.Serif
                )

                Spacer(modifier = Modifier.height(12.dp))

                Column (modifier = Modifier.fillMaxWidth().padding(8.dp).verticalScroll(rememberScrollState()))
                {


                    // --- Summary Section ---
                    Text(
                        text = "Summary",
                        fontSize = 20.sp,
                        color = DeepNavy,
                        fontFamily = FontFamily.Serif
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                        aiResponse.summary.forEach { point ->
                            Text("• $point", fontSize = 15.sp, color = LightText)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // --- Q&A Section ---
                    Text(
                        text = "Questions & Answers",
                        fontSize = 20.sp,
                        color = DeepNavy,
                        fontFamily = FontFamily.Serif
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    aiResponse.questions.forEachIndexed { i, qa ->
                        Column(modifier = Modifier.padding(vertical = 6.dp)) {
                            Text("${i + 1}. Q: ${qa.question}", color = LightText, fontSize = 15.sp)
                            Text("   A: ${qa.answer}", color = LightText, fontSize = 14.sp)
                        }
                    }


                }

            }

            else -> {}
        }
    }

    // --------------- Dialog ----------------
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Choose Image Source") },
            text = {
                Column {
                    Text(
                        text = "Camera",
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                showDialog = false
                                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                            }
                            .padding(12.dp)
                    )
                    Text(
                        text = "Gallery",
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                showDialog = false
                                galleryLauncher.launch("image/*")
                            }
                            .padding(12.dp)
                    )
                }
            },
            confirmButton = {}
        )
    }
}

fun createImageUri(context: Context): Uri {
    val contentValues = ContentValues().apply{ put(MediaStore.Images.Media.DISPLAY_NAME,
        "temp_${System.currentTimeMillis()}.jpg")
        put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg") }
    return context.contentResolver.insert( MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues )!! }


@Preview
@Composable
fun PreviewSearchScreen() {
    SearchScreen()
}

