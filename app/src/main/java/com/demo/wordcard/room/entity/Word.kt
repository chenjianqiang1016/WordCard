package com.demo.wordcard.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 单词表
 */
@Entity(tableName = "word")
data class Word(
    @PrimaryKey(autoGenerate = true) var wordID: Long = 0,
    var word: String = "",
    var sound: String = "",
    var wStatus:Int =0,//单词状态。0表示默认，1表示被斩
    var isStudy: Int = 0,//是否学习过。0表示未学，1表示已学
    var studyTime: Long = 0//学习这个单词的时间

)