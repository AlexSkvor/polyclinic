package ru.alexskvortsov.policlinic.data.storage.database.dao

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy

interface BaseDao<Value : Any> {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(element: Value)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    @JvmSuppressWildcards
    fun insertAll(elements: List<Value>)

    @Delete
    fun delete(element: Value)

}