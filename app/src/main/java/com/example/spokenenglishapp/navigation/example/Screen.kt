package com.example.spokenenglishapp.navigation.example

sealed class Screen(val route: String){
    object MainScreen: Screen("main_screen")
    object SecondScreen: Screen("second_screen")

    fun withArgs(vararg args: String): String{
        return buildString {
            append(route)
            args.forEach {arg ->
                append("/$arg")
            }
        }
    }
}
