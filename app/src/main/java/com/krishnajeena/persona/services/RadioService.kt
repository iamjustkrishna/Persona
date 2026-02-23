package com.krishnajeena.persona.services

import android.app.PendingIntent
import android.content.Intent
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.krishnajeena.persona.MainActivity

class RadioService : MediaSessionService() {
    private var mediaSession: MediaSession? = null
    private lateinit var player: ExoPlayer

    override fun onCreate() {
        super.onCreate()
        // Initialize player once in the Service lifecycle
        player = ExoPlayer.Builder(this).build()

        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)


        mediaSession = MediaSession.Builder(this, player)
            .setId("Persona_Radio_session")
            .setSessionActivity(pendingIntent)
            .build()
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? = mediaSession

    override fun onDestroy() {
        mediaSession?.run {
            player.stop() // Stop data flow
            player.clearMediaItems() // Clear the buffer
            player.release()
            release()
            mediaSession = null
        }
        super.onDestroy()
    }
}