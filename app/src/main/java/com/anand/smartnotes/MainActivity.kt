package com.anand.smartnotes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import com.anand.smartnotes.data.cloud.CloudinaryManager
import com.anand.smartnotes.ui.theme.DeepNavy
import com.anand.smartnotes.ui.theme.LightText
import com.anand.smartnotes.ui.theme.SmartNotesTheme
import com.anand.smartnotes.view.screens.HomeScreen
import com.anand.smartnotes.view.screens.NotesListScreen
import com.anand.smartnotes.view.screens.ProfileScreen
import com.anand.smartnotes.view.screens.SearchScreen
import com.google.firebase.Firebase
import com.google.firebase.initialize
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Firebase.initialize(this)
        CloudinaryManager.init(this)

        val firestore = FirebaseFirestore.getInstance()
        val settings = FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(true)  // ✅ Offline support enable
            .build()
        firestore.firestoreSettings = settings

        enableEdgeToEdge()
        setContent {
            SmartNotesTheme {
                ContentScreen(modifier = Modifier.fillMaxSize(), selectedIndex = 0)
                bottomNav()

            }
        }
    }
}

@Composable
fun bottomNav(){
    var selectedIndex by remember{mutableStateOf(0)}
    val navItems= listOf(
        BottomNavItems("Home","home",Icons.Filled.Home),
        BottomNavItems("Search","search",Icons.Filled.Search),
        BottomNavItems("Notes","notes",Icons.Filled.List),
        BottomNavItems("Profile","profile",Icons.Filled.Person)
    )
    Scaffold(bottomBar ={
        NavigationBar(containerColor = DeepNavy) {
            navItems.forEachIndexed{index,nav_item->
                NavigationBarItem(
                    selected=selectedIndex == index,
                    onClick = {
                        selectedIndex=index
                    },
                    label={ Text(text = nav_item.name, fontFamily = FontFamily.Serif, color = LightText) },
                    icon={
                        Icon(imageVector = nav_item.icon, contentDescription = "Icon")
                    }
                )
            }
        }
    }
    ){innerPadding ->
        ContentScreen(modifier = Modifier.padding(innerPadding),selectedIndex)

    }

}
@Composable
fun ContentScreen(modifier: Modifier,selectedIndex: Int){
    when(selectedIndex){
        0->HomeScreen()
        1->SearchScreen()
        2->NotesListScreen()
        3-> ProfileScreen(onLogout = {})


    }

}

@Preview(showBackground = true)
@Composable
fun bottomNavPreview() {
    SmartNotesTheme {
        bottomNav()
    }
}