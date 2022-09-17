//package com.example.widget.widget
//
//import android.content.Context
//import android.content.res.Resources
//import android.graphics.*
//import android.util.AttributeSet
//import android.util.Log
//import android.view.View
//import androidx.viewpager2.widget.ViewPager2
//import java.text.FieldPosition
//import kotlin.math.*
//
//
//class ArcView : View {
//
//    private var leftPointLength: Float = -1f
//    private var centerPointLength: Float = -1f
//    private var rightPointLength: Float = -1f
//    private val TAG = "ArcView"
//
//    private val paint = Paint().apply {
//        color = Color.RED
//        style = Paint.Style.STROKE
//        strokeWidth = 5f
//    }
//    private val linePaint = Paint().apply {
//        color = Color.RED
//        style = Paint.Style.STROKE
//        strokeWidth = 1f
//    }
//    private val textPaint = Paint().apply {
//        color = Color.BLACK
////        style = Paint.Style.STROKE
//        textSize = 15.toPx().toFloat()
//        textAlign = Paint.Align.CENTER
//    }
//
//    private var startAngle = -1f //椭圆起始角度
//    private var endAngle = -1f //椭圆结束角度
//    private var angle = 90f //测试用 运动圆角度
//
//    private var arcLengthInterval = 0f// 弧长等分间隔
//
//
//    private var centerArchLength = 0f //90度 弧长
//
//    //椭圆实际宽度和控件宽比值
////    private val ovalWidthRatio = 1.302f
//    private val ovalWidthRatio = 1f
//
//    //控件高/宽
//    private val aspectRatio = .314f
//
//    private val centerCircleRadius = 15f
//
//
//    private lateinit var ovalRectF: RectF
//
//    private var progress: Float = 0f
//
//    private var maxLevel = 6
//
//    private var position = 1
//
//
//    constructor(context: Context?) : super(context)
//    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
//    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
//        context,
//        attrs,
//        defStyleAttr
//    )
//
//
//    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
//        val width = MeasureSpec.getSize(widthMeasureSpec)
//        val height = width * aspectRatio
//        val ovalWidth = width * ovalWidthRatio
//        val ovalHeight = height - centerCircleRadius
//        val start = (width - ovalWidth) / 2
//        startAngle = 0f
//        endAngle = 180f
//        ovalRectF = RectF(start, -ovalHeight / 2f, ovalWidth + start, ovalHeight)
//        ini(ovalRectF, width)
//        initPointAngle()
//        test(ovalRectF)
//
//        setMeasuredDimension(width, height.toInt())
//    }
//
//    override fun onDraw(canvas: Canvas) {
//        super.onDraw(canvas)
//        canvas.drawText("${(progress * 100).toInt()}", width / 2f, height / 2f, textPaint)
//        canvas.drawArc(ovalRectF, startAngle, endAngle - startAngle, false, paint)
//
//
//
//        val leftOffset = (180f - leftCircleAngle) * progress
//        val leftAngle = leftCircleAngle + leftOffset
//        //左边的点
//        if (position > 0) {
//            getArcPoint(ovalRectF, leftAngle).run {
//                canvas.drawCircle(
//                    x, y, centerCircleRadius, paint
//                )
//                canvas.drawText(
//                    "${leftAngle.toInt()} 左${position.minus(1).toString()}",
//                    x,
//                    y,
//                    textPaint
//                )
//            }
//        }
//
//
//        val centerOffset = (leftCircleAngle - centerCircleAngle) * progress
//        val centerAngle = centerCircleAngle + centerOffset
//        //中间的点
//        getArcPoint(ovalRectF, centerAngle).run {
//            canvas.drawCircle(x, y, centerCircleRadius, paint)
//            canvas.drawText("${centerAngle.toInt()} 中$position", x, y, textPaint)
//        }
//
//
//        val rightOffset = (centerCircleAngle - rightCircleAngle) * progress
//        val rightAngle = rightCircleAngle + rightOffset
////        if (position < maxLevel && rightAngle>= startAngle) {
//        //右边的点
//        getArcPoint(ovalRectF, rightAngle).run {
//            canvas.drawCircle(
//                x, y, centerCircleRadius, paint
//            )
//            canvas.drawText("${rightAngle.toInt()} 右${position.plus(1)}", x, y, textPaint)
//        }
////        }
//
//
//        //右侧外部的点
//        val outsideOffset = (rightCircleAngle - startAngle / 2f) * progress
//        val outsideAngle = startAngle / 2f + outsideOffset
//        if (outsideAngle > startAngle) {
//            getArcPoint(ovalRectF, outsideAngle).run {
//                canvas.drawCircle(
//                    x, y, centerCircleRadius, paint
//                )
//                canvas.drawText("外${outsideAngle.toInt()} ${position.plus(2)}", x, y, textPaint)
//            }
//        }
//
//
//
//        (width / 2f).let { x -> canvas.drawLine(x, 0f, x, height.toFloat(), linePaint) }
//        getArcPoint(ovalRectF, leftCircleAngle).let {
//            canvas.drawLine(
//                it.x,
//                0f,
//                it.x,
//                height.toFloat(),
//                linePaint
//            )
//        }
//        getArcPoint(ovalRectF, rightCircleAngle).let {
//            canvas.drawLine(
//                it.x,
//                0f,
//                it.x,
//                height.toFloat(),
//                linePaint
//            )
//        }
//    }
//
//    private fun drawPoint(canvas: Canvas, angle: Float, position: Int) {
//        getArcPoint(ovalRectF, angle).run {
//            canvas.drawCircle(x, y, centerCircleRadius, paint)
//            canvas.drawText("${angle.toInt()} $position", x, y, textPaint)
//        }
//    }
//
//    /**
//     * 根据弧度 计算离心角
//     * @param a 长轴
//     * @param b 短轴
//     */
//    private fun eccentricAngle(a: Float, b: Float, radians: Float): Float {
//        return atan2(a * sin(radians), b * cos(radians))
//    }
//}
//
//    private fun getArcPoint(rectF: RectF, angle: Float): PointF {
//        val a = rectF.width() / 2f
//        val b = rectF.height() / 2f
//
//        if (a == 0f || b == 0f) return PointF(rectF.left, rectF.top)
//
//        //弧度
//        val radian = Math.toRadians(angle.toDouble())
//        val yc = sin(radian)
//        val xc = cos(radian)
//        val radio = (a * b) / sqrt((yc * a).pow(2.0) + (xc * b).pow(2.0));
//
//        val ax = radio * xc
//        val ay = radio * yc
//        val x = rectF.left + a + ax
//        val y = rectF.top + b + ay
//
//        return PointF(x.toFloat(), y.toFloat())
//    }
//    /**
//     * @param ovalX 椭圆x轴
//     * 累加算出整个弧长, 等分间距= 弧长/n(等分数量)
//     * 先算中间点 x,y , angle 通过 getArcPoint
//     *  以0.25度的弧长进行累加计算≈间距 的角度值
//     *  根据某个点的角度 累加算出下一个点的角度
//     *
//     */
//    private fun ini(rectF: RectF, width: Int) {
//        val ovalA = ovalRectF.width() / 2f //长轴
//        val ovalB = ovalRectF.height() / 2f //短轴
//        //180度弧长
//        centerArchLength = circumference(rectF, 180f)
//        //静止时显示三个点, 所以弧长划分成4个等分, 两点距离= 弧长/4
//        arcLengthInterval = centerArchLength / 2f
//        Log.d(TAG, "centerArchLength$centerArchLength  arcLengthInterval $arcLengthInterval")
//        initPointAngle(rectF)
//    }
//
//    private fun test(rectF: RectF) {
//        val ovalA = ovalRectF.width() / 2f //长轴
//        val ovalB = ovalRectF.height() / 2f //短轴
//
////	double P = 2 * pi * sqrt((r1*r1 + r2*r2) / 2);
//        val l1 = 2f * PI * sqrt((ovalA.pow(2) + ovalB.pow(2)) / 2f)
//        Log.d(TAG, "方式1 弧长 $l1")
//        val l2 = circumference(rectF, 360f)
//        Log.d(TAG, "方式2 弧长 $l2")
//        val l3 = 360f * PI * 1f / 180f
//        Log.d(TAG, "方式2 弧长 $l3")
//
//
//    }
//
//    /**
//     * 计算角度弧长
//     */
//    private fun circumference(rectF: RectF, ovalAngle: Float): Float {
//        var angle = 0f
//        val ovalA = ovalRectF.width() / 2f //长轴
//        val ovalB = ovalRectF.height() / 2f //短轴
//        var d = 0f
//        var x0 = rectF.centerX()
//        var y0 = 0f
//        while (angle < ovalAngle) {
//            val x = (ovalA * cos(Math.toRadians(angle.toDouble()))).toFloat()
//            val y = (ovalB * sin(Math.toRadians(angle.toDouble()))).toFloat()
//            d += distance(x0, y0, x, y)
//            x0 = x
//            y0 = y
//            angle += 0.25f
//        }
//        Log.d(TAG, "Circumference of ellipse$d")
//        return d
//    }
//
//    /**
//     * 计算180度弧长
//     */
//    private fun initPointAngle(rectF: RectF): Float {
//        var angle = 0f
//        val ovalA = ovalRectF.width() / 2f //长轴
//        val ovalB = ovalRectF.height() / 2f //短轴
//        var d = 0f
//        var x0 = rectF.centerX()
//        var y0 = 0f
//        while (angle < 180f) {
//            val x = (ovalA * cos(Math.toRadians(angle.toDouble()))).toFloat()
//            val y = (ovalB * sin(Math.toRadians(angle.toDouble()))).toFloat()
//            d += distance(x0, y0, x, y)
//
//            if (rightPointLength == -1f && d > arcLengthInterval) {
//                rightPointLength = d
//            } else if (centerPointLength == -1f && d >= centerArchLength) {
//                centerPointLength = d
//            } else if (leftPointLength == -1f && d >= centerArchLength + arcLengthInterval) {
//                leftPointLength = d
//            }
//            x0 = x
//            y0 = y
//            angle += 0.25f
//        }
//        Log.d(TAG, "Circumference of ellipse$d")
//        return d
//    }
//
//    /**
//     * 计算两个角度区间的弧长
//     * @param 起始角度
//     * @param 结束角度
//     */
//    private fun calculateArcLength(startAngle: Float, endAngle: Float, rectF: RectF): Float {
//        var angle = startAngle
//        val ovalA = ovalRectF.width() / 2f //长轴
//        val ovalB = ovalRectF.height() / 2f //短轴
//        var d = 0f
//        var x0 = rectF.centerX()
//        var y0 = 0f
//        while (angle <= endAngle) {
//            val x = (ovalA * cos(Math.toRadians(angle.toDouble()))).toFloat()
//            val y = (ovalB * sin(Math.toRadians(angle.toDouble()))).toFloat()
//            d += distance(x0, y0, x, y)
//            x0 = x
//            y0 = y
//            angle += 0.25f
//        }
//        Log.d(TAG, "Circumference of ellipse$d")
//        return d
//    }
//
//
//    private fun distance(x1: Float, y1: Float, x2: Float, y2: Float): Float {
//        return sqrt((x2 - x1).pow(2) + (y2 - y1).pow(2))
//    }
//
//
//    fun setAngleProgress(progress: Float) {
//        angle = 360f * progress
//        invalidate()
//    }
//
//    fun setAngle(angle: Float) {
//        this.angle = angle
//        invalidate()
//    }
//
//
//    private var viewPager: ViewPager2? = null
//    private var pageChangeCallback: ViewPager2.OnPageChangeCallback =
//        object : ViewPager2.OnPageChangeCallback() {
//            var lastOffsetPx = 0
//            var state = ViewPager2.SCROLL_STATE_IDLE
//            var preState = ViewPager2.SCROLL_STATE_IDLE
//
//            override fun onPageScrolled(
//                pos: Int,
//                posOffset: Float,
//                positionOffsetPixels: Int
//            ) {
//                super.onPageScrolled(pos, posOffset, positionOffsetPixels)
//                /**
//                 * center :
//                 * progress 0->1
//                 * angle 90-> leftAngle
//                 *
//                 */
//
////                Log.d(
////                    TAG,
////                    "current${viewPager?.currentItem} position $pos positionOffset $posOffset  positionOffsetPixels $positionOffsetPixels"
////                )
//
//
//                if (lastOffsetPx < positionOffsetPixels) {
//                    //向右滑动
//                    progress = posOffset
//
////                    Log.d(
////                        TAG,
////                        "左滑current${viewPager?.currentItem} position $pos positionOffset $posOffset  positionOffsetPixels $positionOffsetPixels"
////                    )
//
//                } else if (lastOffsetPx > positionOffsetPixels) {
////                    Log.d(
////                        TAG,
////                        "右滑current${viewPager?.currentItem} position $pos positionOffset $posOffset  positionOffsetPixels $positionOffsetPixels"
////                    )
//                    //向左
//                    progress = posOffset
//                } else {
//                    progress = 0f
//                }
//                position = pos
//                lastOffsetPx = positionOffsetPixels
//
//                invalidate()
//            }
//
//            override fun onPageSelected(pos: Int) {
//                super.onPageSelected(position)
//
//                Log.d(TAG, "==============PageSelect  ============ $position")
//            }
//
//            override fun onPageScrollStateChanged(state: Int) {
//                super.onPageScrollStateChanged(state)
//                this.state = state
//                if (preState != state) {
//                    preState
//                }
//            }
//        }
//
//    fun bindViewPager(viewPager2: ViewPager2) {
//        this.viewPager = viewPager2
//        viewPager2.registerOnPageChangeCallback(pageChangeCallback)
//        maxLevel = viewPager2.adapter?.itemCount ?: 0
//        position = viewPager2.currentItem
//        invalidate()
//    }
//
//
//    fun unbindViewPager() {
//        viewPager?.unregisterOnPageChangeCallback(pageChangeCallback)
//        viewPager = null
//    }
//}
//
//
///**
// * 时间　: 2021/09/13
// * 作者　: Mr.W
// * 描述　: 界面扩展
// */
//
//fun Int.toPx(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()
//fun Int.toDp(): Int = (this / Resources.getSystem().displayMetrics.density).toInt()
//
//// px转sp
//fun Int.px2sp(): Float {
//    val fontScale = Resources.getSystem().displayMetrics.scaledDensity
//    return this / (fontScale + .5f)
//}
//
//// sp转px
//fun Int.sp2px(): Float {
//    val fontScale = Resources.getSystem().displayMetrics.scaledDensity
//    return this * fontScale + .5f
//}
