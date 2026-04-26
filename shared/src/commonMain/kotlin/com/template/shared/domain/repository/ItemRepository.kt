package com.template.shared.domain.repository

import com.template.shared.domain.models.Item

interface ItemRepository {
    suspend fun getItems(): List<Item>
}
