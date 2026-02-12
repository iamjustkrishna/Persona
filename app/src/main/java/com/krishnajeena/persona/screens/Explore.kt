package com.krishnajeena.persona.screens

import android.net.Uri
import android.util.Log
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Article
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.rounded.Article
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.FormatQuote
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.krishnajeena.persona.R
import com.krishnajeena.persona.data_layer.ArticleEntity
import com.krishnajeena.persona.data_layer.BlogCategory
import com.krishnajeena.persona.data_layer.BlogItem
import com.krishnajeena.persona.model.ExploreViewModel

@Composable
fun FounderInsightCard(
    article: ArticleEntity,
    onSummarizeClick: () -> Unit,
    onClick: () -> Unit
) {
    Box(modifier = Modifier.padding(top=12.dp, end= 12.dp)) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() }
                .border(1.dp, MaterialTheme.colorScheme.tertiary.copy(0.5f), RoundedCornerShape(24.dp)),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Spacer(modifier = Modifier.height(8.dp)) // Space for the floating avatar

                Text(
                    text = article.title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                    maxLines = 3
                )

                article.description?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 8.dp),
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "By ${article.founderName}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    IconButton(onClick = onSummarizeClick) {
                        Icon(Icons.Rounded.AutoAwesome, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }

        // FLOATING AVATAR: Positioned at the top left of the card
        AsyncImage(
            model = "https://unavatar.io/twitter/${article.founderName?.replace(" ", "")}",
            contentDescription = null,
            modifier = Modifier
                .size(44.dp)
                .align(Alignment.TopEnd)
                .offset(x = 8.dp, y = (-12).dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surface)
                .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape),
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
fun ExploreHeaderSection(
    categories: List<BlogCategory>,
    onCategoryItemClick: (BlogCategory) -> Unit,
    tags: List<String>,
    selectedTag: String,
    onTagClick: (String) -> Unit
) {
    Column {
        Text("Discover", style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.Bold)

        // 1. TOP CATEGORIES (Image items that navigate to a new screen)
//        LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
//            items(categories) { category ->
//                BlogsCategoryItem(
//                    title = category.name,
//                    image = category.image,
//                    onClick = { onCategoryItemClick(category) }
//                )
//            }
//        }

        Spacer(Modifier.height(24.dp))

        // 2. TRENDING CHIPS (Filter the current Staggered Grid)
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(tags) { tag ->
                FilterChip(
                    selected = (tag == selectedTag),
                    onClick = { onTagClick(tag) },
                    label = { Text(tag) }
                )
            }
        }

        HorizontalDivider(Modifier.padding(vertical = 12.dp))
    }
}

@Composable
fun ExploreScreen(
    navController: NavHostController,
    onCategoryClick: (List<BlogItem>, String) -> Unit, // Re-inserted this!
    viewModel: ExploreViewModel = hiltViewModel()
) {
    // 1. OBSERVE UNIFIED STATE
    val isConnected by viewModel.isConnected.collectAsState()
    val isLoading = viewModel.isLoading
    val selectedTag = viewModel.selectedCategory
    val feed = viewModel.combinedFeed
    val summaryState = viewModel.summaryState

    var showSummarySheet by remember { mutableStateOf(false) }

    Scaffold(containerColor = MaterialTheme.colorScheme.background) { innerPadding ->
        if (!isConnected) {
            OfflineView()
        } else {
            // 2. AI SUMMARY SHEET
            if (showSummarySheet) {
                ArticleSummarySheet(
                    summary = summaryState.content,
                    isLoading = summaryState.isLoading,
                    error = summaryState.error,
                    onDismiss = {
                        showSummarySheet = false
                        viewModel.clearSummary()
                    }
                )
            }

            LazyVerticalStaggeredGrid(
                columns = StaggeredGridCells.Fixed(2),
                modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                contentPadding = PaddingValues(bottom = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalItemSpacing = 12.dp
            ) {
                // 3. HEADER SECTION
                item(span = StaggeredGridItemSpan.FullLine) {
                    ExploreHeaderSection(
                        categories = viewModel.categories,
                        tags = viewModel.articlesCategories,
                        onCategoryItemClick = { category ->
                            onCategoryClick(category.blogs, category.name)
                        },
                        selectedTag = selectedTag,
                        onTagClick = { viewModel.onCategorySelected(it) }
                    )
                }

                // 4. FEED CONTENT (Interleaved automatically by ViewModel)
                if (isLoading && feed.isEmpty()) {
                    items(6) { ShimmerCard() }
                } else {
                    items(feed, key = { it.url }) { article ->
                        if (article.source == "founder") {
                            FounderInsightCard(
                                article = article,
                                onSummarizeClick = {
                                    showSummarySheet = true
                                    viewModel.summarizeArticle(article.url)
                                },
                                onClick = { navController.navigate("webview/${Uri.encode(article.url)}") }
                            )
                        } else {
                            ExploreArticleCard(
                                article = article,
                                onSummarizeClick = {
                                    showSummarySheet = true
                                    viewModel.summarizeArticle(article.url)
                                },
                                onClick = { navController.navigate("webview/${Uri.encode(article.url)}") }
                            )
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
    error: String?,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(24.dp).padding(bottom = 24.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                androidx.compose.material3.Icon(Icons.Rounded.AutoAwesome, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.width(12.dp))
                Text("Persona Intelligence", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold)
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

            when {
                isLoading -> {
                    ShimmerEffect(modifier = Modifier.fillMaxWidth().height(150.dp).clip(RoundedCornerShape(16.dp)))
                }
                error != null -> {
                    Text(error, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
                }
                else -> {
                    Text(
                        text = summary ?: "No analysis available.",
                        style = MaterialTheme.typography.bodyLarge,
                        lineHeight = 24.sp
                    )
                    Button(
                        onClick = { /* TODO: Save to Vault */ },
                        modifier = Modifier.padding(top = 24.dp).fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text("Add to Study Vault")
                    }
                }
            }
        }
    }
}

@Composable
fun ArticleImage(imageUrl: String?) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(
                // FALLBACK: A subtle gradient if the image is missing
                brush = Brush.linearGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primaryContainer,
                        MaterialTheme.colorScheme.surfaceVariant
                    )
                )
            )
    ) {
        if (!imageUrl.isNullOrEmpty()) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(imageUrl)
                    .crossfade(true) // Smooth transition
                    .build(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
                // If the URL is broken (404), show a specific icon
                error = painterResource(R.drawable.ic_launcher_foreground),
                // While downloading
                placeholder = painterResource(R.drawable.log__1_)
            )
        } else {
            // If the URL is literally NULL, show a "Branded" icon in the center
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.Article,
                contentDescription = null,
                modifier = Modifier
                    .size(40.dp)
                    .align(Alignment.Center),
                tint = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.5f)
            )
        }
    }
}

@Composable
fun ExploreArticleCard(
    article: ArticleEntity,
    onSummarizeClick: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            Box(modifier = Modifier.fillMaxWidth()) {

                ArticleImage(article.coverImage)

                Surface(
                    modifier = Modifier.align(Alignment.TopEnd).padding(8.dp),
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)
                ) {
                    IconButton(onClick = onSummarizeClick, modifier = Modifier.size(36.dp)) {
                        androidx.compose.material3.Icon(Icons.Rounded.AutoAwesome, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                    }
                }
            }
            Text(
                text = article.title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(12.dp),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
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
