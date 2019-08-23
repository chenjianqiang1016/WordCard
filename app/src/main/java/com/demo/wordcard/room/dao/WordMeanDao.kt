package com.demo.wordcard.room.dao

import androidx.room.Dao
import androidx.room.Query
import com.demo.wordcard.room.entity.WordMean


@Dao
interface WordMeanDao : BaseDao<WordMean> {

    @Query("select * from word_mean")
    fun getAll(): MutableList<WordMean>

    /**
     * 根据给定ID，获取对应的所有释义
     */
    @Query("select * from word_mean where word_id=:wordId")
    fun getMeansByID(wordId: Long): MutableList<WordMean>


}