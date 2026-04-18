package com.krishnajeena.persona

import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.core.view.WindowCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.foundation.isSystemInDarkTheme
import com.krishnajeena.persona.auth.GoogleAuthUiClient
import com.krishnajeena.persona.model.AuthViewModel
import com.krishnajeena.persona.model.SharedViewModel
import com.krishnajeena.persona.model.ThemeViewModel
import com.krishnajeena.persona.other.DailyQuoteReceiver
import com.krishnajeena.persona.reelstack.VideoDatabase
import com.krishnajeena.persona.services.MusicService
import com.krishnajeena.persona.ui.theme.PersonaTheme
import com.krishnajeena.persona.ui_layer.PersonaApp
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    val sharedViewModel: SharedViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()
    private val themeViewModel: ThemeViewModel by viewModels()

    @Inject
    lateinit var database: VideoDatabase

    @Inject
    lateinit var googleAuthUiClient: GoogleAuthUiClient

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        WindowCompat.setDecorFitsSystemWindows(window, false)
        lifecycle.addObserver(AppLifecycleObserver(this))

        setContent {
            val scope = rememberCoroutineScope()
            val authState by authViewModel.state.collectAsStateWithLifecycle()
            val themeState by themeViewModel.themeState.collectAsStateWithLifecycle()
            val systemDarkMode = isSystemInDarkTheme()

            // Update system dark mode when it changes
            LaunchedEffect(systemDarkMode) {
                if (themeState.useSystemTheme) {
                    themeViewModel.updateSystemDarkMode(systemDarkMode)
                }
            }

            val launcher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.StartIntentSenderForResult(),
                onResult = { result ->
                    if (result.resultCode == Activity.RESULT_OK) {
                        scope.launch {
                            val signInResult = googleAuthUiClient.signInWithIntent(
                                intent = result.data ?: return@launch
                            )
                            authViewModel.onSignInResult(signInResult)
                        }
                    } else {
                        // User cancelled sign-in
                        Toast.makeText(
                            applicationContext,
                            "Sign-in cancelled",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            )

            LaunchedEffect(key1 = Unit) {
                if (authState.isSignInSuccessful) {
                    Toast.makeText(
                        applicationContext,
                        "Welcome back, ${authState.signInUser?.username ?: "User"}!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            PersonaTheme(
                darkTheme = themeState.isDarkMode,
                appTheme = themeState.currentTheme
            ) {
                PersonaApp(
                    onSignInClick = {
                        scope.launch {
                            try {
                                val signInIntentSender = googleAuthUiClient.signIn()
                                if (signInIntentSender != null) {
                                    val request = IntentSenderRequest.Builder(signInIntentSender).build()
                                    launcher.launch(request)
                                } else {
                                    Toast.makeText(
                                        applicationContext,
                                        "Sign-in not available. Please check your configuration.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } catch (e: Exception) {
                                Toast.makeText(
                                    applicationContext,
                                    "Sign-in error: ${e.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                )
            }
            scheduleDailyAlarm(applicationContext)
        }
    }


     private fun scheduleDailyAlarm(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(context, DailyQuoteReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, 6) // 9 AM
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }

        // If the time has already passed today, schedule for tomorrow
        if (calendar.timeInMillis <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }

        alarmManager.setAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        )

    }

    override fun onDestroy() {
        super.onDestroy()
        sharedViewModel.destroyMediaController()
        stopService(Intent(this, MusicService::class.java))
    }

}

