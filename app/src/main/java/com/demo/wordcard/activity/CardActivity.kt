package com.demo.wordcard.activity

import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Message
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import com.demo.wordcard.*
import com.demo.wordcard.Constant.Companion.needStudyNum
import com.demo.wordcard.adapter.WordCardAdapter
import com.demo.wordcard.room.AppDataBase
import com.demo.wordcard.room.entity.Word
import com.demo.wordcard.swipeCardView.SwipeFlingAdapterView
import com.demo.wordcard.util.DateUtil
import com.demo.wordcard.util.LogUtil
import com.demo.wordcard.util.StringCheckUtil
import com.demo.wordcard.util.VoicePlayAnimUtil
import kotlinx.android.synthetic.main.activity_card.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import kotlin.concurrent.thread

/**
 * 卡片界面
 */
class CardActivity : BaseActivity(), SwipeFlingAdapterView.onFlingListener,
    SwipeFlingAdapterView.OnItemClickListener {

    //类型。默认是 非循环模式
    private var type = Constant.NotCycle

    /**
     * 本组单词，学习的位置
     */
    private var wordStudyIndex: Int = 0

    /**
     * 是否是"一卡多词"。
     * true：表示一页5个单词。如果最后一页不足5个，有多少展示多少
     */
    private var isPluralWord = true

    /**
     * 是否自动读音
     * true：自动读音
     */
    private var isAutoRead = true

    /**
     * 卡片中，单词数据
     */
    private var dataList: MutableList<CardBean> = mutableListOf()

    private var adapter: WordCardAdapter? = null

    //今天已学单词个数
    private var todayStudiedNum: Int = 0

    //剩余单词个数。即：未学单词的个数。（全部-已学）
    private var residueWordNum = 0


    //结束控件的类型
    private var finishTvType = 0

    //是否改变了斩的状态
    private var isCutChange = false

    //是否正在加载数据
    private var isLoadingData = true

    //读音控件集合
    private var mVoiceViews: MutableList<ImageView> = mutableListOf()
    //读音所在的单词的对象集合
    private var mVoiceBeans: MutableList<CardBean> = mutableListOf()

    //标记读到了集合中哪个位置
    private var readIndex: Int = 0

    private val HandlerCode: Int = 20190821

    private var mHandler = object : Handler() {

        override fun handleMessage(msg: Message?) {
            super.handleMessage(msg)

            when (msg?.what) {

                HandlerCode -> {

                    try {

                        adapter?.setIsHaveVoice(true)

                        var bean = mVoiceBeans.get(readIndex)

                        if (bean == null || StringCheckUtil.checkStringIsNull(bean.wordSound) || bean.isCut) {
                            //数据为空、单词被斩、读音需要数据为空 以上情况直接跳过
                            sendEmptyMessageDelayed(HandlerCode, 0)
                            readIndex++
                        } else {
                            Toast.makeText(this@CardActivity, bean.wordSound, Toast.LENGTH_SHORT).show()
                            VoicePlayAnimUtil.playVoice(mVoiceViews.get(readIndex))

                            readIndex++

                            sendEmptyMessageDelayed(HandlerCode, 2000)
                        }

                    } catch (e: Exception) {
                        clearVoiceAbout()
                    }
                }

                else -> {
                    clearVoiceAbout()
                }

            }
        }
    }

    companion object {

        fun startCardActivity(context: Context, type: Int) {
            var intent: Intent = Intent(context, CardActivity::class.java)
            intent.putExtra("CardType", type)
            context.startActivity(intent)
        }

    }

    override fun getLayout(): Int = R.layout.activity_card

    override fun initView() {

        type = intent.getIntExtra("CardType", Constant.NotCycle)

        /**
         * 这里控件的可空的 ? ，因为，有时候如果加装数据过慢，会报异常
         *
         * 如：单词数足够过，设置每天学习500单词，取出单词后，再进行转换等，就会耗费很长时间
         *
         * 这个时候，等数据加装完，填充界面的时候，一些控件就会报空的异常。所以需要用 ?
         */
        word_card_pb?.max = dataList.size
        word_card_pb?.progress = wordStudyIndex

        word_card_swipe_view?.setIsNeedSwipe(true)
        word_card_swipe_view?.setFlingListener(this)
        word_card_swipe_view?.setOnItemClickListener(this)

        isPluralWord = SpUtil.getBooleanValue(Constant.WordCardPluralSetStatus, true)
        isAutoRead = SpUtil.getBooleanValue(Constant.WordCardAutoReadSetStatus, true)

        setPluralHelpViewShow()

        adapter = WordCardAdapter(this, mutableListOf<CardBean>())
        adapter?.setWordStudyType(isPluralWord)

        word_card_swipe_view?.setAdapter(adapter)

        when (type) {
            Constant.NotCycle -> {

                Toast.makeText(this, "非循环模式", Toast.LENGTH_SHORT).show()
                card_title_tv.text = "非循环模式"
                loadNotCycleData()
            }

            Constant.Cycle -> {

                Toast.makeText(this, "循环模式", Toast.LENGTH_SHORT).show()
                card_title_tv.text = "循环模式"
                loadCycleData()
            }
        }

    }

    private fun setPluralHelpViewShow() {
        one_help_view.visibility = View.GONE
        if (isPluralWord) {
            plural_help_view.visibility = View.VISIBLE
        }
    }

    //加载非循环模式下的数据
    private fun loadNotCycleData() {

        thread {

            isLoadingData = true

            //全部的单词列表
            var allWord: MutableList<Word> = AppDataBase.instance.getWordDao().getAll()

            //今天学过的单词的列表
            var todayWordList: MutableList<Word> = allWord.filter {

                //单词学习时间，比今天0时0分0秒大，比今天结束的 23时59分59秒小，视为这个单词是今天学的
                it.studyTime > DateUtil.getCurrentDayZeroTime() && it.studyTime < DateUtil.getCurrentDayFinishTime()

            }.toMutableList()

            //今天学习了多少个单词
            todayStudiedNum = todayWordList.size

            //剩余未学单词个数
            residueWordNum = allWord.filter {
                it.isStudy == 0
            }.toMutableList().size


            /**
             * 本次，需要取的单词的个数
             *
             * 算法说明
             *
             * 假设今天要学20个，已经学了7个，用户退出界面，再次进入。这个时候，应该只加载13个单词。
             *
             * 则，需要取的单词个数 = 20 - 7%20 = 20 - 7 = 13
             *
             */
            var neeGetNum = needStudyNum - todayStudiedNum % needStudyNum

            var list = AppDataBase.instance.getWordDao().getNeedWordList(neeGetNum)

            //转换成卡片单词
            var cardBeanList: MutableList<CardBean> = mutableListOf()
            if (list.isNullOrEmpty().not()) {
                var cardBean: CardBean
                list.forEach {
                    cardBean = CardBean(
                        it.wordID,
                        it.word,
                        it.sound,
                        false,
                        false
                    )
                    cardBeanList.add(cardBean)
                }
            }

            //清空之前的旧数据
            dataList.clear()

            if (list.isNullOrEmpty().not()) {
                dataList.addAll(cardBeanList)

            }
            EventBus.getDefault().post(MyEvent(MyEvent.HandleNotCycleData))

        }


    }

    //加载循环复习模式下的数据
    private fun loadCycleData() {

        thread {

            isLoadingData = true

            //全部的单词列表
            var allWord: MutableList<Word> = AppDataBase.instance.getWordDao().getAll()

            //取出已经学习过，但是没有斩掉的单词
            var list = allWord.filter {
                it.isStudy == 1 && it.wStatus == 0
            }


            var cardBeanList: MutableList<CardBean> = mutableListOf()
            if (list.isNullOrEmpty().not()) {
                var cardBean: CardBean
                list.forEach {
                    cardBean = CardBean(
                        it.wordID,
                        it.word,
                        it.sound,
                        false,
                        false
                    )
                    cardBeanList.add(cardBean)
                }
            }

            //清空之前的旧数据
            dataList.clear()

            if (list.isNullOrEmpty().not()) {
                dataList.addAll(cardBeanList)

            }
            EventBus.getDefault().post(MyEvent(MyEvent.HandleCycleData))

        }

    }


    override fun handleClick() {

        //返回键
        card_back.setOnClickListener {
            clearVoiceAbout()
            finish()
        }

        //当前学习组结束控件
        card_study_group_finish_tv.setOnClickListener {

            when (finishTvType) {

                1 -> {
                    loadNotCycleData()
                }
                2 -> {
                    loadCycleData()
                }
            }

        }

        card_to_set_tv.setOnClickListener(object : OnAvoidDoubleClickListener() {
            override fun onNoDoubleClick(v: View?) {

                this@CardActivity.startActivity(Intent(this@CardActivity, SettingActivity::class.java))

            }

        })

    }

    override fun onItemClicked(index: Int) {

        try {
            if (index != -1) {
                var tempBean = getCardFilterList()[index]

                if (!tempBean.isCut) {

                    WordDetailActivity.startWordDetail(this@CardActivity, tempBean.wordID)
                }
            }

        } catch (e: Exception) {
            LogUtil.errorTypeInfo("点中了无效位置，index is $index")
        }


    }

    override fun removeFirstObjectInAdapter() {

        //这句话必须要有，否则，划完最上面的那个卡片，后面的均无法滑动
        adapter?.remove(0)


    }

    override fun onLeftCardExit(dataObject: Any?) {
        LogUtil.errorTypeInfo("左边划出")

        /**
         * 如果是"一卡多词"形式，这里只能拿到一个bean，且，这个bean，是上面第一个单词
         *
         * 如果是"一卡一词"形式，这里只能拿到一个bean，就是卡片展示的那个bean
         *
         * 右划同理
         */
        setPbCurrentIndex(dataObject as CardBean)
    }

    override fun onRightCardExit(dataObject: Any?) {
        LogUtil.errorTypeInfo("右边划出")
        setPbCurrentIndex(dataObject as CardBean)
    }

    override fun onAdapterAboutToEmpty(itemsInAdapter: Int) {

        LogUtil.errorTypeInfo("itemsInAdapter is $itemsInAdapter")

        if (itemsInAdapter != 0 && !isCutChange) {
            prepareVoiceAbout()
        }

        if (isCutChange) {
            isCutChange = false
        }

        if (type == Constant.Cycle && itemsInAdapter <= 1 && !isLoadingData) {
            //循环模式，处理当前组数据即将到达终点的情况

            word_study_result_ll.visibility = View.VISIBLE


            if (itemsInAdapter == 0) {
                //当前组，最后一张卡片划出去了
                word_study_result_ll.visibility = View.VISIBLE
                finishTvType = 2
            }

        } else if (type == Constant.NotCycle && itemsInAdapter <= 1 && !isLoadingData) {
            //非循环，处理当前组数据即将到达终点的情况

            word_study_result_ll.visibility = View.VISIBLE


            if (itemsInAdapter == 0) {
                //当前组，最后一张卡片划出去了
                word_study_result_ll.visibility = View.VISIBLE
                finishTvType = 1
            } else {
                /**
                 * 当前组，最后一张单词卡片还在。
                 * 注意，上面的判断条件是 <=1，并且，已经区分处理了 ==0 的情况。所以，这里，是最后一张卡片，且还在界面上，没有划出去
                 */
            }

        } else {
            word_study_result_ll.visibility = View.GONE
        }


    }

    override fun onScroll(progress: Float, scrollXProgress: Float) {

        /**
         * 如果用户点击卡片（含卡片空白区域），滑动程度为0，这个时候不应该停止播放。
         * 所以，设置一个小范围，在该范围之内时，不停止读音，超过才停止
         */
        if (Math.abs(progress) > 0.2f || Math.abs(scrollXProgress) > 0.2f) {
            clearVoiceAbout()
        }

    }

    /**
     * 更新卡片学习进度，卡片上单词入库等
     */
    private fun setPbCurrentIndex(bean: CardBean) {

        //找到这个bean，在数据集合中的位置
        var index = dataList.indexOf(bean)

        var x = 0

        if (isPluralWord) {
            while (index < dataList.size && x < 5) {
                dataChangeAndInputDB(dataList.get(index))
                index++
                x++
                wordStudyIndex++
            }

        } else {
            wordStudyIndex++
            dataChangeAndInputDB(dataList.get(index))
        }

        //更新进度
        word_card_pb?.progress = wordStudyIndex

        var newList: MutableList<CardBean> = mutableListOf<CardBean>()

        newList = getCardFilterList()

        adapter?.setAll(newList)

    }

    /**
     * 获取卡片列表数据过滤后的数据
     */
    private fun getCardFilterList(): MutableList<CardBean> {
        return dataList.filter {

            !it.isOutCard

        }.toMutableList()
    }

    //数据转换处理和入库
    private fun dataChangeAndInputDB(bean: CardBean) {

        //表示单词为移出卡片状态
        bean.isOutCard = true

        var word: Word = Word(
            bean.wordID,
            bean.word,
            bean.wordSound,
            if (bean.isCut) 1 else 0,
            1,
            System.currentTimeMillis()

        )
        //在数据库中更新这个单词
        AppDataBase.instance.getWordDao().update(word)

    }

    //准备读音相关的
    private fun prepareVoiceAbout() {

        //开始新的之前，先关闭之前的读音相关
        clearVoiceAbout()

        if (adapter == null || adapter?.getVoiceViews().isNullOrEmpty() || adapter?.getVoiceBeans().isNullOrEmpty() || !isAutoRead) {
            return
        }

        var tempBeans: MutableList<CardBean> = mutableListOf()
        var tempVoiceViews: MutableList<ImageView> = mutableListOf()

        if (isPluralWord) {
            tempBeans = adapter!!.getVoiceBeans().take(5).toMutableList()
            tempVoiceViews = adapter!!.getVoiceViews().take(5).toMutableList()
        } else {
            tempBeans = adapter!!.getVoiceBeans().take(1).toMutableList()
            tempVoiceViews = adapter!!.getVoiceViews().take(1).toMutableList()
        }

        mVoiceViews.addAll(tempVoiceViews)
        mVoiceBeans.addAll(tempBeans)

        mHandler.sendEmptyMessageDelayed(HandlerCode, 0)


    }


    //清理读音相关
    private fun clearVoiceAbout() {
        adapter?.setIsHaveVoice(false)
        readIndex = 0
        stopVoice()
        mHandler.removeMessages(HandlerCode)
        mVoiceViews.clear()
        mVoiceBeans.clear()

    }

    override fun onStop() {
        super.onStop()
        clearVoiceAbout()

    }

    private fun stopVoice() {
        //TODO
        /**
         * 这里应该有停止读音，关闭mediaPlayer等的相关操作
         * 但是我这里没有真实读音，是用的一个简单动画模拟读音，所以，这里先预留一个停止方法
         */
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun handleCardEvent(event: MyEvent) {

        when (event.flag) {

            //处理非循环模式下的数据展示
            MyEvent.HandleNotCycleData -> {

                isLoadingData = false
                //需要开启"允许绘制、布局"否则数据不展示

                if (dataList.isNullOrEmpty()) {
                    //取不到单词了，是最后一组了
                    card_study_group_finish_tv.text = "非循环模式，已经都学完了"
                    finishTvType = 0
                } else {
                    //还有单词，做对应处理

                    //加载数据
                    adapter?.setAll(dataList)

                    wordStudyIndex = todayStudiedNum % needStudyNum

                    word_card_pb?.max = wordStudyIndex + dataList.size

                    word_card_pb?.progress = wordStudyIndex

                    if (residueWordNum <= dataList.size) {
                        //剩余的单词数，小于等于当前取到的单词个数
                        card_study_group_finish_tv.text = "非循环模式，已经都学完了"
                        finishTvType = 0
                    }
                }
            }


            //处理非循环模式下的数据展示
            MyEvent.HandleCycleData -> {

                isLoadingData = false

                if (dataList.isNullOrEmpty()) {
                    //取不到单词了
                    card_study_group_finish_tv.text = "循环模式，单词为空"
                    finishTvType = 0
                } else {
                    adapter?.setAll(dataList)

                    word_card_pb?.max = dataList.size
                    word_card_pb?.progress = 0

                    wordStudyIndex = 0
                }


            }

            //处理单词斩的状态
            MyEvent.HandleWordCutStatus -> {
                isCutChange = true
                clearVoiceAbout()
                adapter?.notifyDataSetChanged()

            }


            //根据设置界面的操作，改变界面展示或读音处理
            MyEvent.HandleSetChange -> {

                isPluralWord = SpUtil.getBooleanValue(Constant.WordCardPluralSetStatus, true)
                isAutoRead = SpUtil.getBooleanValue(Constant.WordCardAutoReadSetStatus, true)

                setPluralHelpViewShow()

                adapter?.setWordStudyType(isPluralWord)
                adapter?.setAll(getCardFilterList())

            }

            MyEvent.ShowOneWordHelpView ->{
                plural_help_view.visibility = View.GONE
                one_help_view.visibility = View.VISIBLE
                one_help_view.reShow()

                LogUtil.errorTypeInfo("计算完成")
            }
        }

    }


}
