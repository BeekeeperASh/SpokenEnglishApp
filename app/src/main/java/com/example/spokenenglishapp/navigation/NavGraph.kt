package com.example.spokenenglishapp.navigation

import android.app.Activity.RESULT_OK
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.LifecycleCoroutineScope
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
import com.example.spokenenglishapp.app_screens.ChatLevels
import com.example.spokenenglishapp.app_screens.dialogue
import com.example.spokenenglishapp.app_tools.Level
import com.example.spokenenglishapp.firebase.GoogleAuthUiClient
import com.example.spokenenglishapp.firebase.SignInScreen
import com.example.spokenenglishapp.firebase.SignInViewModel
import com.example.spokenenglishapp.firebase.login.LoginScreen
import com.example.spokenenglishapp.firebase.login.LoginViewModel
import com.example.spokenenglishapp.firebase.login.SignUpScreen
import com.example.spokenenglishapp.profile.ProfileScreen
import com.example.spokenenglishapp.profile.ProfileScreenDefault
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
                        imageResource = "https://www.globaltourismforum.org/wp-content/uploads/2020/02/travel-scaled.jpg"
                    ),
                    Level(
                        "Dialog2",
                        description = "Something",
                        route = "screen_dialogue",
                        imageResource = "https://replicate.delivery/pbxt/0faix0jVnsWfHEXk4hB00UPqqogMCI3kBcgUie4tJWtOUM5fA/out-0.png"
                    ),
                    Level("", description = "Anything"),
                    Level(
                        route = "screen_3",
                        imageResource = "https://cdn.discordapp.com/attachments/995431274267279440/1039324448790151228/Artlandis_a_majestic_colossal_ancient_tree_made_with_intricate__6ec2679d-18d4-42a0-bd23-78e3de632248.png"
                    ),
                    Level(route = "screen_4"),
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
        composable("screen_2") {
            Screen2()
        }
        composable("screen_3") {
            Screen3()
        }
        composable("screen_4") {
            Screen4()
        }
        composable("screen_5") {
            Screen5()
        }

        composable("screen_dialogue") {
            dialogue(
                navHostController,
                textLists[index.value],
                soundLists[index.value],
                imagesList[0]
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

        composable("profile_default"){
            ProfileScreenDefault()
        }
    }
}