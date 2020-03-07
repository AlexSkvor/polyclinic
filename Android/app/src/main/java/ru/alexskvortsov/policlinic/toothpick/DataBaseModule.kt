package ru.alexskvortsov.policlinic.toothpick

import ru.alexskvortsov.policlinic.data.storage.database.AppDatabase
import ru.alexskvortsov.policlinic.data.storage.database.dao.*
import ru.alexskvortsov.policlinic.data.storage.database.providers.*
import toothpick.config.Module

class DataBaseModule : Module() {
    init {
        bind(AppDatabase::class.java).toProvider(AppDatabaseProvider::class.java).singleton()
        bind(DoctorDao::class.java).toProvider(DoctorDaoProvider::class.java).singleton()
        bind(PatientDao::class.java).toProvider(PatientDaoProvider::class.java).singleton()
        bind(WorkShiftDao::class.java).toProvider(WorkShiftDaoProvider::class.java).singleton()
        bind(UserDao::class.java).toProvider(UserDaoProvider::class.java).singleton()
        bind(ProcedureDao::class.java).toProvider(ProcedureDaoProvider::class.java).singleton()
        bind(CompetenceDao::class.java).toProvider(CompetenceDaoProvider::class.java).singleton()
        bind(ConsultationDao::class.java).toProvider(ConsultationDaoProvider::class.java)
            .singleton()
        bind(RegistryStaffDao::class.java).toProvider(RegistryStaffDaoProvider::class.java)
            .singleton()
        bind(FactConsultationDao::class.java).toProvider(FactConsultationDaoProvider::class.java)
            .singleton()
        bind(AttachedDocumentDao::class.java).toProvider(AttachedDocumentDaoProvider::class.java)
            .singleton()
        bind(FactConsultationToProceduresConnectionDao::class.java).toProvider(
            FactConsultationToProceduresConnectionDaoProvider::class.java
        ).singleton()
        bind(DoctorToCompetenceConnectionDao::class.java).toProvider(
            DoctorToCompetenceConnectionDaoProvider::class.java
        ).singleton()
    }
}