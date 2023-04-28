package com.example.spokenenglishapp.firebase.login

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spokenenglishapp.firebase.authDefault.AuthRepository
import kotlinx.coroutines.launch

class LoginViewModel(
    private val repository: AuthRepository = AuthRepository()
) : ViewModel() {

    val currentUser = repository.currentUser

    val hasUser: Boolean
        get() = repository.hasUser()

    var loginUiState by mutableStateOf(LoginUiState())
        private set

    fun onUserNameChange(userName: String){
        loginUiState = loginUiState.copy(userName = userName)
    }
    fun onPasswordChange(password: String){
        loginUiState = loginUiState.copy(password = password)
    }
    fun onUserNameSignUpChange(userName: String){
        loginUiState = loginUiState.copy(userNameSignUp = userName)
    }
    fun onPasswordSignUpChange(password: String){
        loginUiState = loginUiState.copy(passwordSignUp = password)
    }
    fun onConfirmPasswordSignUpChange(password: String){
        loginUiState = loginUiState.copy(confirmPasswordSignUp =  password)
    }

    fun validateLoginForm() =
        loginUiState.userName.isNotBlank() &&
                loginUiState.password.isNotBlank()
    fun validateSignUpForm() =
        loginUiState.userNameSignUp.isNotBlank() &&
                loginUiState.passwordSignUp.isNotBlank() &&
                loginUiState.confirmPasswordSignUp.isNotBlank()

    fun createUser(context: Context) = viewModelScope.launch {
        try {
            if(!validateSignUpForm()){
                throw IllegalArgumentException("Email and password can not be empty")
            }
            loginUiState = loginUiState.copy(isLoading = true)
            if(loginUiState.passwordSignUp != loginUiState.confirmPasswordSignUp){
                throw IllegalArgumentException("Password do not match")
            }
            loginUiState = loginUiState.copy(signUpError = null)

            repository.createUser(
                loginUiState.userNameSignUp,
                loginUiState.passwordSignUp
            ){isSuccessful ->
                if (isSuccessful){
                    Toast.makeText(context, "success Login", Toast.LENGTH_SHORT).show()
                    loginUiState = loginUiState.copy(isSuccessLogin = true)
                } else {
                    Toast.makeText(context, "failed Login", Toast.LENGTH_SHORT).show()
                    loginUiState = loginUiState.copy(isSuccessLogin = false)
                }
            }

        } catch (e: Exception){
            loginUiState = loginUiState.copy(signUpError = e.localizedMessage)
            e.printStackTrace()
        } finally {
            loginUiState = loginUiState.copy(isLoading = false)
        }
    }

    fun loginUser(context: Context) = viewModelScope.launch {
        try {
            if(!validateLoginForm()){
                throw IllegalArgumentException("Email and password can not be empty")
            }
            loginUiState = loginUiState.copy(isLoading = true)

            loginUiState = loginUiState.copy(loginError = null)

            repository.login(
                loginUiState.userName,
                loginUiState.password
            ){isSuccessful ->
                if (isSuccessful){
                    Toast.makeText(context, "success Login", Toast.LENGTH_SHORT).show()
                    loginUiState = loginUiState.copy(isSuccessLogin = true)
                } else {
                    Toast.makeText(context, "failed Login", Toast.LENGTH_SHORT).show()
                    loginUiState = loginUiState.copy(isSuccessLogin = false)
                }
            }

        } catch (e: Exception){
            loginUiState = loginUiState.copy(loginError = e.localizedMessage)
            e.printStackTrace()
        } finally {
            loginUiState = loginUiState.copy(isLoading = false)
        }
    }

}

data class LoginUiState(
    val userName: String = "",
    val password: String = "",
    val userNameSignUp: String = "",
    val passwordSignUp: String = "",
    val confirmPasswordSignUp: String = "",
    val isLoading: Boolean = false,
    val isSuccessLogin: Boolean = false,
    val signUpError: String? = null,
    val loginError: String? = null
)