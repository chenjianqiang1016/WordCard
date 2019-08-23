package com.demo.wordcard.room.dao

import androidx.room.*

@Dao
interface BaseDao<T> {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(element: T):Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg element: T):MutableList<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(list: MutableList<T>):MutableList<Long>

    @Delete
    fun delete(element: T): Int

    @Delete
    fun delete(element:List<T>)

    @Update
    fun update(element: T): Int

    @Update
    fun updateAll(list:MutableList<T>):Int

}