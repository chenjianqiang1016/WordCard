package com.demo.wordcard.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 单词释义表
 */
@Entity(tableName = "word_mean")
data class WordMean(
    //释义的ID
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
    //单词的ID。一个单词ID，可能对应多个释义
    @ColumnInfo(name = "word_id") var wordId: Long = 0,
    //释义内容
    var mean: String = ""
)