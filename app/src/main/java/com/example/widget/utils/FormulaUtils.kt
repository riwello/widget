package com.example.widget.utils

import android.graphics.PointF
import android.graphics.RectF
import kotlin.math.*

object FormulaUtils {

    fun getArcPoint(rectF: RectF, angle: Float): PointF {
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

//    /**
//     * 根据弧度 计算离心角
//     * @param a 长轴
//     * @param b 短轴
//     */
//     fun eccentricAngle(a: Float, b: Float, radians: Float): Float {
//        return atan2(a * sin(radians), b * cos(radians))
//    }


    /**
     * 根据圆心角 计算离心角
     * @param a 长轴
     * @param b 短轴
     * @param centralAngle 圆心角 角度
     * @return
     */
    fun eccentricAngle(a: Float, b: Float, centralAngle: Float): Float {
        // 转弧度
        val centralRadians = Math.toRadians(centralAngle.toDouble())
        //离心角弧度
        val eccentricRadians = atan2(a * sin(centralRadians), b * cos(centralRadians))
        //转离心角 角度
        return Math.toDegrees(eccentricRadians).toFloat()
    }


    fun getAngle(pointA: PointF, pointB: PointF, pointC: PointF): Float {
        val lengthAB = sqrt(
            (pointA.x - pointB.x).pow(2.0f) +
                    (pointA.y - pointB.y).pow(2f)
        )
        val lengthAC = sqrt(
            (pointA.x - pointC.x).pow(2.0f) +
                    (pointA.y - pointC.y).pow(2.0f)
        )
        val lengthBC = sqrt(
            (pointB.x - pointC.x).pow(2.0f) +
                    (pointB.y - pointC.y).pow(2.0f)
        )
        val cosA = (lengthAB.pow(2.0f) + lengthAC.pow(2.0f) - lengthBC.pow(2.0f)) /
                (2 * lengthAB * lengthAC)
        return (acos(cosA) * 180 / Math.PI).toFloat()
    }


}