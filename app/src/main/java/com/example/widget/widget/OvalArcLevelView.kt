package com.example.widget.widget

import android.content.Context
import android.content.res.Resources
import android.graphics.*
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import kotlin.math.*

class OvalArcLevelView : View {


    private val TAG = "ArcView"

    private val arcPaint = Paint().apply {
        color = Color.parseColor("#9CACFF")
        alpha = 200
        style = Paint.Style.STROKE
        strokeWidth = 3.toPx().toFloat()
    }

    private val arcNormalPaint = Paint().apply {
        style = Paint.Style.STROKE
        strokeWidth = 3.toPx().toFloat()
        color = Color.parseColor("#FFFFFF")
        alpha = 38
    }

    private lateinit var arcShader: SweepGradient
    private val arcShaderMatrix = Matrix()

    private val circlePaint = Paint().apply {
        color = Color.BLUE
        style = Paint.Style.FILL
//        shader= LinearGradient(2,4,6,4,)
    }

    private val circleBorderPaint = Paint().apply {
        color = Color.GRAY
        style = Paint.Style.FILL
    }

    private val textPaint = Paint().apply {
        color = Color.WHITE
//        style = Paint.Style.STROKE
        textSize = 12.toPx().toFloat()
        textAlign = Paint.Align.CENTER
    }

    //90度 弧长
    private var centerArcLength: Float = 0f

    //文字Y轴偏移量
    private var labelYOffset: Float = 0f

    //椭圆实际宽度和控件宽比值
    private val ovalWidthRatio = 1.202f
//    private val ovalWidthRatio = 1f

    //控件高/宽
    private val aspectRatio = .314f

    //高亮点的半径
    private val highlightCircleRadius = 4.toPx().toFloat()

    //高亮点边框半径
    private val highlightCircleBorderRadius = 8.toPx().toFloat()

    //其他点的半径
    private val normalCircleRadius = 3.toPx().toFloat()

    //椭圆所在的矩形
    private val ovalRectF = RectF()


    //椭圆长轴
    private var ovalA: Float = 0f

    //椭圆短轴
    private var ovalB: Float = 0f

    //viewpager 滚动 progress
    private var progress: Float = 0f

    //最大圆点数量
    private var maxCircleCount = 6

    //高亮的圆点 序号
    private var highlightCirclePosition = 2

    //viewpager 滑动过程中的 position 位于中间的点的position值
    private var currentPosition = 0


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
        setMeasuredDimension(width, height.toInt())
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        val ovalWidth = width * ovalWidthRatio
        labelYOffset = highlightCircleBorderRadius + getTextTextBounds("Lv.1").let { it.height() }
        val ovalHeight = height - highlightCircleBorderRadius * 2 - labelYOffset
        val start = (width - ovalWidth) / 2
        ovalRectF.set(start, -ovalHeight / 2f, ovalWidth + start, ovalHeight)

