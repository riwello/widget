package com.example.widget.widget

import android.content.Context
import android.graphics.Path
import android.graphics.PathMeasure
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import kotlin.random.Random

class PrizePool:FrameLayout {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )
    private val lotteryPoolRect= Rect()
    private var childViews = listOf<ViewGroup>()
    private val pathMeasureList = ArrayList<PathMeasure>()

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        lotteryPoolRect.set(0, 0, w, h)
        initBallRandomPositon()
    }

   private fun initPath(view: View): PathMeasure {
        val width = view.measuredWidth
        val height = view.measuredHeight
        val x = view.x
        val y = view.y
        val path = Path()
        //path移动到view位置
        path.moveTo(x, y)
       var direction= Random.nextInt(1, 4)
       for (i in 0..3) {
           direction = lineToBorderLimit(path, width, height,direction)
       }
        lineToEndLimit(path, width, height)
        return PathMeasure(path, false)
    }

    private fun lineToEndLimit(path: Path, width: Int, height: Int) {
        path.lineTo(
            Random.nextInt(
                lotteryPoolRect. left,
                lotteryPoolRect.right - height
            ).toFloat(), (lotteryPoolRect.bottom - height).toFloat()
        )
    }

    private fun lineToBorderLimit(path: Path, width: Int, height: Int,lastDirection:Int): Int {
        var direction=  Random.nextInt(1, 4)
        while (direction==lastDirection){
            direction=  Random.nextInt(1, 4)
        }
        when (direction) {
            LotteryLayout.DIRECTION_TOP -> {
                path.lineTo(
                    Random.nextInt(
                        lotteryPoolRect.left,
                        lotteryPoolRect.right - width
                    ).toFloat(), lotteryPoolRect.top.toFloat()
                )
            }
            LotteryLayout.DIRECTION_BOTTOM -> {
                path.lineTo(
                    Random.nextInt(
                        lotteryPoolRect.left,
                        lotteryPoolRect.right - width
                    ).toFloat(), (lotteryPoolRect.bottom - height).toFloat()
                )
            }
            LotteryLayout.DIRECTION_LEFT -> {
                path.lineTo(
                    lotteryPoolRect.left.toFloat(),
                    Random.nextInt(lotteryPoolRect.top, lotteryPoolRect.bottom - height).toFloat()
                )
            }
            LotteryLayout.DIRECTION_RIGHT -> {
                path.lineTo(
                    (lotteryPoolRect.right - width).toFloat(),
                    Random.nextInt(lotteryPoolRect.top, lotteryPoolRect.bottom - height).toFloat()
                )
            }
        }
        return direction
    }


     fun initBallRandomPositon() {
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

    fun addChildViews(list: List<ViewGroup>) {
        childViews = list
        list.forEach {
            addView(it)
        }
        requestLayout()
    }

    fun setBallPosition(progress: Float) {
        childViews.forEachIndexed { index, viewGroup ->
            val pathMeasure = pathMeasureList.get(index)
            val pos = FloatArray(2)
            pathMeasure.getPosTan(pathMeasure.length * progress, pos, null)
            viewGroup.x = pos[0]
            viewGroup.y = pos[1]
        }
    }


}