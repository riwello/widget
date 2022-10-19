package com.example.widget.widget

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.FrameLayout
import androidx.core.animation.addListener
import kotlin.random.Random


class LotteryLayout : FrameLayout {
    val TAG = "LotteryDrawView"

    private val lotteryPoolRect = Rect()

    private var childViews = listOf<ViewGroup>()
    private val pathMeasureList = ArrayList<PathMeasure>()

    private var exportView: View? = null

    private val lotteryBgPaint = Paint().apply {
        color = Color.LTGRAY
    }


    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    override fun onFinishInflate() {
        super.onFinishInflate()
        exportView = findViewWithTag<View>("export")

    }

    override fun dispatchDraw(canvas: Canvas) {

        super.dispatchDraw(canvas)


    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawRect(lotteryPoolRect, lotteryBgPaint)
    }

    override fun drawChild(canvas: Canvas, child: View?, drawingTime: Long): Boolean {

        if (child?.tag=="bg"){
            canvas.save()
            canvas.clipRect(lotteryPoolRect, Region.Op.DIFFERENCE)
            val ret= super.drawChild(canvas, child, drawingTime)
            canvas.restore()
            return ret
        }
        return super.drawChild(canvas, child, drawingTime)
    }

//    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
//        Log.d(TAG, "onMeasure")
////        val width = MeasureSpec.getSize(widthMeasureSpec)
////        val height = MeasureSpec.getSize(heightMeasureSpec)
////        childViews.forEach {
////            val lp = it.layoutParams
////            val childWidth = lp.width
////            val childHeight = lp.height
////            it.measure(
////                MeasureSpec.makeMeasureSpec(childWidth, MeasureSpec.AT_MOST),
////                MeasureSpec.makeMeasureSpec(childHeight, MeasureSpec.AT_MOST)
////            )
////        }
////
////        prizeView?.let {
////            val lp = it.layoutParams
////            val childWidth = lp.width
////            val childHeight = lp.height
////            it.measure(
////                MeasureSpec.makeMeasureSpec(childWidth, MeasureSpec.AT_MOST),
////                MeasureSpec.makeMeasureSpec(childHeight, MeasureSpec.AT_MOST)
////            )
////        }
////        setMeasuredDimension(width, height)
//
//    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        val widthPercent = 0.8f
        val poolWidth = (w * widthPercent).toInt()
        val left = ((w - poolWidth) / 2)
        val top = 0
        lotteryPoolRect.set(left, top, w - left, top + poolWidth)
        initBallRandomPositon()
    }

    private fun initBallRandomPositon() {
        pathMeasureList.clear()
        childViews.forEach {
            it.translationX = 0f
            it.translationY = 0f
            val haftWidth = it.measuredWidth / 2
            val haftHeight = it.measuredHeight / 2
            val initX =
                Random.nextInt(
                    (lotteryPoolRect.left).toInt(),
                    (lotteryPoolRect.right - it.measuredWidth).toInt()
                )
            val initY =
                Random.nextInt(
                    (lotteryPoolRect.top).toInt(),
                    (lotteryPoolRect.bottom - it.measuredHeight).toInt()
                )
//            val left = initX - haftWidth
//            val top = initY - haftHeight
            it.x = initX.toFloat()
            it.y = initY.toFloat()
//            it.layout(left, top, left + it.measuredWidth, top + it.measuredHeight)
            val pathMeasure = initPath(it)
            pathMeasureList.add(pathMeasure)
        }
    }


    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)

        Log.d(TAG, "onLayout")
    }

    var animatorSet: AnimatorSet? = null

    fun addChildViews(list: List<ViewGroup>) {
        childViews = list
        list.forEach {
            addView(it)
        }
        requestLayout()
    }

    @SuppressLint("ObjectAnimatorBinding")
    fun startRoll() {
        animation?.cancel()


        val rollAnimation = ValueAnimator.ofFloat(0f, 1f).apply {
            addUpdateListener {
                val progress = it.animatedValue as Float
                Log.d(TAG, "progress $progress duration ")
                childViews.forEachIndexed { index, viewGroup ->
                    val pathMeasure = pathMeasureList.get(index)
                    val pos = FloatArray(2)
                    pathMeasure.getPosTan(pathMeasure.length * progress, pos, null)
                    viewGroup.x = pos[0]
                    viewGroup.y = pos[1]
                }
            }
            duration = 2000
            interpolator = LinearInterpolator()
        }
        val exportItemAlpha = ObjectAnimator.ofFloat(exportView, "alpha", 1f, 0.5f).apply {
            duration = 1000
            addListener(onStart = {
                exportView?.visibility = VISIBLE
            }, onEnd = {
                exportView?.visibility = GONE

            }, onCancel = {
                exportView?.visibility = GONE
            })
        }
        val exportItemScaleX =
            ObjectAnimator.ofFloat(exportView, "scaleY", 0.5f, 1f).apply { duration = 1000 }

        val exportItemScaleY =
            ObjectAnimator.ofFloat(exportView, "scaleX", 0.5f, 1f).apply { duration = 1000 }

        val exportItemSet = AnimatorSet().apply {
            play(exportItemAlpha).with(exportItemScaleX).with(exportItemScaleY)
            duration = 500
        }

        animatorSet = AnimatorSet().apply {
            play(rollAnimation).before(exportItemSet)/*.with(exportItemScaleX).with(exportItemScaleY)*/
        }

        animatorSet?.start()

    }


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
        return PathMeasure(path, false)
    }

    private fun lineToEndLimit(path: Path, width: Int, height: Int) {
        path.lineTo(
            Random.nextInt(
                lotteryPoolRect.left,
                lotteryPoolRect.right - height
            ).toFloat(), (lotteryPoolRect.bottom - height).toFloat()
        )
    }

    private fun lineToBorderLimit(path: Path, width: Int, height: Int) {
        when (Random.nextInt(1, 4)) {
            DIRECTION_TOP -> {
                path.lineTo(
                    Random.nextInt(
                        lotteryPoolRect.left,
                        lotteryPoolRect.right - width
                    ).toFloat(), lotteryPoolRect.top.toFloat()
                )
            }
            DIRECTION_BOTTOM -> {
                path.lineTo(
                    Random.nextInt(
                        lotteryPoolRect.left,
                        lotteryPoolRect.right - width
                    ).toFloat(), (lotteryPoolRect.bottom - height).toFloat()
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
        initBallRandomPositon()
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