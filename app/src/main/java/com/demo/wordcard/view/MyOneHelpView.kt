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

class MyOneHelpView : View {

    private var paint: Paint by Delegates.notNull()


    private var statusBarHeight: Float = 0f
    private var h1: Float = 0f
    private var h2: Float = 0f
    private var h3: Float = 0f

    private var w1: Float = 0f
    private var w2: Float = 0f
    private var w3: Float = 0f
    private var w4: Float = 0f

    private var viewWidth: Int = 0
    private var viewHeight: Int = 0

    private var isError: Boolean = false

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {

        init()

    }

    private fun init() {

        try {
            isError = false

            paint = Paint(Paint.ANTI_ALIAS_FLAG)

            paint?.style = Paint.Style.STROKE
            paint?.strokeWidth = 1f

            var locations = SpUtil.getStringValue(Constant.OneWordLocationData+"_0")

            var ls = locations?.split("-")

            if (ls.isNullOrEmpty().not()) {

                statusBarHeight = ls!![0].toFloat()

                h1 = ls!![1].toFloat() - statusBarHeight
                h2 = ls!![2].toFloat() - statusBarHeight
                h3 = ls!![3].toFloat() - statusBarHeight

                w1 = ls!![4].toFloat()
                w2 = ls!![5].toFloat()
                w3 = ls!![6].toFloat()
                w4 = ls!![7].toFloat()
            }

            isError = false

        } catch (e: Exception) {
            isError = true
        }

    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        viewWidth = w

        viewHeight = h
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        if (isError) {
            return
        }

        paint?.color = Color.RED

        canvas?.drawLine(0f, h1, viewWidth.toFloat(), h1, paint)
        canvas?.drawLine(0f, h2, viewWidth.toFloat(), h2, paint)
        canvas?.drawLine(0f, h3, viewWidth.toFloat(), h3, paint)

        canvas?.drawLine(w1, 0f, w1, viewHeight.toFloat(), paint)
        canvas?.drawLine(w2, 0f, w2, viewHeight.toFloat(), paint)

        paint?.color = Color.BLACK

        canvas?.drawLine(w3, 0f, w3, viewHeight.toFloat(), paint)
        canvas?.drawLine(w4, 0f, w4, viewHeight.toFloat(), paint)

    }

    fun reShow() {
        init()
        postInvalidate()
    }


}