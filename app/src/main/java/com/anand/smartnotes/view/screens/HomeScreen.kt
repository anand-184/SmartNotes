package com.anand.smartnotes.view.screens

import android.content.Intent
import android.os.Build
import android.provider.AlarmClock
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.anand.smartnotes.ui.theme.DarkGrayishBlue
import com.anand.smartnotes.ui.theme.DeepNavy
import com.anand.smartnotes.ui.theme.LightText
import com.anand.smartnotes.viewmodel.NotesViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(viewModel: NotesViewModel) {
    val context = LocalContext.current
    val notes by viewModel.notes.collectAsState(emptyList())
    val streak = remember { mutableStateOf(3) } // demo value
    val dailyQuote = remember {
        listOf(
            "Learning never exhausts the mind.",
            "Push yourself, because no one else will do it for you.",
            "Small progress each day adds up to big results."
        ).random()
    }

    Column(
        modifier = Modifier
            .fillMaxSize().padding(top=16.dp)
            .verticalScroll(rememberScrollState())
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        DeepNavy,
                        DarkGrayishBlue
                    )
                )
            )
            .padding(16.dp)
    ) {
        // Header with Welcome
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = CenterVertically
        ) {
            Column {
                Text(
                    "Welcome back! 👋",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    "Ready to learn something new?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }

            // Streak Badge
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(
                        color = Color(0xFFFFD700),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🔥", style = MaterialTheme.typography.bodyLarge)
                    Text(
                        streak.value.toString(),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF7B5800)
                    )
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        // AI Insights Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 12.dp,
                    shape = RoundedCornerShape(20.dp),
                    clip = true
                ),
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(0.dp),
            colors = CardDefaults.cardColors(
                containerColor = LightText.copy(alpha = 0.95f)
            )
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    verticalAlignment = CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                color = Color(0xFF667eea).copy(alpha = 0.1f),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🤖", style = MaterialTheme.typography.bodyLarge)
                    }
                    Spacer(Modifier.width(12.dp))
                    Text(
                        "AI Insights",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2D3748)
                    )
                }

                Spacer(Modifier.height(16.dp))

                // Insight Items
                InsightItem(
                    icon = "📊",
                    title = "Top Topic",
                    value = viewModel.getTopTopic(notes)
                )
                InsightItem(
                    icon = "📅",
                    title = "Weekly Uploads",
                    value = viewModel.getWeeklyUploads(notes).toString()
                )
                InsightItem(
                    icon = "🔄",
                    title = "Need Revision",
                    value = viewModel.getOldNotes(notes)
                )
            }
        }

        Spacer(Modifier.height(20.dp))

        // Motivation Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 12.dp,
                    shape = RoundedCornerShape(20.dp),
                    clip = true
                ),
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(0.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFFFF9C4).copy(alpha = 0.95f)
            )
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    verticalAlignment = CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                color = Color(0xFFFFB300).copy(alpha = 0.2f),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("💫", style = MaterialTheme.typography.bodyLarge)
                    }
                    Spacer(Modifier.width(12.dp))
                    Text(
                        "Daily Motivation",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF7B5800)
                    )
                }

                Spacer(Modifier.height(16.dp))

                // Quote with decorative elements
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = Color.White.copy(alpha = 0.6f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(16.dp)
                ) {
                    Text(
                        "''$dailyQuote''",
                        style = MaterialTheme.typography.bodyLarge,
                        fontStyle = FontStyle.Italic,
                        color = Color(0xFF5D4037),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        // Quick Stats Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                title = "Total Notes",
                value = notes.size.toString(),
                icon = "📝",
                color = Color(0xFF4CAF50)
            )
            StatCard(
                title = "This Week",
                value = viewModel.getWeeklyUploads(notes).toString(),
                icon = "🚀",
                color = Color(0xFF2196F3)
            )
        }
    }
}

@Composable
fun InsightItem(icon: String, title: String, value: String) {
    Row(
        verticalAlignment = CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            icon,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.width(32.dp)
        )
        Spacer(Modifier.width(12.dp))
        Column {
            Text(
                title,
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF718096),
                fontWeight = FontWeight.Medium
            )
            Text(
                value,
                style = MaterialTheme.typography.bodyLarge,
                color = Color(0xFF2D3748),
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun StatCard(title: String, value: String, icon: String, color: Color) {
    Card(
        modifier = Modifier.width(150.dp)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(16.dp),
                clip = true
            ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(0.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.9f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = color.copy(alpha = 0.1f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(icon, style = MaterialTheme.typography.titleLarge)
            }
            Spacer(Modifier.height(8.dp))
            Text(
                value,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(
                title,
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF718096),
                textAlign = TextAlign.Center
            )
        }
    }
}