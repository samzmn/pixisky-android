package com.pixisky.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pixi_table")
data class Pixi(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val qrUrl: String = "",
    val audioUrl: String = "",
)