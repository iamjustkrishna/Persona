package com.krishnajeena.persona.ui_layer

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlarmManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material.icons.rounded.SkipPrevious
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.ExperimentalComposeApi
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import coil.compose.AsyncImage
import com.google.common.util.concurrent.MoreExecutors
import com.krishnajeena.persona.R
import com.krishnajeena.persona.data_layer.BlogItem
import com.krishnajeena.persona.data_layer.RadioLibrary
import com.krishnajeena.persona.model.BlogUrlViewModel
import com.krishnajeena.persona.model.CameraClickViewModel
import com.krishnajeena.persona.model.CameraPhotoViewModel
import com.krishnajeena.persona.model.CategoryBlogViewModel
import com.krishnajeena.persona.model.QuoteViewModel
import com.krishnajeena.persona.model.RadioViewModel
import com.krishnajeena.persona.model.SharedViewModel
import com.krishnajeena.persona.other.BottomNavItem
import com.krishnajeena.persona.screens.DailyCameraScreen
import com.krishnajeena.persona.screens.DismissBackground
import com.krishnajeena.persona.screens.ExploreScreen
import com.krishnajeena.persona.screens.MusicScreen
import com.krishnajeena.persona.screens.ReelScreen
import com.krishnajeena.persona.screens.StudyScreen
import com.krishnajeena.persona.screens.ToolsScreen
import com.krishnajeena.persona.screens.VaultWebView
import com.krishnajeena.persona.screens.WebViewItem
import com.krishnajeena.persona.services.RadioService
import com.krishnajeena.persona.ui.theme.PersonaTheme
import dev.shreyaspatil.capturable.capturable
import dev.shreyaspatil.capturable.controller.rememberCaptureController
import kotlinx.coroutines.launch
import soup.compose.photo.ExperimentalPhotoApi
import soup.compose.photo.PhotoBox
import java.io.File
import java.util.Date


