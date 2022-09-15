package com.example.widget

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.SeekBar
import android.widget.TextView
import com.example.widget.entiy.AddressEntity
import com.example.widget.utils.StreamUtil
import com.example.widget.widget.ArcView
import com.github.gzuliyujiang.wheelpicker.AddressPicker
import com.github.gzuliyujiang.wheelpicker.annotation.AddressMode
import com.github.gzuliyujiang.wheelpicker.utility.AddressJsonParser
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class MainActivity : AppCompatActivity() {
    val TAG = "MainActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val arcView= findViewById<ArcView>(R.id.arc_view)
        val tvProgress= findViewById<TextView>(R.id.tv_progress)

        findViewById<SeekBar>(R.id.progress).apply {
            setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(p0: SeekBar?, progress: Int, formUser: Boolean) {
//                    Log.d(TAG,"progress change $progress")
                    tvProgress.setText("progress $progress  angle ${progress/100f*360f}")
                    arcView.setAngleProgress(progress/100f)

                }

                override fun onStartTrackingTouch(p0: SeekBar?) {

                }

                override fun onStopTrackingTouch(p0: SeekBar?) {

                }

            })
        }


    }

    fun printJsonData() {
        val postStr = StreamUtil.loadAssetFile(this, "json/address.json")
        val data =
            Gson().fromJson<ArrayList<AddressEntity>>(
                postStr,
                object : TypeToken<ArrayList<AddressEntity>>() {}.type
            )
        Log.d("Test", "地址数量 ${data.size}")

        for (province in data) {
            if (province.children == null || province.children.size == 0) {
                Log.e(TAG, "province ${province.label}")
            }
            for (city in province.children) {
                if (city.children == null || city.children.size == 0) {
                    Log.e(TAG, "province ${province.label} city ${city.label}")
                }

                for (county in city.children) {
                    if (county.children == null || county.children.size == 0) {
                        Log.e(
                            TAG,
                            "province ${province.label} city ${city.label}  county${county.label}"
                        )
                    }
//                    for (street in county.children) {
//
//
//
//                    }
                }
            }
        }
    }

    fun onViewClick(view: View) {


    }
}