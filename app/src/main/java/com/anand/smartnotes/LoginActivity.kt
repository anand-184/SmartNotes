package com.anand.smartnotes

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester.Companion.createRefs
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
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

import com.anand.smartnotes.ui.theme.SmartNotesTheme
import com.anand.smartnotes.viewmodel.AuthViewModel

class LoginActivity : ComponentActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SmartNotesTheme {
                Column(modifier = Modifier.fillMaxSize().background(DarkGrayishBlue)) {
                    LoginScreenUi(
                        onNavigateToRegister = {
                            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
                            startActivity(intent)
                        },
                        onNavigateToForgotPassword = {},
                        onNavigateToHome = {
                            val intent = Intent(this@LoginActivity, MainActivity::class.java)
                            startActivity(intent)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun LoginScreenUi(
    onNavigateToRegister:()->Unit,
    onNavigateToForgotPassword:()->Unit,
    onNavigateToHome:()->Unit,
    viewModel: AuthViewModel =
        ViewModelProvider(LocalContext.current as ViewModelStoreOwner)
            [AuthViewModel::class.java]

) {
    val context = LocalContext.current
    var userId by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    ConstraintLayout(modifier = Modifier.fillMaxSize().background(DarkGrayishBlue)){
        val (logo, userIdColumn, passColumn, signUpText, loginButton) = createRefs()

        // User ID Field
        Column(
            modifier = Modifier.constrainAs(userIdColumn) {
                start.linkTo(parent.start, margin = 15.dp)
                end.linkTo(parent.end, margin = 15.dp)
                bottom.linkTo(passColumn.top, margin = 10.dp)
            }
        ) {
            OutlinedTextField(
                value = userId,
                onValueChange = { userId = it },
                label = { Text("User Id", fontFamily = FontFamily.Serif, color = LightText, fontWeight = FontWeight.Bold) },
                placeholder = { Text("Enter User Id") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp),
                textStyle = TextStyle(color = Color.White),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.White,
                    focusedLabelColor = Color.LightGray,
                    unfocusedLabelColor = Color.White
                )
            )
        }

        // Password Field
        Column(
            modifier = Modifier.constrainAs(passColumn) {
                start.linkTo(parent.start, margin = 15.dp)
                end.linkTo(parent.end, margin = 15.dp)
                bottom.linkTo(loginButton.top, margin = 10.dp)
            }
        ) {
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password", fontFamily = FontFamily.Serif, color = LightText, fontWeight = FontWeight.Bold) },
                placeholder = { Text("Enter Password") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp),
                textStyle = TextStyle(color = Color.White),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.White,
                    focusedLabelColor = Color.LightGray,
                    unfocusedLabelColor = Color.White
                )
            )
        }

        // Login Button
        OutlinedButton(
            onClick = {
                viewModel.login(userId, password)
                onNavigateToHome()
            },
            shape = RoundedCornerShape(12.dp),
            elevation = ButtonDefaults.buttonElevation(20.dp),
            modifier = Modifier
                .constrainAs(loginButton) {
                    start.linkTo(parent.start, margin = 15.dp)
                    end.linkTo(parent.end, margin = 15.dp)
                    bottom.linkTo(signUpText.top, margin = 10.dp)
                }
                .fillMaxWidth()
                .padding(horizontal = 10.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = Color.Blue,
                containerColor = Color.White,
                disabledContentColor = Color.DarkGray,
                disabledContainerColor = Color.Transparent,
            )
        ) {
            Text(
                text = "Login",
                modifier = Modifier.padding(horizontal = 10.dp),
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Serif,
                color = DeepNavy
            )
        }

        // Sign Up Text
        Row(
            modifier = Modifier.constrainAs(signUpText) {
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                bottom.linkTo(parent.bottom, margin = 60.dp)
            }.fillMaxWidth(),horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Don't have an account?", fontFamily = FontFamily.Serif, color = Color.LightGray)
            TextButton(onClick = onNavigateToRegister) {
                Text(text = "Register", fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color.White)
            }
        }



    }




}
@Preview
@Composable
fun LoginScreenUiPreview() {
    LoginScreenUi(
        onNavigateToRegister = {},
        onNavigateToForgotPassword = {},
        onNavigateToHome = {}
    )
}