@RequiresApi(Build.VERSION_CODES.R)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalPhotoApi::class,
    ExperimentalComposeUiApi::class, ExperimentalComposeApi::class
)
@Composable
fun PersonaApp(viewModel: CameraPhotoViewModel = viewModel(),
               quoteViewModel: QuoteViewModel = hiltViewModel(),
) {

    var isDark by rememberSaveable { mutableStateOf(false) }

    PersonaTheme(darkTheme = isDark) {
        val navController = rememberNavController()
        var title by remember { mutableStateOf("Persona") }
        val context = LocalContext.current

        val cameraClickViewModel: CameraClickViewModel = hiltViewModel<CameraClickViewModel>()

        BackHandler(enabled = true) {
            if (navController.currentDestination?.route != BottomNavItem.Explore.route) {
                navController.popBackStack()
            } else {
                (context as? Activity)?.finish()
            }
        }

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        if (!alarmManager.canScheduleExactAlarms()) {
            // Prompt the user to allow exact alarms in settings
            val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                .setData(Uri.parse("package:" + context.packageName))
            context.startActivity(intent)
        }
    }

        LaunchedEffect(Unit) {
            quoteViewModel.loadQuote()
        }

        val pullOffset = remember { androidx.compose.animation.core.Animatable(0f) }
        val scope = rememberCoroutineScope()

        // Reset animation when navigation happens
        LaunchedEffect(navController.currentBackStackEntry) {
            scope.launch {
                pullOffset.animateTo(0f, animationSpec = spring(stiffness = Spring.StiffnessLow))
            }
        }

// Apply this modifier to the Box or Column containing the pull effect

        var showPopup by remember { mutableStateOf(false) }
        var showQuoteDialog by remember { mutableStateOf(false) }
        val captureController = rememberCaptureController()
        if (showQuoteDialog) {
         Dialog(onDismissRequest = { showQuoteDialog = false }) {
                AnimatedVisibility(
                    visible = true,
                    enter = scaleIn(tween(500)) + fadeIn(),
                    exit = scaleOut(tween(500)) + fadeOut()
                ) {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color.White,
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .padding(16.dp).capturable(captureController)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("🌞 Quote of the Day", style = MaterialTheme.typography.titleMedium)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(quoteViewModel.quoteText, style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center)
                            Spacer(modifier = Modifier.height(8.dp))

                            Row(modifier = Modifier,
                                verticalAlignment = Alignment.CenterVertically){
                                Text("- Persona", style = MaterialTheme.typography.labelSmall)
                                IconButton(onClick = {
                                    scope.launch {
                                        val bitmapAsync = captureController.captureAsync()
                                        try {
                                            val bitmap = bitmapAsync.await()
                                            val uri = saveBitmapToCacheAndGetUri(context, bitmap.asAndroidBitmap())
                                            shareImageUri(context, uri)
                                        } catch (e: Exception) {
                                            Toast.makeText(context, "Something went wrong!", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }){
                                    Icon(imageVector = Icons.Filled.Share , contentDescription = "ShareQuote")
                                }
                            }
                        }
                    }
                }
            }
        }

        var topBarWidth by remember { mutableStateOf(0) }

        Scaffold(


            topBar = {
                TopAppBar(
                    modifier = Modifier.onGloballyPositioned { layoutCoordinates ->
                        topBarWidth = layoutCoordinates.size.width
                    },
                    title = {

                        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
                            ) {
                            Text(title,
                                modifier = Modifier, maxLines = 1,)
                            Box(modifier = Modifier.width(topBarWidth.dp),
                                contentAlignment = Alignment.Center){
                            IconButton(onClick = { showQuoteDialog = true }) {
                                Icon(
                                    painter = painterResource(id = R.drawable.flash),
                                    contentDescription = "Quote of the Day",
                                    tint = Color(0xFFFF9800), // warm light color
                                    modifier = Modifier.size(28.dp),
                                )
                            }
                            }
                        }
                            },
                    actions = {

                        IconButton(onClick = { isDark = !isDark }) {
                            Icon(
                                imageVector = if (isDark) Icons.Default.WbSunny else Icons.Default.DarkMode,
                                contentDescription = "Toggle Theme"
                            )
                        }
                        IconButton(onClick = {

                            showPopup = true
                        }) {
                            Icon(
                                imageVector = Icons.Default.AccountCircle,
                                contentDescription = "Profile"
                            )
                        }

                        // Popup
                        DropdownMenu(
                            expanded = showPopup,
                            onDismissRequest = { showPopup = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("🚧 Under Construction") },
                                onClick = { showPopup = false }
                            )
                        }
                    }
                )
            },
            bottomBar = { BottomBar(navController, cameraClickViewModel) },
        ) { innerPadding ->

            val activity = context as? Activity
            val sharedText = activity?.intent?.getStringExtra(Intent.EXTRA_TEXT)


            val clicksUri by viewModel.images.collectAsState()
//
    // Fetch images when the composable is first displayed
    LaunchedEffect(Unit) {
        viewModel.fetchImages(context)
    }

            val categoryBlogViewModel: CategoryBlogViewModel = viewModel()

            NavHost(navController, startDestination = if(sharedText!=null)
                BottomNavItem.Tools.route else BottomNavItem.Explore.route, modifier = Modifier
                .padding(
                    top = innerPadding.calculateTopPadding(),
                    start = innerPadding.calculateStartPadding(LayoutDirection.Ltr),
                    end = innerPadding.calculateEndPadding(LayoutDirection.Ltr)
                )
                .fillMaxSize()
            ) {

                composable(BottomNavItem.Explore.route) {
                    ExploreScreen(
                        onCategoryClick = { blogList, name ->
                            // This handles navigation to your existing "BlogsOfCategory" screen
                            categoryBlogViewModel.setBlogs(blogList)
                            categoryBlogViewModel.setName(name)
                            navController.navigate(BottomNavItem.BlogsOfCategory.route)
                        },
                        navController = navController
                    )
                }

                composable(BottomNavItem.ReelStack.route) { ReelScreen() }
                composable(BottomNavItem.Clicks.route,
                    deepLinks = listOf(navDeepLink { uriPattern = "app://com.krishnajeena.persona/clicks" })) { DailyCameraScreen(navController, cameraClickViewModel) }
                composable(BottomNavItem.Study.route) { StudyScreen(navController) }
                composable(BottomNavItem.Tools.route) { ToolsScreen() }
                composable(BottomNavItem.Music.route){
                    val sharedViewModel: SharedViewModel = hiltViewModel()
                    MusicScreen(sharedViewModel = sharedViewModel)
                }
                composable(
                    route = BottomNavItem.BlogsOfCategory.route
                ) {
                    CategoryClickedScreen(categoryBlogViewModel.selectedBlogs.value, categoryBlogViewModel.blogCategoryName.value, navController)
                }

                composable(
                    route = "webview/{url}",
                    arguments = listOf(navArgument("url") { type = NavType.StringType })
                ) { backStackEntry ->
                    val encodedUrl = backStackEntry.arguments?.getString("url") ?: ""
                    val url = Uri.decode(encodedUrl) // Decode in case it's encoded
                    VaultWebView(url, navController)
                }

                composable("personaImagesList") {
                    viewModel.fetchImages(context)
                    if(clicksUri.isEmpty()){
                        AsyncImage(model = R.drawable.undraw_empty_4zx0, contentDescription = null,
                            modifier = Modifier.fillMaxSize())
                    }else {
                        Box(modifier = Modifier.fillMaxSize()) {
                            LazyVerticalGrid(
                                modifier = Modifier.padding(2.dp),
                                columns = GridCells.Fixed(2),
                                contentPadding = PaddingValues(10.dp),
                                verticalArrangement = Arrangement.Center,
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                items(
                                    items = clicksUri,
                                    key = { it.toString() } // Use a unique and stable key for each item
                                ) { img ->
                                    var showDelDialog by remember {mutableStateOf(false)}

                                    val dismissState = rememberSwipeToDismissBoxState(
                                        confirmValueChange = {
                                            when (it) {
                                                SwipeToDismissBoxValue.StartToEnd -> {
                                                    showDelDialog = true
                                                }

                                                else -> Unit
                                            }
                                            true
                                        },
                                        positionalThreshold = { it * 0.25f }
                                    )

                                    ConfirmDeleteDialog(
                                        showDialog = showDelDialog,
                                        onConfirm = {
                                            showDelDialog = false
                                            viewModel.removeImage(context, img)
                                        },
                                        onDismiss = {
                                            showDelDialog = false
                                        }
                                    )
                                    SwipeToDismissBox(
                                        state = dismissState,
                                        modifier = Modifier,
                                        enableDismissFromStartToEnd = true,
                                        enableDismissFromEndToStart = false,
                                        backgroundContent = { DismissBackground(dismissState) },
                                    ) {
                                        Card(
                                            modifier = Modifier.padding(2.dp),
                                            onClick = {
                                                navController.navigate(
                                                    "openPersonaImage/${Uri.encode(img.toString())}"
                                                )
                                            },
                                            elevation = CardDefaults.elevatedCardElevation(10.dp),
                                            shape = RoundedCornerShape(10.dp)
                                        ) {
                                            AsyncImage(
                                                model = img,
                                                contentDescription = null,
                                                contentScale = ContentScale.FillWidth,
                                                modifier = Modifier.fillMaxSize()
                                            )
                                        }
                                    }
                                }
                            }

                        }
                    }
        }

                composable(
                    "openPersonaImage/{imageUri}",
                    arguments = listOf(navArgument("imageUri") { type = NavType.StringType })
                ) { backStackEntry ->
                    val context = LocalContext.current
                    val imageUriString = backStackEntry.arguments?.getString("imageUri")
                    val imageUri = imageUriString?.let { Uri.parse(it) }

                    rememberCoroutineScope()
                    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

                    var showSheet by remember { mutableStateOf(false) }

                    if (imageUri != null) {
                        if (showSheet) {
                            ModalBottomSheet(
                                onDismissRequest = { showSheet = false },
                                sheetState = bottomSheetState
                            ) {
                                // Fetch file info
                                val file = File(imageUri.path ?: "")
                                val name = file.name
                                val size = file.length() / 1024 // size in KB
                                val location = file.absolutePath
                                val lastModified = Date(file.lastModified()).toString()

                                Column(Modifier.padding(16.dp)) {
                                    Text("File Name: $name")
                                    Text("Size: ${size}KB")
                                    Text("Location: $location")
                                    Text("Last Updated: $lastModified")
                                }
                            }
                        }

                        Box(modifier = Modifier.fillMaxSize()) {
                            PhotoBox {
                            AsyncImage(
                                model = imageUri,
                                contentDescription = null,
                                contentScale = ContentScale.Fit,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(bottom = 100.dp)
                            )
                            }

                            Row(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(16.dp)
                            ) {
                                IconButton(onClick = {
                                    val intent = Intent(Intent.ACTION_SEND).apply {
                                        type = "image/*"
                                        putExtra(Intent.EXTRA_STREAM, imageUri)
                                        putExtra(Intent.EXTRA_TEXT, "Shared from Persona 📱\nCheck this out!")
                                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                    }
                                    context.startActivity(Intent.createChooser(intent, "Share image via"))
                                }) {
                                    Icon(Icons.Default.Share, contentDescription = "Share")
                                }

                                IconButton(onClick = { showSheet = true }) {
                                    Icon(Icons.Default.Info, contentDescription = "Details")
                                }
                            }
                        }
                    }
                }


            }

        }
    }
}

