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
