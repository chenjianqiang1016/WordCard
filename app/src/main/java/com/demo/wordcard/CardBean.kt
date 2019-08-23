package com.demo.wordcard

//卡片的bean
data class CardBean(var wordID:Long,var word:String,var wordSound:String,var isOutCard:Boolean = false,var isCut:Boolean=false) {
}