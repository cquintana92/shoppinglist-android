package dev.cquintana.shoppinglist

import io.reactivex.rxjava3.core.Single

class ShoppingListInteractor(val service: ShoppingListService) {

    fun getAll(): Single<List<ShoppingListItem>> {
        return service.getAll()
    }

    fun createNew(name: String): Single<List<ShoppingListItem>> {
        return service.createNew(NewShoppingListRequest(name))
    }

    fun toggleChecked(id: Int): Single<List<ShoppingListItem>> {
        return service.toggleChecked(id)
    }

    fun updateName(id: Int, name: String): Single<List<ShoppingListItem>> {
        return service.updateName(id, UpdateShoppingListRequest(name))
    }

    fun updatePosition(id: Int, position: Int): Single<List<ShoppingListItem>> {
        return service.updatePosition(id, UpdateShoppingListPositionRequest(position))
    }

    fun deleteAllChecked(): Single<List<ShoppingListItem>> {
        return service.deleteAllChecked()
    }

    fun deleteOne(id: Int): Single<List<ShoppingListItem>> {
        return service.deleteOne(id)
    }

}