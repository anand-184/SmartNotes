package com.anand.smartnotes.view.screens

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.anand.smartnotes.data.dataclasses.Note
import com.anand.smartnotes.ui.theme.BrightYellow
import com.anand.smartnotes.ui.theme.DarkGrayishBlue
import com.anand.smartnotes.ui.theme.DeepNavy
import com.anand.smartnotes.ui.theme.LightText
import com.anand.smartnotes.ui.theme.SkyBlue
import com.anand.smartnotes.viewmodel.NotesViewModel
import java.text.SimpleDateFormat
import java.util.*


@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun NotesListScreen(
    navController: NavController
) {
    val viewModel: NotesViewModel = viewModel()
    val notes by viewModel.notes.collectAsState()
    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    val isExpanded = remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "My Notes",
                        color = LightText,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DeepNavy,
                    titleContentColor = LightText
                ),
                actions = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = SkyBlue,
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .clickable { /* Handle search */ }
                    )
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* Handle new note */ },
                containerColor = SkyBlue,
                contentColor = DeepNavy
            ) {
                Icon(Icons.Default.Add, "Add Note")
            }
        },
        containerColor = DeepNavy
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(DeepNavy)
        ) {
            // Stats Header
            StatsHeader(notes.size)

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(notes, key = { it.id }) { note ->
                    NoteItem(
                        note = note,
                        onClick = {
                            navController.currentBackStackEntry?.savedStateHandle?.set("note", note)
                            navController.navigate("noteDetail")
                        },
                        dateFormat = dateFormat
                    )
                }
            }
        }
    }
}

@Composable
fun StatsHeader(totalNotes: Int) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(16.dp),
                clip = true
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = DarkGrayishBlue
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    "Total Notes",
                    color = LightText.copy(alpha = 0.7f),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    "$totalNotes",
                    color = BrightYellow,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Progress indicator or additional stats can go here
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .background(
                        color = SkyBlue.copy(alpha = 0.1f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "📚",
                    fontSize = 24.sp
                )
            }
        }
    }
}

@Composable
fun NoteItem(
    note: Note,
    onClick: () -> Unit,
    dateFormat: SimpleDateFormat
) {
    val elevation = animateDpAsState(
        targetValue = if (false) 8.dp else 4.dp,
        label = "cardElevation"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = elevation.value,
                shape = RoundedCornerShape(16.dp),
                clip = true
            )
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = DarkGrayishBlue
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Colored accent bar
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(40.dp)
                    .background(
                        color = SkyBlue,
                        shape = RoundedCornerShape(2.dp)
                    )
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = note.topic,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = LightText,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = dateFormat.format(Date(note.timestamp)),
                        style = MaterialTheme.typography.bodySmall,
                        color = LightText.copy(alpha = 0.6f),
                        fontWeight = FontWeight.Medium
                    )

                    // Program tag
                    note.program?.let { program ->
                        Box(
                            modifier = Modifier
                                .background(
                                    color = SkyBlue.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = program,
                                style = MaterialTheme.typography.labelSmall,
                                color = SkyBlue,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            // Chevron or action indicator
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(
                        color = SkyBlue.copy(alpha = 0.1f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "→",
                    color = SkyBlue,
                    fontSize = 16.sp
                )
            }
        }
    }
}