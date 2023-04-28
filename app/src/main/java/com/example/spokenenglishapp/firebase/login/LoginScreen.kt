package com.example.spokenenglishapp.firebase.login

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.spokenenglishapp.firebase.GoogleAuthUiClient
import com.example.spokenenglishapp.firebase.SignInScreen
import com.example.spokenenglishapp.firebase.SignInViewModel
import com.example.spokenenglishapp.ui.theme.SpokenEnglishAppTheme
import com.google.android.gms.auth.api.identity.Identity
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    loginViewModel: LoginViewModel? = null,
    onNavToHome: () -> Unit,
    onNavToSignUp: () -> Unit
) {

    val loginUiState = loginViewModel?.loginUiState
    val isError = loginUiState?.loginError != null
    val context = LocalContext.current
    val googleAuthUiClient by lazy {
        GoogleAuthUiClient(
            context = context,
            oneTapClient = Identity.getSignInClient(context)
        )
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Login",
            style = MaterialTheme.typography.h3,
            fontWeight = FontWeight.Black,
            color = MaterialTheme.colors.primary
        )

        if (isError){
            Text(text = loginUiState?.loginError ?: "unknown error", color = Color.Red)
        }

        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
            ,
            value = loginUiState?.userName ?: "",
            onValueChange = {loginViewModel?.onUserNameChange(it)},
            leadingIcon = { Icon(imageVector = Icons.Default.Person, contentDescription = null)},
            label = {
                Text(text = "Email")
            },
            isError = isError
        )

        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
            ,
            value = loginUiState?.password ?: "",
            onValueChange = {loginViewModel?.onPasswordChange(it)},
            leadingIcon = { Icon(imageVector = Icons.Default.Lock, contentDescription = null)},
            label = {
                Text(text = "Password")
            },
            visualTransformation = PasswordVisualTransformation(),
            isError = isError
        )

        Button(onClick = { loginViewModel?.loginUser(context) }) {
            Text(text = "Sign In")
        }
        
        Spacer(modifier = Modifier.size(16.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(text = "Don't have an Account?", textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.size(8.dp))
            TextButton(onClick = { onNavToSignUp.invoke() }) {
                Text(text = "SignUp")
            }
        }

        if (loginUiState?.isLoading == true){
            CircularProgressIndicator()
        }

        LaunchedEffect(key1 = loginViewModel?.hasUser){
            if (loginViewModel?.hasUser == true){
                onNavToHome.invoke()
            }
        }

        val viewModel = viewModel<SignInViewModel>()
        val state by viewModel.state.collectAsStateWithLifecycle()
        LaunchedEffect(key1 = Unit) {
            if (googleAuthUiClient.getSignedInUser() != null) {
                //navHostController.navigate("profile")
                onNavToHome.invoke()
            }
        }
        val lifecycleOwner = LocalLifecycleOwner.current
        val launcher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartIntentSenderForResult(),
            onResult = { result ->
                if (result.resultCode == Activity.RESULT_OK) {
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
                //navHostController.navigate("profile")
                onNavToHome.invoke()
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

}


@Composable
fun SignUpScreen(
    loginViewModel: LoginViewModel? = null,
    onNavToHome: () -> Unit,
    onNavToLogin: () -> Unit
) {

    val loginUiState = loginViewModel?.loginUiState
    val isError = loginUiState?.signUpError != null
    val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Sign Up",
            style = MaterialTheme.typography.h3,
            fontWeight = FontWeight.Black,
            color = MaterialTheme.colors.primary
        )

        if (isError){
            Text(text = loginUiState?.signUpError ?: "unknown error", color = Color.Red)
        }

        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
            ,
            value = loginUiState?.userNameSignUp ?: "",
            onValueChange = {loginViewModel?.onUserNameSignUpChange(it)},
            leadingIcon = { Icon(imageVector = Icons.Default.Person, contentDescription = null)},
            label = {
                Text(text = "Email")
            },
            isError = isError
        )

        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
            ,
            value = loginUiState?.passwordSignUp ?: "",
            onValueChange = {loginViewModel?.onPasswordSignUpChange(it)},
            leadingIcon = { Icon(imageVector = Icons.Default.Lock, contentDescription = null)},
            label = {
                Text(text = "Password")
            },
            visualTransformation = PasswordVisualTransformation(),
            isError = isError
        )

        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
            ,
            value = loginUiState?.confirmPasswordSignUp ?: "",
            onValueChange = {loginViewModel?.onConfirmPasswordSignUpChange(it)},
            leadingIcon = { Icon(imageVector = Icons.Default.Lock, contentDescription = null)},
            label = {
                Text(text = "Confirm Password")
            },
            visualTransformation = PasswordVisualTransformation(),
            isError = isError
        )

        Button(onClick = { loginViewModel?.createUser(context) }) {
            Text(text = "Sign Up")
        }

        Spacer(modifier = Modifier.size(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(text = "Already have an Account?", textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.size(8.dp))
            TextButton(onClick = { onNavToLogin.invoke() }) {
                Text(text = "SignIn")
            }
        }

        if (loginUiState?.isLoading == true){
            CircularProgressIndicator()
        }

        LaunchedEffect(key1 = loginViewModel?.hasUser){
            if (loginViewModel?.hasUser == true){
                onNavToHome.invoke()
            }
        }

    }

}

//@Preview(showSystemUi = true)
//@Composable
//fun prevLoginScreen(){
//    SpokenEnglishAppTheme {
//        LoginScreen(onNavToHome = { /*TODO*/ }) {
//
//        }
//    }
//}
//
//@Preview(showSystemUi = true)
//@Composable
//fun prevSingUpScreen(){
//    SpokenEnglishAppTheme {
//        SignUpScreen(onNavToHome = { /*TODO*/ }) {
//
//        }
//    }
//}