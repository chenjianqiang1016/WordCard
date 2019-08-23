package com.demo.wordcard.activity

import android.content.Context
import android.content.Intent
import com.demo.wordcard.R
import com.demo.wordcard.room.AppDataBase
import kotlinx.android.synthetic.main.activity_detail.*

/**
 * 单词详情页
 */
class WordDetailActivity : BaseActivity() {

    private var wordID: Long = 0L

    companion object {

        fun startWordDetail(context: Context, wID: Long) {
            var intent: Intent = Intent(context, WordDetailActivity::class.java)
            intent.putExtra("wordID", wID)
            context.startActivity(intent)
        }

    }

    override fun getLayout(): Int = R.layout.activity_detail

    override fun initView() {

        wordID = intent.getLongExtra("wordID", 0L)

        word_detail_tv.text = AppDataBase.instance.getWordDao().getWordById(wordID)?.word

    }

    override fun handleClick() {

        //返回键
        word_detail_back.setOnClickListener {
            finish()
        }


    }


}
