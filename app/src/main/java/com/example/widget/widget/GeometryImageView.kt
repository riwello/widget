package com.example.widget.widget

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent

/**
 * 屏蔽Imageview透明区域的点击事件
 * 用于几个不规则几何图形的ImageVIew一起摆放且透明区域存在叠加时,点击事件不好处理的情况
 * 目前仅适用于 [android.widget.ImageView.getDrawable] is [BitmapDrawable]
 */
class GeometryImageView : androidx.appcompat.widget.AppCompatImageView {

    val TAG = "GeometryImageView"

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    private fun isTransparent(x: Float, y: Float): Boolean {
        val srcBitmap = drawable as? BitmapDrawable
        srcBitmap?.bitmap?.let {
            val srcX = x / width * it.width
            val srcY = y / height * it.height
            if (IntRange(0, it.width).contains(srcX.toInt())
                && IntRange(0, it.height).contains(srcY.toInt())
            ) {
                return try {
                    val pixel = it.getPixel(srcX.toInt(), srcY.toInt())
                    val transparent = pixel == Color.TRANSPARENT
                    Log.e(TAG, "$tag x $srcX   y $srcY pixel${pixel} transparent ${transparent}")
                    transparent
                } catch (e: Exception) {
                    Log.e(TAG, "$tag x $x   y $y ${e.message}")
                    false
                }
            }
        }
        return false
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        val result =
            if ((event.action == MotionEvent.ACTION_DOWN || event.action == MotionEvent.ACTION_UP)
                && isTransparent(event.x, event.y)
            ) {
                false
            } else {
                super.dispatchTouchEvent(event)
            }
//        Log.e(TAG, "${tag} dispatchTouchEvent ${event.action.actionToString()} ${result}")
        return result
    }

//    override fun onTouchEvent(event: MotionEvent?): Boolean {
//        val clickable = isClickable
//
//
//        val result = super.onTouchEvent(event)
//        if (clickable) {
//            isClickable = true6
//        }
//        return result
//
//    }

    private fun Int.actionToString(): String {
        return when (this) {
            MotionEvent.ACTION_DOWN -> "ACTION_DOWN"
            MotionEvent.ACTION_UP -> "ACTION_UP"
            MotionEvent.ACTION_MOVE -> "ACTION_MOVE"
            else -> toString()
        }
    }

}