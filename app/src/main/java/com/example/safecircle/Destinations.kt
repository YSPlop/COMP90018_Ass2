package com.example.safecircle

interface Destinations {
    val route: String
}

interface MapDestinations: Destinations {
    fun ofUser(username: String): String
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

object Map: MapDestinations {
    override fun ofUser(username: String): String {
        return "Map?username=${username}"
    }

    override val route = "Map"
    val routeTemplate = "Map?username={username}"

}
object Dashboard: Destinations {
    override val route = "Dashboard"
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
