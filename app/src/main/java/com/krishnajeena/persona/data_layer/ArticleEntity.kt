package com.krishnajeena.persona.data_layer

import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

data class ArticleEntity(
    @PrimaryKey val url: String,
    val title: String,
    val description: String?,
    val coverImage: String?,
    val category: String,
    val source: String,
    @SerializedName("founder_name")
    val founderName: String? = null,
    val isSaved: Boolean = false,
    val interactionCount: Int = 0,
    val timestamp: Long = System.currentTimeMillis()
)