        ovalA = ovalRectF.width() / 2f
        ovalB = ovalRectF.height() / 2f
        arcShader = SweepGradient(
            ovalRectF.centerX(),
            ovalRectF.centerY(),
            Color.parseColor("#6F87FF"),
            Color.parseColor("#38E5FF")
        ).apply {
            setLocalMatrix(arcShaderMatrix)
        }
        centerArcLength = getCircumference(ovalRectF, 90f)

    }

    private fun getTextTextBounds(text: String): Rect {
        return Rect().apply {
            textPaint.getTextBounds(text, 0, text.length, this)
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
//        canvas.drawText(
//            "progress ${(progress * 100).toInt()} positoin $currentPosition",
//            width / 2f,
//            height / 2f,
//            textPaint
//        )


        val arcLocation = locationPosition()
        val eccentricAngle = eccentricAngle(arcLocation.highlightPoint.angle)

        arcShaderMatrix.setRotate(
            arcLocation.highlightPoint.angle,
            ovalRectF.centerX(),
            ovalRectF.centerY()
        )
        canvas.drawArc(
            ovalRectF,
            eccentricAngle,
            180f - eccentricAngle,
            false,
            arcPaint.apply {
                shader = arcShader.apply {
                    setLocalMatrix(arcShaderMatrix)
                }
            })
//        Log.d(TAG,"currentLevelArcPoint ${arcLocation.currentLevelArcPoint.angle}  eccentricAngle $eccentricAngle   sweepAngle ${180f - eccentricAngle}")
        canvas.drawArc(ovalRectF, 0f, eccentricAngle, false, arcNormalPaint)


        drawCirclePoint(canvas, arcLocation.firstArcPoint)
        drawCirclePoint(canvas, arcLocation.secondArcPoint)
        drawCirclePoint(canvas, arcLocation.thirdArcPoint)
        drawCirclePoint(canvas, arcLocation.fourthArcPoint)


    }


    /**
     *
     *  pos 3  current  2  1  l = interval + centerArcLength
     *  pos 3  current 4   -1 l = -interval + centerArcLength
     *  pos 3  current 3   0 l =  centerArcLength
     *  pos 4  current 2    interval * 2 +centerArcLength + offset
     *  pos 3  current 2    interval * (1)+centerArcLength + offset
     *
     */
    private fun locationPosition(): ArcLocation {
        var firstPoint: ArcPoint? = null
        var secondPoint: ArcPoint? = null
        var thirdPoint: ArcPoint? = null
        var fourthPoint: ArcPoint? = null
        var currentLevelPoint: ArcPoint? = null
        //画各个圆点
        var x0 = ovalRectF.right
        var y0 = ovalRectF.centerY()
        var angle = 0f
        var d = 0f
        //两点之间弧长距离
        val interval = centerArcLength / 2f
        //当前拖动的弧长偏移量
        val offset = interval * progress

        val maxAngle = 180f

        while (angle <= maxAngle) {
            val point = getArcPoint(ovalRectF, angle)
            d += distance(x0, y0, point.x, point.y)
            x0 = point.x
            y0 = point.y
            if (firstPoint == null && d >= centerArcLength * 1.5f + offset) {
                firstPoint = ArcPoint(getArcPoint(ovalRectF, angle), angle, currentPosition - 1)
            } else if (secondPoint == null && d >= centerArcLength + offset) {
                secondPoint = ArcPoint(getArcPoint(ovalRectF, angle), angle, currentPosition)
            } else if (thirdPoint == null && d >= centerArcLength * 0.5f + offset) {
                thirdPoint = ArcPoint(getArcPoint(ovalRectF, angle), angle, currentPosition + 1)
            } else if (fourthPoint == null && d > 0 + offset) {
                fourthPoint = ArcPoint(getArcPoint(ovalRectF, angle), angle, currentPosition + 2)
            } else if (currentLevelPoint == null && d >= interval * (currentPosition - highlightCirclePosition) + centerArcLength + offset) {
                currentLevelPoint =
                    ArcPoint(getArcPoint(ovalRectF, angle), angle, highlightCirclePosition)
            }
            angle += 0.25f
        }


        return ArcLocation(
            firstPoint ?: ArcPoint(),
            secondPoint ?: ArcPoint(),
            thirdPoint ?: ArcPoint(),
            fourthPoint ?: ArcPoint(),
            currentLevelPoint ?: ArcPoint(angle = maxAngle)
        )
    }

    private fun distance(x1: Float, y1: Float, x2: Float, y2: Float): Float {
        return sqrt((x2 - x1).pow(2) + (y2 - y1).pow(2))
    }


    private fun drawCirclePoint(
        canvas: Canvas,
        arcPoint: ArcPoint
    ) {
        arcPoint.run {
            if (position in 0 until maxCircleCount) {
                if (position == highlightCirclePosition) {
                    canvas.drawCircle(
                        point.x,
                        point.y,
                        highlightCircleBorderRadius,
                        circleBorderPaint.apply {
                            color = Color.parseColor("#9CACFF")
                            alpha = 127
                        })

                    canvas.drawCircle(point.x, point.y, highlightCircleRadius, circlePaint.apply {
                        color = Color.parseColor("#9CACFF")
                    })

                } else {
                    if (position>highlightCirclePosition){
                        canvas.drawCircle(point.x, point.y, normalCircleRadius, circlePaint.apply {
                            color = Color.parseColor("#606060")
                            alpha = 255
                        })
                    }else
                        canvas.drawCircle(point.x, point.y, normalCircleRadius, circlePaint.apply {
                            color = Color.parseColor("#9CACFF")
                            alpha = 191
                        })
                }
                canvas.save()
                // TODO: 这里稍微有点偏移, 需要短轴+y轴偏移 画更大的椭圆的x,y轴
                canvas.translate(point.x, point.y)
                canvas.rotate((eccentricAngle(angle) - 90f) / 2f)
                canvas.drawText("V${position + 1}", 0f, 0f + labelYOffset, textPaint.apply {
                    if (position == highlightCirclePosition) {
                        color = Color.WHITE
                    } else {
                        color = Color.parseColor("#C4C4C4")
                    }

                })
                canvas.restore()
            }
        }
    }


    /**
     * 根据圆心角 计算离心角
     * @param a 长轴
     * @param b 短轴
     * @param angle 圆心角 角度
     * @return
     */
    private fun eccentricAngle(angle: Float): Float {
        val a = ovalRectF.width() / 2f
        val b = ovalRectF.height() / 2f
        // 转弧度
        val centralRadians = Math.toRadians(angle.toDouble())
        //离心角弧度
        val centrifugalRadians = atan2(a * sin(centralRadians), b * cos(centralRadians))
        //转离心角 角度
        return Math.toDegrees(centrifugalRadians).toFloat()
    }


    private class ArcLocation(
        var firstArcPoint: ArcPoint,
        var secondArcPoint: ArcPoint,
        var thirdArcPoint: ArcPoint,
        var fourthArcPoint: ArcPoint,
        var highlightPoint: ArcPoint

    )

    private class ArcPoint(
        //圆弧上的坐标
        val point: PointF = PointF(),
        //角度
        val angle: Float = 0f,
        // 第几个点
        val position: Int = -1
    )


    /**
     * 根据角度获取椭圆上的坐标
     */
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


    /**
     * 根据角度获取弧长
     */
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

    private var viewPager: ViewPager2? = null
    private val pageChangeCallback: ViewPager2.OnPageChangeCallback =
        object : ViewPager2.OnPageChangeCallback() {
            var lastOffsetPx = 0
            override fun onPageScrolled(
                pos: Int,
                posOffset: Float,
                positionOffsetPixels: Int
            ) {
                super.onPageScrolled(pos, posOffset, positionOffsetPixels)
                progress = posOffset
                currentPosition = pos
                lastOffsetPx = positionOffsetPixels
                invalidate()
            }

            override fun onPageSelected(pos: Int) {
                super.onPageSelected(currentPosition)
            }

            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)
            }
        }


    private val gestureDetector = GestureDetector(context, object :
        GestureDetector.SimpleOnGestureListener() {
        override fun onDown(e: MotionEvent?): Boolean {
            parent.requestDisallowInterceptTouchEvent(true)
            viewPager?.beginFakeDrag()
            return true
        }

        override fun onScroll(
            e1: MotionEvent?,
            e2: MotionEvent?,
            distanceX: Float,
            distanceY: Float
        ): Boolean {
            viewPager?.fakeDragBy(-distanceX)
            return true
        }


        override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
            return onClickPoint(e.x, e.y)

        }
    })

    private fun onClickPoint(x: Float, y: Float): Boolean {
        if (x < width / 4f) {
            viewPager?.let {
                it.currentItem = it.currentItem - 1
            }
            return true
        } else if (x >= (width * 3 / 4f)) {
            viewPager?.let {
                it.currentItem = it.currentItem + 1
            }
            return true
        }
        return false
    }


    override fun onTouchEvent(event: MotionEvent): Boolean {
//        //父view不要拦截Move事件
//        if (event.action == MotionEvent.ACTION_MOVE) {
//        }
        if (!gestureDetector.onTouchEvent(event) && event.action == MotionEvent.ACTION_UP) {
            viewPager?.endFakeDrag()
        }
        return true
    }


    private val dataChangeObserver = object : RecyclerView.AdapterDataObserver() {
        override fun onChanged() {
            maxCircleCount = viewPager?.adapter?.itemCount ?: 0
            currentPosition = viewPager?.currentItem ?: 0
        }
    }

    fun bindViewPager(viewPager2: ViewPager2) {
        this.viewPager = viewPager2
        viewPager2.registerOnPageChangeCallback(pageChangeCallback)
        viewPager2.adapter?.registerAdapterDataObserver(dataChangeObserver)
        maxCircleCount = viewPager2.adapter?.itemCount ?: 0
        currentPosition = viewPager2.currentItem
        invalidate()
    }


    fun unbindViewPager() {
        viewPager?.unregisterOnPageChangeCallback(pageChangeCallback)
        viewPager?.adapter?.unregisterAdapterDataObserver(dataChangeObserver)
        viewPager = null
    }

    /**
     * 设置当前高亮的点
     *从0开始算
     */
    fun setHighlightPosition(position: Int) {
        highlightCirclePosition = position
        invalidate()
    }

    private fun Int.toPx(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()

    private fun Int.sp2px(): Float = this * Resources.getSystem().displayMetrics.scaledDensity + .5f
}

