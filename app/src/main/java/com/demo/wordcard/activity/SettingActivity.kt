package com.demo.wordcard.activity

import com.demo.wordcard.Constant
import com.demo.wordcard.MyEvent
import com.demo.wordcard.R
import com.demo.wordcard.SpUtil
import com.demo.wordcard.view.MySwitchView
import kotlinx.android.synthetic.main.activity_setting.*
import org.greenrobot.eventbus.EventBus

/**
 * 设置界面
 */
class SettingActivity : BaseActivity() {

    private var currentPluralSet = true

    private var currentAutoRead = true

    override fun getLayout(): Int = R.layout.activity_setting

    override fun initView() {


        currentPluralSet = SpUtil.getBooleanValue(Constant.WordCardPluralSetStatus, true)
        currentAutoRead = SpUtil.getBooleanValue(Constant.WordCardAutoReadSetStatus, true)

        //一卡多词设置
        setSwitchStatus(
            plural_set_swv,
            currentPluralSet
        )

        //自动读音设置
        setSwitchStatus(
            auto_read_swv,
            currentAutoRead
        )

    }

    /**
     * 设置滑动按钮状态
     */
    private fun setSwitchStatus(mySwitchView: MySwitchView, status: Boolean) {

        if (status) {
            mySwitchView.goRight()
        } else {
            mySwitchView.goLeft()
        }

    }

    override fun handleClick() {

        //返回键
        set_back.setOnClickListener {
            finish()
        }


    }

    override fun onDestroy() {

        //保存当前设置的值
        SpUtil.put(Constant.WordCardPluralSetStatus, plural_set_swv.status)
        SpUtil.put(Constant.WordCardAutoReadSetStatus, auto_read_swv.status)

        if (currentPluralSet != plural_set_swv.status || currentAutoRead != auto_read_swv.status) {
            //设置中，有改变，才进行后续处理
            EventBus.getDefault().post(MyEvent(MyEvent.HandleSetChange))
        }


        super.onDestroy()
    }





}
