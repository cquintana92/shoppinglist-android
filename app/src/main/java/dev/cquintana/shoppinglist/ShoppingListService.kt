package dev.cquintana.shoppinglist

import io.reactivex.rxjava3.core.Single
import retrofit2.Response
import retrofit2.http.*

interface ShoppingListService {

    @GET("/")
    fun getAll(): Single<List<ShoppingListItem>>

    @POST("/")
    fun createNew(@Body request: NewShoppingListRequest): Single<Response<List<ShoppingListItem>>>

    @PATCH("/{id}")
    fun toggleChecked(@Path("id") id: Int): Single<List<ShoppingListItem>>

    @PUT("/{id}")
    fun updateName(@Path("id") id: Int, @Body request: UpdateShoppingListRequest): Single<List<ShoppingListItem>>

    @PUT("/{id}/position")
    fun updatePosition(@Path("id") id: Int, @Body request: UpdateShoppingListPositionRequest): Single<List<ShoppingListItem>>

    @DELETE("/")
    fun deleteAllChecked(): Single<List<ShoppingListItem>>

    @DELETE("/{id}")
    fun deleteOne(@Path("id") id: Int): Single<List<ShoppingListItem>>
}