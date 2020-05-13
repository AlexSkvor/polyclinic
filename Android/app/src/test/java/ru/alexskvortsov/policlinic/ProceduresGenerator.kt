package ru.alexskvortsov.policlinic

import com.google.gson.Gson
import ru.alexskvortsov.policlinic.data.storage.database.entities.ProcedureEntity
import java.io.File

class ProceduresGenerator {

    companion object{
        private val proceduresNames = listOf(
            "Осмотр",
            "Консультация",
            "Лоботомия",
            "Ампутация",
            "Вскрытие"
        )
    }

    private val gson = Gson()

    fun generateProcedures() {
        val procedures = generate()

        val file = File("procedures.json")
        file.createNewFile()
        file.writeText(gson.toJson(procedures))
    }

    private fun generate(): List<ProcedureEntity> {
        return proceduresNames.map {
            ProcedureEntity(
                id = uuid,
                description = "Автоматически сгенерированный объект процедуры, для примера",
                name = it
            )
        }
    }
}