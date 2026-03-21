package com.example.wavecast.core.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "podcasts")
data class PodcastEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val author: String,
    val description: String,
    val imageUrl: String,
    val feedUrl: String,
    val isSubscribed: Boolean = false
)
