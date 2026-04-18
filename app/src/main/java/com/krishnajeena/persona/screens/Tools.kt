package com.krishnajeena.persona.screens

import android.app.Activity
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.krishnajeena.persona.data_layer.ToolItem
import com.krishnajeena.persona.network.RetrofitClient
import com.krishnajeena.persona.network.VideoResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun ToolsScreen(modifier: Modifier = Modifier) {
    var selectedTool by remember { mutableStateOf<String?>(null) }

    val tools = remember {
        listOf(
            ToolItem(
                id = "voice_memos",
                title = "Voice Memos",
                description = "Quick voice notes & recordings",
                icon = Icons.Rounded.Mic,
                route = "voice_memos"
            )
        )
    }

    Box(modifier = modifier.fillMaxSize()) {
        if (selectedTool == null) {
            // Main tools grid
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            ) {
                // Header
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "Productivity Tools",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Smart tools to boost your productivity",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                // Tools grid
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(tools) { tool ->
                        ToolCard(
                            tool = tool,
                            onClick = {
                                if (!tool.comingSoon) {
                                    selectedTool = tool.id
                                }
                            }
                        )
                    }
                }
            }
        } else {
            // Show selected tool
            when (selectedTool) {
                "video_downloader" -> {
                    ToolDetailScreen(
                        title = "Media Downloader",
                        onBack = { selectedTool = null }
                    ) {
                        VideoDownloaderScreen()
                    }
                }
                "voice_memos" -> {
                    ToolDetailScreen(
                        title = "Voice Memos",
                        onBack = { selectedTool = null }
                    ) {
                        VoiceMemosScreen()
                    }
                }
            }
        }
    }
}

