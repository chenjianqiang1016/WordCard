package com.demo.wordcard.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.demo.wordcard.Constant
import com.demo.wordcard.SpUtil
import kotlin.properties.Delegates

class MyPluralHelpView : View {

    private var paint: Paint by Delegates.notNull()


    private var statusBarHeight: Float = 0f
    private var location_ws: Float = 0f
    private var location_we: Float = 0f

    private var location_1: Float = 0f
    private var location_2: Float = 0f
    private var location_3: Float = 0f
    private var location_4: Float = 0f
    private var location_5: Float = 0f
    private var location_6: Float = 0f

    private var viewWidth: Int = 0
    private var viewHeight: Int = 0

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {

        init()

    }

    private fun init() {


        paint = Paint(Paint.ANTI_ALIAS_FLAG)

        paint?.style = Paint.Style.STROKE
        paint?.strokeWidth = 1f
        paint?.color = Color.RED

        var locations = SpUtil.getStringValue(Constant.PluralWordLocationData)

        var ls = locations?.split("-")

        if (ls.isNullOrEmpty().not()) {

            statusBarHeight = ls!![0].toFloat()

            location_ws = ls!![1].toFloat()
            location_we = ls!![2].toFloat()

            //控件的展示区域，不包含状态栏，但是之前计算的时候，已经算进去了，这里要减掉
            location_1 = ls!![3].toFloat()-statusBarHeight
            location_2 = ls!![4].toFloat()-statusBarHeight
            location_3 = ls!![5].toFloat()-statusBarHeight
            location_4 = ls!![6].toFloat()-statusBarHeight
            location_5 = ls!![7].toFloat()-statusBarHeight
            location_6 = ls!![8].toFloat()-statusBarHeight
        }

    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        viewWidth = w

        viewHeight = h
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)


        canvas?.drawLine(location_ws, 0f, location_ws,viewHeight.toFloat(),paint)
        canvas?.drawLine(location_we, 0f, location_we,viewHeight.toFloat(),paint)

        canvas?.drawLine(0f, location_1, viewWidth.toFloat(),location_1,paint)
        canvas?.drawLine(0f, location_2, viewWidth.toFloat(),location_2,paint)
        canvas?.drawLine(0f, location_3, viewWidth.toFloat(),location_3,paint)
        canvas?.drawLine(0f, location_4, viewWidth.toFloat(),location_4,paint)
        canvas?.drawLine(0f, location_5, viewWidth.toFloat(),location_5,paint)
        canvas?.drawLine(0f, location_6, viewWidth.toFloat(),location_6,paint)



    }


}