package com.example.appcorsosistemimobile.data.model

data class DiveSiteComment (
    val id: String = "",
    val diveId: String = "",
    val authorName: String = "",
    val title: String = "",
    val description: String = "",
    val stars: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)
