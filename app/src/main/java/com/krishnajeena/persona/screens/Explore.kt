package com.krishnajeena.persona.screens

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.krishnajeena.persona.R
import com.krishnajeena.persona.data_layer.BlogItem
import com.krishnajeena.persona.data_layer.DevToArticle
import com.krishnajeena.persona.model.ArticlesViewModel
import com.krishnajeena.persona.model.ExploreViewModel
import com.krishnajeena.persona.model.QuoteViewModel
import java.util.Locale

@Composable
fun ExploreScreen(
    modifier: Modifier = Modifier,
    onCategoryClick: (List<BlogItem>, String) -> Unit,
    navController: NavHostController,
    quoteViewModel: QuoteViewModel = hiltViewModel()
) {
    val viewModel: ExploreViewModel = hiltViewModel()
    val articlesViewModel: ArticlesViewModel = viewModel()

    val isConnected by viewModel.isConnected.collectAsState()
    val categoriesArticles = viewModel.articlesCategories
    val articles = articlesViewModel.articles
    val isLoading = articlesViewModel.isLoading || viewModel.isLoading
    val selected = articlesViewModel.selectedCategory

    LaunchedEffect(viewModel.firstSelected) {
        articlesViewModel.fetchArticles(viewModel.firstSelected)
    }

    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        if (!isConnected) {
            OfflineView()
        } else {

            val summaryState = articlesViewModel.summaryState
            var showSummarySheet by remember { mutableStateOf(false) }

            if (showSummarySheet) {
                ArticleSummarySheet(
                    summary = summaryState.content,
                    isLoading = summaryState.isLoading,
                    onDismiss = { showSummarySheet = false }
                )
            }

            LazyVerticalStaggeredGrid(
                columns = StaggeredGridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp), // Side padding for the whole grid
                contentPadding = PaddingValues(
                    bottom = 24.dp
                ),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalItemSpacing = 10.dp // Increased for better breathing room
            ) {

                // 1. HEADER SECTION
                item(span = StaggeredGridItemSpan.FullLine) {
                    Column {
                        // 1. Header Section
                        Column{
                            Text(
                                "Discover",
                                style = MaterialTheme.typography.displaySmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                            )
                            Text(
                                "Curated for you.",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontWeight = FontWeight.Normal
                                )
                            )
                        }

                        // ADJUST THIS SPACER for the specific "initial" gap you want
                        Spacer(modifier = Modifier.height(8.dp))

                        // 2. Chip Filter Section
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            items(categoriesArticles) { category ->
                                val isSelected = category == selected
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { articlesViewModel.onCategoryClick(category) },
                                    label = {
                                        Text(
                                            text = category.replaceFirstChar { it.uppercase() },
                                            style = MaterialTheme.typography.labelLarge
                                        )
                                    },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                                    ),
                                    border = null
                                )
                            }
                        }

                        HorizontalDivider(
                            modifier = Modifier.padding(top = 8.dp, bottom = 12.dp),
                            thickness = 0.5.dp,
                            color = MaterialTheme.colorScheme.outlineVariant
                        )

                        Text(
                            "Trending Now",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = (-0.5).sp
                            )
                        )

                        // This extra spacer acts as the "gap" before the first cards start
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                }
                // 3. ARTICLES
                if (isLoading) {
                    items(6) { ShimmerCard() }
                } else {
                    items(articles) { article ->
                        ExploreArticleCard(article = article,
                            onSummarizeClick = {
                                selectedArticle ->
                                showSummarySheet = true
                                articlesViewModel.summarizeArticle(selectedArticle.url)
                            }) {
                            val encodedUrl = Uri.encode(article.url)
                            navController.navigate("webview/$encodedUrl")
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArticleSummarySheet(
    summary: String?,
    isLoading: Boolean,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        // Using a semi-transparent container to match your new cards
        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
        tonalElevation = 8.dp,
        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .padding(bottom = 40.dp)
        ) {
            // Header with the Persona "Sparkle"
            Row(verticalAlignment = Alignment.CenterVertically) {
                androidx.compose.material3.Icon(
                    imageVector = Icons.Rounded.AutoAwesome,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.width(12.dp))
                Text(
                    "Persona Intelligence",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 20.dp), thickness = 0.5.dp)

            if (isLoading) {
                // Use your existing ShimmerEffect here for consistency
                ShimmerEffect(modifier = Modifier.fillMaxWidth().height(120.dp).clip(RoundedCornerShape(12.dp)))
            } else {
                Text(
                    text = summary ?: "No summary available.",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        lineHeight = 26.sp,
                        letterSpacing = 0.2.sp
                    )
                )

                // ACTION BUTTON: This is the bridge to your next feature!
                Button(
                    onClick = { /* TODO: Save to Study Tab */ },
                    modifier = Modifier.padding(top = 24.dp).fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("Add to Study Vault")
                }
            }
        }
    }
}
@Composable
fun ExploreArticleCard(
    article: DevToArticle,
    onSummarizeClick: (DevToArticle) -> Unit,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(24.dp), // Increased for modern aesthetics
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
    ) {
        Column {
            // BOX allows us to layer the button on top of the image
            Box(modifier = Modifier.fillMaxWidth()) {
                article.cover_image?.let {
                    AsyncImage(
                        model = it,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .clip(RoundedCornerShape(24.dp)) // Uniform roundness
                    )
                }

                // FLOATING AI BUTTON (Overlaid on Image)
                Surface(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(horizontal = 1.dp, vertical= 1.dp),
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f), // Glassy effect
                    tonalElevation = 4.dp
                ) {
                    IconButton(
                        onClick = { onSummarizeClick(article) },
                        modifier = Modifier.size(40.dp)
                    ) {
                        androidx.compose.material3.Icon(
                            imageVector = Icons.Rounded.AutoAwesome,
                            contentDescription = "AI Summary",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // TEXT CONTENT
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)) {
                Text(
                    text = article.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        lineHeight = 22.sp
                    ),
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )

                // Optional: Tiny source tag to fill the space
//                Text(
//                    text = "Dev.to • 5 min read",
//                    style = MaterialTheme.typography.labelSmall,
//                    color = MaterialTheme.colorScheme.onSurfaceVariant,
//                    modifier = Modifier.padding(top = 8.dp)
//                )
            }
        }
    }
}
@Composable
fun ShimmerCard() {
    Card(
        modifier = Modifier.fillMaxWidth().height(200.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        ShimmerEffect(modifier = Modifier.fillMaxSize())
    }
}

@Composable
fun OfflineView() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            androidx.compose.material3.Icon(imageVector = Icons.Default.CloudOff, contentDescription = null, modifier = Modifier.size(64.dp))
            Text("You're offline", style = MaterialTheme.typography.titleLarge)
            Text("Check your connection to see new blogs.", style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
fun ShimmerEffect(
    modifier: Modifier,
    widthOfShadowBrush: Int = 500,
    angleOfAxisY: Float = 270f,
    durationMillis: Int = 1000,
) {


    val shimmerColors = listOf(
        Color.White.copy(alpha = 0.3f),
        Color.White.copy(alpha = 0.5f),
        Color.White.copy(alpha = 1.0f),
        Color.White.copy(alpha = 0.5f),
        Color.White.copy(alpha = 0.3f),
    )

    val transition = rememberInfiniteTransition(label = "")

    val translateAnimation = transition.animateFloat(
        initialValue = 0f,
        targetValue = (durationMillis + widthOfShadowBrush).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = durationMillis,
                easing = LinearEasing,
            ),
            repeatMode = RepeatMode.Restart,
        ),
        label = "Shimmer loading animation",
    )

    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset(x = translateAnimation.value - widthOfShadowBrush, y = 0.0f),
        end = Offset(x = translateAnimation.value, y = angleOfAxisY),
    )

    Box(
        modifier = modifier
    ) {
        Spacer(
            modifier = Modifier
                .matchParentSize()
                .background(brush)
        )
    }


}

@Composable
fun BlogsCategoryItem(
    modifier: Modifier = Modifier,
    title: String,
    image: String,
    onClick: () -> Unit
) {
    Column(modifier = modifier.clickable { onClick() }) {
        Card(modifier = Modifier.size(120.dp)) {
            if (image.isNotBlank()) {
                AsyncImage(model = image, contentDescription = title, contentScale = ContentScale.Crop)
            }
        }
        Text(title, modifier = Modifier.padding(2.dp), style = MaterialTheme.typography.bodyMedium)
    }
}
