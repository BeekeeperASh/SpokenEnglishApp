package com.example.spokenenglishapp.firebase

import com.google.firebase.Timestamp

data class SignInResult(
    val data: UserData?,
    val errorMessage: String?
)

data class UserData(
    val userId: String = "",
    val username: String? = "",
    val profilePictureUrl: String? = "",
    val timestamp: Timestamp? = Timestamp.now(),
    val documentId: String = "",
    val accuracyMap: HashMap<String, Any> = hashMapOf<String, Any>(Pair("First day", 0)),
)
