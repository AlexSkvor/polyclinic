package ru.alexskvortsov.policlinic.domain.states.records.list

data class RecordsViewState(
    val listType: ListType = ListType.FUTURE,
    val list: List<Record> = emptyList()
) {
    enum class ListType(val value: Int) { FUTURE(0), PAST(1) }
}