package com.demo.wordcard.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.demo.wordcard.CardBean
import com.demo.wordcard.MyEvent
import com.demo.wordcard.R
import com.demo.wordcard.room.AppDataBase
import com.demo.wordcard.util.DataUtil
import com.demo.wordcard.util.LogUtil
import com.demo.wordcard.util.StringCheckUtil
import com.demo.wordcard.util.VoicePlayAnimUtil
import com.demo.wordcard.view.CardItemView
import com.demo.wordcard.view.ImaginaryLineView
import org.greenrobot.eventbus.EventBus


class WordCardAdapter(val context: Context, val list: MutableList<CardBean>) :
    BaseAdapter() {

    //是否是"一卡多词"模式
    private var isPluralWord: Boolean = true

    private var isHaveVoice = false

    private var voiceViews: MutableList<ImageView> = mutableListOf()
    private var voiceBeans: MutableList<CardBean> = mutableListOf()

    //单词学习类型。即：是否是一卡多词
    fun setWordStudyType(isPluralWord: Boolean) {
        this.isPluralWord = isPluralWord
    }

    //是否正在自动读音过程中
    fun setIsHaveVoice(isHaveVoice: Boolean) {
        this.isHaveVoice = isHaveVoice

    }

    //设置新数据的方法
    fun setAll(dataList: MutableList<CardBean>) {
        list.clear()
        list.addAll(dataList)
        this.notifyDataSetChanged()
    }

    fun remove(index: Int) {
        if (index > -1 && index < list.size) {
            list.removeAt(index)
            notifyDataSetChanged()
        }
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {

        /**
         *
         * 算法说明：切换、展示的时候，从当前单词的位置开始算
         *
         * 如：当前组有20个单词（0-19）
         *
         * 一开多词情况下：划出1张，即，学了0-4，当前展示第二张，5-9。
         *
         * 然后切换一卡一词，划出2张，即，学了5，6。当前展示的7（因为6划出去了）
         *
         * 再切换回一卡多词，当前卡片上，展示的是：7，8，9，10，11 这5个单词。而不是 5，6，7，8，9
         *
         *
         * 一卡多词。
         *
         *      当前单词集合的长度%5，是当前一共的"张"数
         *
         *      如果这个值不为0，就要再多加一张，用于承载最后剩余的单词
         *
         */

        var realWordCount = list.size

        if (isPluralWord) {
            //一卡多词模式
            if (realWordCount % 5 == 0) {
                return realWordCount / 5
            } else {
                return realWordCount / 5 + 1
            }

        } else {
            return realWordCount
        }

    }

    override fun getItem(position: Int): Any {

        return list.get(position)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        LogUtil.errorTypeInfo("position is $position")

        if (position == 0) {
            voiceViews.clear()
            voiceBeans.clear()
        }

        var holder: MyViewHolder
        var v: View
        if (convertView == null) {
            v = LayoutInflater.from(context).inflate(R.layout.item_swipe_card_view, parent, false)

            holder = MyViewHolder(v)
            v.tag = holder

        } else {
            v = convertView
            holder = v.tag as MyViewHolder
        }

        if (isPluralWord) {
            //一卡多词

            holder.swipe_card_plural_ll.visibility = View.VISIBLE
            holder.swipe_card_single_rl.visibility = View.GONE

            //第一个单词
            try {

                var bean: CardBean = list.get(position * 5)

                holder.swipe_card_item_word_1.visibility = View.VISIBLE
                holder.swipe_card_item_word_1.setData(bean)

                focusReceiveView(
                    holder.swipe_card_item_word_1,
                    holder.swipe_card_item_word_1.getWordView(),
                    holder.swipe_card_item_word_1.getMeanView(),
                    holder.swipe_card_item_word_1.getVoiceView(),
                    holder.swipe_card_item_word_1.getCutImgView(),
                    bean
                )

            } catch (e: Exception) {
                holder.swipe_card_item_word_1.visibility = View.INVISIBLE
                holder.swipe_card_item_line_1.visibility = View.INVISIBLE
            }

            //第二个单词
            try {
                var bean: CardBean = list.get(position * 5 + 1)

                holder.swipe_card_item_word_2.visibility = View.VISIBLE
                holder.swipe_card_item_word_2.setData(bean)

                focusReceiveView(
                    holder.swipe_card_item_word_2,
                    holder.swipe_card_item_word_2.getWordView(),
                    holder.swipe_card_item_word_2.getMeanView(),
                    holder.swipe_card_item_word_2.getVoiceView(),
                    holder.swipe_card_item_word_2.getCutImgView(),
                    bean
                )

            } catch (e: Exception) {
                holder.swipe_card_item_word_2.visibility = View.INVISIBLE
                holder.swipe_card_item_line_2.visibility = View.INVISIBLE
            }

            //第三个单词
            try {
                var bean: CardBean = list.get(position * 5 + 2)

                holder.swipe_card_item_word_3.visibility = View.VISIBLE
                holder.swipe_card_item_word_3.setData(bean)

                focusReceiveView(
                    holder.swipe_card_item_word_3,
                    holder.swipe_card_item_word_3.getWordView(),
                    holder.swipe_card_item_word_3.getMeanView(),
                    holder.swipe_card_item_word_3.getVoiceView(),
                    holder.swipe_card_item_word_3.getCutImgView(),
                    bean
                )

            } catch (e: Exception) {
                holder.swipe_card_item_word_3.visibility = View.INVISIBLE
                holder.swipe_card_item_line_3.visibility = View.INVISIBLE
            }

            //第四个单词
            try {
                var bean: CardBean = list.get(position * 5 + 3)

                holder.swipe_card_item_word_4.visibility = View.VISIBLE
                holder.swipe_card_item_word_4.setData(bean)

                focusReceiveView(
                    holder.swipe_card_item_word_4,
                    holder.swipe_card_item_word_4.getWordView(),
                    holder.swipe_card_item_word_4.getMeanView(),
                    holder.swipe_card_item_word_4.getVoiceView(),
                    holder.swipe_card_item_word_4.getCutImgView(),
                    bean
                )

            } catch (e: Exception) {
                holder.swipe_card_item_word_4.visibility = View.INVISIBLE
                holder.swipe_card_item_line_4.visibility = View.INVISIBLE
            }

            //第五个单词
            try {
                var bean: CardBean = list.get(position * 5 + 4)

                holder.swipe_card_item_word_5.visibility = View.VISIBLE
                holder.swipe_card_item_word_5.setData(bean)

                focusReceiveView(
                    holder.swipe_card_item_word_5,
                    holder.swipe_card_item_word_5.getWordView(),
                    holder.swipe_card_item_word_5.getMeanView(),
                    holder.swipe_card_item_word_5.getVoiceView(),
                    holder.swipe_card_item_word_5.getCutImgView(),
                    bean
                )

            } catch (e: Exception) {
                holder.swipe_card_item_word_5.visibility = View.INVISIBLE
            }


        } else {
            //一卡一词 模式

            var bean: CardBean = list.get(position)

            holder.swipe_card_plural_ll.visibility = View.GONE
            holder.swipe_card_single_rl.visibility = View.VISIBLE

            holder.swipe_card_single_word.text = bean.word

            //这个单词下，所有的释义
            var means = AppDataBase.instance.getWordMeanDao().getMeansByID(bean.wordID)

            if (means.isNullOrEmpty().not()) {

                var sb: StringBuilder = StringBuilder()

                for (i in 0..means.size - 1) {

                    sb.append(means[i].mean)

                    if (i != means.size - 1) {
                        sb.append(System.lineSeparator())
                    }
                }

                holder.swipe_card_single_mean.text = sb.toString()

            }

            focusReceiveView(
                holder.swipe_card_item_single_cl,
                holder.swipe_card_single_word,
                holder.swipe_card_single_mean,
                holder.swipe_card_single_voice,
                holder.swipe_card_single_cut,
                bean
            )

            //去计算坐标相关
            DataUtil.computeOneCardOneWordCoordinates(context, bean.word, means,position)


        }

        return v
    }

    private inner class MyViewHolder(itemView: View) {

        //复数单词布局
        var swipe_card_plural_ll: LinearLayout = itemView.findViewById(R.id.swipe_card_plural_ll) as LinearLayout

        //第一个单词的布局
        var swipe_card_item_word_1: CardItemView =
            itemView.findViewById(R.id.swipe_card_item_word_1) as CardItemView

        //第一个、第二个单词之间的分隔虚线
        var swipe_card_item_line_1: ImaginaryLineView =
            itemView.findViewById(R.id.swipe_card_item_line_1) as ImaginaryLineView

        //第二个单词
        var swipe_card_item_word_2: CardItemView =
            itemView.findViewById(R.id.swipe_card_item_word_2) as CardItemView

        //第二个、第三个单词之间的分隔虚线
        var swipe_card_item_line_2: ImaginaryLineView =
            itemView.findViewById(R.id.swipe_card_item_line_2) as ImaginaryLineView

        //第三个单词
        var swipe_card_item_word_3: CardItemView =
            itemView.findViewById(R.id.swipe_card_item_word_3) as CardItemView

        //第三个、第四个单词之间的分隔虚线
        var swipe_card_item_line_3: ImaginaryLineView =
            itemView.findViewById(R.id.swipe_card_item_line_3) as ImaginaryLineView

        //第四个单词
        var swipe_card_item_word_4: CardItemView =
            itemView.findViewById(R.id.swipe_card_item_word_4) as CardItemView

        //第四个、第五个单词之间的分隔虚线
        var swipe_card_item_line_4: ImaginaryLineView =
            itemView.findViewById(R.id.swipe_card_item_line_4) as ImaginaryLineView

        //第五个单词
        var swipe_card_item_word_5: CardItemView =
            itemView.findViewById(R.id.swipe_card_item_word_5) as CardItemView

        //独立单词布局
        var swipe_card_single_rl: RelativeLayout = itemView.findViewById(R.id.swipe_card_single_rl) as RelativeLayout

        var swipe_card_item_single_cl: ConstraintLayout =
            itemView.findViewById(R.id.swipe_card_item_single_cl) as ConstraintLayout
        var swipe_card_single_word: TextView = itemView.findViewById(R.id.swipe_card_single_word) as TextView
        var swipe_card_single_mean: TextView = itemView.findViewById(R.id.swipe_card_single_mean) as TextView
        var swipe_card_single_voice: ImageView = itemView.findViewById(R.id.swipe_card_single_voice) as ImageView

        var swipe_card_single_cut: ImageView = itemView.findViewById(R.id.swipe_card_single_cut) as ImageView

    }

    //集中接收控件和Bean，用于后面统一处理
    private fun focusReceiveView(
        cardItemView: View,
        wordView: TextView,
        meanView: TextView,
        voiceView: ImageView,
        cutView: ImageView,
        bean: CardBean
    ) {

        voiceViews.add(voiceView)
        voiceBeans.add(bean)


        //处理斩的点击事件
        handleCut(cutView, bean)

        //处理展示样式
        showViewStyle(wordView, meanView, voiceView, bean)

        //处理发音
        handlePlaySound(voiceView, bean)

    }

    //处理 斩 的点击事件
    private fun handleCut(cutView: ImageView, bean: CardBean) {

        cutView.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {

                bean.isCut = !bean.isCut

                EventBus.getDefault()
                    .post(MyEvent(MyEvent.HandleWordCutStatus))

            }

        })
    }

    //展示控件样式
    private fun showViewStyle(
        wordView: TextView,
        meanView: TextView,
        voiceView: ImageView,
        bean: CardBean
    ) {

        if (bean.isCut) {

            wordView.setTextColor(ContextCompat.getColor(context, R.color.color_ccc))
            meanView.setTextColor(ContextCompat.getColor(context, R.color.color_ccc))
            voiceView.setImageResource(R.mipmap.ic_voice_unread)

        } else {

            wordView.setTextColor(ContextCompat.getColor(context, R.color.color_black))
            meanView.setTextColor(ContextCompat.getColor(context, R.color.color_black))
            voiceView.setImageResource(R.mipmap.ic_voice_2)

        }

    }

    //处理发音
    private fun handlePlaySound(
        voiceView: ImageView,
        bean: CardBean
    ) {

        voiceView.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {

                if (!bean.isCut && !StringCheckUtil.checkStringIsNull(bean.wordSound)) {

                    if (isHaveVoice) {
                        //有读音，正在进行自动读音播放

                        EventBus.getDefault()
                            .post(MyEvent(MyEvent.HandleCardVoice, obj = bean))

                    } else {
                        //自动读音已经结束，点击后，单独处理这个单词的读音
                        Toast.makeText(context, bean.wordSound, Toast.LENGTH_SHORT).show()
                        VoicePlayAnimUtil.playVoice(voiceView)
                    }

                }
            }

        })

    }


    fun getVoiceViews(): MutableList<ImageView> {
        return voiceViews
    }

    fun getVoiceBeans(): MutableList<CardBean> {
        return voiceBeans
    }

}