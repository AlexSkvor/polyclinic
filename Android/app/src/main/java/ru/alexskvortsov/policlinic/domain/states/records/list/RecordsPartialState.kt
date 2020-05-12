package ru.alexskvortsov.policlinic.domain.states.records.list

sealed class RecordsPartialState(private val log: String) {

    data class Loading(val flag: Boolean) : RecordsPartialState("Loading $flag")
    data class Error(val e: Throwable) : RecordsPartialState("Error $e")

    data class ListTypeChanged(val type: RecordsViewState.ListType) : RecordsPartialState("ListTypeChanged $type")
    data class ListLoaded(val list: List<Record>) : RecordsPartialState("ListLoaded $list")

    override fun toString(): String = log
    fun partial() = this
}