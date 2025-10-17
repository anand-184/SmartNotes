package com.anand.smartnotes

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.anand.smartnotes.ui.theme.DarkGrayishBlue
import com.anand.smartnotes.ui.theme.DeepNavy
import com.anand.smartnotes.ui.theme.LightText
import com.anand.smartnotes.viewmodel.AuthState
import com.anand.smartnotes.viewmodel.AuthViewModel



class RegisterActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
                RegisterScreen(
                    onRegisterSuccess ={
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
@Composable
fun RegisterScreen(onRegisterSuccess: () -> Unit,
                   onNavigateToLogin: () -> Unit,
                   viewModel: AuthViewModel =
                       ViewModelProvider(LocalContext.current as ViewModelStoreOwner)
                           [AuthViewModel::class.java] // ✅ correct for Activity
)
{
    var userId by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var selectedUni by remember { mutableStateOf("Select University") }
    var selectedClass by remember { mutableStateOf("Select Class") }
    var selectedSem by remember { mutableStateOf("Select Semester") }
    val authState by viewModel.authState.collectAsState()

    LaunchedEffect(authState) {
        if (authState is AuthState.Success) {
            onRegisterSuccess()
            viewModel.resetState()
        }
    }






    Box(modifier = Modifier.fillMaxSize().background(DarkGrayishBlue),

    ) {


            ConstraintLayout(
                modifier = Modifier
                    .fillMaxSize().background(DarkGrayishBlue).padding(top = 40.dp)
            ) {
                val (
                    userIdColumn,
                    nameColumn,
                    passColumn,
                    confirmColumn,
                    uniColumn,
                    classColumn,
                    semColumn,
                    registerButton,
                    loginText
                ) = createRefs()



                OutlinedTextField(
                    value = userId,
                    onValueChange = { userId = it },
                    label = { Text("User ID", color = LightText) },
                    modifier = Modifier
                        .fillMaxWidth().padding(start = 10.dp, end = 10.dp,top = 30.dp)
                        .constrainAs(userIdColumn) {
                            top.linkTo(parent.top, margin = 30.dp)
                        },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color.White,
                        focusedLabelColor = Color.Black,
                        unfocusedLabelColor = Color.Black
                    )
                )

                // Name

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name", color = LightText) },
                    modifier = Modifier
                        .fillMaxWidth().padding(horizontal = 10.dp)
                        .constrainAs(nameColumn) {
                            top.linkTo(userIdColumn.bottom, margin = 16.dp)
                        },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color.White,
                        focusedLabelColor = Color.Black,
                        unfocusedLabelColor = Color.Black
                    )
                )

                // Password

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password", color = LightText) },
                    modifier = Modifier
                        .fillMaxWidth().padding(horizontal = 10.dp)
                        .constrainAs(passColumn) {
                            top.linkTo(nameColumn.bottom, margin = 16.dp)
                        },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color.White,
                        focusedLabelColor = Color.Black,
                        unfocusedLabelColor = Color.Black
                    )
                )

                // Confirm Password
                var confirmPassword by remember { mutableStateOf("") }
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text("Confirm Password", color = LightText) },
                    modifier = Modifier
                        .fillMaxWidth().padding(horizontal = 10.dp)
                        .constrainAs(confirmColumn) {
                            top.linkTo(passColumn.bottom, margin = 16.dp)
                        },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color.White,
                        focusedLabelColor = Color.Black,
                        unfocusedLabelColor = Color.Black
                    )
                )

                // --- SpinnerField Composable ---
                @Composable
                fun <T> SpinnerField(
                    label: String,
                    options: List<T>,
                    selected: T,
                    onSelectedChange: (T) -> Unit,
                    modifier: Modifier = Modifier,
                    colors: TextFieldColors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Gray,
                        unfocusedBorderColor = Color.White,
                        focusedLabelColor = Color.LightGray,
                        unfocusedLabelColor = LightText
                    )
                ) {
                    var expanded by remember { mutableStateOf(false) }
                    Box(modifier = modifier) {
                        OutlinedTextField(
                            value = selected.toString(),
                            onValueChange = {},
                            readOnly = true,
                            label = { Text(label, color = LightText) },
                            trailingIcon = {
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = "Dropdown",
                                    modifier = Modifier.clickable { expanded = true }
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth().padding(horizontal = 10.dp)
                                .clickable { expanded = true },
                            colors = colors
                        )

                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            options.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option.toString()) },
                                    onClick = {
                                        onSelectedChange(option)
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                // University Dropdown

                val universities = listOf("University A", "University B", "University C")
                SpinnerField(
                    label = "University",
                    options = universities,
                    selected = selectedUni,
                    onSelectedChange = { selectedUni = it },
                    modifier = Modifier.constrainAs(uniColumn) {
                        top.linkTo(confirmColumn.bottom, margin = 16.dp)
                    }
                )

                // Class Dropdown
                val classes = listOf("B.Tech CSE", "B.Tech CS", "B.Tech AI&DS")
                SpinnerField(
                    label = "Class",
                    options = classes,
                    selected = selectedClass,
                    onSelectedChange = { selectedClass = it },
                    modifier = Modifier.constrainAs(classColumn) {
                        top.linkTo(uniColumn.bottom, margin = 16.dp)
                    }
                )

                // Semester Dropdown
                val semesters = listOf("Semester 1", "Semester 2", "Semester 3", "Semester 4")
                SpinnerField(
                    label = "Semester",
                    options = semesters,
                    selected = selectedSem,
                    onSelectedChange = { selectedSem = it },
                    modifier = Modifier.constrainAs(semColumn) {
                        top.linkTo(classColumn.bottom, margin = 16.dp)
                    }
                )

                // Register Button
                Button(
                    onClick = { viewModel.register(userId, password, confirmPassword, name, selectedUni, selectedClass, selectedSem) },
                    modifier = Modifier
                        .fillMaxWidth().padding(horizontal = 10.dp)
                        .constrainAs(registerButton) {
                            top.linkTo(semColumn.bottom, margin = 30.dp)
                        },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Register", fontSize = 18.sp,color = DeepNavy, fontFamily = FontFamily.Serif)
                }


                Row(
                    modifier = Modifier.constrainAs(loginText) {
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        bottom.linkTo(parent.bottom, margin = 40.dp)
                    }
                ) {
                    Text(text = "Already have an account?", fontFamily = FontFamily.Serif, color = Color.LightGray)
                    TextButton(onClick = onNavigateToLogin) {
                        Text(text = "Login", fontFamily = FontFamily.Serif,
                            color = Color.White)
                    }

                    }
            }

        }
    }



@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    RegisterScreen(
        onRegisterSuccess = {},
        onNavigateToLogin = {}
    )
}