fun shareImageUri(context: Context, uri: Uri) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "image/png"
        putExtra(Intent.EXTRA_STREAM, uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    context.startActivity(Intent.createChooser(intent, "Share Quote!"))
}

fun saveBitmapToCacheAndGetUri(context: Context, bitmap: Bitmap): Uri {
    val file = File(context.cacheDir, "shared_image.png")
    file.outputStream().use {
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
    }

    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.provider",
        file
    )
}

@Composable
fun ConfirmDeleteDialog(
    showDialog: Boolean,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = {
                Text(text = "Delete Item?")
            },
            text = {
                Text("Are you sure you want to delete this image? This action cannot be undone.")
            },
            confirmButton = {
                TextButton(onClick = onConfirm) {
                    Text("Delete", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
            }
        )
    }
}


@Composable
fun CategoryClickedScreen(
    value: List<BlogItem>,
    blogCatName: String,
    navController: NavController
) {

Column(modifier = Modifier
    .fillMaxSize()
    .padding(16.dp)){
    Text(
        text = blogCatName,
        style = MaterialTheme.typography.headlineSmall,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 8.dp)
    )
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(4.dp)
    ) {
        items(value) { blog ->
            Card(
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .clickable {
                        navController.navigate("webview/${Uri.encode(blog.url)}")
                    }
            ) {
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp), horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically){
                Column(
                    modifier = Modifier
                        .padding(6.dp)
                        .fillMaxWidth(.75f)
                ) {
                    Text(
                        text = blog.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = blog.url,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
                    val context = LocalContext.current

                    val blogUrlViewModel = hiltViewModel<BlogUrlViewModel>()
                    IconButton(modifier = Modifier
                        .padding(2.dp)
                        .size(20.dp),
                        onClick = {
                        blogUrlViewModel.addUrl(blog.title, blog.url)
                        if(blogUrlViewModel.isAlreadyAdded(blog.url)){
                            Toast.makeText(context, "Added to My Blogs", Toast.LENGTH_SHORT).show()
                        }
                    }, enabled = !blogUrlViewModel.isAlreadyAdded(blog.url),
                        ){
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Add Blog",
                            modifier = Modifier.size(20.dp),
                            tint = if(blogUrlViewModel.isAlreadyAdded(blog.url)) Color.Gray else Color.Black)
                    }
            }
            }
        }
    }

}
}



