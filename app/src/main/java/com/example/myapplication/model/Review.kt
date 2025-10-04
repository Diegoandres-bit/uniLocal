package com.example.myapplication.model

import java.time.LocalDateTime

data class Review(
    val id: String,
    val userID: String,
    val placeID: String,
    val rating: Int,
    val comment: String,
    val date: LocalDateTime
)