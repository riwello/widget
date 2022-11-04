package com.example.widget.widget

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.FrameLayout
import androidx.core.animation.addListener


class LotteryLayout : FrameLayout {
    val TAG = "LotteryDrawView"

    private var exportView: View? = null

    private var poolView: PrizePool? = null

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    private var animatorEndCallBack: () -> Unit = {}
    fun setAnimatorEndCallBack(animatorEndCallBack: () -> Unit) {
        this.animatorEndCallBack = animatorEndCallBack
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        poolView = findViewWithTag("pool")
        exportView = findViewWithTag("export")
        exportView?.visibility = INVISIBLE
    }

    var animatorSet: AnimatorSet? = null

    @SuppressLint("ObjectAnimatorBinding")
    fun startRoll() {
//        reset()
        animatorSet?.cancel()
        //奖池动画
        val rollAnimation = ValueAnimator.ofFloat(0f, 1f).apply {
            addUpdateListener {
                val progress = it.animatedValue as Float
                Log.d(TAG, "progress $progress duration ")
                poolView?.setBallPosition(progress)
            }
            duration = 2000
            interpolator = LinearInterpolator()
        }
        //出奖口动画
        val exportItemSet = AnimatorSet().apply {
            play(ObjectAnimator.ofFloat(exportView, "alpha", 1f, 0.5f))
                .with(ObjectAnimator.ofFloat(exportView, "scaleY", 0.8f, 1f))
                .with(ObjectAnimator.ofFloat(exportView, "scaleX", 0.8f, 1f))
            addListener(onStart = {
                exportView?.visibility = VISIBLE
            }, onEnd = {
                exportView?.visibility = INVISIBLE
                //
            }, onCancel = {
                exportView?.visibility = INVISIBLE
            })
            duration = 500
        }
        //动画合集
        animatorSet = AnimatorSet().apply {
            play(rollAnimation).before(exportItemSet)
            addListener(onEnd = {
                animatorEndCallBack.invoke()
                poolView?.resetPath()
            })
        }
        animatorSet?.start()
    }

    private fun reset() {
        animatorSet?.cancel()
        poolView?.resetPath()
        exportView?.visibility = GONE
//        requestLayout()
    }

    companion object {
        const val DIRECTION_TOP = 1
        const val DIRECTION_BOTTOM = 2
        const val DIRECTION_LEFT = 3
        const val DIRECTION_RIGHT = 4
    }
}