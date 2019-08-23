package com.demo.wordcard.activity

import android.widget.Toast
import com.demo.wordcard.MyEvent
import com.demo.wordcard.R
import com.demo.wordcard.adapter.WordListAdapter
import com.demo.wordcard.room.AppDataBase
import com.demo.wordcard.room.entity.Word
import kotlinx.android.synthetic.main.activity_cut_word.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import kotlin.concurrent.thread

/**
 * 已斩单词界面
 */
class CutWordActivity : BaseActivity() {

    private var wordList: MutableList<Word>? = null

    private var wordListAdapter: WordListAdapter? = null

    override fun getLayout(): Int = R.layout.activity_cut_word

    override fun initView() {

        wordListAdapter = WordListAdapter(this@CutWordActivity, mutableListOf())
        cut_word_recycler_view?.adapter = wordListAdapter

        getCutWordList()


    }

    override fun handleClick() {

        //返回键
        cut_word_back.setOnClickListener {
            finish()
        }

    }

    private fun getCutWordList() {

        thread {
            wordList = AppDataBase.instance.getWordDao().getCutWordList()
            EventBus.getDefault().post(MyEvent(MyEvent.ShowCutWord))
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun handleAllWordEvent(event: MyEvent) {

        if (event.flag == MyEvent.ShowCutWord) {

            if (wordList.isNullOrEmpty()) {
                Toast.makeText(this@CutWordActivity, "没有数据", Toast.LENGTH_SHORT).show()
                return
            }

            wordListAdapter?.setData(wordList!!)



        }


    }


}
