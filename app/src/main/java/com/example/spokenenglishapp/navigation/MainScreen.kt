package com.example.spokenenglishapp.navigation

import android.annotation.SuppressLint
import androidx.compose.material.Scaffold
import androidx.compose.runtime.*
import androidx.navigation.compose.rememberNavController
import com.example.spokenenglishapp.firebase.login.LoginViewModel

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun MainScreen (
    loginViewModel: LoginViewModel
){

    val navController = rememberNavController()

    val isSub = remember{
        mutableStateOf(false)
    }


    Scaffold(
        bottomBar = {
            if (!isSub.value) BottomNavigation(navController = navController)
        },
        topBar = {
            if (!isSub.value) TopNavigation(navController = navController)
        }

    ) {
        NavGraph(navHostController = navController, isSub, loginViewModel)
    }
}