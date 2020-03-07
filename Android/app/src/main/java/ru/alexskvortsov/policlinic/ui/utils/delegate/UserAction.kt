package ru.alexskvortsov.policlinic.ui.utils.delegate


import io.reactivex.Observable

sealed class UserAction<T>(private val logMessage: String) {
    data class ItemPressed<T>(val item: T) : UserAction<T>("ItemPressed $item")
    data class ItemEdit<T>(val item: T) : UserAction<T>("ItemEdit $item")
    data class ItemDeleted<T>(val item: T) : UserAction<T>("ItemDelete $item")
    data class ItemReturned<T>(val item: T) : UserAction<T>("ItemReturned $item")

    override fun toString(): String = logMessage
    fun action() = this
}

inline fun <reified T> Observable<UserAction<T>>.pressedItems(): Observable<T> =
        ofType(UserAction.ItemPressed::class.java)
                .map { it.item }
                .ofType(T::class.java)

inline fun <reified T> Observable<UserAction<T>>.returnedItems(): Observable<T> =
        ofType(UserAction.ItemReturned::class.java)
                .map { it.item }
                .ofType(T::class.java)

inline fun <reified T> Observable<UserAction<T>>.deletedItems(): Observable<T> =
        ofType(UserAction.ItemDeleted::class.java)
                .map { it.item }
                .ofType(T::class.java)

inline fun <reified T> Observable<UserAction<T>>.editItems(): Observable<T> =
        ofType(UserAction.ItemEdit::class.java)
                .map { it.item }
                .ofType(T::class.java)
