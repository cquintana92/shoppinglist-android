package dev.cquintana.shoppinglist

import com.squareup.moshi.Json

data class ShoppingListItem(
    @field:Json(name = "id") val id: Int,
    @field:Json(name = "name") var name: String,
    @field:Json(name = "listOrder") val listOrder: Int,
    @field:Json(name = "checked") val checked: Boolean,
    @field:Json(name = "createdAt") val createdAt: String
)

data class NewShoppingListRequest(
    @field:Json(name = "name") val name: String
)

data class UpdateShoppingListRequest(
    @field:Json(name = "name") val name: String
)

data class UpdateShoppingListPositionRequest(
    @field:Json(name = "position") val position: Int
)
