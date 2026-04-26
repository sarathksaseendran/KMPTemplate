package com.template.shared.data.repository

import com.template.shared.domain.models.Item
import com.template.shared.domain.repository.ItemRepository
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*

class ItemRepositoryImpl(private val httpClient: HttpClient) : ItemRepository {
    override suspend fun getItems(): List<Item> {
        // Placeholder for real API call
        // return httpClient.get("https://dummyjson.com/products").body<List<Item>>()
        return listOf(
            Item(1, "Template Item 1", "This is a sample item from the template."),
            Item(2, "Template Item 2", "Another sample item to show the list.")
        )
    }
}
