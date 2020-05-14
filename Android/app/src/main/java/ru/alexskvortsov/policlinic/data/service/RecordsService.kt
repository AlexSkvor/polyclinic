package ru.alexskvortsov.policlinic.data.service

import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.functions.Function4
import io.reactivex.functions.Function6
import ru.alexskvortsov.policlinic.data.storage.database.dao.*
import ru.alexskvortsov.policlinic.data.storage.database.entities.*
import ru.alexskvortsov.policlinic.data.storage.prefs.AppPrefs
import ru.alexskvortsov.policlinic.data.system.schedulers.Scheduler
import ru.alexskvortsov.policlinic.domain.flattenFlatMap
import ru.alexskvortsov.policlinic.domain.repository.RecordsRepository
import ru.alexskvortsov.policlinic.domain.states.auth.UserAuthInfo
import ru.alexskvortsov.policlinic.domain.states.records.list.Record
import ru.alexskvortsov.policlinic.domain.states.records.list.RecordsViewState
import javax.inject.Inject

class RecordsService @Inject constructor(
    private val scheduler: Scheduler,
    private val patientDao: PatientDao,
    private val doctorDao: DoctorDao,
    private val competenceDao: CompetenceDao,
    private val consultationDao: ConsultationDao,
    private val factConsultationDao: FactConsultationDao,
    private val userDao: UserDao,
    private val proceduresDao: FactConsultationToProceduresConnectionDao,
    private val prefs: AppPrefs
) : RecordsRepository {

    override fun getRecordsList(type: RecordsViewState.ListType): Observable<List<Record>> =
        when (prefs.currentUser.type) {
            UserAuthInfo.UserType.DOCTOR -> getRecordsListForDoctor(type)
            UserAuthInfo.UserType.REGISTRY -> getRecordsListForRegistry(type)
            UserAuthInfo.UserType.PATIENT -> getRecordsListForPatient(type)
        }.map { sort(type, it) }
            .toObservable()
            .subscribeOn(scheduler.io())
            .observeOn(scheduler.ui())

    private fun getRecordsListForDoctor(type: RecordsViewState.ListType): Single<List<Record>> = when (type) {
        RecordsViewState.ListType.FUTURE ->
            consultationDao.getListForDoctorIdFuture(prefs.currentUser.realId, prefs.currentUser.userId).toRecordsFuture()
        RecordsViewState.ListType.PAST ->
            consultationDao.getListForDoctorIdPast(prefs.currentUser.realId, prefs.currentUser.userId).toRecordsPast()
    }

    private fun getRecordsListForPatient(type: RecordsViewState.ListType): Single<List<Record>> = when (type) {
        RecordsViewState.ListType.FUTURE -> consultationDao.getListForPatientIdFuture(prefs.currentUser.realId, prefs.currentUser.userId)
            .toRecordsFuture()
        RecordsViewState.ListType.PAST -> consultationDao.getListForPatientIdPast(prefs.currentUser.realId, prefs.currentUser.userId).toRecordsPast()
    }

    private fun getRecordsListForRegistry(type: RecordsViewState.ListType): Single<List<Record>> = when (type) {
        RecordsViewState.ListType.FUTURE -> consultationDao.getListForRegistryIdFuture(prefs.currentUser.userId).toRecordsFuture()
        RecordsViewState.ListType.PAST -> consultationDao.getListForRegistryIdPast(prefs.currentUser.userId).toRecordsPast()
    }

    private fun Single<List<ConsultationEntity>>.toRecordsPast(): Single<List<Record>> = flattenFlatMap {
        Single.zip(
            doctorDao.getById(it.doctorId),
            patientDao.getById(it.patientId),
            competenceDao.getById(it.competenceId),
            userDao.getById(it.userAskedId),
            factConsultationDao.getFactConsultationForPlan(it.id),
            proceduresDao.getProceduresForPlan(it.id),
            Function6<DoctorEntity, PatientEntity, CompetenceEntity, UserEntity, FactConsultationEntity, List<ProcedureEntity>, Record>
            { doctor: DoctorEntity, patient: PatientEntity, competence: CompetenceEntity, userCreated: UserEntity, factConsultation: FactConsultationEntity, procedures: List<ProcedureEntity> ->
                Record(
                    consultationId = it.id,
                    doctorEntity = doctor,
                    patientEntity = patient,
                    competenceEntity = competence,
                    createdUserEntity = userCreated,
                    startTimePlan = it.startTimePlan,
                    endTimePlan = it.endTimePlan,
                    reason = it.descriptionOfReason,
                    cancelled = it.cancelled,
                    proceduresUsed = procedures,
                    startTimeFact = factConsultation.startTimeFact,
                    endTimeFact = factConsultation.endTimeFact,
                    doctorNotes = factConsultation.notes
                )
            }
        ).toObservable()
    }

    private fun Single<List<ConsultationEntity>>.toRecordsFuture(): Single<List<Record>> = flattenFlatMap {
        Single.zip(
            doctorDao.getById(it.doctorId),
            patientDao.getById(it.patientId),
            competenceDao.getById(it.competenceId),
            userDao.getById(it.userAskedId),
            Function4<DoctorEntity, PatientEntity, CompetenceEntity, UserEntity, Record>
            { doctor: DoctorEntity, patient: PatientEntity, competence: CompetenceEntity, userCreated: UserEntity ->
                Record(
                    consultationId = it.id,
                    doctorEntity = doctor,
                    patientEntity = patient,
                    competenceEntity = competence,
                    createdUserEntity = userCreated,
                    startTimePlan = it.startTimePlan,
                    endTimePlan = it.endTimePlan,
                    reason = it.descriptionOfReason,
                    cancelled = it.cancelled,
                    proceduresUsed = null,
                    startTimeFact = null,
                    endTimeFact = null,
                    doctorNotes = null
                )
            }
        ).toObservable()
    }

    private fun sort(type: RecordsViewState.ListType, list: List<Record>): List<Record> =
        when (type) {
            RecordsViewState.ListType.FUTURE -> list.sortedBy { it.startTimePlan }
            RecordsViewState.ListType.PAST -> list.sortedByDescending { it.startTimePlan }
        }
}