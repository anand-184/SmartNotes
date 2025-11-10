package com.anand.smartnotes.view.screens

import SearchViewModel
import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Face
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.anand.smartnotes.data.repositories.AuthRepository
import com.anand.smartnotes.ui.theme.BrightYellow
import com.anand.smartnotes.ui.theme.DarkGrayishBlue
import com.anand.smartnotes.ui.theme.DeepNavy
import com.anand.smartnotes.ui.theme.LightText
import com.anand.smartnotes.ui.theme.SkyBlue
import com.anand.smartnotes.viewmodel.AuthViewModel



@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
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

    val isProcessing = uploadState is GeminiUiState.Processing

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
                    batch = user.batch,
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
                        batch = user.batch,
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

    // ---------------- Enhanced UI Layout ----------------
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(DeepNavy, DarkGrayishBlue)
                )
            )
    ) {
        // Header Section
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(
                            color = SkyBlue.copy(alpha = 0.1f),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Face,
                        contentDescription = "AI Analysis",
                        tint = SkyBlue,
                        modifier = Modifier.size(40.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "AI Notes Analyzer",
                    color = LightText,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "Upload your notes and get instant AI-powered insights",
                    color = LightText.copy(alpha = 0.7f),
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }

        // Upload Card Section
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 8.dp)
                    .shadow(
                        elevation = 12.dp,
                        shape = RoundedCornerShape(20.dp),
                        clip = true
                    ),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = DarkGrayishBlue)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Upload Button
                    Button(
                        onClick = { showDialog = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SkyBlue,
                            contentColor = DeepNavy
                        ),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 8.dp,
                            pressedElevation = 4.dp
                        )
                    ) {
                        Icon(
                            Icons.Default.Done,
                            contentDescription = "Upload",
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            "Upload Photo Notes",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Upload Status
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        val animatedAlpha by animateFloatAsState(
                            targetValue = if (isImageUploaded) 1f else 0.5f,
                            label = "statusAlpha"
                        )

                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .background(
                                    color = if (isImageUploaded) BrightYellow else LightText.copy(alpha = 0.3f),
                                    shape = CircleShape
                                )
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Text(
                            text = if (isImageUploaded) "✓ Notes Uploaded Successfully" else "Waiting for upload...",
                            color = LightText.copy(alpha = animatedAlpha),
                            fontSize = 14.sp,
                            fontWeight = if (isImageUploaded) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                }
            }
        }

        // Processing State
        if (isProcessing) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SkyBlue.copy(alpha = 0.1f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(
                            color = SkyBlue,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = (uploadState as GeminiUiState.Processing).message,
                            color = LightText,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        // Results Section
        when (uploadState) {
            is GeminiUiState.Success -> {
                val aiResponse = (uploadState as GeminiUiState.Success).aiResponse

                // Success Header
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = BrightYellow.copy(alpha = 0.1f))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(
                                        color = BrightYellow.copy(alpha = 0.2f),
                                        shape = CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("✅", fontSize = 18.sp)
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                text = "AI Analysis Completed",
                                color = BrightYellow,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Summary Section
                item {
                    AnalysisSection(
                        title = "📝 Summary",
                        content = aiResponse.summary,
                        accentColor = SkyBlue,
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                    )
                }

                // Q&A Section
                item {
                    AnalysisSection(
                        title = "❓ Questions & Answers",
                        content = aiResponse.questions.mapIndexed { i, qa ->
                            "Q${i + 1}: ${qa.question}\nA: ${qa.answer}"
                        },
                        accentColor = BrightYellow,
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                    )
                }

                // Add some bottom padding
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }

            is GeminiUiState.Error -> {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFEE2E2))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("⚠️", fontSize = 24.sp)
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                text = (uploadState as GeminiUiState.Error).message,
                                color = Color(0xFFDC2626),
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }

            else -> {
                // Idle state - no additional content
            }
        }
    }

    // --------------- Enhanced Dialog ----------------
    if (showDialog) {
        Dialog(onDismissRequest = { showDialog = false }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = DarkGrayishBlue)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp)
                ) {
                    Text(
                        text = "Choose Image Source",
                        color = LightText,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Camera Option
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                showDialog = false
                                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                            }
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(
                                    color = SkyBlue.copy(alpha = 0.1f),
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.ArrowDropDown,
                                contentDescription = "Camera",
                                tint = SkyBlue
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = "Take Photo",
                            color = LightText,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    // Gallery Option
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                showDialog = false
                                galleryLauncher.launch("image/*")
                            }
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(
                                    color = BrightYellow.copy(alpha = 0.1f),
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.ArrowDropDown,
                                contentDescription = "Gallery",
                                tint = BrightYellow
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = "Choose from Gallery",
                            color = LightText,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AnalysisSection(
    title: String,
    content: List<String>,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(16.dp),
                clip = true
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkGrayishBlue)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = title,
                color = accentColor,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                content.forEach { item ->
                    Text(
                        text = "• $item",
                        color = LightText,
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    )
                }
            }
        }
    }
}

fun createImageUri(context: Context): Uri {
    val contentValues = ContentValues().apply {
        put(MediaStore.Images.Media.DISPLAY_NAME, "temp_${System.currentTimeMillis()}.jpg")
        put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
    }
    return context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)!!
}

@Preview
@Composable
fun PreviewSearchScreen() {
    SearchScreen()
}