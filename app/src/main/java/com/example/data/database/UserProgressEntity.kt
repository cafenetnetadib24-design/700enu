package com.example.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_progress")
data class UserProgressEntity(
    @PrimaryKey val sentenceId: Int,
    val isCompleted: Boolean = false,
    val isMastered: Boolean = false,
    val correctCount: Int = 0,
    val incorrectCount: Int = 0,
    val lastAttemptTime: Long = System.currentTimeMillis()
)