@Composable
fun ToolCard(
    tool: ToolItem,
    onClick: () -> Unit
) {
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .clickable {
                if (tool.comingSoon) {
                    Toast.makeText(context, "Coming Soon! 🚀", Toast.LENGTH_SHORT).show()
                } else {
                    onClick()
                }
            },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (tool.comingSoon)
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            else
                MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (tool.comingSoon) 0.dp else 4.dp
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Icon and badge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .background(
                                if (tool.comingSoon)
                                    MaterialTheme.colorScheme.surfaceVariant
                                else
                                    MaterialTheme.colorScheme.primaryContainer,
                                RoundedCornerShape(16.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = tool.icon,
                            contentDescription = null,
                            tint = if (tool.comingSoon)
                                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                            else
                                MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    if (tool.comingSoon) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.secondaryContainer
                        ) {
                            Text(
                                text = "Soon",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }
                }

                // Title and description
                Column {
                    Text(
                        text = tool.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = if (tool.comingSoon)
                            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        else
                            MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = tool.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(
                            alpha = if (tool.comingSoon) 0.5f else 1f
                        ),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 16.sp
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToolDetailScreen(
    title: String,
    onBack: () -> Unit,
    content: @Composable () -> Unit
) {

        Column(modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally){
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
//                    .windowInsetsPadding(WindowInsets.statusBars)
                    .height(56.dp)
                    .padding(horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back"
                    )
                }
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                content()
            }

    }
}
@Composable
fun VideoDownloaderScreen() {

        val context = LocalContext.current
        val activity = context as? Activity
        val sharedText = activity?.intent?.getStringExtra(Intent.EXTRA_TEXT)


        var videoUrl by remember { mutableStateOf("") }
        var fetchedUrl by remember { mutableStateOf<String?>(null) }
        var isLoading by remember { mutableStateOf(false) }
        var selectedType by remember { mutableStateOf("video") } // "video" or "audio"

        LaunchedEffect(sharedText) {
                if(!sharedText.isNullOrEmpty()){
                        videoUrl = sharedText
                        isLoading = true
                        CoroutineScope(Dispatchers.IO).launch {
                                val response = fetchVideoUrl(videoUrl, selectedType)
                                withContext(Dispatchers.Main) {
                                        isLoading = false
                                        if (!response.url.isNullOrEmpty()) {
                                                fetchedUrl = response.url
                                        } else {
                                                Toast.makeText(context, "Error fetching", Toast.LENGTH_SHORT).show()
                                        }
                                }
                        }
                }
        }

        Column(modifier = Modifier
                .padding(16.dp)
                .verticalScroll(rememberScrollState())) {
                OutlinedTextField(
                        value = videoUrl,
                        onValueChange = { videoUrl = it },
                        label = { Text("Enter Video URL") },
                        modifier = Modifier.fillMaxWidth(),
                        trailingIcon = {
                                if (videoUrl.isNotEmpty()) {
                                        IconButton(onClick = { videoUrl = "" }) {
                                                Icon(imageVector = Icons.Default.Close, contentDescription = "Clear text")
                                        }
                                }
                        }
                )


                Spacer(modifier = Modifier.height(16.dp))

                // Radio buttons for selecting download type
                Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                                selected = selectedType == "video",
                                onClick = { selectedType = "video" }
                        )
                        Text("Video", modifier = Modifier.padding(start = 8.dp))

                        Spacer(modifier = Modifier.width(16.dp))

                        RadioButton(
                                selected = selectedType == "audio",
                                onClick = { selectedType = "audio" }
                        )
                        Text("Audio", modifier = Modifier.padding(start = 8.dp))
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                        onClick = {
                                isLoading = true
                                CoroutineScope(Dispatchers.IO).launch {
                                        val response = fetchVideoUrl(videoUrl, selectedType)
                                        withContext(Dispatchers.Main) {
                                                isLoading = false
                                                if (!response.url.isNullOrEmpty()) {
                                                        fetchedUrl = response.url
                                                } else {
                                                        Toast.makeText(context, "Error fetching", Toast.LENGTH_SHORT).show()
                                                }
                                        }
                                }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isLoading
                ) {
                        Text("Get $selectedType")
                }

                if (isLoading) {
                        Spacer(modifier = Modifier.height(16.dp))
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                fetchedUrl?.let { url ->
                        Spacer(modifier = Modifier.height(16.dp))

                        if (selectedType == "video") {
                                VideoPlayer(
                                    videoUrl = url,
                                    onAddToReels = { }
                                )
                        } else {
                                AudioPlayer(audioUrl = url)
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                                onClick = { downloadMedia(context, url, selectedType) },
                                modifier = Modifier.fillMaxWidth()
                        ) {
                                Text("Download $selectedType")
                        }
                }
                }
        }
}

suspend fun fetchVideoUrl(url: String, downloadType: String): VideoResponse {
        return try {
                RetrofitClient.instance.getVideo(
                        mapOf("url" to url, "download_type" to downloadType)
                )
        } catch (e: Exception) {
                VideoResponse(null, null, null, null, null, "Error: ${e.message}")
        }
}


@Composable
fun VideoPlayer(
        videoUrl: String,
        onAddToReels: (String) -> Unit // callback when + is clicked
) {
        val context = LocalContext.current
        val lifecycleOwner = LocalLifecycleOwner.current

        val exoPlayer = remember {
                ExoPlayer.Builder(context).build().apply {
                        setMediaItem(MediaItem.fromUri(Uri.parse(videoUrl)))
                        prepare()
                        playWhenReady = true
                }
        }

        // Pause/resume on lifecycle change
        DisposableEffect(lifecycleOwner) {
                val observer = LifecycleEventObserver { _, event ->
                        when (event) {
                                Lifecycle.Event.ON_PAUSE -> exoPlayer.playWhenReady = false
                                Lifecycle.Event.ON_RESUME -> exoPlayer.playWhenReady = true
                                else -> {}
                        }
                }

                lifecycleOwner.lifecycle.addObserver(observer)

                onDispose {
                        lifecycleOwner.lifecycle.removeObserver(observer)
                        exoPlayer.release()
                }
        }
        
        Box(
                modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
        ) {
                // ExoPlayer view
                AndroidView(
                        factory = { ctx ->
                                PlayerView(ctx).apply {
                                        player = exoPlayer
                                        useController = true
                                }
                        },
                        modifier = Modifier
                                .fillMaxSize()
                )


        }
}

fun downloadMedia(context: Context, mediaUrl: String, mediaType: String) {
        val fileName = if (mediaType == "audio") "downloaded_audio.mp3" else "downloaded_video.mp4"

        try {
                val request = DownloadManager.Request(Uri.parse(mediaUrl)).apply {
                        setTitle("Downloading $mediaType")
                        setDescription("Downloading from $mediaUrl")
                        setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                        setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
                        allowScanningByMediaScanner()
                        setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
                }

                val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                downloadManager.enqueue(request)

                Toast.makeText(context, "Download Started...", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
                Toast.makeText(context, "Download Failed: ${e.message}", Toast.LENGTH_LONG).show()
                Log.e("TAG::", "Download Error: ${e.message}")
        }
}
@Composable
fun AudioPlayer(audioUrl: String) {
        LocalContext.current
        var mediaPlayer: MediaPlayer? by remember { mutableStateOf(null) }
        var isPlaying by remember { mutableStateOf(false) }
        var currentPosition by remember { mutableStateOf(0f) }
        var totalDuration by remember { mutableStateOf(1f) }

        DisposableEffect(audioUrl) {
                mediaPlayer = MediaPlayer().apply {
                        setAudioStreamType(AudioManager.STREAM_MUSIC)
                        setDataSource(audioUrl)
                        prepareAsync()
                        setOnPreparedListener {
                                totalDuration = it.duration.toFloat()
                        }
                }

                val handler = Handler(Looper.getMainLooper())
                val updatePosition = object : Runnable {
                        override fun run() {
                                mediaPlayer?.let {
                                        currentPosition = it.currentPosition.toFloat()
                                        handler.postDelayed(this, 500) // Update every 500ms
                                }
                        }
                }
                handler.post(updatePosition)

                onDispose {
                        mediaPlayer?.release()
                        mediaPlayer = null
                }
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // SeekBar (Slider)
                Slider(
                        value = currentPosition,
                        onValueChange = { newPosition ->
                                mediaPlayer?.seekTo(newPosition.toInt())
                                currentPosition = newPosition
                        },
                        valueRange = 0f..totalDuration,
                        modifier = Modifier.fillMaxWidth(0.8f)
                )

                // Time Text (Current Time / Total Duration)
                Text(
                        text = "${formatTime(currentPosition.toInt())} / ${formatTime(totalDuration.toInt())}",
                        style = MaterialTheme.typography.bodySmall
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Play/Pause Button
                Button(
                        onClick = {
                                if (isPlaying) {
                                        mediaPlayer?.pause()
                                } else {
                                        mediaPlayer?.start()
                                }
                                isPlaying = !isPlaying
                        }
                ) {
                        Text(if (isPlaying) "Pause" else "Play")
                }
        }
}

// Helper function to format time (mm:ss)
fun formatTime(milliseconds: Int): String {
        val minutes = (milliseconds / 1000) / 60
        val seconds = (milliseconds / 1000) % 60
        return String.format("%02d:%02d", minutes, seconds)
}
