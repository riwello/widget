package com.example.widget.utils

import android.content.Context
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

/**
 * @description 流工具
 * @author 生擒嫦娥炖玉兔
 * @time 2022/4/26 14:53
 */
object StreamUtil {

    /**
     * @description 读取Asset文件
     * @param assetFileName 文件名称
     * @author 生擒嫦娥炖玉兔
     * @time 2022/4/26 14:56
     */
    fun loadAssetFile(context: Context, assetFileName: String):String{
        val stringBuilder = StringBuilder()
        val am = context.assets
        try {
            BufferedReader(InputStreamReader(am.open(assetFileName))).use { bf ->
                var line: String?
                while (bf.readLine().also { line = it } != null) {
                    stringBuilder.append(line)
                }
            }
        } catch (e: IOException) {
            return ""
        }
        return stringBuilder.toString()

       /* val inputStreamReader = InputStreamReader(context.assets.open(assetFileName), "UTF-8")
        val bufferedReader = BufferedReader(inputStreamReader)

        var line: String?
        val stringBuilder = StringBuilder()
        while (bufferedReader.readLine().also { line = it } != null) {
            stringBuilder.append(line)
        }
        bufferedReader.close()
        inputStreamReader.close()
        return stringBuilder.toString()*/
    }
}