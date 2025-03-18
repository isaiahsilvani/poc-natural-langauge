package com.example.poc_nail_shop_ai.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class GeminiResponse(
    val action: String,
    val id: Int? = null,
    val appointment: Appointment? = null,
    val errorMessage: String? = null
)

@Serializable
data class Appointment(
    val date: String,
    val time: String,
    val provider: String,
    val services: List<String>,
    val id: Int
)
