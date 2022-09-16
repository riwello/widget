package com.example.widget.widget

import android.content.Context
import android.content.res.Resources
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.viewpager2.widget.ViewPager2
import java.math.BigDecimal
import java.text.NumberFormat
import kotlin.math.*

class ArcView : View {
    private val TAG = "ArcView"

    private val paint = Paint().apply {
        color = Color.RED
        style = Paint.Style.STROKE
        strokeWidth = 5f
    }
    private val linePaint = Paint().apply {
        color = Color.RED
        style = Paint.Style.STROKE
        strokeWidth = 1f
    }
    private val textPaint = Paint().apply {
        color = Color.BLACK
//        style = Paint.Style.STROKE
        textSize = 15.toPx().toFloat()
        textAlign = Paint.Align.CENTER
    }

    private var startAngle = -1f //椭圆起始角度
    private var endAngle = -1f //椭圆结束角度
    private var angle = 90f //测试用 运动圆角度
    private var centerCircleAngle = 90f //中间圆角度
    private var leftCircleAngle = -1f //左边圆角度
    private var rightCircleAngle = -1f//右边圆的角度
    private var leftPointX = 0f//静止时 左边点的X坐标
    private var rightPointX = 0f//静止时 右边点的X坐标

    //椭圆实际宽度和控件宽比值
    private val ovalWidthRatio = 1.302f
//    private val ovalWidthRatio = 1f

    //控件高/宽
    private val aspectRatio = .314f

    private val centerCircleRadius = 15f


    private lateinit var ovalRectF: RectF

    private var progress: Float = 0f

    private var maxLevel = 6

    private var position = 1


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
        val height = width * aspectRatio
        val ovalWidth = width * ovalWidthRatio
        val ovalHeight = height - centerCircleRadius
        val start = (width - ovalWidth) / 2
        ovalRectF = RectF(start, -ovalHeight / 2f, ovalWidth + start, ovalHeight)


        initAngle(ovalRectF, width)
        setMeasuredDimension(width, height.toInt())
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawText("${(progress * 100).toInt()}", width / 2f, height / 2f, textPaint)
        canvas.drawArc(ovalRectF, startAngle, endAngle - startAngle, false, paint)
        val leftOffset = (180f - leftCircleAngle) * progress
        val leftAngle = leftCircleAngle + leftOffset
        //左边的点
        if (position > 0) {
            getArcPoint(ovalRectF, leftAngle).run {
                canvas.drawCircle(
                    x, y, centerCircleRadius, paint
                )
                canvas.drawText(
                    "${leftAngle.toInt()} 左${position.minus(1).toString()}",
                    x,
                    y,
                    textPaint
                )
            }
        }

        val centerOffset = (leftCircleAngle - centerCircleAngle) * progress
        val centerAngle = centerCircleAngle + centerOffset
        //中间的点
        getArcPoint(ovalRectF, centerAngle).run {
            canvas.drawCircle(x, y, centerCircleRadius, paint)
            canvas.drawText("${centerAngle.toInt()} 中$position", x, y, textPaint)
        }


        val rightOffset = (centerCircleAngle - rightCircleAngle) * progress
        val rightAngle = rightCircleAngle + rightOffset
//        if (position < maxLevel && rightAngle>= startAngle) {
        //右边的点
        getArcPoint(ovalRectF, rightAngle).run {
            canvas.drawCircle(
                x, y, centerCircleRadius, paint
            )
            canvas.drawText("${rightAngle.toInt()} 右${position.plus(1)}", x, y, textPaint)
        }
//        }


        //右侧外部的点
        val outsideOffset = (rightCircleAngle - startAngle / 2f) * progress
        val outsideAngle = startAngle / 2f + outsideOffset
        if (outsideAngle > startAngle) {
            getArcPoint(ovalRectF, outsideAngle).run {
                canvas.drawCircle(
                    x, y, centerCircleRadius, paint
                )
                canvas.drawText("外${outsideAngle.toInt()} ${position.plus(2)}", x, y, textPaint)
            }
        }



