package com.palimson.mildy.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "projects")
data class ProjectEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val toolType: String, // "subtitles", "photofix", "videotools", "thumbnail"
    val previewPath: String? = null,
    val mediaUri: String? = null,
    val metadataJson: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
