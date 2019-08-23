package com.demo.wordcard.activity

import android.widget.Toast
import com.demo.wordcard.MyEvent
import com.demo.wordcard.R
import com.demo.wordcard.adapter.WordListAdapter
import com.demo.wordcard.room.AppDataBase
import com.demo.wordcard.room.entity.Word
import kotlinx.android.synthetic.main.activity_all_word.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import kotlin.concurrent.thread

/**
 * 全部单词界面
 */
class AllWordActivity : BaseActivity() {

    private var wordList: MutableList<Word>? = null

    private var wordListAdapter: WordListAdapter? = null

    override fun getLayout(): Int = R.layout.activity_all_word

    override fun initView() {

        wordListAdapter = WordListAdapter(this@AllWordActivity, mutableListOf())
        all_word_recycler_view?.adapter = wordListAdapter

        getAllWordList()


    }

    override fun handleClick() {

        //返回键
        all_word_back.setOnClickListener {
            finish()
        }

        /**
         * 排序
         *
         * 这里的排序，指按字母顺序排序。排序后
         *
         * word1,word10,word11...word2,word20...这样的
         */
        all_word_order.setOnClickListener {

            wordList?.sortBy { it.word.toLowerCase() }
            wordListAdapter?.setData(wordList!!)
        }

    }


    /**
     * 获取所有单词数据
     */
    private fun getAllWordList() {

        thread {
            wordList = AppDataBase.instance.getWordDao().getAll()
            EventBus.getDefault().post(MyEvent(MyEvent.ShowAllWord))
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun handleAllWordEvent(event: MyEvent) {

        if (event.flag == MyEvent.ShowAllWord) {

            if (wordList.isNullOrEmpty()) {
                Toast.makeText(this@AllWordActivity, "没有数据", Toast.LENGTH_SHORT).show()
                return
            }

            wordListAdapter?.setData(wordList!!)

        }


    }


}
