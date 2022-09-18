package com.example.widget.widget

import android.content.Context
import android.content.res.Resources
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.viewpager2.widget.ViewPager2
import kotlin.math.*

class ArcView : View {

    private val TAG = "ArcView"

    private val arcPaint = Paint().apply {
        color = Color.parseColor("#9CACFF")
        style = Paint.Style.STROKE
        strokeWidth = 3.toPx().toFloat()
    }

    private val highlightCirclePaint = Paint().apply {
        color = Color.RED
        style = Paint.Style.FILL
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

    //90度弧长
    private var centerArcLength: Float = 0f

    //文字Y轴偏移量
    private var labelYOffset: Float = 0f

    private var startAngle = -1f //椭圆起始角度
    private var endAngle = -1f //椭圆结束角度
    private var centerCircleAngle = 90f //中间圆角度
    private var leftCircleAngle = -1f //左边圆角度
    private var rightCircleAngle = -1f//右边圆的角度

    //椭圆实际宽度和控件宽比值
//    private val ovalWidthRatio = 1.302f
    private val ovalWidthRatio = 1f

    //控件高/宽
    private val aspectRatio = .314f

    //中间圆的半径
    private val centerCircleRadius = 8.toPx().toFloat()


    private lateinit var ovalRectF: RectF

    private var progress: Float = 0f

    private var maxLevel = 6
    private var currentLevel = 3
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
        val ovalHeight = height - centerCircleRadius * 2 - labelYOffset
        val start = (width - ovalWidth) / 2
        ovalRectF = RectF(start, -ovalHeight / 2f, ovalWidth + start, ovalHeight)


        iniParams(ovalRectF, width)
        ini(ovalRectF)
        setMeasuredDimension(width, height.toInt())
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawText(
            "progress ${(progress * 100).toInt()} positoin $position",
            width / 2f,
            height / 2f,
            textPaint
        )
        val arcLocation = locationPosition()

        val currentLevelAngle = arcLocation.secondArcPoint.angle
        val angleByArcPoint = getAngleByArcPoint(arcLocation.secondArcPoint)
//        Log.e(TAG, "currentLevelAngle $currentLevelAngle $position")
        canvas.drawArc(
            ovalRectF,
            currentLevelAngle,
            180f - currentLevelAngle,
            false,
            arcPaint.apply {
                color = Color.parseColor("#38E5FF")
                alpha = 255
            })
        canvas.drawArc(ovalRectF, 0f, currentLevelAngle, false, arcPaint.apply {
            color = Color.parseColor("#000000")
            alpha = 38
        })


        drawCirclePoint(canvas, arcLocation.firstArcPoint.angle, position - 1, "fir")
        drawCirclePoint(canvas, arcLocation.secondArcPoint.angle, position, "sec")
        drawCirclePoint(canvas, arcLocation.thirdArcPoint.angle, position + 1, "thir")
        drawCirclePoint(canvas, arcLocation.fourthArcPoint.angle, position + 2, "four")


        (width / 2f).let { x -> canvas.drawLine(x, 0f, x, height.toFloat(), linePaint) }
        getArcPoint(ovalRectF, 45f).run {
            canvas.drawLine(
                x,
                0f,
                x,
                height.toFloat(),
                linePaint
            )
        }
        getArcPoint(ovalRectF, 135f).run {
            canvas.drawLine(
                x,
                0f,
                x,
                height.toFloat(),
                linePaint
            )
        }
//        getArcPoint(ovalRectF, arcLocation.thirdArcPoint.angle).run {
//            canvas.drawLine(
//                x,
//                0f,
//                x,
//                height.toFloat(),
//                linePaint
//            )
//        }
    }

    private fun getAngleByArcPoint(pointEnd: ArcPoint): Float {
        val pointStart = PointF(ovalRectF.right, ovalRectF.centerY())
        val oPoint = PointF(ovalRectF.centerX(), ovalRectF.centerY())
        val angle = getAngle(oPoint, pointStart, pointEnd.point)
        Log.e(
            TAG,
            "pointStart ${pointStart}  oPoint ${oPoint} pointEnd ${pointEnd.point}  secondArcPointAngle ${pointEnd.angle}  getAngle $angle"
        )
        return angle
    }

    fun getAngle(pointA: PointF, pointB: PointF, pointC: PointF): Float {
        val lengthAB = sqrt(
            (pointA.x - pointB.x).pow(2.0f) +
                    (pointA.y - pointB.y).pow(2f)
        )
        val lengthAC = sqrt(
            (pointA.x - pointC.x).pow(2.0f) +
                    (pointA.y - pointC.y).pow(2.0f)
        )
        val lengthBC = sqrt(
            (pointB.x - pointC.x).pow(2.0f) +
                    (pointB.y - pointC.y).pow(2.0f)
        )
        val cosA = (lengthAB.pow(2.0f) + lengthAC.pow(2.0f) - lengthBC.pow(2.0f)) /
                (2 * lengthAB * lengthAC)
        return (acos(cosA) * 180 / Math.PI).toFloat()
    }

