package ru.alexskvortsov.policlinic.domain

import androidx.annotation.CheckResult
import io.reactivex.*
import io.reactivex.disposables.Disposable
import timber.log.Timber

inline fun <reified T> Observable<T>.endWith(tail: T): Observable<T> =
    this.concatWith(Single.just(tail))

fun String.filterDigits(): String =
    this.filter { it in '0'..'9' || it == '-' || it == '+' || it == ',' || it == '.' }
        .replace(',', '.')

val Int.even: Boolean
    get() = this % 2 == 0


inline fun <reified T> Completable.toObservableWithDefault(default: T): Observable<T> =
    this.toSingleDefault(default).toObservable()

inline fun <reified T> List<T>.crush(subSize: Int = 100): List<List<T>> {
    var current = this
    val result = mutableListOf<List<T>>()
    while (current.isNotEmpty()) {
        result.add(current.take(subSize))
        current = current.drop(subSize)
    }
    return result
}

fun List<Int>.maxOrZero() = max() ?: 0

inline fun <reified T> Collection<T>.containsAny(other: Collection<T>): Boolean {
    other.forEach { if (it in this) return true }
    return false
}

inline fun <reified T, U> Single<List<T>>.flattenMap(crossinline mapper: (T) -> U): Single<List<U>> =
    map { it.map { element -> mapper(element) } }

inline fun <reified T, U> Observable<List<T>>.flattenMap(crossinline mapper: (T) -> U): Observable<List<U>> =
    map { it.map { element -> mapper(element) } }

inline fun <reified T, U> Single<List<T>>.flattenFlatMap(crossinline mapper: (T) -> Observable<U>): Single<List<U>> =
    flattenAsObservable { it }.flatMap { mapper(it) }.toList()

inline fun <reified T> Single<List<T>>.flattenFilter(crossinline filter: (T) -> Boolean): Single<List<T>> =
    map { list -> list.filter { filter(it) } }

inline fun <reified T> Observable<List<T>>.flattenFilter(crossinline filter: (T) -> Boolean): Observable<List<T>> =
    map { list -> list.filter { filter(it) } }

inline fun <reified T> List<T>.add(newElement: T, index: Int = this.size): List<T> =
    toMutableList().apply { add(index, newElement) }

inline fun <reified T> Single<T>.flatMapCompletableAction(crossinline block: (T) -> Unit): Completable =
    flatMapCompletable { Completable.fromAction { block(it) } }

inline fun <reified T> Flowable<T>.flatMapCompletableAction(crossinline block: (T) -> Unit): Completable =
    flatMapCompletable { Completable.fromAction { block(it) } }

inline fun <reified T> Maybe<T>.flatMapCompletableAction(crossinline block: (T) -> Unit): Completable =
    flatMapCompletable { Completable.fromAction { block(it) } }

inline fun <reified T> T?.defaultIfNull(default: T): T = this ?: default

@CheckResult
fun Completable.subscribeBy(
    onComplete: () -> Unit = onCompleteStub,
    onError: (Throwable) -> Unit = onErrorStub
): Disposable = subscribe(onComplete, onError)

@CheckResult
fun <T> Single<T>.subscribeBy(
    onComplete: (T) -> Unit = { Timber.d("$it") },
    onError: (Throwable) -> Unit = { Timber.e(it) }
): Disposable = subscribe(onComplete, onError)

@CheckResult
fun <T> Observable<T>.subscribeBy(
    onNext: (T) -> Unit,
    onError: (Throwable) -> Unit = { Timber.e(it) }
): Disposable = subscribe(onNext, onError)

val onErrorStub: (Throwable) -> Unit = { Timber.e(it, "On error not implemented") }
val onCompleteStub: () -> Unit = {}
fun doNothing() {}