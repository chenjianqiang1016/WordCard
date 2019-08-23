package com.demo.wordcard.activity

import android.content.Intent
import android.view.View
import android.widget.Toast
import com.demo.wordcard.Constant
import com.demo.wordcard.MyEvent
import com.demo.wordcard.OnAvoidDoubleClickListener
import com.demo.wordcard.R
import com.demo.wordcard.room.AppDataBase
import com.demo.wordcard.util.DataUtil
import kotlinx.android.synthetic.main.activity_main.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class MainActivity : BaseActivity() {

    /**
     * 单词数据是否已经准备好
     */
    private var isWordDataInitSuccess = false


    override fun getLayout(): Int = R.layout.activity_main

    override fun initView() {

        DataUtil.computeCardDataCoordinates(this)

        //检查有没数据，没有的话，就先准备下
        if (AppDataBase.instance.getWordDao().getAll().isNullOrEmpty()) {
            isWordDataInitSuccess = false

            //准备单词数据
            DataUtil.initWordData()

        } else {
            haveData()
        }

    }

    //处理点击事件
    override fun handleClick(){

        //普通学习，非循环模式
        to_card_1.setOnClickListener(object :OnAvoidDoubleClickListener(){
            override fun onNoDoubleClick(v: View?) {

                if(AppDataBase.instance.getWordDao().getUnStudyWordList().isNullOrEmpty()){
                    //未学列表为空，说明单词都学过了
                    Toast.makeText(this@MainActivity, "已学完全部单词，请重置", Toast.LENGTH_SHORT).show()
                }else{
                    CardActivity.startCardActivity(this@MainActivity,Constant.NotCycle)
                }



            }

        })
        //循环模式
        to_card_2.setOnClickListener(object :OnAvoidDoubleClickListener(){
            override fun onNoDoubleClick(v: View?) {

                if(AppDataBase.instance.getWordDao().getStudyWordList().isNullOrEmpty()){
                    Toast.makeText(this@MainActivity, "没有学习过，请先学习", Toast.LENGTH_SHORT).show()
                    return
                }

                var studyList = AppDataBase.instance.getWordDao().getStudyWordList()

                var num = studyList?.filter {
                    it.wStatus==0
                }?.size

                if(num ==null || num <=0){
                    Toast.makeText(this@MainActivity, "已学单词都被斩了，无复习数据", Toast.LENGTH_SHORT).show()
                    return
                }

                CardActivity.startCardActivity(this@MainActivity,Constant.Cycle)

            }

        })
        //查看全部单词
        all_word.setOnClickListener(object :OnAvoidDoubleClickListener(){
            override fun onNoDoubleClick(v: View?) {

                this@MainActivity.startActivity(Intent(this@MainActivity,AllWordActivity::class.java))

            }

        })
        //查看单词学习状态
        word_study_status.setOnClickListener(object :OnAvoidDoubleClickListener(){
            override fun onNoDoubleClick(v: View?) {

                this@MainActivity.startActivity(Intent(this@MainActivity,WordStudyStatusActivity::class.java))

            }

        })
        //已斩单词
        cut_word.setOnClickListener(object :OnAvoidDoubleClickListener(){
            override fun onNoDoubleClick(v: View?) {

                this@MainActivity.startActivity(Intent(this@MainActivity,CutWordActivity::class.java))

            }

        })

        //数据重置
        reset.setOnClickListener(object :OnAvoidDoubleClickListener(){
            override fun onNoDoubleClick(v: View?) {

                var dataList = AppDataBase.instance.getWordDao().getAll()

                dataList.forEach {

                    it.wStatus=0
                    it.isStudy=0
                    it.studyTime=0L

                }

                AppDataBase.instance.getWordDao().updateAll(dataList)

                Toast.makeText(this@MainActivity, "数据重置完成", Toast.LENGTH_SHORT).show()


            }

        })


    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun handleEvent(event: MyEvent) {

        when (event.flag) {

            MyEvent.InitDataSuccess -> {

                haveData()

            }
        }
    }

    //有数据了
    private fun haveData() {
        isWordDataInitSuccess = true
        Toast.makeText(this@MainActivity, "数据已准备好", Toast.LENGTH_SHORT).show()
    }

}
