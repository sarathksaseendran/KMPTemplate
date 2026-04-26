package com.template.shared.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class Item(
    val id: Int,
    val title: String,
    val description: String
)
