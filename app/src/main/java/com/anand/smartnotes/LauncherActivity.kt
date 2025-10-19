package com.anand.smartnotes

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LauncherActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            // Optional: show splash for 1–2 seconds
            delay(1000)

            val user = FirebaseAuth.getInstance().currentUser

            if (user != null) {
                // ✅ User is logged in → go to Home
                startActivity(Intent(this@LauncherActivity, MainActivity::class.java))
            } else {
                // ❌ Not logged in → go to Login
                startActivity(Intent(this@LauncherActivity, LoginActivity::class.java))
            }

            finish() // Close Splash
        }
    }
}
