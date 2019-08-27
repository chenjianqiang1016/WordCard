package com.demo.wordcard

//卡片的bean
data class CardBean(
    var wordID:Long,//单词ID
    var word:String,//单词
    var wordSound:String,//单词声音
    var isOutCard:Boolean = false,//是否划出卡片
    var isCut:Boolean=false//是否被斩
) {
}