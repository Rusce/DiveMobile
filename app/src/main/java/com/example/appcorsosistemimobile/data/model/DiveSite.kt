package com.example.appcorsosistemimobile.data.model

data class DiveSite(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val minDepth: Int? = null,
    val maxDepth: Int? = null,
    val authorName: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val imageUrls: List<String> = emptyList()
)
