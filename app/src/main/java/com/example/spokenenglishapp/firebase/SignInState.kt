package com.example.spokenenglishapp.firebase

data class SignInState(
    val isSignInSuccessful: Boolean = false,
    val signInError: String? = null
)