        (width / 2f).let { x -> canvas.drawLine(x, 0f, x, height.toFloat(), linePaint) }
        leftPointX.let { canvas.drawLine(x, 0f, x, height.toFloat(), linePaint) }
        rightPointX.let { canvas.drawLine(x, 0f, x, height.toFloat(), linePaint) }
    }


    private fun getArcPoint(rectF: RectF, angle: Float): PointF {
        val a = rectF.width() / 2f
        val b = rectF.height() / 2f

        if (a == 0f || b == 0f) return PointF(rectF.left, rectF.top)

        //弧度
        val radian = Math.toRadians(angle.toDouble())
        val yc = sin(radian)
        val xc = cos(radian)
        val radio = (a * b) / sqrt((yc * a).pow(2.0) + (xc * b).pow(2.0));

        val ax = radio * xc
        val ay = radio * yc
        val x = rectF.left + a + ax
        val y = rectF.top + b + ay

        return PointF(x.toFloat(), y.toFloat())
    }

    private fun initAngle(rectF: RectF, width: Int) {
        leftPointX= width * 0.2f
        rightPointX = width - leftPointX
        var leftTemp = width.toFloat()
        var rightTemp = width.toFloat()
        val angleRange = 1800
        for (i in 0..angleRange) {
            val angle = i / 10f
            val point = getArcPoint(rectF, angle.toFloat())
//            Log.d(TAG,"angle $angle  point $point")

            if (point.x <= width && startAngle == -1f) {
                startAngle = angle.toInt().toFloat()
            }
            if ((point.x <= 0f && endAngle == -1f) || (i == angleRange && endAngle == -1f)) {
                endAngle = ceil(angle.toDouble()).toFloat()
            }
            leftTemp = min(abs(leftPointX - point.x).also {
                if (it < leftTemp) leftCircleAngle = angle
            }, leftTemp)
            rightTemp = min(abs(rightPointX - point.x).also {
                if (it < rightTemp) rightCircleAngle = angle
            }, rightTemp)
        }
        Log.d(
            TAG,
            "width $width height $height oval $ovalRectF startAngle $startAngle endAngle $endAngle  leftCircleAngle $leftCircleAngle  rightCircleAngle$rightCircleAngle"
        )
    }


    private fun getStartAngle(rectF: RectF, width: Int): Float {
        for (angle in 0..180) {
            val point = getArcPoint(rectF, angle.toFloat())
            if (point.x <= width) {
                return angle.toFloat()
            }
        }
        return 0f
    }

    private fun getEndAngle(rectF: RectF): Float {
        for (angle in 180 downTo 0) {
            val point = getArcPoint(rectF, angle.toFloat())
            if (point.x >= 0) {
                return angle.toFloat()
            }
        }
        return 0f
    }

    fun setAngleProgress(progress: Float) {
        angle = 360f * progress
        invalidate()
    }

    fun setAngle(angle: Float) {
        this.angle = angle
        invalidate()
    }


    private var viewPager: ViewPager2? = null
    private var pageChangeCallback: ViewPager2.OnPageChangeCallback =
        object : ViewPager2.OnPageChangeCallback() {
            var lastOffsetPx = 0
            var state = ViewPager2.SCROLL_STATE_IDLE
            var preState = ViewPager2.SCROLL_STATE_IDLE

            override fun onPageScrolled(
                pos: Int,
                posOffset: Float,
                positionOffsetPixels: Int
            ) {
                super.onPageScrolled(pos, posOffset, positionOffsetPixels)
                /**
                 * center :
                 * progress 0->1
                 * angle 90-> leftAngle
                 *
                 */

//                Log.d(
//                    TAG,
//                    "current${viewPager?.currentItem} position $pos positionOffset $posOffset  positionOffsetPixels $positionOffsetPixels"
//                )


                if (lastOffsetPx < positionOffsetPixels) {
                    //向右滑动
                    progress = posOffset

//                    Log.d(
//                        TAG,
//                        "左滑current${viewPager?.currentItem} position $pos positionOffset $posOffset  positionOffsetPixels $positionOffsetPixels"
//                    )

                } else if (lastOffsetPx > positionOffsetPixels) {
//                    Log.d(
//                        TAG,
//                        "右滑current${viewPager?.currentItem} position $pos positionOffset $posOffset  positionOffsetPixels $positionOffsetPixels"
//                    )
                    //向左
                    progress = posOffset
                } else {
                    progress = 0f
                }
                position = pos
                lastOffsetPx = positionOffsetPixels

                invalidate()
            }

            override fun onPageSelected(pos: Int) {
                super.onPageSelected(position)

                Log.d(TAG, "==============PageSelect  ============ $position")
            }

            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)
                this.state = state
                if (preState != state) {
                    preState
                }
            }
        }

    fun bindViewPager(viewPager2: ViewPager2) {
        this.viewPager = viewPager2
        viewPager2.registerOnPageChangeCallback(pageChangeCallback)
        maxLevel = viewPager2.adapter?.itemCount ?: 0
        position = viewPager2.currentItem
        invalidate()
    }


    fun unbindViewPager() {
        viewPager?.unregisterOnPageChangeCallback(pageChangeCallback)
        viewPager = null
    }
}


/**
 * 时间　: 2021/09/13
 * 作者　: Mr.W
 * 描述　: 界面扩展
 */

fun Int.toPx(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()
fun Int.toDp(): Int = (this / Resources.getSystem().displayMetrics.density).toInt()

// px转sp
fun Int.px2sp(): Float {
    val fontScale = Resources.getSystem().displayMetrics.scaledDensity
    return this / (fontScale + .5f)
}

// sp转px
fun Int.sp2px(): Float {
    val fontScale = Resources.getSystem().displayMetrics.scaledDensity
    return this * fontScale + .5f
}
