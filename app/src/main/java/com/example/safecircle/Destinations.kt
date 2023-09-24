package com.example.safecircle

interface Destinations {
    val route: String
}

object Register: Destinations {
    override val route = "Register"
}
object Login: Destinations {
    override val route = "Login"
}
object Landing: Destinations {
    override val route = "Landing"
}

object Dashboard: Destinations {
    override val route = "Dashboard"
}
object Map: Destinations {
    override val route = "Map"
}

object Settings: Destinations{
    override val route = "Settings"
}

object About: Destinations{
    override val route = "About"
}

object Help: Destinations{
    override val route = "Help"
}

object ChildMap: Destinations{
    override val route = "ChildMap"
}

object ChildSettings: Destinations{
    override val route = "ChildSettings"
}
