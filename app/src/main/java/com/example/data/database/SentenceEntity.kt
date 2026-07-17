package com.example.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sentences")
data class SentenceEntity(
    @PrimaryKey val id: Int,
    val sentence: String,
    val translation: String,
    val image: String,
    val audio: String,
    val lessonId: Int
)
