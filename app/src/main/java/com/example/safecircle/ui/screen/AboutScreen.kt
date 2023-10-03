package com.example.safecircle.ui.screen

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.safecircle.ui.components.AppDrawer
import com.example.safecircle.ui.components.AppTopBar
import com.example.safecircle.ui.theme.CyanSecondary
import com.example.safecircle.ui.theme.SafeCircleTheme
import com.example.safecircle.ui.theme.YellowPrimary


@Composable
fun AboutScreen(navController: NavHostController) {
    /*
    * Background color = cyan light (settings page) (done)
    * Widget color = Same - color (settings page) (done)
    * Button color = YellowPastel color (done)
    * Information text = use outlined text field instead of text
    *
    * Double card drop like in reminders
    * */
    val drawerState = rememberDrawerState(DrawerValue.Closed)

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = { AppDrawer(drawerState, navController) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = CyanSecondary)
        ) {
            AppTopBar(drawerState, "Help")
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    //.verticalScroll(rememberScrollState())
            ) {
                // Have a hyperlink to how to keep your children safe
                // Have a green call button to parents
                // Do we need more?
                RecyclerView(listOf("About Us", "Parent View", "Children View", "Creators of the App", "Contact Us"))
            }
        }
    }
}

@Composable
fun ListItem(name: String){

    val expanded = remember{ mutableStateOf(false) }
    val extrapadding by animateDpAsState(
        if (expanded.value) 24.dp else 0.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessLow,
        ),
//        animationSpec = tween(
//            easing = LinearOutSlowInEasing
//        ),
        label = "",
    )

    val aboutUsText = "God Loves You"
    val parentViewText = "This is the parent view text"
    val childrenViewText = "This is the children view text"
    val contactUsText = "This is the contact us text"
    val errorText = "If you see this please contact the creators"



    ElevatedCard(
        modifier = Modifier
            .padding(vertical = 5.dp, horizontal = 10.dp)
    ) {

        Row(){

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 10.dp, horizontal = 15.dp)
            ) {
                Text(text = name,
                    style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold)
                    // slightly larger font size
                )
            }

            ElevatedButton(
                onClick =
                {
                    expanded.value = !expanded.value
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = YellowPrimary,
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .padding(horizontal = 15.dp)
            ) {
                Text(
                    if (expanded.value) "Show less" else "Show more",
                    color = Color.Black,
                    fontSize = 12.sp
                )
            }
        }
        if (expanded.value){
            Column(
                modifier = Modifier
                    .padding(
                        bottom = extrapadding.coerceAtLeast(0.dp),// This will prevent it from going to a negative value
                    )
                    .padding(horizontal = 15.dp)
            ){
                when (name) {
                    "About Us" -> {
                        InformationView(textValue = aboutUsText)
                    }
                    "Parent View" -> {
                        InformationView(textValue = parentViewText)

                    }
                    "Children View" -> {
                        InformationView(textValue = childrenViewText)

                    }
                    "Creators of the App" -> {
                        Column(){
                            CreatorInformation(
                                name = "Luchen Zhou",
                                email = "luczhou@student.unimelb.edu.au",
                                description = "The Carry"
                            )
                            CreatorInformation(
                                name = "Wei Wang",
                                email = "wangw16@student.unimelb.edu.au",
                                description = "The UI designer"
                            )
                            CreatorInformation(
                                name = "Yifan Cheng",
                                email = "yifacheng@student.unimelb.edu.au",
                                description = "The Great Support"
                            )
                            CreatorInformation(
                                name = "Sichen Lu",
                                email = "sichen2@student.unimelb.edu.au",
                                description = "The Sensor Implementor"
                            )
                            CreatorInformation(
                                name = "Chi Zhang",
                                email = "czzhang5@student.unimelb.edu.au",
                                description = "The Map Master"
                            )
                            CreatorInformation(
                                name = "Yukash Sivaraj",
                                email = "ysivaraj@student.unimelb.edu.au",
                                description = "The Information Provider"
                            )



                        }

                    }
                    "Contact Us" -> {
                        InformationView(textValue = contactUsText)
                    }
                    else -> {
                        InformationView(textValue = errorText)
                    }
                }
            }
        }

    }





}

@Composable
fun CreatorInformation(name: String, email: String, description: String){
    ElevatedCard (
        modifier = Modifier
            .padding(
                vertical = 10.dp,

                )
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(),
    ){
        Text(
            buildAnnotatedString(
            ) {
                withStyle(style = SpanStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                )) {
                    append("Name: ")
                }
                withStyle(SpanStyle(
                    fontSize = 12.sp,
                    )){
                    append(name)
                }

                withStyle(style = SpanStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                )) {
                    append("\nContact Email: ")
                }
                withStyle(SpanStyle(
                    fontSize = 12.sp,
                )){
                    append(email)
                }

                withStyle(style = SpanStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                )) {
                    append("\nDescription: ")
                }
                withStyle(SpanStyle(
                    fontSize = 12.sp,
                )){
                    append(description)
                }

            },


            modifier = Modifier
                    .padding(horizontal = 10.dp, vertical = 10.dp)
        )

    }
}
@Composable
fun InformationView(textValue: String){
    ElevatedCard (
        modifier = Modifier
            .padding(
                vertical = 10.dp,

                )
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(),
    ){
        Text(
            text = textValue,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier
                .padding(horizontal = 10.dp, vertical = 10.dp)
            ,
        )
    }
}

@Composable
fun RecyclerView(names: List<String> = List(5) {"$it"}){

    // The difference between column and lazy column
    // Column will compose everything at once
    // Lazy column will only compose everything only when it is in screen
    LazyColumn(
        modifier = Modifier
            .padding(vertical = 4.dp)
//            .fillMaxWidth()
    ){
        items(items = names){name ->
            ListItem(name = name)
        }
    }

}


@Preview
@Composable
private fun ComposablePreview(){
    SafeCircleTheme {
        RecyclerView(listOf("About Us", "Parent View", "Children View", "Creators of the App", "Contact Us"))
    }
}
