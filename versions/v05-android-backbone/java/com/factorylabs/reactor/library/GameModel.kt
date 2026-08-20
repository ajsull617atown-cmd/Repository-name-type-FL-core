package com.factorylabs.reactor.library
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "assets")
data class GameModel(
    @PrimaryKey val id: String,
    val title: String,
    val packageId: String,
    val isInstalled: Boolean,
    val isLaunchable: Boolean = true,
    val isFavorite: Boolean = false,
    val provider: String,
    val glow: String,
    val status: String,
    val fps: Int,
    val core: String
)