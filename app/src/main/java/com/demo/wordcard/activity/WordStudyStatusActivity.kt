package com.demo.wordcard.activity

import android.widget.Toast
import com.demo.wordcard.MyEvent
import com.demo.wordcard.R
import com.demo.wordcard.adapter.WordListAdapter
import com.demo.wordcard.room.AppDataBase
import com.demo.wordcard.room.entity.Word
import kotlinx.android.synthetic.main.activity_word_status.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import kotlin.concurrent.thread

/**
 * 单词学习状态
 */
class WordStudyStatusActivity : BaseActivity() {

    private var wordList: MutableList<Word>? = null

    private var wordListAdapter: WordListAdapter? = null

    override fun getLayout(): Int = R.layout.activity_word_status

    override fun initView() {

        wordListAdapter = WordListAdapter(this@WordStudyStatusActivity, mutableListOf())
        word_status_recycler_view?.adapter = wordListAdapter

    }

    override fun handleClick() {

        //返回键
        word_status_back.setOnClickListener {
            finish()
        }

        word_status_study.setOnClickListener {
            getStudyWordList()
        }

        word_status_un_study.setOnClickListener {
            getUnStudyWordList()
        }


    }

    private fun getStudyWordList() {

        thread {
            wordList = AppDataBase.instance.getWordDao().getStudyWordList()
            EventBus.getDefault().post(MyEvent(MyEvent.WordStatus))
        }

    }

    private fun getUnStudyWordList() {

        thread {
            wordList = AppDataBase.instance.getWordDao().getUnStudyWordList()
            EventBus.getDefault().post(MyEvent(MyEvent.WordStatus))
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun handleWordStatusEvent(event: MyEvent) {

        if (event.flag == MyEvent.WordStatus) {

            if (wordList.isNullOrEmpty()) {
                Toast.makeText(this@WordStudyStatusActivity, "没有数据", Toast.LENGTH_SHORT).show()
                return
            }

            wordListAdapter?.setData(wordList!!)



        }


    }


}
