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
    private var angleDivider = 60f

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

    inner class LevelPoint(
        val position: Int,
        var angle: Float
    )


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
//        startAngle = getStartAngle(ovalRectF, width)
//        endAngle = getEndAngle(ovalRectF)

        initAngle(ovalRectF, width)
        setMeasuredDimension(width, height.toInt())
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.drawArc(ovalRectF, startAngle, endAngle - startAngle, false, paint)
        val leftOffset = angleDivider / 1.5f * progress
        //左边的点
        if (position > 0 && leftCircleAngle + leftOffset <= endAngle) {
            getArcPoint(ovalRectF, leftCircleAngle + leftOffset).run {
                canvas.drawCircle(
                    x, y, centerCircleRadius, paint
                )
                canvas.drawText(position.minus(1).toString(), x, y, textPaint)
            }
        }

        val centerOffset = angleDivider * progress
        Log.d(TAG, "center offset $centerOffset  angleDivider $angleDivider progress $progress")
        //中间的点
        getArcPoint(ovalRectF, centerCircleAngle + centerOffset).run {
            canvas.drawCircle(x, y, centerCircleRadius, paint)
            canvas.drawText(position.toString(), x, y, textPaint)
        }


        val rightOffset = angleDivider / 1.5f * progress
        if (position < maxLevel && rightCircleAngle + centerCircleAngle >= startAngle) {
            //右边的点
            getArcPoint(ovalRectF, rightCircleAngle + rightOffset).run {
                canvas.drawCircle(
                    x, y, centerCircleRadius, paint
                )
                canvas.drawText(position.plus(1).toString(), x, y, textPaint)
            }
        }

//        getArcPoint(ovalRectF, angle).run {
//            canvas.drawCircle(x, y, centerCircleRadius, paint)
//            canvas.drawText(position.toString(),x,y,textPaint)
//        }

//        (width / 2f).let { x -> canvas.drawLine(x, 0f, x, height.toFloat(), paint) }
//        (width / 4f).let { x -> canvas.drawLine(x, 0f, x, height.toFloat(), paint) }
//        (width * 3 / 4f).let { x -> canvas.drawLine(x, 0f, x, height.toFloat(), paint) }


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
        val leftCircleX = width / 4f
        val rightCircleX = width * 3 / 4f
        var leftTemp = width.toFloat()
        var rightTemp = width.toFloat()
        val count = 1800f
        for (i in 0..1800) {
            val angle = i / 10f
            val point = getArcPoint(rectF, angle.toFloat())
//            Log.d(TAG,"angle $angle  point $point")

            if (point.x <= width && startAngle == -1f) {
                startAngle = angle.toInt().toFloat()
            }
            if ((point.x <= 0f && endAngle == -1f) || (i == 1800 && endAngle == -1f)) {

                endAngle = ceil(angle.toDouble()).toFloat()
            }
            leftTemp = min(abs(leftCircleX - point.x).also {
                if (it > leftTemp && leftCircleAngle == -1f) leftCircleAngle = angle.toFloat()
            }, leftTemp)
            rightTemp = min(abs(rightCircleX - point.x).also {
                if (it > rightTemp && rightCircleAngle == -1f) rightCircleAngle = angle.toFloat()
            }, rightTemp)
        }
        angleDivider = (centerCircleAngle - rightCircleAngle)
        Log.d(
            TAG,
            "width $width height $height oval $ovalRectF startAngle $startAngle endAngle $endAngle  leftCircleAngle $leftCircleAngle  rightCircleAngle$rightCircleAngle"
        )
//        centerOffsetRangeAngle = leftCircleAngle - centerCircleAngle
//        leftOffsetRangeAngle = endAngle - leftCircleAngle
//        rightOffsetRangeAngle = rightCircleAngle - startAngle

//        Log.d(TAG,"leftOffsetRangeAngle $leftOffsetRangeAngle  centerOffsetRangeAngle $centerOffsetRangeAngle rightOffsetRangeAngle $rightOffsetRangeAngle")
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
            var positiveScroll = true

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
                    position = pos
                    Log.d(
                        TAG,
                        "左滑current${viewPager?.currentItem} position $pos positionOffset $posOffset  positionOffsetPixels $positionOffsetPixels"
                    )

                } else if (lastOffsetPx > positionOffsetPixels) {
                    Log.d(
                        TAG,
                        "右滑current${viewPager?.currentItem} position $pos positionOffset $posOffset  positionOffsetPixels $positionOffsetPixels"
                    )
                    //向左
                    progress = posOffset
                    position = pos

                } else {
                    progress= 0f
                    position = pos

                }


                //从当前页 往左滑
//                progress= positionOffset

                lastOffsetPx = positionOffsetPixels
//                prePosition= position
//                progress = positionOffset
//                lastOffset = positionOffsetPixels
                invalidate()
            }

            override fun onPageSelected(pos: Int) {
                super.onPageSelected(position)

                Log.d(TAG, "==============PageSelect  ============ $position")
            }

            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)
                this.state = state
            }
        }

    fun bindViewPager(viewPager2: ViewPager2) {
        this.viewPager = viewPager2
        viewPager2.registerOnPageChangeCallback(pageChangeCallback)
        maxLevel = viewPager2.adapter?.itemCount ?: 0
        position = viewPager2.currentItem

        val list = mutableListOf<LevelPoint>()

//        for ( index in 0..maxLevel){
//            list.add(LevelPoint(index,90f))
//        }
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