@SuppressLint("SuspiciousIndentation")
@Composable
fun BottomBar(navController: NavController, cameraClickViewModel: CameraClickViewModel) {
    val items = listOf(
        BottomNavItem.Explore,
        BottomNavItem.ReelStack,
        BottomNavItem.Clicks,
        BottomNavItem.Study,
        BottomNavItem.Tools
    )

    val currentBackStack by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStack?.destination?.route

    var isBottomBarVisible by remember { mutableStateOf(true) }

    val context = LocalContext.current
    val isGesture = remember{
        isGestureNavigationEnabled(context)
    }
    File(
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
        "Persona/Clicks"
    ).apply { if (!exists()) mkdirs() }




    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            ,
        contentAlignment = Alignment.BottomCenter
    ) {

    // BottomAppBar
        AnimatedVisibility(
            visible = isBottomBarVisible,
            enter = slideInVertically(initialOffsetY = { it }),
            exit = slideOutVertically(targetOffsetY = { it }),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
        ) {
            BottomAppBar(
                tonalElevation = 0.dp,
                modifier = Modifier,
                contentPadding = PaddingValues(horizontal = 6.dp)
            ) {
                items.forEachIndexed { index, item ->
                    if (index == 2) {
                        Spacer(Modifier.weight(1f))
                    } else {
                        NavigationBarItem(
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label) },
                            selected = currentRoute == item.route,
                            onClick = { navController.navigate(item.route) },
                            alwaysShowLabel = true
                        )
                    }
                }
            }
        }

        // Center FAB (Camera)
        AnimatedVisibility(
            visible = isBottomBarVisible,
            enter = slideInVertically(initialOffsetY = { it }),
            exit = slideOutVertically(targetOffsetY = { it }),
            modifier = Modifier
                .align(Alignment.BottomCenter)

        ) {
            FloatingActionButton(
                onClick = {
                    if (currentRoute == BottomNavItem.Clicks.route) {
                        cameraClickViewModel.triggerCapture()
                    } else {
                        navController.navigate(BottomNavItem.Clicks.route) {
                            launchSingleTop = true
                        }
                    }
                },
                containerColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .offset(y = if (isGesture) (-42).dp else (-72).dp)
                    .size(64.dp),
                shape = CircleShape,
                elevation = FloatingActionButtonDefaults.elevation(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = "Clicks",
                    modifier = Modifier.size(32.dp),
                    tint = Color.White
                )
            }
        }

        val animatedBottomPadding1 by animateDpAsState(
            targetValue = if (isBottomBarVisible) {if(isGesture) 110.dp else 140.dp} else {if(isGesture) 30.dp else 50.dp},
            label = "fab_bottom_padding"
        )

            // Bottom Left FAB (Music)
        RadioMiniPlayer(bottomPadding = animatedBottomPadding1)



        val animatedBottomPadding2 by animateDpAsState(
            targetValue = if (isBottomBarVisible) {if(isGesture) 80.dp else 110.dp} else {if(isGesture) 30.dp else 50.dp},
            label = "fab_bottom_padding"
        )

            // Bottom Right FAB (Toggle Bottom Bar)
            FloatingActionButton(
                onClick = { isBottomBarVisible = !isBottomBarVisible },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 20.dp, bottom = animatedBottomPadding2)
                    .size(34.dp),
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = CircleShape,
                elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 6.dp)

            ) {
                Icon(
                    imageVector = if (isBottomBarVisible)
                        Icons.Default.KeyboardArrowDown
                    else
                        Icons.Default.KeyboardArrowUp,
                    contentDescription = "Toggle Bottom Bar",
                    tint = Color.White
                )
            }


    }
}