    class ArcLocation(
        var firstArcPoint: ArcPoint,
        var secondArcPoint: ArcPoint,
        var thirdArcPoint: ArcPoint,
        var fourthArcPoint: ArcPoint
    )

    class ArcPoint(
        val point: PointF = PointF(),
        val angle: Float = 0f,
    )


    /**
     *
     */
    private fun locationPosition(): ArcLocation {
        var firstPoint: ArcPoint? = null
        var secondPoint: ArcPoint? = null
        var thirdPoint: ArcPoint? = null
        var fourthPoint: ArcPoint? = null
        //画各个圆点
        var x0 = ovalRectF.right
        var y0 = ovalRectF.centerY()
        var angle = 0f
        var d = 0f
        val offset = centerArcLength / 2f * progress
        while (angle <= 180f) {
            val point = getArcPoint(ovalRectF, angle)
            d += distance(x0, y0, point.x, point.y)
            x0 = point.x
            y0 = point.y
            if (firstPoint == null && d >= centerArcLength * 1.5f + offset) {
                firstPoint = ArcPoint(getArcPoint(ovalRectF, angle), angle)
            } else if (secondPoint == null && d >= centerArcLength + offset) {
                secondPoint = ArcPoint(getArcPoint(ovalRectF, angle), angle)
            } else if (thirdPoint == null && d >= centerArcLength * 0.5f + offset) {
                thirdPoint = ArcPoint(getArcPoint(ovalRectF, angle), angle)
            } else if (fourthPoint == null && d > 0 + offset) {
                fourthPoint = ArcPoint(getArcPoint(ovalRectF, angle), angle)
            }
            angle += 0.25f
        }
        return ArcLocation(
            firstPoint ?: ArcPoint(),
            secondPoint ?: ArcPoint(),
            thirdPoint ?: ArcPoint(),
            fourthPoint ?: ArcPoint()
        )
    }


    private fun drawCirclePoint(canvas: Canvas, angle: Float, position: Int, desc: String = "") {
        if (position in 0 until maxLevel) {
            getArcPoint(ovalRectF, angle).run {
                canvas.drawCircle(x, y, centerCircleRadius, highlightCirclePaint)
                canvas.drawText("${angle.toInt()} $position $desc", x, y + labelYOffset, textPaint)
            }
        }
    }

    private fun drawCirclePoint(
        canvas: Canvas,
        arcPoint: ArcPoint,
        position: Int,
        desc: String = ""
    ) {
        if (position in 0 until maxLevel) {
            arcPoint.point.run {
                canvas.drawCircle(x, y, centerCircleRadius, highlightCirclePaint)
                canvas.drawText("Lv.$position $desc", x, y + labelYOffset, textPaint)
            }
        }
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

    private fun distance(x1: Float, y1: Float, x2: Float, y2: Float): Float {
        return sqrt((x2 - x1).pow(2) + (y2 - y1).pow(2))

    }

    private fun ini(rectF: RectF) {
        centerArcLength = getCircumference(rectF, 90f)
        Log.e(TAG, "Circumference of ellipse = centerArcLength $centerArcLength ")

    }

    private fun getCircumference(rectF: RectF, limitAngle: Float): Float {
        var x0 = ovalRectF.right
        var y0 = ovalRectF.centerY()
        var angle = 0f
        var d = 0f
        while (angle <= limitAngle) {
            val point = getArcPoint(rectF, angle)
            d += distance(x0, y0, point.x, point.y)
            x0 = point.x
            y0 = point.y
            angle += 0.25f
        }
        return d
    }

    /**
     * @param rectF 椭圆 外矩形
     * @param width View的可见宽度
     */
    private fun iniParams(rectF: RectF, width: Int) {
        val leftPointX = width * 0.2f
        val rightPointX = width - leftPointX
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
        val ovalA = ovalRectF.width() / 2f //长轴
        val ovalB = ovalRectF.height() / 2f //短轴
//        2 * pi * sqrt((r1*r1 + r2*r2) / 2);
        //周长
        var arcLength = 2 * Math.PI * sqrt((ovalA.pow(2) + ovalB.pow(2)) / 2)

        Log.d(
            TAG,
            "width $width height $height oval $ovalRectF  outerCircleRadius  ${ovalRectF.width() / 2f} innerCircleRadius  ${ovalRectF.height() / 2f}  arcLength $arcLength"
        )
        Log.d(
            TAG,
            "startAngle $startAngle endAngle $endAngle  leftCircleAngle $leftCircleAngle  rightCircleAngle$rightCircleAngle"
        )
        val sampleText = "Lv.1"
        val bounds = Rect()
        labelYOffset =
            centerCircleRadius + textPaint.getTextBounds(sampleText, 0, sampleText.length, bounds)
                .let { bounds.height() }

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
