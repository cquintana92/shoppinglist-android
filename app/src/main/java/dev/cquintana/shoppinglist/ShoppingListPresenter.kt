package dev.cquintana.shoppinglist

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.PublishSubject

sealed class Error {
    object BaseUrlNotConfigured: Error()
    data class IOError(val message: String): Error()
}

class Result private constructor(
    val state: List<ShoppingListItem>?,
    val error: Error?
) {

    companion object {
        fun withState(state: List<ShoppingListItem>): Result = Result(state, null)
        fun withError(error: Error): Result = Result(null, error)
    }

    fun isOk(): Boolean = this.state != null
    fun isError(): Boolean = this.error != null
}

interface ShoppingListView {
    fun showRefreshing()
    fun hideRefreshing()

    fun showSetupServerUrl(initialValue: String, initialBearerValue: String, cancelable: Boolean, callback: (String, String) -> Unit)
}

class ShoppingListPresenter(
    var view: ShoppingListView?,
    val shoppingListInteractor: ShoppingListInteractor,
    val preferencesManager: SharedPreferencesManager
) {

    var producer: PublishSubject<Result> = PublishSubject.create()

    fun bind(observer: Observer<Result>) {
        producer.subscribe(observer)
    }

    fun getAll() {
        this.withUrlCheck {
            shoppingListInteractor
                .getAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        view?.hideRefreshing()
                        producer.onNext(Result.withState(it))
                    },
                    {
                        view?.hideRefreshing()
                        producer.onNext(Result.withError(Error.IOError(it.message ?: "")))
                    })
        }
    }

    fun createNew(name: String) {
        this.withUrlCheck {
            shoppingListInteractor
                .createNew(name)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        view?.hideRefreshing()
                        producer.onNext(Result.withState(it))
                    },
                    {
                        view?.hideRefreshing()
                        producer.onNext(Result.withError(Error.IOError(it.message ?: "")))
                    })
        }
    }

    fun toggleChecked(id: Int) {
        this.withUrlCheck {
            shoppingListInteractor
                .toggleChecked(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        view?.hideRefreshing()
                        producer.onNext(Result.withState(it))
                    },
                    {
                        view?.hideRefreshing()
                        producer.onNext(Result.withError(Error.IOError(it.message ?: "")))
                    })
        }
    }

    fun updateName(id: Int, name: String) {
        this.withUrlCheck {
            shoppingListInteractor
                .updateName(id, name)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        view?.hideRefreshing()
                        producer.onNext(Result.withState(it))
                    },
                    {
                        view?.hideRefreshing()
                        producer.onNext(Result.withError(Error.IOError(it.message ?: "")))
                    })
        }
    }

    fun deleteAllChecked() {
        this.withUrlCheck {
            shoppingListInteractor
                .deleteAllChecked()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        view?.hideRefreshing()
                        producer.onNext(Result.withState(it))
                    },
                    {
                        view?.hideRefreshing()
                        producer.onNext(Result.withError(Error.IOError(it.message ?: "")))
                    })
        }
    }

    fun deleteOne(id: Int) {
        this.withUrlCheck {
            shoppingListInteractor
                .deleteOne(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        view?.hideRefreshing()
                        producer.onNext(Result.withState(it))
                    },
                    {
                        view?.hideRefreshing()
                        producer.onNext(Result.withError(Error.IOError(it.message ?: "")))
                    })
        }
    }

    fun updatePosition(id: Int, position: Int) {
        this.withUrlCheck {
            shoppingListInteractor
                .updatePosition(id, position)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        view?.hideRefreshing()
                        //producer.onNext(it)
                    },
                    {
                        view?.hideRefreshing()
                        producer.onNext(Result.withError(Error.IOError(it.message ?: "")))
                        getAll()
                    })
        }
    }

    fun onConfigureServerUrlClicked() {
        val storedUrl = preferencesManager.getBaseUrl()
        val storedBearer = preferencesManager.getBearer()
        val callback = { url: String, bearer: String ->
            preferencesManager.setBaseUrl(url)
            if (bearer.isNotEmpty()) {
                preferencesManager.setBearer(bearer)
            }
            getAll()
        }
        if (storedUrl != null) {
            view?.showSetupServerUrl(storedUrl, storedBearer ?: "", true, callback)
        } else {
            view?.showSetupServerUrl("", storedBearer ?: "", false, callback)
        }
    }

    private fun withUrlCheck(f: () -> Unit) {
        view?.showRefreshing()
        if (preferencesManager.getBaseUrl() != null) {
            f()
        } else {
            view?.hideRefreshing()
            this.onConfigureServerUrlClicked()
            //producer.onNext(Result.withError(Error.BaseUrlNotConfigured))
        }
    }

}

