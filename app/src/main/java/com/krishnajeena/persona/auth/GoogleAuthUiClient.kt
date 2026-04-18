package com.krishnajeena.persona.auth

import android.content.Context
import android.content.Intent
import android.content.IntentSender
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.BeginSignInRequest.GoogleIdTokenRequestOptions
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.auth.api.identity.Identity
import kotlinx.coroutines.tasks.await
import java.util.concurrent.CancellationException

class GoogleAuthUiClient(
    private val context: Context,
    private val oneTapClient: SignInClient
) {
    private val signInRequest = BeginSignInRequest.builder()
        .setGoogleIdTokenRequestOptions(
            GoogleIdTokenRequestOptions.builder()
                .setSupported(true)
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId("138146477718-352rpdrjsa3qu4hmbhfrc77ehhoo14hb.apps.googleusercontent.com") // Replace with your actual Web Client ID from Firebase
                .build()
        )
        .setAutoSelectEnabled(true)
        .build()

    suspend fun signIn(): IntentSender? {
        val result = try {
            oneTapClient.beginSignIn(signInRequest).await()
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is CancellationException) throw e
            null
        }
        return result?.pendingIntent?.intentSender
    }

    suspend fun signInWithIntent(intent: Intent): SignInResult {
        val credential = oneTapClient.getSignInCredentialFromIntent(intent)
        val googleIdToken = credential.googleIdToken
        val googleId = credential.id
        val username = credential.displayName
        val profilePictureUrl = credential.profilePictureUri?.toString()

        return SignInResult(
            data = googleIdToken?.let {
                UserData(
                    userId = googleId,
                    username = username,
                    profilePictureUrl = profilePictureUrl
                )
            },
            errorMessage = null
        )
    }

    suspend fun signOut() {
        try {
            oneTapClient.signOut().await()
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is CancellationException) throw e
        }
    }

    fun getSignedInUser(): UserData? {
        // Check if user data is stored in SharedPreferences
        val prefs = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
        val userId = prefs.getString("userId", null)
        val username = prefs.getString("username", null)
        val profilePictureUrl = prefs.getString("profilePictureUrl", null)

        return if (userId != null) {
            UserData(
                userId = userId,
                username = username,
                profilePictureUrl = profilePictureUrl
            )
        } else null
    }

    fun saveUserData(userData: UserData) {
        val prefs = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
        prefs.edit().apply {
            putString("userId", userData.userId)
            putString("username", userData.username)
            putString("profilePictureUrl", userData.profilePictureUrl)
            apply()
        }
    }

    fun clearUserData() {
        val prefs = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
        prefs.edit().clear().apply()
    }
}

data class SignInResult(
    val data: UserData?,
    val errorMessage: String?
)

data class UserData(
    val userId: String,
    val username: String?,
    val profilePictureUrl: String?
)
