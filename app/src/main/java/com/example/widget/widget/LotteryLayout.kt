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

    override fun onFinishInflate() {
        super.onFinishInflate()
        exportView = findViewWithTag("export")
        poolView = findViewWithTag("pool")
    }

    var animatorSet: AnimatorSet? = null

    @SuppressLint("ObjectAnimatorBinding")
    fun startRoll() {
        animatorSet?.cancel()
        val rollAnimation = ValueAnimator.ofFloat(0f, 1f).apply {
            addUpdateListener {
                val progress = it.animatedValue as Float
                Log.d(TAG, "progress $progress duration ")
                poolView?.setBallPosition(progress)
            }
            duration = 2000
//            interpolator = LinearInterpolator()
        }

        val exportItemSet = AnimatorSet().apply {
            play(ObjectAnimator.ofFloat(exportView, "alpha", 1f, 0.5f))
                .with(ObjectAnimator.ofFloat(exportView, "scaleY", 0.8f, 1f))
                .with(ObjectAnimator.ofFloat(exportView, "scaleX", 0.8f, 1f))
            addListener(onStart = {
                exportView?.visibility = VISIBLE
            }, onEnd = {
                exportView?.visibility = GONE

            }, onCancel = {
                exportView?.visibility = GONE
            })
            duration = 500
        }

        animatorSet = AnimatorSet().apply {
            play(rollAnimation).before(exportItemSet)/*.with(exportItemScaleX).with(exportItemScaleY)*/
        }
        animatorSet?.start()
    }

    fun reset() {
        animatorSet?.cancel()
        poolView?.initBallRandomPositon()
        requestLayout()
    }


    companion object {
        const val DIRECTION_TOP = 1
        const val DIRECTION_BOTTOM = 2
        const val DIRECTION_LEFT = 3
        const val DIRECTION_RIGHT = 4
    }

}