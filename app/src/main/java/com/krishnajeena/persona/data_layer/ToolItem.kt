package com.krishnajeena.persona.data_layer

import androidx.compose.ui.graphics.vector.ImageVector

data class ToolItem(
    val id: String,
    val title: String,
    val description: String,
    val icon: ImageVector,
    val route: String,
    val isPremium: Boolean = false,
    val comingSoon: Boolean = false
)
