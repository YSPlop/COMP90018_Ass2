package com.example.safecircle.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ListItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.safecircle.About
import com.example.safecircle.Dashboard
import com.example.safecircle.Help
import com.example.safecircle.R
import com.example.safecircle.Settings
import com.example.safecircle.ui.theme.CyanSecondary
import com.example.safecircle.ui.theme.PlaypenSans
import com.example.safecircle.ui.theme.PlaypenSansBold
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(drawerState: DrawerState, title: String) {
    val scope = rememberCoroutineScope()
    TopAppBar(
        title = { Text(title, fontFamily = PlaypenSans,) },
        navigationIcon = {
            IconButton(onClick = {
                scope.launch {
                    if (drawerState.isOpen) drawerState.close()
                    else drawerState.open()
                }
            }) {
                Icon(imageVector = Icons.Default.Menu, contentDescription = "Open Drawer")
            }
        }
    )
    Divider()  // Drawing a line below the TopAppBar
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AppDrawer(drawerState: DrawerState, navController: NavHostController) {
    val scope = rememberCoroutineScope()

    ModalDrawerSheet(
        modifier = Modifier
            .fillMaxHeight()
            .width(250.dp) // Ensuring it takes the full height of the screen
    ) {
        Column(
            modifier = Modifier
                .height(120.dp)  // Set the desired height
                .fillMaxWidth()
                .background(color = CyanSecondary),  // Change to your preferred color
            verticalArrangement = Arrangement.Bottom
        ) {
            // Indent the text with padding if desired
            Text(text = "SafeCircle", modifier = Modifier.padding(start = 8.dp), color = Color.White, fontSize = 24.sp, fontFamily = PlaypenSans)
            Spacer(modifier = Modifier.height(12.dp))
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)) // Rounded top corners
        ) {
            Spacer(modifier = Modifier.height(12.dp))
        ListItem(

            text = {
                Row {
                    Icon(imageVector = Icons.Default.LocationOn, contentDescription = "Dashboard")
                    Spacer(modifier = Modifier.size(8.dp) )
                    Text(text = "Dashboard", fontFamily = PlaypenSans)
                }

                   },
            modifier = Modifier.clickable(onClick = {
                scope.launch {
                    drawerState.close()
                    navController.navigate(Dashboard.route)
                }
            })
        )
        ListItem(
            text = { Row {
                Icon(imageVector = Icons.Default.Settings, contentDescription = "Settings")
                Spacer(modifier = Modifier.size(8.dp) )
                Text(text = "Settings", fontFamily = PlaypenSans) }},
            modifier = Modifier.clickable(onClick = {
                scope.launch {
                    drawerState.close()
                    navController.navigate(Settings.route)
                }
            })
        )
        ListItem(
            text = { Row {
                Icon(imageVector = Icons.Default.Build, contentDescription = "Settings")
                Spacer(modifier = Modifier.size(8.dp) )
                Text(text = "Help", fontFamily = PlaypenSans) }},
            modifier = Modifier.clickable(onClick = {
                scope.launch {
                    drawerState.close()
                    navController.navigate(Help.route)
                }
            })
        )
        ListItem(
            text = { Row {
                Icon(imageVector = Icons.Default.Info, contentDescription = "Settings")
                Spacer(modifier = Modifier.size(8.dp) )
                Text(text = "About", fontFamily = PlaypenSans) }},
            modifier = Modifier.clickable(onClick = {
                scope.launch {
                    drawerState.close()
                    navController.navigate(About.route)
                }
            })
        )}
    }
}
