package com.demo.wordcard.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.demo.wordcard.MyEvent
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

abstract class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayout())
        //隐藏titleBar
        supportActionBar?.hide()

        EventBus.getDefault().register(this)

        initView()

        handleClick()

    }

    abstract fun getLayout():Int


    abstract fun initView()

    //处理点击事件
    abstract fun handleClick()


    override fun onDestroy() {

        EventBus.getDefault().unregister(this)

        super.onDestroy()
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun handleBaseEvent(event: MyEvent) {

    }

}
