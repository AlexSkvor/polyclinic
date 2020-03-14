package ru.alexskvortsov.policlinic.data.storage.database.dao

import androidx.room.Dao
import androidx.room.Query
import io.reactivex.Single
import ru.alexskvortsov.policlinic.data.storage.database.entities.UserEntity

@Dao
interface UserDao : BaseDao<UserEntity> {

    @Query("SELECT * FROM users WHERE id = :id")
    fun getUserEntityById(id: String): Single<UserEntity>

}