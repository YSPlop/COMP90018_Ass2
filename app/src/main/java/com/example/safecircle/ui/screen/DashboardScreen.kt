package com.example.safecircle.ui.screen

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.foundation.clickable
import androidx.compose.ui.unit.dp
import androidx.compose.material3.DrawerState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.sp
import com.example.safecircle.database.Family
import com.example.safecircle.database.FamilyDatabase
import com.example.safecircle.database.PersonalDetails
import com.example.safecircle.utils.PreferenceHelper

data class PersonInfo(
    val name: String,
    val location: String,
    val temperature: String,
    val phoneBattery: String
)
@Composable
fun AppTopBar(drawerState: DrawerState, title: String) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp), // Explicitly set height of the Surface
        color = MaterialTheme.colorScheme.primary
    ) {
        Row(
            modifier = Modifier
                .fillMaxHeight()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "≡",
                modifier = Modifier
                    .padding(end = 16.dp)
                    .clickable { /* toggle drawer state, open or close */ },
                color = MaterialTheme.colorScheme.onPrimary,
                style = MaterialTheme.typography.labelMedium
            )

            // Increased the size of the title text
            Text(
                text = title,
                color = MaterialTheme.colorScheme.onPrimary,
                style = MaterialTheme.typography.labelLarge.copy(fontSize = 24.sp) // Adjusted Font Size
            )
        }
    }
}

@Composable
fun AppDrawer(drawerState: DrawerState, navController: NavHostController) {
    Text("Drawer Item 1")
}
@Composable
fun DashboardScreen(navController: NavHostController) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()
    val persons = listOf(
        PersonInfo("John Doe", "location1", "25°C", "85%"),
        PersonInfo("Jane Smith", "location2", "28°C", "65%"),
    )

    val context = LocalContext.current
    val preferenceHelper = PreferenceHelper(context)
    val familyID = preferenceHelper.getFamilyID()
    val (childrenList, setChildrenList) = remember { mutableStateOf(listOf<PersonInfo>()) }
    val familyDatabase: FamilyDatabase = FamilyDatabase()

    // Fetch children details on screen load
    LaunchedEffect(Unit) {
        familyDatabase.getAllChildrenInfo(familyID!!) { children ->
            Log.i("test", "$children")
            setChildrenList(children)
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = { AppDrawer(drawerState, navController) }
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            AppTopBar(drawerState, "Dashboard")
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                items(childrenList) { person ->
                    PersonCard(person = person) {

                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
fun PersonCard(person: PersonInfo, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(8.dp)
            )
            .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
            .clickable(onClick = onClick) // Added clickable modifier here
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "Name: ${person.name}",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Location: ${person.location}",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Temperature: ${person.temperature}",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Phone Battery: ${person.phoneBattery}",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

// Example usage
