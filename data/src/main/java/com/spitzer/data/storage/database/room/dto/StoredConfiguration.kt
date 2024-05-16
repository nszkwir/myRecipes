package com.spitzer.data.storage.database.room.dto

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "configuration",
)
data class StoredConfiguration(
    @PrimaryKey
    val id: Long,
    val configuration: String
)
