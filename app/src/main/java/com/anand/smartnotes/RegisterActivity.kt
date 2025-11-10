package com.anand.smartnotes

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.anand.smartnotes.ui.theme.*
import com.anand.smartnotes.viewmodel.AuthState
import com.anand.smartnotes.viewmodel.AuthViewModel

class RegisterActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RegisterScreen(
                onRegisterSuccess = {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                },
                onNavigateToLogin = {
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit,
    viewModel: AuthViewModel =
        ViewModelProvider(LocalContext.current as ViewModelStoreOwner)[AuthViewModel::class.java]
) {
    var userId by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var selectedUni by remember { mutableStateOf("Select University") }
    var selectedClass by remember { mutableStateOf("Select Class") }
    var selectedSem by remember { mutableStateOf("Select Semester") }
    var selectedBatch by remember { mutableStateOf("Select Batch") }

    var isPasswordVisible by remember { mutableStateOf(false) }
    var isConfirmPasswordVisible by remember { mutableStateOf(false) }

    val authState by viewModel.authState.collectAsState()

    LaunchedEffect(authState) {
        if (authState is AuthState.Success) {
            onRegisterSuccess()
            viewModel.resetState()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(DeepNavy, DarkGrayishBlue)
                )
            )
    ) {
        // Header Section
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .padding(top = 50.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                modifier = Modifier
                    .size(80.dp)
                    .shadow(
                        elevation = 16.dp,
                        shape = CircleShape,
                        clip = true
                    ),
                shape = CircleShape,
                colors = CardDefaults.cardColors(containerColor = SkyBlue.copy(alpha = 0.2f))
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Register",
                        modifier = Modifier.size(40.dp),
                        tint = SkyBlue
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Create Account",
                color = LightText,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Join SmartNotes today",
                color = LightText.copy(alpha = 0.7f),
                fontSize = 14.sp
            )
        }

        // Scrollable content (inputs)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp,vertical = 24.dp)
                .verticalScroll(rememberScrollState())
                .padding(top = 180.dp, bottom = 140.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // User ID Field
            EnhancedTextField(
                value = userId,
                onValueChange = { userId = it },
                label = "User ID",
                icon = Icons.Default.Email,
                placeholder = "Enter your user ID"
            )

            // Name Field
            EnhancedTextField(
                value = name,
                onValueChange = { name = it },
                label = "Full Name",
                icon = Icons.Default.Person,
                placeholder = "Enter your full name"
            )

            // Password Field
            EnhancedPasswordField(
                value = password,
                onValueChange = { password = it },
                label = "Password",
                isVisible = isPasswordVisible,
                onVisibilityToggle = { isPasswordVisible = !isPasswordVisible },
                placeholder = "Create a strong password"
            )

            // Confirm Password Field
            EnhancedPasswordField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = "Confirm Password",
                isVisible = isConfirmPasswordVisible,
                onVisibilityToggle = { isConfirmPasswordVisible = !isConfirmPasswordVisible },
                placeholder = "Re-enter your password"
            )

            // University Spinner
            EnhancedSpinnerField(
                label = "University",
                options = listOf("PTU", "GNDU", "LPU", "Chandigarh University"),
                selected = selectedUni,
                onSelectedChange = { selectedUni = it },
                icon = Icons.Default.Favorite
            )

            // Class Spinner
            EnhancedSpinnerField(
                label = "Program",
                options = listOf("B.Tech CSE", "B.Tech CS", "B.Tech AI&DS", "B.Tech IT", "MCA"),
                selected = selectedClass,
                onSelectedChange = { selectedClass = it },
                icon = Icons.Default.FavoriteBorder
            )

            // Semester Spinner
            EnhancedSpinnerField(
                label = "Semester",
                options = listOf("Semester 1", "Semester 2", "Semester 3", "Semester 4",
                    "Semester 5", "Semester 6", "Semester 7", "Semester 8"),
                selected = selectedSem,
                onSelectedChange = { selectedSem = it },
                icon = Icons.Default.DateRange
            )

            // Batch Spinner
            EnhancedSpinnerField(
                label = "Batch",
                options = listOf("2022-26", "2023-27", "2024-28", "2025-29", "2026-30"),
                selected = selectedBatch,
                onSelectedChange = { selectedBatch = it },
                icon = Icons.Default.DateRange
            )
        }

        // Bottom-aligned Section (Button + Login Text)
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Register Button
            Button(
                onClick = {
                    viewModel.register(
                        userId, password, confirmPassword,
                        name, selectedUni, selectedClass, selectedSem, selectedBatch
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SkyBlue,
                    contentColor = DeepNavy
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 8.dp,
                    pressedElevation = 4.dp
                )
            ) {
                Text(
                    "Create Account",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Login Prompt
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Already have an account? ",
                    color = LightText.copy(alpha = 0.7f),
                    fontSize = 14.sp
                )
                TextButton(
                    onClick = onNavigateToLogin,
                    colors = ButtonDefaults.textButtonColors(contentColor = SkyBlue)
                ) {
                    Text(
                        text = "Sign In",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        // Loading State
        if (authState is AuthState.Loading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier
                        .width(200.dp)
                        .padding(24.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkGrayishBlue)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        CircularProgressIndicator(
                            color = SkyBlue,
                            strokeWidth = 3.dp
                        )
                        Text(
                            text = "Creating Account...",
                            color = LightText,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnhancedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    placeholder: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(12.dp),
                clip = true
            ),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = DarkGrayishBlue),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = SkyBlue
            )

            Spacer(modifier = Modifier.width(12.dp))

            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                label = {
                    Text(
                        label,
                        color = LightText.copy(alpha = 0.7f),
                        fontSize = 14.sp
                    )
                },
                placeholder = {
                    Text(
                        placeholder,
                        color = LightText.copy(alpha = 0.4f),
                        fontSize = 14.sp
                    )
                },
                modifier = Modifier.weight(1f),
                textStyle = TextStyle(color = LightText, fontSize = 16.sp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = SkyBlue,
                    unfocusedBorderColor = LightText.copy(alpha = 0.3f),
                    focusedLabelColor = SkyBlue,
                    unfocusedLabelColor = LightText.copy(alpha = 0.7f),
                    cursorColor = SkyBlue
                ),
                singleLine = true
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnhancedPasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    isVisible: Boolean,
    onVisibilityToggle: () -> Unit,
    placeholder: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(12.dp),
                clip = true
            ),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = DarkGrayishBlue),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = SkyBlue
            )

            Spacer(modifier = Modifier.width(12.dp))

            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                label = {
                    Text(
                        label,
                        color = LightText.copy(alpha = 0.7f),
                        fontSize = 14.sp
                    )
                },
                placeholder = {
                    Text(
                        placeholder,
                        color = LightText.copy(alpha = 0.4f),
                        fontSize = 14.sp
                    )
                },
                modifier = Modifier.weight(1f),
                textStyle = TextStyle(color = LightText, fontSize = 16.sp),
                visualTransformation = if (isVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = onVisibilityToggle) {
                        Icon(
                            imageVector = if (isVisible) Icons.Default.Done else Icons.Default.Clear,
                            contentDescription = if (isVisible) "Hide password" else "Show password",
                            tint = SkyBlue
                        )
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = SkyBlue,
                    unfocusedBorderColor = LightText.copy(alpha = 0.3f),
                    focusedLabelColor = SkyBlue,
                    unfocusedLabelColor = LightText.copy(alpha = 0.7f),
                    cursorColor = SkyBlue
                ),
                singleLine = true
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnhancedSpinnerField(
    label: String,
    options: List<String>,
    selected: String,
    onSelectedChange: (String) -> Unit,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    var expanded by remember { mutableStateOf(false) }
    val isSelected = selected != "Select ${label.split(" ").lastOrNull() ?: label}"

    Card(
        modifier = Modifier
            .fillMaxWidth().background(LightText.copy(alpha = 0.1f))
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(12.dp),
                clip = true
            ),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = DarkGrayishBlue),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true }
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = SkyBlue
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = label,
                        color = LightText.copy(alpha = 0.7f),
                        fontSize = 14.sp
                    )
                    Text(
                        text = selected,
                        color = if (isSelected) LightText else LightText.copy(alpha = 0.4f),
                        fontSize = 16.sp,
                        fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal
                    )
                }

                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Dropdown",
                    tint = SkyBlue
                )
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.fillMaxWidth(0.9f)
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                option,
                                color = LightText,
                                fontWeight = if (option == selected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        onClick = {
                            onSelectedChange(option)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    RegisterScreen(onRegisterSuccess = {}, onNavigateToLogin = {})
}