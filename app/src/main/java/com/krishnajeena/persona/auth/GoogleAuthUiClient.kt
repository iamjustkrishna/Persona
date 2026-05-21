package com.krishnajeena.persona.auth

import android.content.Context
import android.content.Intent
import android.content.IntentSender
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.BeginSignInRequest.GoogleIdTokenRequestOptions
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.auth.api.identity.Identity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await
import java.util.concurrent.CancellationException

class GoogleAuthUiClient(
    private val context: Context,
    private val oneTapClient: SignInClient,
    private val firebaseAuth: FirebaseAuth
) {
    private val signInRequest = BeginSignInRequest.builder()
        .setGoogleIdTokenRequestOptions(
            GoogleIdTokenRequestOptions.builder()
                .setSupported(true)
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId("138146477718-t9bel2o2v9ua8aaq78mlk7o24gqgoivs.apps.googleusercontent.com") // Replace with your actual Web Client ID from Firebase
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

        return try {
            googleIdToken?.let { token ->
                // Sign in to Firebase with Google ID token
                val firebaseCredential = GoogleAuthProvider.getCredential(token, null)
                firebaseAuth.signInWithCredential(firebaseCredential).await()
                
                SignInResult(
                    data = UserData(
                        userId = googleId,
                        username = username,
                        profilePictureUrl = profilePictureUrl
                    ),
                    errorMessage = null
                )
            } ?: SignInResult(
                data = null,
                errorMessage = "No Google ID token received"
            )
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is CancellationException) throw e
            SignInResult(
                data = null,
                errorMessage = "Firebase sign-in failed: ${e.message}"
            )
        }
    }

    suspend fun signOut() {
        try {
            firebaseAuth.signOut()
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
