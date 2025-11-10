package com.anand.smartnotes

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.anand.smartnotes.data.cloud.CloudinaryManager
import com.anand.smartnotes.data.dataclasses.Note
import com.anand.smartnotes.ui.theme.DeepNavy
import com.anand.smartnotes.ui.theme.LightText
import com.anand.smartnotes.ui.theme.SmartNotesTheme
import com.anand.smartnotes.view.screens.HomeScreen
import com.anand.smartnotes.view.screens.NotesListScreen
import com.anand.smartnotes.view.screens.ProfileScreen
import com.anand.smartnotes.view.screens.SearchScreen
import com.anand.smartnotes.viewmodel.NotesViewModel
import com.google.firebase.Firebase
import com.google.firebase.initialize
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Firebase.initialize(this)
        CloudinaryManager.init(this)

        val firestore = FirebaseFirestore.getInstance()
        val settings = FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(true)
            .build()
        firestore.firestoreSettings = settings

        enableEdgeToEdge()
        setContent {
            SmartNotesTheme {
                val navController = rememberNavController()
                var selectedIndex by remember { mutableStateOf(0) }

                Scaffold(
                    bottomBar = {
                        NavigationBar(containerColor = DeepNavy) {
                            val navItems = listOf(
                                BottomNavItems("Home", "home", Icons.Filled.Home),
                                BottomNavItems("Search", "search", Icons.Filled.Search),
                                BottomNavItems("Notes", "notes", Icons.Filled.List),
                                BottomNavItems("Profile", "profile", Icons.Filled.Person)
                            )
                            navItems.forEachIndexed { index, navItem ->
                                NavigationBarItem(
                                    selected = selectedIndex == index,
                                    onClick = { selectedIndex = index },
                                    label = { Text(text = navItem.name, fontFamily = FontFamily.Serif, color = LightText) },
                                    icon = { Icon(imageVector = navItem.icon, contentDescription = "Icon") }
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "main"
                    ) {
                        composable("main") {
                            ContentScreen(
                                modifier = Modifier.padding(innerPadding),
                                selectedIndex = selectedIndex,
                                navController = navController,
                                viewModel = NotesViewModel()
                            )
                        }
                        composable("noteDetail") {
                            val note = navController.previousBackStackEntry
                                ?.savedStateHandle
                                ?.get<Note>("note")

                            note?.let {
                                NoteDetailScreen(
                                    note = it,
                                    onBack = { navController.popBackStack() }
                                )
                            }
                        }

                    }
                }
            }
        }
    }

}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ContentScreen(
    modifier: Modifier,
    selectedIndex: Int,
    navController: NavController,
    viewModel: NotesViewModel = NotesViewModel()
) {
    when (selectedIndex) {
        0 -> HomeScreen(viewModel = viewModel)
        1 -> SearchScreen()
        2 -> NotesListScreen(navController = navController)
        3 -> ProfileScreen(onLogout = {})
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteDetailScreen(
    note: Note,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Note Details") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = note.topic,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "Created on: ${SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(note.timestamp))}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Divider()
            Text(
                text = "Summary",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold
            )
            note.summary.forEach { summary ->
                Text(
                    text = summary,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Divider()
            Text(
                text = "Questions & Answers",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold
            )
            note.questions.forEach { qa ->
                Text(
                    text = "Q: ${qa.question}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "A: ${qa.answer}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun bottomNavPreview() {
    SmartNotesTheme {
        val navController = rememberNavController()
        var selectedIndex by remember { mutableStateOf(0) }
        Scaffold(
            bottomBar = {
                NavigationBar(containerColor = DeepNavy) {
                    val navItems = listOf(
                        BottomNavItems("Home", "home", Icons.Filled.Home),
                        BottomNavItems("Search", "search", Icons.Filled.Search),
                        BottomNavItems("Notes", "notes", Icons.Filled.List),
                        BottomNavItems("Profile", "profile", Icons.Filled.Person)
                    )
                    navItems.forEachIndexed { index, navItem ->
                        NavigationBarItem(
                            selected = selectedIndex == index,
                            onClick = { selectedIndex = index },
                            label = { Text(text = navItem.name, fontFamily = FontFamily.Serif, color = LightText) },
                            icon = { Icon(imageVector = navItem.icon, contentDescription = "Icon") }
                        )
                    }
                }
            }
        ) { innerPadding ->
            ContentScreen(
                modifier = Modifier.padding(innerPadding),
                selectedIndex = selectedIndex,
                navController = navController
            )
        }
    }
}
