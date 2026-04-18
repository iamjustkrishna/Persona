package com.krishnajeena.persona.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.MusicOff
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material.icons.rounded.Timeline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.krishnajeena.persona.data_layer.LeaderboardEntry
import com.krishnajeena.persona.model.FocusViewModel
import com.krishnajeena.persona.model.TimerState
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FocusScreen(
    viewModel: FocusViewModel = hiltViewModel(),
    onFocusModeChange: (Boolean) -> Unit = {}
) {
    val timerState = viewModel.timerState
    val remainingSeconds = viewModel.remainingSeconds
    val selectedDuration = viewModel.selectedDuration
    val withMusic = viewModel.withMusic
    val progress = viewModel.getProgressPercentage()
    val formattedTime = viewModel.getFormattedTime()
    val motivationalQuote = viewModel.currentMotivationalQuote

    val todayMinutes by viewModel.todayMinutes.collectAsState()
    val totalMinutes by viewModel.totalMinutes.collectAsState()
    val weeklyStats by viewModel.weeklyStats.collectAsState()
    val focusComparison by viewModel.focusComparison.collectAsState()
    val leaderboard by viewModel.leaderboard.collectAsState()
    val currentStreak by viewModel.currentStreak.collectAsState()

    // Track focus mode state
    val isFocusMode = timerState == TimerState.RUNNING
    LaunchedEffect(isFocusMode) {
        onFocusModeChange(isFocusMode)
    }

    // Animated progress with spring
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "progress"
    )

    // Scroll state for scroll indicator
    val scrollState = rememberLazyListState()
    val context = androidx.compose.ui.platform.LocalContext.current
    val prefs = remember { context.getSharedPreferences("focus_prefs", android.content.Context.MODE_PRIVATE) }
    val hasSeenScrollIndicator = remember { prefs.getBoolean("has_seen_scroll_indicator", false) }

    val showScrollIndicator by remember {
        derivedStateOf {
            !hasSeenScrollIndicator &&
            timerState == TimerState.IDLE &&
            scrollState.firstVisibleItemIndex == 0 &&
            scrollState.firstVisibleItemScrollOffset < 100
        }
    }

    // Mark scroll indicator as seen when user scrolls
    LaunchedEffect(scrollState.firstVisibleItemIndex) {
        if (scrollState.firstVisibleItemIndex > 0 && !hasSeenScrollIndicator) {
            prefs.edit().putBoolean("has_seen_scroll_indicator", true).apply()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Focused Mode UI - Minimal, distraction-free
        AnimatedVisibility(
            visible = isFocusMode,
            enter = fadeIn(animationSpec = tween(500)) + scaleIn(initialScale = 0.9f),
            exit = fadeOut(animationSpec = tween(300)) + scaleOut(targetScale = 0.9f)
        ) {
            FocusedModeUI(
                formattedTime = formattedTime,
                progress = animatedProgress,
                motivationalQuote = motivationalQuote,
                onExit = { viewModel.pauseTimer() }
            )
        }

        // Regular UI - Full stats and controls
        AnimatedVisibility(
            visible = !isFocusMode,
            enter = fadeIn(animationSpec = tween(300)),
            exit = fadeOut(animationSpec = tween(200))
        ) {
            LazyColumn(
            state = scrollState,
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
        item { Spacer(modifier = Modifier.height(16.dp)) }

        // Header Stats Row with improved design
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                QuickStatCard(
                    icon = Icons.Rounded.Timeline,
                    value = todayMinutes.toString(),
                    label = "Today (min)",
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f)
                )
                QuickStatCard(
                    icon = Icons.Rounded.LocalFireDepartment,
                    value = currentStreak.toString(),
                    label = "Day Streak",
                    color = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.weight(1f)
                )
                QuickStatCard(
                    icon = Icons.Rounded.EmojiEvents,
                    value = (totalMinutes / 60).toString(),
                    label = "Total (hrs)",
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Circular Timer - Clean Design
        item {
            Spacer(modifier = Modifier.height(32.dp))

            Box(
                modifier = Modifier.size(280.dp),
                contentAlignment = Alignment.Center
            ) {
                // Background circle
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawArc(
                        color = Color.Gray.copy(alpha = 0.15f),
                        startAngle = -90f,
                        sweepAngle = 360f,
                        useCenter = false,
                        style = Stroke(width = 20.dp.toPx(), cap = StrokeCap.Round),
                        size = Size(size.width, size.height)
                    )
                }

                // Progress arc with gradient
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val gradient = Brush.sweepGradient(
                        colors = listOf(
                            Color(0xFF667EEA),
                            Color(0xFF764BA2),
                            Color(0xFFF093FB),
                            Color(0xFF667EEA)
                        )
                    )

                    drawArc(
                        brush = gradient,
                        startAngle = -90f,
                        sweepAngle = 360f * animatedProgress,
                        useCenter = false,
                        style = Stroke(width = 20.dp.toPx(), cap = StrokeCap.Round),
                        size = Size(size.width, size.height)
                    )
                }

                // Timer display
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = formattedTime,
                        style = MaterialTheme.typography.displayLarge,
                        fontWeight = FontWeight.Bold,
                        fontSize = 60.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = when (timerState) {
                            TimerState.IDLE -> "Ready to focus?"
                            TimerState.RUNNING -> "Stay focused! 🎯"
                            TimerState.PAUSED -> "Take a breath"
                            TimerState.COMPLETED -> "Well done! 🎉"
                        },
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // Timer duration selection (only when idle)
        if (timerState == TimerState.IDLE) {
            item {
                Spacer(modifier = Modifier.height(32.dp))
                Text(
                    text = "Choose Duration",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(listOf(15, 25, 45, 60)) { duration ->
                        DurationChip(
                            duration = duration,
                            isSelected = selectedDuration == duration,
                            onClick = { viewModel.setDuration(duration) }
                        )
                    }
                }
            }
        }

        // Control Buttons with improved layout
        item {
            Spacer(modifier = Modifier.height(40.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Music toggle with better design
                AnimatedVisibility(
                    visible = timerState == TimerState.IDLE || timerState == TimerState.RUNNING,
                    enter = scaleIn() + fadeIn(),
                    exit = scaleOut() + fadeOut()
                ) {
                    FilledTonalIconButton(
                        onClick = { viewModel.toggleMusic() },
                        modifier = Modifier.size(64.dp),
                        colors = IconButtonDefaults.filledTonalIconButtonColors(
                            containerColor = if (withMusic)
                                MaterialTheme.colorScheme.primaryContainer
                            else
                                MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Icon(
                            imageVector = if (withMusic) Icons.Default.MusicNote else Icons.Default.MusicOff,
                            contentDescription = "Toggle Music",
                            modifier = Modifier.size(28.dp),
                            tint = if (withMusic)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Main action button with better elevation
                FloatingActionButton(
                    onClick = {
                        when (timerState) {
                            TimerState.IDLE -> viewModel.startTimer()
                            TimerState.RUNNING -> viewModel.pauseTimer()
                            TimerState.PAUSED -> viewModel.startTimer()
                            TimerState.COMPLETED -> viewModel.acknowledgeCompletion()
                        }
                    },
                    modifier = Modifier.size(88.dp),
                    containerColor = MaterialTheme.colorScheme.primary,
                    elevation = FloatingActionButtonDefaults.elevation(
                        defaultElevation = 8.dp,
                        pressedElevation = 12.dp
                    )
                ) {
                    Icon(
                        imageVector = when (timerState) {
                            TimerState.IDLE, TimerState.PAUSED -> Icons.Default.PlayArrow
                            TimerState.RUNNING -> Icons.Default.Pause
                            TimerState.COMPLETED -> Icons.Default.Refresh
                        },
                        contentDescription = "Control",
                        modifier = Modifier.size(44.dp),
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }

                // Reset button with improved visibility
                AnimatedVisibility(
                    visible = timerState != TimerState.IDLE,
                    enter = scaleIn() + fadeIn(),
                    exit = scaleOut() + fadeOut()
                ) {
                    FilledTonalIconButton(
                        onClick = { viewModel.resetTimer() },
                        modifier = Modifier.size(64.dp),
                        colors = IconButtonDefaults.filledTonalIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Reset",
                            modifier = Modifier.size(28.dp),
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }

        // Comparison Stats
        focusComparison?.let { comparison ->
            item {
                Spacer(modifier = Modifier.height(32.dp))
                ComparisonCard(comparison)
            }
        }

        // Weekly Calendar
        item {
            Spacer(modifier = Modifier.height(40.dp))
            Text(
                text = "This Week",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(16.dp))
            WeeklyCalendarView(weeklyStats)
        }

        // Leaderboard
        if (leaderboard.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(40.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "🏆",
                            style = MaterialTheme.typography.headlineSmall
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Top Performers",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = "This Week",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Medium
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            itemsIndexed(leaderboard.take(7)) { index, entry ->
                LeaderboardItem(entry, index)
                if (index < 6) Spacer(modifier = Modifier.height(12.dp))
            }
        } else {
            // Empty state for leaderboard
            item {
                Spacer(modifier = Modifier.height(32.dp))
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(20.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outlineVariant
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "🚀",
                            style = MaterialTheme.typography.displayMedium
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Start your focus journey",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Complete your first session to join the leaderboard",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(120.dp))
        }
            }
        }

        // Scroll Indicator - positioned above camera button
        AnimatedVisibility(
            visible = showScrollIndicator,
            enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 }),
            exit = fadeOut() + slideOutVertically(targetOffsetY = { it / 2 }),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 180.dp)
        ) {
            Surface(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(20.dp),
                shadowElevation = 8.dp
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "Scroll down",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Scroll for more",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
fun QuickStatCard(
    icon: ImageVector,
    value: String,
    label: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.height(110.dp),
        color = color.copy(alpha = 0.08f),
        shape = RoundedCornerShape(20.dp),
        border = androidx.compose.foundation.BorderStroke(
            width = 1.5.dp,
            color = color.copy(alpha = 0.3f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun DurationChip(
    duration: Int,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected)
            MaterialTheme.colorScheme.primary
        else
            MaterialTheme.colorScheme.surfaceVariant,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "chipColor"
    )

    val contentColor by animateColorAsState(
        targetValue = if (isSelected)
            MaterialTheme.colorScheme.onPrimary
        else
            MaterialTheme.colorScheme.onSurfaceVariant,
        label = "chipContentColor"
    )

    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .height(56.dp),
        color = backgroundColor,
        shape = RoundedCornerShape(20.dp),
        shadowElevation = if (isSelected) 4.dp else 0.dp
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "${duration} min",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = contentColor
            )
        }
    }
}

@Composable
fun ComparisonCard(comparison: com.krishnajeena.persona.data_layer.FocusComparison) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
        shape = RoundedCornerShape(24.dp),
        shadowElevation = 0.dp,
        border = androidx.compose.foundation.BorderStroke(
            width = 1.5.dp,
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "🐠 ${comparison.attentionSpanComparison}",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "You're in the top ${comparison.percentileRank}% of users!",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun WeeklyCalendarView(weeklyStats: List<com.krishnajeena.persona.data_layer.DailyFocusStats>) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
        shape = RoundedCornerShape(20.dp),
        shadowElevation = 0.dp,
        border = androidx.compose.foundation.BorderStroke(
            width = 1.5.dp,
            color = MaterialTheme.colorScheme.outlineVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            weeklyStats.forEach { stat ->
                DayColumn(stat)
            }
        }
    }
}

@Composable
fun DayColumn(stat: com.krishnajeena.persona.data_layer.DailyFocusStats) {
    val (backgroundColor, textColor) = when {
        stat.totalMinutes == 0 -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f) to MaterialTheme.colorScheme.onSurfaceVariant
        stat.totalMinutes < 30 -> MaterialTheme.colorScheme.primary.copy(alpha = 0.3f) to MaterialTheme.colorScheme.primary
        stat.totalMinutes < 60 -> MaterialTheme.colorScheme.primary.copy(alpha = 0.6f) to MaterialTheme.colorScheme.onPrimary
        else -> MaterialTheme.colorScheme.primary to MaterialTheme.colorScheme.onPrimary
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(40.dp)
    ) {
        Text(
            text = SimpleDateFormat("EEE", Locale.getDefault()).format(
                SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(stat.date) ?: Date()
            ),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.SemiBold,
            fontSize = 11.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(backgroundColor, RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            if (stat.totalMinutes > 0) {
                Text(
                    text = stat.totalMinutes.toString(),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )
            }
        }
    }
}

@Composable
fun LeaderboardItem(entry: LeaderboardEntry, rank: Int) {
    val badgeEmoji = when (rank) {
        0 -> "🥇"
        1 -> "🥈"
        2 -> "🥉"
        else -> ""
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
        shape = RoundedCornerShape(16.dp),
        shadowElevation = 0.dp,
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Rank badge - simple circle
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (badgeEmoji.isNotEmpty()) {
                    Text(
                        text = badgeEmoji,
                        style = MaterialTheme.typography.titleLarge,
                        fontSize = 24.sp
                    )
                } else {
                    Text(
                        text = "#${rank + 1}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // User info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = entry.username,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${entry.totalMinutes} minutes",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Stats
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${entry.totalMinutes / 60}h ${entry.totalMinutes % 60}m",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Rank #${entry.rank}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun FocusedModeUI(
    formattedTime: String,
    progress: Float,
    motivationalQuote: String,
    onExit: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        // Exit button at top left
        FilledTonalIconButton(
            onClick = onExit,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(20.dp)
                .size(48.dp),
            colors = IconButtonDefaults.filledTonalIconButtonColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Icon(
                imageVector = Icons.Default.Pause,
                contentDescription = "Pause",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Large Timer
            Box(
                modifier = Modifier.size(320.dp),
                contentAlignment = Alignment.Center
            ) {
                // Background circle
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawArc(
                        color = Color.Gray.copy(alpha = 0.1f),
                        startAngle = -90f,
                        sweepAngle = 360f,
                        useCenter = false,
                        style = Stroke(width = 16.dp.toPx(), cap = StrokeCap.Round),
                        size = Size(size.width, size.height)
                    )
                }

                // Progress arc
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val gradient = Brush.sweepGradient(
                        colors = listOf(
                            Color(0xFF667EEA),
                            Color(0xFF764BA2),
                            Color(0xFFF093FB),
                            Color(0xFF667EEA)
                        )
                    )

                    drawArc(
                        brush = gradient,
                        startAngle = -90f,
                        sweepAngle = 360f * progress,
                        useCenter = false,
                        style = Stroke(width = 16.dp.toPx(), cap = StrokeCap.Round),
                        size = Size(size.width, size.height)
                    )
                }

                // Time display
                Text(
                    text = formattedTime,
                    style = MaterialTheme.typography.displayLarge,
                    fontWeight = FontWeight.Bold,
                    fontSize = 72.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(60.dp))

            // Motivational Quote with animation
            AnimatedContent(
                targetState = motivationalQuote,
                transitionSpec = {
                    fadeIn(animationSpec = tween(800)) togetherWith
                    fadeOut(animationSpec = tween(400))
                },
                label = "quoteAnimation"
            ) { quote ->
                if (quote.isNotEmpty()) {
                    Text(
                        text = quote,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 40.dp)
                    )
                }
            }
        }
    }
}
