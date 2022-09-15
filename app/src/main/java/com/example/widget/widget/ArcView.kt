package com.example.widget.widget

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View
import java.lang.Math.pow
import java.lang.Math.sin
import kotlin.math.cos
import kotlin.math.sqrt

class ArcView : View {

    private val paint = Paint().apply {
        color = Color.RED
        style = Paint.Style.STROKE
        strokeWidth = 5f
    }
    private val rate = 4.1418f

    private var angle = 90f

    //椭圆实际宽度和控件宽比值
    private val ovalWidthRatio = 1.302f
    //控件高/宽
    private val aspectRatio= .314f

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)
        setMeasuredDimension(width, height)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val ovalWidth = width *ovalWidthRatio
        val ovalHeight = ovalWidth / rate
        val start = (width - ovalWidth) / 2
        val oval = RectF(start, 100f, ovalWidth + start, ovalHeight + 100f)
        canvas.drawArc(oval, 0f, 180f, false, paint);
//        canvas.drawRect(oval,paint)
        Log.d("ArcView", " angle $angle")
        getArcPoint(oval, angle).run { canvas.drawCircle(x, y, 15f, paint) }

        canvas.drawLine(width / 2f, 0f, width / 2f, height.toFloat(), paint)

    }

    fun getArcPoint(rectF: RectF, angle: Float): PointF {
        val a = rectF.width() / 2f
        val b = rectF.height() / 2f

        if (a == 0f || b == 0f) return PointF(rectF.left, rectF.top)

        //弧度
        val radian = Math.toRadians(angle.toDouble())
        val yc = sin(radian)
        val xc = cos(radian)
        val radio = (a * b) / sqrt(pow(yc * a, 2.0) + pow(xc * b, 2.0));

        val ax = radio * xc
        val ay = radio * yc
        val x = rectF.left + a + ax
        val y = rectF.top + b + ay
        return PointF(x.toFloat(), y.toFloat())

    }

    fun setAngleProgress(progress: Float) {
        angle = 360f * progress
        invalidate()
    }

}