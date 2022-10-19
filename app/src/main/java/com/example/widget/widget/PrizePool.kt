//package com.example.widget.widget
//
//import android.content.Context
//import android.graphics.Path
//import android.graphics.PathMeasure
//import android.util.AttributeSet
//import android.view.View
//import android.widget.FrameLayout
//import kotlin.random.Random
//
//class PrizePool:FrameLayout {
//    constructor(context: Context) : super(context)
//    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
//    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
//        context,
//        attrs,
//        defStyleAttr
//    )
//
//    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
//        super.onSizeChanged(w, h, oldw, oldh)
//        initBallRandomPositon()
//    }
//
//   private fun initPath(view: View): PathMeasure {
//        val width = view.measuredWidth
//        val height = view.measuredHeight
//        val left = view.left
//        val top = view.top
//        val x = view.x
//        val pivotX = view.pivotX
//        val y = view.y
//        val pivotY = view.pivotY
//        val path = Path()
//        //path移动到view位置
//        path.moveTo(x, y)
//        lineToBorderLimit(path, width, height)
//        lineToBorderLimit(path, width, height)
//        lineToBorderLimit(path, width, height)
//        lineToBorderLimit(path, width, height)
//        lineToEndLimit(path, width, height)
//        return PathMeasure(path, false)
//    }
//
//    private fun lineToEndLimit(path: Path, width: Int, height: Int) {
//        path.lineTo(
//            Random.nextInt(
//                lotteryPoolRect.left,
//                lotteryPoolRect.right - height
//            ).toFloat(), (lotteryPoolRect.bottom - height).toFloat()
//        )
//    }
//
//    private fun lineToBorderLimit(path: Path, width: Int, height: Int) {
//        when (Random.nextInt(1, 4)) {
//            LotteryLayout.DIRECTION_TOP -> {
//                path.lineTo(
//                    Random.nextInt(
//                        lotteryPoolRect.left,
//                        lotteryPoolRect.right - width
//                    ).toFloat(), lotteryPoolRect.top.toFloat()
//                )
//            }
//            LotteryLayout.DIRECTION_BOTTOM -> {
//                path.lineTo(
//                    Random.nextInt(
//                        lotteryPoolRect.left,
//                        lotteryPoolRect.right - width
//                    ).toFloat(), (lotteryPoolRect.bottom - height).toFloat()
//                )
//            }
//            LotteryLayout.DIRECTION_LEFT -> {
//                path.lineTo(
//                    lotteryPoolRect.left.toFloat(),
//                    Random.nextInt(lotteryPoolRect.top, lotteryPoolRect.bottom - height).toFloat()
//                )
//            }
//            LotteryLayout.DIRECTION_RIGHT -> {
//                path.lineTo(
//                    (lotteryPoolRect.right - width).toFloat(),
//                    Random.nextInt(lotteryPoolRect.top, lotteryPoolRect.bottom - height).toFloat()
//                )
//            }
//        }
//    }
//
//
//    private fun initBallRandomPositon() {
//        pathMeasureList.clear()
//        childViews.forEach {
//            it.translationX = 0f
//            it.translationY = 0f
//            val haftWidth = it.measuredWidth / 2
//            val haftHeight = it.measuredHeight / 2
//            val initX =
//                Random.nextInt(
//                    (lotteryPoolRect.left).toInt(),
//                    (lotteryPoolRect.right - it.measuredWidth).toInt()
//                )
//            val initY =
//                Random.nextInt(
//                    (lotteryPoolRect.top).toInt(),
//                    (lotteryPoolRect.bottom - it.measuredHeight).toInt()
//                )
////            val left = initX - haftWidth
////            val top = initY - haftHeight
//            it.x = initX.toFloat()
//            it.y = initY.toFloat()
////            it.layout(left, top, left + it.measuredWidth, top + it.measuredHeight)
//            val pathMeasure = initPath(it)
//            pathMeasureList.add(pathMeasure)
//        }
//    }
//
//}