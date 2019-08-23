package com.demo.wordcard.util

import android.content.Context
import android.text.TextPaint
import android.util.DisplayMetrics
import android.view.WindowManager
import com.demo.wordcard.Constant
import com.demo.wordcard.MyEvent
import com.demo.wordcard.SpUtil
import com.demo.wordcard.room.AppDataBase
import com.demo.wordcard.room.entity.Word
import com.demo.wordcard.room.entity.WordMean
import org.greenrobot.eventbus.EventBus
import java.util.*
import kotlin.concurrent.thread

class DataUtil {

    companion object {

        /**
         * 初始化单词数据
         */
        fun initWordData() {


            thread {

                //单词列表
                var wordList: MutableList<Word> = mutableListOf()

                //单词释义
                var wordMeanList: MutableList<WordMean> = mutableListOf()

                var random: Random = Random()

                var x = 0

                var meanID = 0

                for (i in 1..53) {

                    var word: Word = Word(
                        wordID = i.toLong(),
                        word = "这是单词 $i",
                        sound = "单词 $i 的读音",
                        wStatus = 0,
                        isStudy = 0,
                        studyTime = 0L
                    )

                    wordList.add(word)

                    //为一个单词，准备多个释义
                    x = random.nextInt(3) + 1

                    for (j in 1..x) {

                        meanID++

                        var additional_mean = ""
                        if (i % 7 == 0) {
                            additional_mean = "附加释义，附加释义"
                        } else if (i % 9 == 0)
                            additional_mean = "附加释义，附加释义，超过一行"
                        else {
                            additional_mean = ""
                        }

                        var wordMean: WordMean = WordMean(
                            meanID.toLong(),//释义的ID
                            i.toLong(),//单词的ID
                            "单词 $i 的释义 $j；" + additional_mean
                        )

                        wordMeanList.add(wordMean)
                    }
                }

                AppDataBase.instance.getWordDao().insertAll(wordList)
                AppDataBase.instance.getWordMeanDao().insertAll(wordMeanList)

                EventBus.getDefault().post(MyEvent(MyEvent.InitDataSuccess))


            }

        }


        /**
         * 获取字符串类型的释义
         *
         * 通过wordID，拿到的是多个"释义对象"，列表中需要用到的是单行展示的字符串
         */
        fun getStringDefinition(wordID: Long): String {

            var means = AppDataBase.instance.getWordMeanDao().getMeansByID(wordID)

            var sb: StringBuilder = StringBuilder()

            if (means != null && means.size > 0) {

                for (i in 0..means.size - 1) {

                    sb.append(means[i].mean)

                    if (i != means.size - 1) {
                        sb.append("；")
                    }
                }
            }

            if (sb.toString().isEmpty()) {
                return ""
            } else {
                return sb.toString()
            }

        }


        /**
         * 计算卡片中数据的坐标
         *
         * 用于点击单词跳转
         */
        fun computeCardDataCoordinates(context: Context) {

            thread {

                //状态栏高度
                var statusBarHeight: Int = 0;
                //获取status_bar_height资源的ID
                var resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android");
                if (resourceId > 0) {
                    //根据资源ID获取响应的尺寸值
                    statusBarHeight = context.resources.getDimensionPixelSize(resourceId);
                }

                //屏幕宽度。
                val sWidth = getScreenWidth(context).toInt()

                //屏幕高度。应用部分高度，除去状态栏的部分。sHeight = title + 卡片 + 剩余其他部分
                val sHeight = getScreenHeight(context).toInt()

                /**
                 * 特别说明
                 *
                 * 我用的是华为mate20  2244x1080
                 *
                 * 经过上面的计算
                 * statusBarHeight = 81
                 * sWidth = 1080.0
                 * sHeight = 2163.0
                 *
                 * 因为：statusBarHeight + sHeight = 2244
                 * 所以，这里获取到的 sHeight，是不包含状态栏的屏幕高度
                 */

                /**
                 * 计算卡片页，title的高度。
                 * 注：这里，只关心 卡片页 的。其他页，不考虑
                 */
                var titleHeight = dp2px(context, 50f)

                /**
                 * 卡片高度。
                 * 详见：layout 下 activity_card
                 */
                val cardHeight = dp2px(context, 485f)

                //剩余区域总高度
                val residueHeight: Int = sHeight - titleHeight - cardHeight

                /**
                 * 参考：
                 * activity_card 布局得知，卡片控件SwipeFlingAdapterView 占的空间，是界面上除 title 以外的部分（不含状态栏）
                 * item_swipe_card_view 布局得知，权重 上面：下面 = 2：4
                 *
                 * 这里，拿到上面部分的高度
                 */
                val topResidueHeight = residueHeight / 6 * 2

                val fullItemHeight = dp2px(context, 90f)

                /**
                 * 以下，开始计算单词对应热区。
                 */

                var lineHeight: Int = dp2px(context, 1f)

                //第一个item顶部，距离屏幕顶部的距离。这里要加上状态栏，因为 onTouch 中，rawY，是包含状态栏的
                var location_1: Int = statusBarHeight + titleHeight + topResidueHeight

                //第二个item顶部（第一个item底部），距离屏幕顶部的距离
                var location_2: Int = location_1 + dp2px(context, 15f) + fullItemHeight

                //第三个item顶部（第二个item底部），距离屏幕顶部的距离
                var location_3: Int = location_2 + fullItemHeight + lineHeight

                //第四个item顶部（第三个item底部），距离屏幕顶部的距离
                var location_4: Int = location_3 + fullItemHeight + lineHeight

                //第五个item顶部（第四个item底部），距离屏幕顶部的距离
                var location_5: Int = location_4 + fullItemHeight + lineHeight

                //第五个item底部，距离屏幕顶部的距离
                var location_6: Int = location_5 + +dp2px(context, 15f) + fullItemHeight

                //单词距离屏幕左边的距离。参考布局得知：15+20+14
                var location_ws = dp2px(context, (30 + 15 + 20 + 15).toFloat())

                //单词距离屏幕右边的距离。
                var location_we = sWidth - dp2px(context, (30 + 15 + 40).toFloat())

                //记录一卡多词需要的定位数据
                var sb: StringBuilder = StringBuilder()

                sb.append(statusBarHeight)
                sb.append("-")
                sb.append(location_ws)
                sb.append("-")
                sb.append(location_we)
                sb.append("-")
                sb.append(location_1)
                sb.append("-")
                sb.append(location_2)
                sb.append("-")
                sb.append(location_3)
                sb.append("-")
                sb.append(location_4)
                sb.append("-")
                sb.append(location_5)
                sb.append("-")
                sb.append(location_6)

                SpUtil.put(Constant.PluralWordLocationData, sb.toString())

            }


        }

        /**
         * 计算一卡一词坐标
         *
         * word：单词
         * meanList：释义集合
         */
        fun computeOneCardOneWordCoordinates(
            context: Context,
            word: String,
            meanList: MutableList<WordMean>,
            positopn: Int
        ) {

            thread {

                var startTime = System.currentTimeMillis()

                //状态栏高度
                var statusBarHeight: Int = 0;
                //获取status_bar_height资源的ID
                var resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android");
                if (resourceId > 0) {
                    //根据资源ID获取响应的尺寸值
                    statusBarHeight = context.resources.getDimensionPixelSize(resourceId);
                }

                //屏幕宽度。
                val sWidth = getScreenWidth(context).toInt()

                //屏幕高度。应用部分高度，除去状态栏的部分。sHeight = title + 卡片 + 剩余其他部分
                val sHeight = getScreenHeight(context).toInt()

                /**
                 * 特别说明
                 *
                 * 我用的是华为mate20  2244x1080
                 *
                 * 经过上面的计算
                 * statusBarHeight = 81
                 * sWidth = 1080.0
                 * sHeight = 2163.0
                 *
                 * 因为：statusBarHeight + sHeight = 2244
                 * 所以，这里获取到的 sHeight，是不包含状态栏的屏幕高度
                 */

                /**
                 * 计算卡片页，title的高度。
                 * 注：这里，只关心 卡片页 的。其他页，不考虑
                 */
                var titleHeight = dp2px(context, 50f)

                /**
                 * 卡片高度。
                 * 详见：layout 下 activity_card
                 */
                val cardHeight = dp2px(context, 485f)

                //剩余区域总高度
                val residueHeight: Int = sHeight - titleHeight - cardHeight

                /**
                 * 参考：
                 * activity_card 布局得知，卡片控件SwipeFlingAdapterView 占的空间，是界面上除 title 以外的部分（不含状态栏）
                 * item_swipe_card_view 布局得知，权重 上面：下面 = 2：4
                 *
                 * 这里，拿到上面部分的高度
                 */
                val topResidueHeight = residueHeight / 6 * 2

                var h1 = statusBarHeight + titleHeight + topResidueHeight + dp2px(context, 170f)

                //设置一个画笔，用于后面文字的测量
                val textPaint = TextPaint()

                //卡片中，word的文字大小为22dp
                textPaint.textSize = dp2px(context, 22f).toFloat()

                var ascent = textPaint.ascent()
                var descent = textPaint.descent()

                //下坡度-上坡度，得到较准确的文字高度。（单行）
                val wordHeight = descent - ascent

                //单词的宽度
                var wordWidth = textPaint.measureText(word)

                LogUtil.errorTypeInfo("wordWidth is $wordWidth")
                LogUtil.errorTypeInfo("word is $word")

                var w1 = sWidth / 2 - wordWidth / 2

                var w2 = w1 + wordWidth

                //卡片中，word中文释义的文字大小为14dp
                textPaint.textSize = dp2px(context, 14f).toFloat()

                ascent = textPaint.ascent()
                descent = textPaint.descent()

                var h2 = h1 + wordHeight

                //释义的高度（单行）
                val meanHeight = descent - ascent

                var meanNum = meanList.size

                //多行的时候，行与行之间有间距，就根据释义个数，多补几个2dp，单行也可以补，视情况而定
                var h3 = h1 + wordHeight + dp2px(context, 12f) + meanHeight * meanNum + dp2px(context, 2f) * meanNum

                //最大展示释义的宽度
                var maxMeanWidth = sWidth - dp2px(context, 100f)

                //释义文字的宽度
                var meanWidth: Float = 0f

                //拿到最长的那个宽度
                for (i in 0..meanNum - 1) {

                    var tempMeanWidth = textPaint.measureText(meanList[i].mean)

                    if (tempMeanWidth >= meanWidth) {
                        meanWidth = tempMeanWidth
                    }

                }

                //单词释义超过最大释义宽度，就用最大释义宽度
                if (meanWidth > maxMeanWidth) {
                    meanWidth = maxMeanWidth.toFloat()
                }

                var w3 = sWidth / 2 - meanWidth / 2

                var w4 = w3 + meanWidth

                var sb: StringBuilder = StringBuilder()

                sb.append(statusBarHeight)
                sb.append("-")
                sb.append(h1)
                sb.append("-")
                sb.append(h2)
                sb.append("-")
                sb.append(h3)
                sb.append("-")
                sb.append(w1)
                sb.append("-")
                sb.append(w2)
                sb.append("-")
                sb.append(w3)
                sb.append("-")
                sb.append(w4)

                SpUtil.put(Constant.OneWordLocationData + "_$positopn", sb.toString())

                LogUtil.errorTypeInfo("计算用时 ${System.currentTimeMillis() - startTime}")

                EventBus.getDefault().post(MyEvent(MyEvent.ShowOneWordHelpView))

            }


        }


        fun dp2px(context: Context, dpValue: Float): Int {

            var scale: Float = context.resources.displayMetrics.density

            return (dpValue * scale + 0.5f).toInt()

        }

        /**
         * 屏幕高度
         *
         * @param context
         * @return
         */
        fun getScreenHeight(context: Context): Float {
            return getDisplayMetrics(context).heightPixels.toFloat()
        }

        /**
         * 屏幕宽度
         *
         * @param context
         * @return
         */
        fun getScreenWidth(context: Context): Float {
            return getDisplayMetrics(context).widthPixels.toFloat()
        }

        fun getDisplayMetrics(context: Context): DisplayMetrics {

            val displaymetrics = DisplayMetrics()

            (context.getSystemService(
                Context.WINDOW_SERVICE
            ) as WindowManager).defaultDisplay.getMetrics(
                displaymetrics
            )
            return displaymetrics
        }


    }


}