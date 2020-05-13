package ru.alexskvortsov.policlinic.domain.states.records.record_info

import ru.alexskvortsov.policlinic.data.storage.database.entities.ProcedureEntity

sealed class ConsultationInfoPartialState(private val log: String) {

    data class Loading(val flag: Boolean) : ConsultationInfoPartialState("Loading $flag")
    data class Error(val e: Throwable) : ConsultationInfoPartialState("Error $e")

    object Start : ConsultationInfoPartialState("Start")
    object Undo : ConsultationInfoPartialState("Undo")

    object CloseAndReloadList : ConsultationInfoPartialState("CloseAndReloadList")

    data class AddProcedure(val procedure: ProcedureEntity) : ConsultationInfoPartialState("AddProcedure $procedure")
    data class RemoveProcedure(val procedure: ProcedureEntity) : ConsultationInfoPartialState("RemoveProcedure $procedure")
    data class ChangeNote(val note: String) : ConsultationInfoPartialState("ChangeNote $note")

    data class PossibleProceduresLoaded(val procedures: List<ProcedureEntity>) : ConsultationInfoPartialState("PossibleProceduresLoaded $procedures")

    override fun toString(): String = log
    fun partial() = this
}