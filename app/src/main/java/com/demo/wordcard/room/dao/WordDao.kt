package com.demo.wordcard.room.dao

import androidx.room.Dao
import androidx.room.Query
import com.demo.wordcard.room.entity.Word


@Dao
interface WordDao : BaseDao<Word> {

    //不计条件，获取全部
    @Query("select * from word")
    fun getAll(): MutableList<Word>

    //获取已斩单词
    @Query("select * from word where wStatus=1")
    fun getCutWordList(): MutableList<Word>?

    //获取已学单词
    @Query("select * from word where isStudy=1")
    fun getStudyWordList(): MutableList<Word>?

    //获取未学单词
    @Query("select * from word where isStudy=0")
    fun getUnStudyWordList(): MutableList<Word>?

    //模糊查找
    @Query("select * from word where word like :key || '%'  order by word COLLATE NOCASE")
    fun getLikeCheck(key: String): MutableList<Word>?

    //通过ID获取单词
    @Query("select * from word where wordID = :id")
    fun getWordById(id:Long):Word?

    //全部删除
    @Query("delete from word")
    fun deleteAll(): Int

    //获取所需的，一定数量的单词
    @Query("select * from word where isStudy=0 limit :num")
    fun getNeedWordList(num:Int):MutableList<Word>


}