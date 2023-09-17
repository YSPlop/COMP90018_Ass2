package com.example.safecircle.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ListItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.safecircle.AccountSettings
import com.example.safecircle.ConnectionSettings
import com.example.safecircle.Dashboard
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(drawerState: DrawerState, title: String) {
    val scope = rememberCoroutineScope()
    TopAppBar(
        title = { Text(title) },
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
        Text("Menu", modifier = Modifier.padding(16.dp))
        Divider()
        ListItem(
            text = { Text(text = "Dashboard") },
            modifier = Modifier.clickable(onClick = {
                scope.launch {
                    drawerState.close()
                    navController.navigate(Dashboard.route)
                }
            })
        )
        ListItem(
            text = { Text(text = "Account Settings") },
            modifier = Modifier.clickable(onClick = {
                scope.launch {
                    drawerState.close()
                    navController.navigate(AccountSettings.route)
                }
            })
        )
        ListItem(
            text = { Text(text = "Connection Settings") },
            modifier = Modifier.clickable(onClick = {
                scope.launch {
                    drawerState.close()
                    navController.navigate(ConnectionSettings.route)
                }
            })
        )
    }
}
