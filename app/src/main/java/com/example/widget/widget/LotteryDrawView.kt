package com.example.widget.widget

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import kotlin.random.Random


class LotteryDrawView : ViewGroup {
    val TAG = "LotteryDrawView"

    private val lotteryPoolRect = Rect()

    private var childViews = listOf<ViewGroup>()
    private val pathMeasureList = ArrayList<PathMeasure>()

    private val lotteryBgPaint = Paint().apply {
        color = Color.LTGRAY
    }

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawRect(lotteryPoolRect, lotteryBgPaint)
        canvas.clipRect(lotteryPoolRect)
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        Log.d(TAG, "onMeasure")
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)
        childViews.forEach {
            val lp = it.layoutParams
            val childWidth = lp.width
            val childHeight = lp.height
            it.measure(
                MeasureSpec.makeMeasureSpec(childWidth, MeasureSpec.AT_MOST),
                MeasureSpec.makeMeasureSpec(childHeight, MeasureSpec.AT_MOST)
            )
        }
        setMeasuredDimension(width, height)

    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        val widthPercent = 0.8f
        val poolWidth = (w * widthPercent).toInt()
        val left = ((w - poolWidth) / 2)
        val top = 0
        lotteryPoolRect.set(left, top, w - left, top + poolWidth)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        pathMeasureList.clear()
        childViews.forEach {
            it.translationX = 0f
            it.translationY = 0f
            val haftWidth = it.measuredWidth / 2
            val haftHeight = it.measuredHeight / 2
            val initX =
                Random.nextInt(
                    (lotteryPoolRect.left + haftWidth).toInt(),
                    (lotteryPoolRect.right - haftWidth).toInt()
                )
            val initY =
                Random.nextInt(
                    (lotteryPoolRect.top + haftHeight).toInt(),
                    (lotteryPoolRect.bottom - haftHeight).toInt()
                )
            val left = initX - haftWidth
            val top = initY - haftHeight
            it.layout(left, top, left + it.measuredWidth, top + it.measuredHeight)
            val pathMeasure =initPath(it)
            pathMeasureList.add(pathMeasure)
        }
    }

    var animation: ValueAnimator? = null

    fun addChildViews(list: List<ViewGroup>) {
        childViews = list
        list.forEach {
            addView(it)
        }
        invalidate()
    }

    fun startRoll() {
        animation?.cancel()

//        childViews.forEach {
//            initPath(it)
//        }
        animation = ValueAnimator.ofFloat(0f, 1f).apply {
            addUpdateListener {
                val progress = it.animatedValue as Float
                Log.d(TAG, "progress $progress duration ${it.totalDuration}")

                childViews.forEachIndexed { index, viewGroup ->
                   val pathMeasure = pathMeasureList.get(index)
                    val pos =FloatArray(2)
                    pathMeasure.getPosTan(pathMeasure.length*progress,pos,null)

                    viewGroup.x = pos[0]
                    viewGroup.y = pos[1]
                }
            }
            duration = 2000
            interpolator = LinearInterpolator()
        }
        animation?.start()

    }
//
//    private fun calculateOutLimit( viewGroup: ViewGroup) {
//        if (viewGroup.x <= lotteryPoolRect.left || viewGroup.x >= lotteryPoolRect.right - viewGroup.measuredWidth) {
//            ball.vX = -ball.vX
//        }
//        if (viewGroup.y <= lotteryPoolRect.top || viewGroup.y >= lotteryPoolRect.bottom - viewGroup.measuredHeight) {
//            ball.vY = -ball.vY
//        }
//
//    }

    fun initPath(view: View): PathMeasure {
        val width = view.measuredWidth
        val height = view.measuredHeight
        val left = view.left
        val top = view.top
        val x = view.x
        val pivotX = view.pivotX
        val y = view.y
        val pivotY = view.pivotY
        val path = Path()
        //path移动到view位置
        path.moveTo(x, y)
        lineToBorderLimit(path, width, height)
        lineToBorderLimit(path, width, height)
        lineToBorderLimit(path, width, height)
        lineToBorderLimit(path, width, height)
        lineToEndLimit(path, width, height)
        return PathMeasure(path,false)
    }

    private fun lineToEndLimit(path: Path, width: Int, height: Int) {
           path.lineTo(
               Random.nextInt(
                   lotteryPoolRect.left + width,
                   lotteryPoolRect.right - height
               ).toFloat(), (lotteryPoolRect.bottom -height).toFloat()
           )
    }

    private fun lineToBorderLimit(path:Path, width:Int, height:Int){
        when (Random.nextInt(1, 4)) {
            DIRECTION_TOP -> {
                path.lineTo(
                    Random.nextInt(
                        lotteryPoolRect.left + width,
                        lotteryPoolRect.right - height
                    ).toFloat(), lotteryPoolRect.top.toFloat()
                )
            }
            DIRECTION_BOTTOM -> {
                path.lineTo(
                    Random.nextInt(
                        lotteryPoolRect.left + width,
                        lotteryPoolRect.right - height
                    ).toFloat(), (lotteryPoolRect.bottom -height).toFloat()
                )
            }
            DIRECTION_LEFT -> {
                path.lineTo(
                    lotteryPoolRect.left.toFloat(),
                    Random.nextInt(lotteryPoolRect.top, lotteryPoolRect.bottom - height).toFloat()
                )
            }
            DIRECTION_RIGHT -> {
                path.lineTo(
                    (lotteryPoolRect.right - width).toFloat(),
                    Random.nextInt(lotteryPoolRect.top, lotteryPoolRect.bottom - height).toFloat()
                )
            }
        }
    }

    fun reset() {
        animation?.cancel()
        requestLayout()
    }



    companion object {
        const val DIRECTION_TOP = 1
        const val DIRECTION_BOTTOM = 2
        const val DIRECTION_LEFT = 3
        const val DIRECTION_RIGHT = 4
    }

}