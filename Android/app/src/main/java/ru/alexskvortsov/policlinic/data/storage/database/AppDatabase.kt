package ru.alexskvortsov.policlinic.data.storage.database

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.alexskvortsov.policlinic.data.storage.database.dao.*
import ru.alexskvortsov.policlinic.data.storage.database.entities.*

@Database(
    entities = [
        DoctorEntity::class,
        PatientEntity::class,
        RegistryStaffEntity::class,
        UserEntity::class,
        ConsultationEntity::class,
        FactConsultationEntity::class,
        AttachedDocumentEntity::class,
        ProcedureEntity::class,
        FactConsultationToProceduresConnectionEntity::class,
        CompetenceEntity::class,
        DoctorToCompetenceConnectionEntity::class
    ], version = 1, exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun doctorDao(): DoctorDao
    abstract fun patientDao(): PatientDao
    abstract fun registryStaffDao(): RegistryStaffDao
    abstract fun userDao(): UserDao
    abstract fun consultationDao(): ConsultationDao
    abstract fun factConsultationDao(): FactConsultationDao
    abstract fun attachedDocumentDao(): AttachedDocumentDao
    abstract fun procedureDao(): ProcedureDao
    abstract fun factConsultationToProceduresConnectionDao(): FactConsultationToProceduresConnectionDao
    abstract fun competenceDao(): CompetenceDao
    abstract fun doctorToCompetenceConnectionDao(): DoctorToCompetenceConnectionDao
}