fun checkInternetConnection(context: Context): Boolean {
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val network = connectivityManager.activeNetwork ?: return false
    val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
    return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
}

@Composable
fun RadioMiniPlayer(
    bottomPadding: Dp,
    modifier: Modifier = Modifier,
    viewModel: RadioViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    var isExpanded by remember { mutableStateOf(false) }

    // Connect to the background service
    val controllerFuture = remember {
        val sessionToken = SessionToken(context, ComponentName(context, RadioService::class.java))
        MediaController.Builder(context, sessionToken).buildAsync()
    }

    var controller by remember { mutableStateOf<MediaController?>(null) }
    var isPlaying by remember { mutableStateOf(false) }

    val stations by viewModel.stations.collectAsState()

    var currentStationIndex by remember { mutableIntStateOf(0) }
    val currentStation = stations.getOrNull(currentStationIndex) ?: RadioLibrary.stations[0]


    LaunchedEffect(Unit){
        viewModel.refreshRadioStations()
    }
    DisposableEffect(Unit) {
        controllerFuture.addListener({
            controller = controllerFuture.get()
            // Sync UI state with existing playback if service was already running
            isPlaying = controller?.isPlaying ?: false
        }, MoreExecutors.directExecutor())

        onDispose {
            MediaController.releaseFuture(controllerFuture)
        }
    }

    // Handle station changes and Play/Pause through the controller
    LaunchedEffect(currentStationIndex, isPlaying) {
        controller?.let { player ->
            if (isPlaying) {
                player.setMediaItem(MediaItem.fromUri(currentStation.streamUrl))
                player.prepare()
                player.play()
            } else {
                player.pause()
            }
        }
    }

    Box(modifier = modifier.fillMaxSize().padding(start = 20.dp, bottom = bottomPadding)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.align(Alignment.BottomStart).animateContentSize()
        ) {
            FloatingActionButton(
                onClick = {
                    if (checkInternetConnection(context)) isExpanded = !isExpanded
                    else Toast.makeText(context, "No Internet", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.size(48.dp),
                shape = CircleShape,
                containerColor = if (isExpanded) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primaryContainer,
                elevation = FloatingActionButtonDefaults.elevation(0.dp)
            ) {
                Icon(painterResource(id = R.drawable.vinyl_disc), contentDescription = "Radio")
            }

            AnimatedVisibility(visible = isExpanded) {
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.9f),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier.padding(start = 8.dp).height(48.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 12.dp)
                    ) {
                        MusicWaveAnimation(isPlaying = isPlaying)
                        Spacer(Modifier.width(12.dp))

                        Column(modifier = Modifier.widthIn(max = 100.dp)) {
                            Text(currentStation.name, style = MaterialTheme.typography.labelLarge, maxLines = 1)
                            Text(currentStation.genre, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                        }

                        IconButton(onClick = {
                            currentStationIndex = if (currentStationIndex > 0) currentStationIndex - 1 else RadioLibrary.stations.size - 1
                        }) {
                            Icon(Icons.Rounded.SkipPrevious, null, modifier = Modifier.size(20.dp))
                        }

                        IconButton(onClick = { isPlaying = !isPlaying }) {
                            Icon(if (isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow, null)
                        }

                        IconButton(onClick = {
                            currentStationIndex = (currentStationIndex + 1) % RadioLibrary.stations.size
                        }) {
                            Icon(Icons.Rounded.SkipNext, null, modifier = Modifier.size(20.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MusicWaveAnimation(isPlaying: Boolean) {
    val infiniteTransition = rememberInfiniteTransition(label = "wave")

    // Function to create individual bar animations
    @Composable
    fun animateBar(duration: Int): Float {
        return if (isPlaying) {
            infiniteTransition.animateFloat(
                initialValue = 0.2f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(duration, easing = LinearOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "bar"
            ).value
        } else 0.2f
    }

    Row(
        modifier = Modifier.width(24.dp).height(20.dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        // Four bars with different animation speeds
        listOf(400, 600, 500, 700).forEach { duration ->
            val scale = animateBar(duration)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(scale)
                    .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(2.dp))
            )
        }
    }
}

fun isGestureNavigationEnabled(context: Context): Boolean{

    return try{
        val mode = Settings.Secure.getInt(
            context.contentResolver,
            "navigation_mode"
        )
        mode == 2
    } catch(e : Settings.SettingNotFoundException){
        false
    }

}