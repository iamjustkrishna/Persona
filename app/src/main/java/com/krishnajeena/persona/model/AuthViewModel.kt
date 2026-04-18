package com.krishnajeena.persona.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.krishnajeena.persona.auth.GoogleAuthUiClient
import com.krishnajeena.persona.auth.SignInResult
import com.krishnajeena.persona.auth.UserData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val googleAuthUiClient: GoogleAuthUiClient
) : ViewModel() {

    private val _state = MutableStateFlow(AuthState())
    val state: StateFlow<AuthState> = _state.asStateFlow()

    init {
        // Check if user is already signed in
        val signedInUser = googleAuthUiClient.getSignedInUser()
        _state.update { it.copy(
            isSignInSuccessful = signedInUser != null,
            signInUser = signedInUser
        ) }
    }

    fun onSignInResult(result: SignInResult) {
        _state.update { it.copy(
            isSignInSuccessful = result.data != null,
            signInError = result.errorMessage,
            signInUser = result.data
        ) }

        // Save user data to SharedPreferences
        result.data?.let { userData ->
            googleAuthUiClient.saveUserData(userData)
        }
    }

    fun signOut() {
        viewModelScope.launch {
            googleAuthUiClient.signOut()
            googleAuthUiClient.clearUserData()
            _state.update { AuthState() }
        }
    }

    fun resetState() {
        _state.update { it.copy(
            signInError = null
        ) }
    }

    fun isUserLoggedIn(): Boolean {
        return _state.value.isSignInSuccessful && _state.value.signInUser != null
    }

    fun getSignedInUser(): UserData? {
        return _state.value.signInUser
    }
}

data class AuthState(
    val isSignInSuccessful: Boolean = false,
    val signInError: String? = null,
    val signInUser: UserData? = null
)
