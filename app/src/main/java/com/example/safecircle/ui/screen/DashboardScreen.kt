package com.example.safecircle.ui.screen

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.compose.material3.Text
import androidx.compose.foundation.background
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp
import com.example.safecircle.Map
import com.example.safecircle.R
import com.example.safecircle.database.FamilyDatabase
import com.example.safecircle.sensors.ForegroundSensorService
import com.example.safecircle.ui.components.AppDrawer
import com.example.safecircle.ui.components.AppTopBar
import com.example.safecircle.ui.theme.CyanSecondary
import com.example.safecircle.ui.theme.DarkGreen
import com.example.safecircle.ui.theme.PlaypenSansBold
import com.example.safecircle.ui.theme.YellowPrimary
import com.example.safecircle.utils.GlobalState
import com.example.safecircle.utils.PreferenceHelper


data class PersonInfo(
    val name: String,
    val location: String,
    val temperature: String,
    val phoneBattery: String,
    val markerName: String
)



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(navController: NavHostController) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
//    val coroutineScope = rememberCoroutineScope()
//    val persons = listOf(
//        PersonInfo("John Doe", "location1", "25°C", "85%"),
//        PersonInfo("Jane Smith", "location2", "28°C", "65%"),
//    )

    val context = LocalContext.current
    val preferenceHelper = PreferenceHelper(context)
    val familyID = preferenceHelper.getFamilyID()
//  val (childrenList, setChildrenList) = remember { mutableStateOf(listOf<PersonInfo>()) }
    val childrenList = GlobalState.childList

    val familyDatabase: FamilyDatabase = FamilyDatabase()
    val username = preferenceHelper.getUsername()
    val objectID = preferenceHelper.getObjectId()

    fun isServiceRunning(serviceClass: Class<*>): Boolean {
        val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
        return false
    }

//    LaunchedEffect(Unit) {
//        // Start ForegroundSensorService
//        Log.i("Dashboard", "objectId: $objectID")
//        if (!isServiceRunning(ForegroundSensorService::class.java)) {
////            ForegroundSensorService.getInstance()?.setUser(familyID.toString(), username.toString(), Role.PARENT)
//            val serviceIntent = Intent(context, ForegroundSensorService::class.java)
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                context.startForegroundService(serviceIntent)
//            } else {
//                context.startService(serviceIntent)
//            }
//        }
//    }
    fun startForegroundService(context: Context) {
        val serviceIntent = Intent(context, ForegroundSensorService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(serviceIntent)
        } else {
            context.startService(serviceIntent)
        }
    }

    LaunchedEffect(Unit) {
        // Start ForegroundSensorService
        Log.i("Dashboard", "objectId: $objectID")
        val lastObjectID = preferenceHelper.getLastObjectID()

        if (!isServiceRunning(ForegroundSensorService::class.java)) {
            // If service isn't running, start it
            startForegroundService(context)
        } else if (objectID != lastObjectID) {
            // If service is running and objectId has changed, stop and then start it
            val stopIntent = Intent(context, ForegroundSensorService::class.java)
            context.stopService(stopIntent)
            startForegroundService(context)
        }

        // Store the current objectId as lastObjectID
        preferenceHelper.setLastObjectID(objectID.toString())
    }


    // Check permissions and start service here.
//    val requestRecordAudioPermissionLauncher = rememberUpdatedState(
//        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
//            if (it) {  // Permission granted
//                ForegroundSensorService.getInstance()?.startNoiseSensor()
//            }
//        }
//    ).value

//    LaunchedEffect(Unit) {
//        if (ContextCompat.checkSelfPermission(
//                context,
//                android.Manifest.permission.RECORD_AUDIO
//            ) != PackageManager.PERMISSION_GRANTED
//        ) {
//            requestRecordAudioPermissionLauncher.launch(android.Manifest.permission.RECORD_AUDIO)
//        } else {
//            ForegroundSensorService.getInstance()?.startNoiseSensor()
//        }
//    }

    // Fetch children details on screen load
//    LaunchedEffect(Unit) {
//        familyDatabase.getAllChildrenInfo(familyID!!) { children ->
//            Log.i("test", "$children")
//            setChildrenList(children)
//        }
//    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = { AppDrawer(drawerState, navController) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = CyanSecondary)

        ) {
            AppTopBar(drawerState, "Dashboard")
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                items(childrenList) { person ->
                    PersonCard(person = person, navController)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

/*
* I removed the functionality of passing in an onClick function
* Can revert to main branch code
* */
@Composable
fun PersonCard(person: PersonInfo, navController: NavHostController) {
    ElevatedCard(
//        modifier = Modifier
//            .padding(vertical = 5.dp, horizontal = 10.dp)
    ) {
        ElevatedCard (
            modifier = Modifier
                .padding(horizontal = 15.dp, vertical = 10.dp)
                .fillMaxWidth(),
            colors = CardDefaults.cardColors(),
        ) {

            Column(
                modifier = Modifier
                    .padding(vertical = 10.dp, horizontal = 15.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                InformationInPersonCard(person)
            }

        }


        ElevatedButton(
            onClick =
            {
                navController.navigate(Map.ofUser(person.name))
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = YellowPrimary,
                contentColor = Color.White
            ),
            modifier = Modifier
                .padding(horizontal = 15.dp, vertical = 10.dp)
                .fillMaxWidth(),

        ) {
            Text(
                text = "Show Map",
                color = Color.Black,
                fontSize = 13.sp,
                fontFamily = PlaypenSansBold
            )
        }
    }
}

@Composable
fun InformationInPersonCard(person:PersonInfo){

    Text(
        text = person.name,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        fontFamily = PlaypenSansBold

    )
    // If you would like to left align the below to the center then put the below in a column
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start
    ){
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            Spacer(modifier = Modifier.width(32.dp))
            val temperatureStyle = if (person.temperature.toFloat() >= 40.0) {
                SpanStyle(fontWeight = FontWeight.Bold, color = Color.Red)
            } else {
                SpanStyle(fontWeight = FontWeight.Bold, color = DarkGreen)
            }

            Text(
                buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(
                            fontWeight = FontWeight.Bold,
                            fontFamily = PlaypenSansBold
                        )
                    ) {
                        append("Temperature: ")
                    }
                    withStyle(temperatureStyle) {
                        append("${person.temperature}°C")
                    }
                }
            )

            if (person.temperature.toFloat() >= 40.0) {
                Spacer(modifier = Modifier.width(8.dp))
                Icon(Icons.Default.Warning, contentDescription = "Temperature Alert", tint = Color.Red)
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.width(32.dp))
            val batteryStyle = if (person.phoneBattery.toFloat() < 20.0) {
                SpanStyle(fontWeight = FontWeight.Bold, color = Color.Red)
            } else {
                SpanStyle(fontWeight = FontWeight.Bold, color = DarkGreen)
            }

            Text(
                buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(
                            fontWeight = FontWeight.Bold,
                            fontFamily = PlaypenSansBold
                        )
                    ) {
                        append("Phone Battery: ")
                    }
                    withStyle(batteryStyle) {
                        append("${person.phoneBattery}%")
                    }
                }
            )

            if (person.phoneBattery.toFloat() < 20.0) {
                Spacer(modifier = Modifier.width(8.dp))
                Icon(Icons.Default.Warning, contentDescription = "Battery Low Alert", tint = Color.Red)
            }
        }
    }

}

