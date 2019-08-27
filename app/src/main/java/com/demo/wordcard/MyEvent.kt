package com.demo.wordcard

class MyEvent {


    companion object {
        //数据准备完成
        val InitDataSuccess = "initDataSuccess"

        //展示所有单词
        val ShowAllWord = "showAllWord"

        //展示所有单词
        val ShowCutWord = "ShowCutWord"

        //单词学习状态
        val WordStatus = "WordStatus"

        //处理单词斩的状态
        val HandleWordCutStatus = "HandleWordCutStatus"

        //处理读音
        val HandleCardVoice = "HandleCardVoice"

        //卡片开始自动读音
        val CardStartAutoVoice = "cardStartAutoVoice"

        //处理非循环类型数据
        val HandleNotCycleData = "HandleNotCycleData"

        //处理循环类型数据
        val HandleCycleData = "HandleCycleData"

        //处理设置的改变
        var HandleSetChange = "HandleSetChange"

        var ShowOneWordHelpView = "ShowOneWordHelpView"

    }

    var flag = ""

    //描述信息
    var describe: String = ""

    var obj:Any? = null

    constructor(flag: String, desc: String = "",obj: Any? = null) {
        this.flag = flag
        this.describe = desc
    }



}