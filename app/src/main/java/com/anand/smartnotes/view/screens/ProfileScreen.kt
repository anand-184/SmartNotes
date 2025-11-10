package com.anand.smartnotes.view.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.anand.smartnotes.ui.theme.BrightYellow
import com.anand.smartnotes.ui.theme.DarkGrayishBlue
import com.anand.smartnotes.ui.theme.DeepNavy
import com.anand.smartnotes.ui.theme.LightText
import com.anand.smartnotes.ui.theme.SkyBlue
import com.anand.smartnotes.viewmodel.ProfileUiState
import com.anand.smartnotes.viewmodel.ProfileViewModel



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = viewModel(),
    onLogout: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var dialogLabel by remember { mutableStateOf("") }
    var dialogValue by remember { mutableStateOf("") }
    var dialogOptions by remember { mutableStateOf<List<String>?>(null) }
    var onSave: (String) -> Unit by remember { mutableStateOf({}) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "My Profile",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = LightText
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = DeepNavy,
                    titleContentColor = LightText
                )
            )
        },
        containerColor = DeepNavy
    ) { paddingValues ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            color = Color.Transparent
        ) {
            when (uiState) {
                is ProfileUiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            CircularProgressIndicator(
                                color = SkyBlue,
                                strokeWidth = 3.dp
                            )
                            Text(
                                text = "Loading Profile...",
                                color = LightText,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
                is ProfileUiState.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            colors = CardDefaults.cardColors(containerColor = DarkGrayishBlue),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(32.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(80.dp)
                                        .background(
                                            color = Color(0xFFFEE2E2),
                                            shape = CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = "Error",
                                        modifier = Modifier.size(40.dp),
                                        tint = Color(0xFFDC2626)
                                    )
                                }
                                Text(
                                    text = "Oops!",
                                    style = MaterialTheme.typography.headlineSmall,
                                    color = LightText,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = (uiState as ProfileUiState.Error).message,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = LightText.copy(alpha = 0.8f),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
                is ProfileUiState.Success -> {
                    val user = (uiState as ProfileUiState.Success).user
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(DeepNavy, DarkGrayishBlue)
                                )
                            )
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Enhanced Profile Avatar
                        Card(
                            modifier = Modifier
                                .size(140.dp)
                                .shadow(
                                    elevation = 16.dp,
                                    shape = CircleShape,
                                    clip = true
                                ),
                            shape = CircleShape,
                            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                            colors = CardDefaults.cardColors(containerColor = SkyBlue.copy(alpha = 0.2f))
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "Profile",
                                    modifier = Modifier.size(60.dp),
                                    tint = SkyBlue
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // User Name with Badge
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 32.dp),
                            colors = CardDefaults.cardColors(containerColor = BrightYellow.copy(alpha = 0.1f)),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = user.userName,
                                    style = MaterialTheme.typography.headlineSmall,
                                    color = BrightYellow,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Student",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = LightText.copy(alpha = 0.7f),
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        // Enhanced User Information Card
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                            colors = CardDefaults.cardColors(containerColor = DarkGrayishBlue),
                            shape = RoundedCornerShape(24.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                // Name
                                EnhancedProfileInfoItem(
                                    icon = Icons.Default.Person,
                                    label = "Full Name",
                                    value = user.userName,
                                    onEditClick = {
                                        dialogLabel = "Name"
                                        dialogValue = user.userName
                                        dialogOptions = null
                                        onSave = { viewModel.updateUserName(it) }
                                        showDialog = true
                                    },
                                    iconColor = SkyBlue
                                )

                                Divider(color = LightText.copy(alpha = 0.1f), thickness = 1.dp)

                                // Email (Non-editable)
                                EnhancedProfileInfoItem(
                                    icon = Icons.Default.Email,
                                    label = "Email Address",
                                    value = user.userEmail,
                                    onEditClick = null, // No edit button for email
                                    iconColor = BrightYellow
                                )

                                Divider(color = LightText.copy(alpha = 0.1f), thickness = 1.dp)

                                // University
                                EnhancedProfileInfoItem(
                                    icon = Icons.Default.Favorite,
                                    label = "University",
                                    value = user.university,
                                    onEditClick = {
                                        dialogLabel = "University"
                                        dialogValue = user.university
                                        dialogOptions = listOf("PTU", "GNDU", "LPU", "Chandigarh University")
                                        onSave = { viewModel.updateUniversity(it) }
                                        showDialog = true
                                    },
                                    iconColor = Color(0xFF10B981)
                                )

                                Divider(color = LightText.copy(alpha = 0.1f), thickness = 1.dp)

                                // Program
                                EnhancedProfileInfoItem(
                                    icon = Icons.Default.AccountBox,
                                    label = "Program",
                                    value = user.program,
                                    onEditClick = {
                                        dialogLabel = "Program"
                                        dialogValue = user.program
                                        dialogOptions = listOf("B.Tech CSE", "B.Tech CS", "B.Tech AI&DS", "B.Tech IT", "MCA", "MBA")
                                        onSave = { viewModel.updateProgram(it) }
                                        showDialog = true
                                    },
                                    iconColor = Color(0xFF8B5CF6)
                                )

                                Divider(color = LightText.copy(alpha = 0.1f), thickness = 1.dp)

                                // Batch
                                EnhancedProfileInfoItem(
                                    icon = Icons.Default.DateRange,
                                    label = "Batch",
                                    value = user.batch,
                                    onEditClick = {
                                        dialogLabel = "Batch"
                                        dialogValue = user.batch
                                        dialogOptions = listOf("2022-26", "2023-27", "2024-28", "2025-29", "2026-30")
                                        onSave = { viewModel.updateBatch(it) }
                                        showDialog = true
                                    },
                                    iconColor = Color(0xFFF59E0B)
                                )

                                Divider(color = LightText.copy(alpha = 0.1f), thickness = 1.dp)

                                // Semester
                                EnhancedProfileInfoItem(
                                    icon = Icons.Default.DateRange,
                                    label = "Semester",
                                    value = user.semester,
                                    onEditClick = {
                                        dialogLabel = "Semester"
                                        dialogValue = user.semester
                                        dialogOptions = listOf("Semester 1", "Semester 2", "Semester 3", "Semester 4",
                                            "Semester 5", "Semester 6", "Semester 7", "Semester 8")
                                        onSave = { viewModel.updateSemester(it) }
                                        showDialog = true
                                    },
                                    iconColor = Color(0xFFEF4444)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        // Enhanced Logout Button
                        Button(
                            onClick = onLogout,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFDC2626).copy(alpha = 0.9f),
                                contentColor = Color.White
                            ),
                            elevation = ButtonDefaults.buttonElevation(
                                defaultElevation = 8.dp,
                                pressedElevation = 4.dp
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.ExitToApp,
                                contentDescription = "Logout",
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Logout",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    }

    // Enhanced Dialog for editing
    if (showDialog) {
        if (dialogOptions != null) {
            EnhancedSpinnerDialog(
                label = dialogLabel,
                currentValue = dialogValue,
                options = dialogOptions!!,
                onSave = onSave,
                onDismiss = { showDialog = false }
            )
        } else {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = {
                    Text(
                        "Edit $dialogLabel",
                        color = LightText,
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    OutlinedTextField(
                        value = dialogValue,
                        onValueChange = { dialogValue = it },
                        label = { Text("Enter new $dialogLabel", color = LightText.copy(alpha = 0.7f)) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SkyBlue,
                            unfocusedBorderColor = LightText.copy(alpha = 0.3f)
                        )
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            onSave(dialogValue)
                            showDialog = false
                        },
                        colors = ButtonDefaults.textButtonColors(contentColor = SkyBlue)
                    ) {
                        Text("Save", fontWeight = FontWeight.SemiBold)
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showDialog = false },
                        colors = ButtonDefaults.textButtonColors(contentColor = LightText.copy(alpha = 0.7f))
                    ) {
                        Text("Cancel")
                    }
                },
                containerColor = DarkGrayishBlue,
                titleContentColor = LightText,
                textContentColor = LightText
            )
        }
    }
}

@Composable
fun EnhancedSpinnerDialog(
    label: String,
    currentValue: String,
    options: List<String>,
    onSave: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedValue by remember { mutableStateOf(currentValue) }
    var expanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "Edit $label",
                color = LightText,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Box {
                OutlinedTextField(
                    value = selectedValue,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Select $label", color = LightText.copy(alpha = 0.7f)) },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "Dropdown",
                            modifier = Modifier.clickable { expanded = true },
                            tint = SkyBlue
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { expanded = true },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SkyBlue,
                        unfocusedBorderColor = LightText.copy(alpha = 0.3f)
                    )
                )
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    options.forEach { option ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    option,
                                    color = LightText,
                                    fontWeight = if (option == selectedValue) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            onClick = {
                                selectedValue = option
                                expanded = false
                            }
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onSave(selectedValue)
                    onDismiss()
                },
                colors = ButtonDefaults.textButtonColors(contentColor = SkyBlue)
            ) {
                Text("Save", fontWeight = FontWeight.SemiBold)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(contentColor = LightText.copy(alpha = 0.7f))
            ) {
                Text("Cancel")
            }
        },
        containerColor = DarkGrayishBlue,
        titleContentColor = LightText,
        textContentColor = LightText
    )
}

@Composable
private fun EnhancedProfileInfoItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    onEditClick: (() -> Unit)?,
    iconColor: Color
) {
    val animatedElevation by animateFloatAsState(
        targetValue = if (onEditClick != null) 4f else 0f,
        label = "cardElevation"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = onEditClick != null) { onEditClick?.invoke() }
            .shadow(
                elevation = animatedElevation.dp,
                shape = RoundedCornerShape(12.dp),
                clip = true
            ),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(
                        color = iconColor.copy(alpha = 0.1f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = iconColor
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium,
                    color = LightText.copy(alpha = 0.7f)
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyLarge,
                    color = LightText,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // Edit Icon Button (only show if onEditClick is provided)
            if (onEditClick != null) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(
                            color = SkyBlue.copy(alpha = 0.1f),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit $label",
                        modifier = Modifier.size(18.dp),
                        tint = SkyBlue
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun ProfileScreenPreview() {
    ProfileScreen(onLogout = {})
}