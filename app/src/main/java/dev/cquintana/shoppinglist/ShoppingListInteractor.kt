package dev.cquintana.shoppinglist

import io.reactivex.rxjava3.core.Single

class ShoppingListInteractor(val service: ShoppingListService) {

    companion object {
        const val HTTP_DUPLICATED_STATUS_CODE = 409
    }

    fun getAll(): Single<Result> {
        return service.getAll()
            .map {
                Result.withState(it)
            }
            .onErrorReturn {
                Result.withError(Error.IOError(it.message ?: ""))
            }
    }

    fun createNew(name: String): Single<Result> {
        return service.createNew(NewShoppingListRequest(name))
            .map {
                if (it.isSuccessful) {
                    Result.withState(it.body()!!)
                } else {
                    if (it.code() == HTTP_DUPLICATED_STATUS_CODE) {
                        Result.withError(Error.ItemAlreadyExists)
                    } else {
                        Result.withError(Error.IOError(it.errorBody().toString()))
                    }
                }
            }
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