package ru.alexskvortsov.policlinic.data.storage.database.dao

import androidx.room.Dao
import androidx.room.Query
import io.reactivex.Completable
import io.reactivex.Single
import ru.alexskvortsov.policlinic.data.storage.database.entities.UserEntity

@Dao
interface UserDao : BaseDao<UserEntity> {

    @Query("SELECT * FROM users WHERE id = :id")
    fun getUserEntityById(id: String): Single<UserEntity>

    @Query("UPDATE users SET password = :newPassword WHERE id = :id")
    fun updatePassword(id: String, newPassword: String): Completable

    @Query("SELECT COUNT(*) FROM users")
    fun count(): Single<Int>
}