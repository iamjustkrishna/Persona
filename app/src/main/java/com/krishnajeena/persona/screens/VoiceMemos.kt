package com.krishnajeena.persona.screens

import android.Manifest
import android.app.Activity
import android.app.RecoverableSecurityException
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.krishnajeena.persona.R
import com.krishnajeena.persona.features.audio.AudioFile
import com.krishnajeena.persona.features.audio.AudioRecorder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

@Composable
fun VoiceMemosScreen() {
    val context = LocalContext.current
    val audioRecorder = remember { AudioRecorder(context) }
    var isRecording by remember { mutableStateOf(false) }
    var showSaveDialog by remember { mutableStateOf(false) }
    var recordings by remember { mutableStateOf(listOf<AudioFile>()) }
    var showTooltip by remember { mutableStateOf(false) }

    // Permission handling logic preserved
    val permissionToRecordAudio = handleVoicePermissions(context)

    // Smooth color and scale animations for the FAB
    val pulseScale by animateFloatAsState(targetValue = if (isRecording) 1.3f else 1f, label = "pulse")
    val fabColor by animateColorAsState(
        targetValue = if (isRecording) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
        label = "color"
    )

    LaunchedEffect(Unit) {
        recordings = audioRecorder.getRecordings()
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {

            Column(modifier = Modifier.fillMaxSize()) {
                // 1. BRANDED HEADER
                Text(
                    text = "Voice Memos",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Black),
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)
                )

                // 2. RECORDINGS LIST (Logic preserved)
                RecordingsList(
                    recordings = recordings,
                    modifier = Modifier.weight(1f)
                ) { deletedFile ->
                    recordings = recordings.filter { it != deletedFile }
                }
            }

            // 3. THE "STUDIO" RECORDING BUTTON
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 160.dp), // Matches your navigation clearance
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Tooltip logic preserved but visually improved
                AnimatedVisibility(visible = showTooltip || isRecording) {
                    Text(
                        text = if (isRecording) "Recording..." else "Hold to record",
                        style = MaterialTheme.typography.labelMedium,
                        color = if (isRecording) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp))
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                FloatingActionButton(
                    onClick = { showTooltip = true },
                    modifier = Modifier
                        .size(72.dp)
                        .scale(pulseScale)
                        .pointerInput(Unit) {
                            awaitPointerEventScope {
                                while (true) {
                                    val event = awaitPointerEvent()
                                    when (event.type) {
                                        PointerEventType.Press -> {
                                            if (permissionToRecordAudio) {
                                                isRecording = true
                                                audioRecorder.startRecording()
                                            }
                                        }
                                        PointerEventType.Release -> {
                                            if (isRecording) {
                                                isRecording = false
                                                try {
                                                    audioRecorder.stopRecording()
                                                    showSaveDialog = true
                                                } catch (e: RuntimeException) {
                                                    Log.e("AudioRecorder", "Stop Error", e)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        },
                    containerColor = fabColor,
                    contentColor = Color.White,
                    shape = CircleShape,
                    elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 8.dp)
                ) {
                    Icon(
                        imageVector = if (isRecording) Icons.Default.Stop else Icons.Default.Mic,
                        contentDescription = null,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }
    }

    // 4. DIALOGS (Logic preserved)
    if (showSaveDialog) {
        SaveAudioDialog(
            onDismiss = {
                showSaveDialog = false
                audioRecorder.discardRecording()
            },
            onSave = { fileName ->
                audioRecorder.saveRecording(fileName).let {
                    recordings = audioRecorder.getRecordings()
                    Toast.makeText(context, "Memo secured", Toast.LENGTH_SHORT).show()
                }
                showSaveDialog = false
            }
        )
    }

    // Auto-hide tooltip logic preserved
    LaunchedEffect(showTooltip) {
        if (showTooltip) {
            delay(2000)
            showTooltip = false
        }
    }
}


@Composable
fun RecordingsList(recordings: List<AudioFile>, modifier: Modifier = Modifier, onDelete: (AudioFile) -> Unit) {
    if(recordings.isEmpty()){
        Column(modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally){
//     AsyncImage(model = R.drawable._264828, contentDescription = "No Voice Memos!",)
            Text("No recorded memos! Hold mic to record one.", fontSize = 18.sp)
        }
    }
    else {
        LazyColumn(modifier = modifier.fillMaxSize()) {
            items(recordings) { file ->
                RecordingItem(file) {
                    onDelete(file)
                }
            }
        }
    }
}

@Composable
fun RecordingItem(file: AudioFile, onDelete: () -> Unit) {
    val context = LocalContext.current

    Card(modifier = Modifier.fillMaxWidth().padding(2.dp)
    , elevation = CardDefaults.cardElevation(5.dp),
        onClick = {
            val fileUri = file.uri

            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(fileUri, "audio/*")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            try {
                context.startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(context, "No app found to play audio", Toast.LENGTH_SHORT).show()
            }

        })
    {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
        , horizontalArrangement = Arrangement.SpaceBetween
        , verticalAlignment = Alignment.CenterVertically
    ) {
        var duration by remember { mutableStateOf(0L) }

        LaunchedEffect(file) {
            duration = getAudioDurationAsync(context, file.uri)
        }
        Text(text = file.name, fontSize = 18.sp, textAlign = TextAlign.Center)
        Text(text = formatDuration(duration), fontSize = 14.sp, textAlign = TextAlign.Center)

        IconButton(onClick = {
            try {
                // Attempt to delete the file using ContentResolver
                val deletedRows = context.contentResolver.delete(
                    file.uri, // Use file.uri if you're working with MediaStore URIs
                    null,
                    null
                )

                if (deletedRows > 0) {
                    Toast.makeText(context, "File deleted successfully", Toast.LENGTH_SHORT).show()
                    onDelete() // Notify parent to refresh the list
                } else {
                    Toast.makeText(context, "Failed to delete file", Toast.LENGTH_SHORT).show()
                }
            } catch (securityException: SecurityException) {
                val recoverableSecurityException =
                    securityException as? RecoverableSecurityException
                recoverableSecurityException?.userAction?.actionIntent?.intentSender?.let { intentSender ->
                    // Launch a request to get user permission to delete the file
                    val activity = context as? Activity
                    activity?.startIntentSenderForResult(
                        intentSender,
                        DELETE_REQUEST_CODE,
                        null,
                        0,
                        0,
                        0,
                        null
                    )
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Error deleting file: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }, modifier = Modifier.size(24.dp)) {
            Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete audio")
        }


    }
    }
}

const val DELETE_REQUEST_CODE = 1001

suspend fun getAudioDurationAsync(context: Context, uri: Uri): Long = withContext(Dispatchers.IO) {
    val mmr = MediaMetadataRetriever()
    mmr.setDataSource(context, uri)
    val durationStr = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
    durationStr?.toLong() ?: 0
}

fun formatDuration(durationMs: Long): String {
    val seconds = (durationMs / 1000) % 60
    val minutes = (durationMs / (1000 * 60)) % 60
    return String.format("%02d:%02d", minutes, seconds)
}

@Composable
fun TooltipBox(text: String, modifier: Modifier = Modifier) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 4.dp,
        shadowElevation = 4.dp,
        shape = MaterialTheme.shapes.small,
        modifier = modifier.padding(8.dp)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(8.dp),
            style = MaterialTheme.typography.bodySmall
        )
    }
}


@Composable
fun SaveAudioDialog(
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var fileName by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Save Audio") },
        text = {
            Column {
                Text("Enter a name for your audio file:")
                TextField(
                    value = fileName,
                    onValueChange = { fileName = it },
                    label = { Text("File Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    ".mp3",
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(fileName) },
                enabled = fileName.isNotBlank()
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun handleVoicePermissions(context: Context):Boolean{
    var permissions: Array<String> = arrayOf(Manifest.permission.RECORD_AUDIO)

    val arePermissionsGranted = permissions.all { permission ->
        androidx.core.content.ContextCompat.checkSelfPermission(context, permission) ==
                android.content.pm.PackageManager.PERMISSION_GRANTED
    }

    val requestPermissionsLauncher  = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()) { permissionsMap ->
        val allGranted = permissionsMap.all { it.value }
        Toast.makeText(
            context,
            if (allGranted) "All Permissions Granted" else "Permissions Denied",
            Toast.LENGTH_SHORT
        ).show()
    }
    LaunchedEffect(Unit) {
        if (!arePermissionsGranted) {
            requestPermissionsLauncher.launch(permissions)
        }
    }

    return arePermissionsGranted
    }

