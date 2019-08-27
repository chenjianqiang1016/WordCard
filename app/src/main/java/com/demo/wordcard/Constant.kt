package com.demo.wordcard

/**
 * 常数、恒量
 */
class Constant {

    companion object {

        //假设，每天应该学习20个单词。即：学习计划设定了，每天学20个单词
        val needStudyNum = 20

        /**
         * 非循环模式
         */
        val NotCycle: Int = 1
        /**
         * 循环模式
         */
        val Cycle: Int = NotCycle + 1

        /**
         * 单词卡中，用于保存，一卡多词 设置状态标记值的key
         */
        var WordCardPluralSetStatus = "WordCardPluralSetStatus"
        /**
         * 单词卡中，用于保存，自动读音 设置状态标记值的key
         */
        var WordCardAutoReadSetStatus = "WordCardAutoReadSetStatus"

       const val PluralWordLocationData = "PluralWordLocationData"

        const val OneWordLocationData = "OneWordLocationData"


    }


}