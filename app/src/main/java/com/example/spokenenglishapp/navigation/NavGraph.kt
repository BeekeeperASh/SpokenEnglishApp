package com.example.spokenenglishapp.navigation

import android.app.Activity.RESULT_OK
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.spokenenglishapp.MainActivity.Companion.imagesList
import com.example.spokenenglishapp.MainActivity.Companion.soundLists
import com.example.spokenenglishapp.MainActivity.Companion.textLists
import com.example.spokenenglishapp.app_screens.*
import com.example.spokenenglishapp.app_tools.Level
import com.example.spokenenglishapp.firebase.GoogleAuthUiClient
import com.example.spokenenglishapp.firebase.SignInScreen
import com.example.spokenenglishapp.firebase.SignInViewModel
import com.example.spokenenglishapp.firebase.login.LoginScreen
import com.example.spokenenglishapp.firebase.login.LoginViewModel
import com.example.spokenenglishapp.firebase.login.SignUpScreen
import com.example.spokenenglishapp.profile.ProfileScreen
import com.google.android.gms.auth.api.identity.Identity
import kotlinx.coroutines.launch

@Composable
fun NavGraph(
    navHostController: NavHostController,
    isSub: MutableState<Boolean>,
    loginViewModel: LoginViewModel
) {
    val context = LocalContext.current
    val googleAuthUiClient by lazy {
        GoogleAuthUiClient(
            context = context,
            oneTapClient = Identity.getSignInClient(context)
        )
    }
    val index = remember {
        mutableStateOf(0)
    }
    val backStackEntry by navHostController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    isSub.value = currentRoute == "screen_dialogue"

    NavHost(navController = navHostController, startDestination = "chat_levels") {
        composable("chat_levels") {
            ChatLevels(
                listOf(
                    Level(
                        "Travel and Tourism",
                        description = "Путешествия и туризм",
                        route = "screen_dialogue",
                        imageResource = "https://thumbnails.production.thenounproject.com/BZRlWdoa49K0kzNWKPek5j8gm2o=/fit-in/1000x1000/photos.production.thenounproject.com/photos/47D81F75-46AF-49CD-A372-E06F07FD8C8C.jpg"
                    ),
                    Level(
                        "Museum",
                        description = "Музей",
                        route = "screen_dialogue",
                        imageResource = "https://images.unsplash.com/photo-1564399579883-451a5d44ec08?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8MTd8fG11c2V1bXxlbnwwfHwwfHx8MA%3D%3D&auto=format&fit=crop&w=500&q=60"
                    ),
                    Level(
                        "Example",
                        description = "Пример",
                        route = "screen_dialogue",
                        imageResource = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSuT2E_Y0aivI43xfQ66jPt9yc4j5T11d0DlQ&usqp=CAU"
                    ),
                    Level("Test",
                        description = "Тест",
                        route = "screen_dialogue",
                        imageResource = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSuT2E_Y0aivI43xfQ66jPt9yc4j5T11d0DlQ&usqp=CAU"),
                    Level(),
                    Level(),
                    Level(),
                    Level(),
                    Level(),
                    Level(),
                    Level(),
                    Level(),
                    Level(),
                    Level()
                ), navHostController, index
            )
        }
        composable("custom_exercise") {
            TextInput()
        }
        composable("screen_5") {
            Screen5()
        }

        composable("screen_dialogue") {
            dialogue(
                navHostController,
                textLists[index.value],
                soundLists[index.value],
                imagesList[index.value]
            )
        }

        composable("sign_in") {
            val viewModel = viewModel<SignInViewModel>()
            val state by viewModel.state.collectAsStateWithLifecycle()
            LaunchedEffect(key1 = Unit) {
                if (googleAuthUiClient.getSignedInUser() != null) {
                    navHostController.navigate("profile")
                }
            }
            val lifecycleOwner = LocalLifecycleOwner.current
            val launcher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.StartIntentSenderForResult(),
                onResult = { result ->
                    if (result.resultCode == RESULT_OK) {
                        lifecycleOwner.lifecycleScope.launch {
                            val signInResult = googleAuthUiClient.signInWithIntent(
                                intent = result.data ?: return@launch
                            )
                            viewModel.onSignInResult(signInResult)
                        }
                    }
                }
            )

            LaunchedEffect(key1 = state.isSignInSuccessful) {
                if (state.isSignInSuccessful) {
                    Toast.makeText(
                        context,
                        "Sign in successful",
                        Toast.LENGTH_LONG
                    ).show()
                    navHostController.navigate("profile")
                    viewModel.resetState()
                }
            }

            SignInScreen(
                state = state,
                onSignInClick = {
                    lifecycleOwner.lifecycleScope.launch {
                        val signInIntentSender = googleAuthUiClient.signIn()
                        launcher.launch(
                            IntentSenderRequest.Builder(
                                signInIntentSender ?: return@launch
                            ).build()
                        )
                    }
                }
            )
        }

        composable("profile") {
            val lifecycleOwner = LocalLifecycleOwner.current
            ProfileScreen(
                userData = googleAuthUiClient.getSignedInUser(),
                onSignOut = {
                    lifecycleOwner.lifecycleScope.launch {
                        googleAuthUiClient.signOut()
                        Toast.makeText(context, "Signed out", Toast.LENGTH_LONG).show()
                        navHostController.popBackStack()
                    }
                }
            )
        }

        composable("auth") {
            LoginScreen(
                onNavToHome = {
                    navHostController.navigate("profile") {
                        launchSingleTop = true
                        popUpTo("auth") {
                            inclusive = true
                        }
                    }
                },
                loginViewModel = loginViewModel
            ) {
                navHostController.navigate("auth_sign_up") {
                    launchSingleTop = true
                    popUpTo("auth") {
                        inclusive = true
                    }
                }
            }
        }

        composable("auth_sign_up") {
            SignUpScreen(
                onNavToHome = {
                    navHostController.navigate("profile") {
                        popUpTo("auth_sign_up") {
                            inclusive = true
                        }
                    }
                },
                loginViewModel = loginViewModel
            ) {
                navHostController.navigate("auth")
            }
        }

        composable("sub_screen") {
            SubScreen()

        }
    }
}