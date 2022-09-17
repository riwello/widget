package com.example.widget.utils

import android.graphics.PointF
import android.graphics.RectF
import kotlin.math.*

class formulaUtils {

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
     * 根据弧度 计算离心角
     * @param a 长轴
     * @param b 短轴
     */
    private fun eccentricAngle(a: Float, b: Float, radians: Float): Float {
        return atan2(a * sin(radians), b * cos(radians))
    }

}