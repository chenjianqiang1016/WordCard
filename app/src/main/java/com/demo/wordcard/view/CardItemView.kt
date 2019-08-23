package com.demo.wordcard.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.demo.wordcard.CardBean
import com.demo.wordcard.R
import com.demo.wordcard.util.DataUtil
import kotlinx.android.synthetic.main.view_card_item.view.*

/**
 * 卡片中，一个单词item的布局
 */
class CardItemView : LinearLayout {

    private var mContext: Context? = null

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {

        mContext = context

        init()
    }

    private fun init() {

        var rootView = LayoutInflater.from(mContext).inflate(R.layout.view_card_item, this)

    }

    fun setData(bean: CardBean) {

        view_card_item_word.text = bean.word
        view_card_item_mean.text = DataUtil.getStringDefinition(bean.wordID)

    }


    //返回展示单词的TextView
    fun getWordView(): TextView {
        return view_card_item_word
    }

    //返回释义的TextView
    fun getMeanView(): TextView {
        return view_card_item_mean
    }

    //返回播放语言的ImageView
    fun getVoiceView(): ImageView {
        return view_card_item_voice
    }

    //返回 斩 控件
    fun getCutImgView(): ImageView {
        return view_card_item_cut
    }

}