package com.krishnajeena.persona.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.krishnajeena.persona.R


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EarlyAppBar(modifier: Modifier = Modifier, title: String, isDark: Boolean,
                ) {
    var isDark by rememberSaveable { mutableStateOf(false) }

    var topBarWidth by remember { mutableIntStateOf(0) }
    var showPopup by remember { mutableStateOf(false) }
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
                    IconButton(onClick = { var showQuoteDialog = true }) {
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

}