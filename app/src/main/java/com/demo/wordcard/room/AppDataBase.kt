package com.demo.wordcard.room

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.demo.wordcard.MyApplication
import com.demo.wordcard.room.dao.WordDao
import com.demo.wordcard.room.dao.WordMeanDao
import com.demo.wordcard.room.entity.Word
import com.demo.wordcard.room.entity.WordMean

@Database(entities = [Word::class, WordMean::class], version = 1)
abstract class AppDataBase : RoomDatabase() {

    abstract fun getWordDao(): WordDao

    abstract fun getWordMeanDao(): WordMeanDao

    //这里的单例，是静态内部类写法

    companion object {

        val instance = Single.sin

    }

    private object Single {

        val sin: AppDataBase = Room.databaseBuilder(
            MyApplication.instance(),
            AppDataBase::class.java,
            "MyWord.db"
        )
            .allowMainThreadQueries()
            .build()
    }

}