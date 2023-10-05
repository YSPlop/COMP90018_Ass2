package com.example.safecircle.ui.screen

import android.graphics.PointF.length
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
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
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
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
            AppTopBar(drawerState, "About")
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
                RecyclerView(listOf("Parent View", "Children View", "Creators of the App", "Contact Us"))
            }
        }
    }
}
@Composable
fun CreatorInformation(name: String, email: String){
    val fontSizeValue = 13
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
                    fontSize = fontSizeValue.sp,
                )) {
                    append("Name: ")
                }
                withStyle(SpanStyle(
                    fontSize = 11.sp,
                )){
                    append(name)
                }

                withStyle(style = SpanStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = fontSizeValue.sp,
                )) {
                    append("\nContact Email: ")
                }
                withStyle(SpanStyle(
                    fontSize = 11.sp,
                )){
                    append(email)
                }

//                withStyle(style = SpanStyle(
//                    fontWeight = FontWeight.Bold,
//                    fontSize = fontSizeValue.sp,
//                )) {
//                    append("\nDescription: ")
//                }
//                withStyle(SpanStyle(
//                    fontSize = 11.sp,
//                )){
//                    append(description)
//                }

            },


            modifier = Modifier
                .padding(horizontal = 10.dp, vertical = 10.dp)
        )

    }
}

@Composable
fun InformationView(textValue: String){
    val fontSizeValue = 13
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
            fontSize = fontSizeValue.sp,
            modifier = Modifier
                .padding(horizontal = 10.dp, vertical = 10.dp)
            ,
        )
    }
}

@Composable
fun ParentTextView(){
    val fontSizeValue = 13
    ElevatedCard (
        modifier = Modifier
            .padding(
                vertical = 10.dp,

                )
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(),
    ){
        Text(
            text = "At SafeCircle, we understand that being a parent comes with its own unique set of challenges, and ensuring the safety and well-being of your children is always a top priority. That's why we've created a powerful and user-friendly Parent View, designed to provide parents with the tools they need to stay connected and informed.",
            fontSize = fontSizeValue.sp,
            modifier = Modifier
                .padding(horizontal = 10.dp, vertical = 10.dp)
            ,
        )
    }

    CardWithHeadingAndText(
        heading = "Location Tracking with Safety Circle:\n",
        value = "Our Parent View offers an intuitive map interface where you can effortlessly keep track of all your children's locations in real-time. What sets us apart is the Safety Circle feature, which allows you to define a safety zone around your child's location. If your child ventures beyond this designated area, you'll be promptly notified, ensuring you're always aware of their whereabouts."
    )

    CardWithHeadingAndText(
        heading = "Customized Filtering:\n",
        value = "We understand that every family is unique, and your app experience should reflect that. Our handy hamburger menu allows you to filter and view the location of each of your children individually, making it easy to stay updated on each child's activities"
    )

    CardWithHeadingAndText(
        heading = "Within the Parent View, you'll also find quick access to essential pages:\n",
        value = "Help Page: In times of need, our Help Page is just a tap away. It provides a direct line of communication between you and your child, ensuring you can respond swiftly to any situation.\n" +
                "About Page: Learn more about the inspiration behind our app and the dedicated team that brought it to life.\n" +
                "Settings Page: Tailor your app experience to your family's unique needs. Here, you can add your children to the \"family,\" set safety distances, establish temperature thresholds for notifications, and provide emergency contact information. It's your toolkit for personalized safety.\n"
    )

    CardWithHeadingAndText(
        heading = "Finally\n",
        value = "At SafeCircle, we believe in putting the power of safety and peace of mind right at your fingertips. Our Parent View is designed to empower parents, allowing you to stay informed, connected, and in control, no matter where life takes your family."
    )



}
@Composable
fun ContactUsView(heading: String, contactUsText: String){
    val fontSizeValue = 13
    ElevatedCard(
        modifier = Modifier
            .padding(
                vertical = 10.dp,
            )
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(),
    ) {
        Column (
            modifier = Modifier
                .padding(horizontal = 10.dp, vertical = 10.dp)
        ){

            Text(
                fontWeight = FontWeight.Bold,
                text = heading,
                fontSize = fontSizeValue.sp,
            )
            CustomSelectableText(contactUsText)
        }
    }
}

@Composable
fun CardWithHeadingAndText(heading: String, value: String) {
    val fontSizeValue = 13
    ElevatedCard(
        modifier = Modifier
            .padding(
                vertical = 10.dp,
            )
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(),
    ) {
        Text(
            buildAnnotatedString(
            ) {
                withStyle(
                    style = SpanStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = fontSizeValue.sp,
                    )
                ) {
                    append(heading + "\n")
                }
                withStyle(
                    SpanStyle(
                        fontSize = fontSizeValue.sp,
                    )
                ) {
                    append(value)
                }
            },


            modifier = Modifier
                .padding(horizontal = 10.dp, vertical = 10.dp)
        )
    }
}

@Composable
fun CustomSelectableText(contactUsText: String) {
    val fontSizeValue = 13
    SelectionContainer() {
        Column() {
            Text(
                text = contactUsText,
                fontSize = fontSizeValue.sp,
            )
        }
    }
}

@Composable
fun ListItem(name: String){

    val fontSizeValue = 13
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
    val childrenViewText = "The children will be able to view a map in which they can locate their " +
            "parents within their surrounding" +
            "\nIn addition to this the children's devices will be constantly updating the database on " +
            "their current location, device temperature, background noise and other information that " +
            "will be extremely useful for the parent"
    val contactUsText = "ysivaraj@student.unimelb.edu.au" // We can change this
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
                    fontSize = fontSizeValue.sp
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
                        ParentTextView()

                    }
                    "Children View" -> {
                        InformationView(textValue = childrenViewText)

                    }
                    "Creators of the App" -> {
                        Column(){
                            CreatorInformation(
                                name = "Luchen Zhou",
                                email = "luczhou@student.unimelb.edu.au",
                            )
                            CreatorInformation(
                                name = "Wei Wang",
                                email = "wangw16@student.unimelb.edu.au",
                            )
                            CreatorInformation(
                                name = "Yifan Cheng",
                                email = "yifacheng@student.unimelb.edu.au",
                            )
                            CreatorInformation(
                                name = "Sichen Lu",
                                email = "sichen2@student.unimelb.edu.au",
                            )
                            CreatorInformation(
                                name = "Chi Zhang",
                                email = "czzhang5@student.unimelb.edu.au",
                            )
                            CreatorInformation(
                                name = "Yukash Sivaraj",
                                email = "ysivaraj@student.unimelb.edu.au",
                            )
                        }

                    }
                    "Contact Us" -> {
                        ContactUsView(
                            heading = "Please send an email to :",
                            contactUsText = contactUsText
                        )
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
fun RecyclerView(names: List<String> = List(5) {"$it"}){

    // The difference between column and lazy column
    // Column will compose everything at once
    // Lazy column will only compose everything only when it is in screen
    LazyColumn(
        modifier = Modifier
            .padding(vertical = 4.dp)
            .fillMaxWidth()
            .fillMaxHeight()

    ){
        items(items = names){name ->
            ListItem(name = name)
        }
    }

}


//@Preview
//@Composable
//private fun ComposablePreview(){
//    SafeCircleTheme {
//        RecyclerView(listOf("About Us", "Parent View", "Children View", "Creators of the App", "Contact Us"))
//    }
//}
