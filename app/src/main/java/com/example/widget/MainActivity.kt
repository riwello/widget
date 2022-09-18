package com.example.widget

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.SeekBar
import android.widget.TextView
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.example.widget.entiy.AddressEntity
import com.example.widget.utils.StreamUtil
import com.example.widget.widget.ArcView
import com.github.gzuliyujiang.wheelpicker.AddressPicker
import com.github.gzuliyujiang.wheelpicker.annotation.AddressMode
import com.github.gzuliyujiang.wheelpicker.utility.AddressJsonParser
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class MainActivity : AppCompatActivity() {
    val TAG = "MainActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val arcView = findViewById<ArcView>(R.id.arc_view)
        val viewPager2 = findViewById<ViewPager2>(R.id.viewpager)
        val levelArr = arrayOf("0", "1", "2", "3", "4", "5")
        viewPager2.adapter =
            object : BaseQuickAdapter<String, BaseViewHolder>(
                R.layout.item_paghe,
                levelArr.toMutableList()
            ) {
                override fun convert(holder: BaseViewHolder, item: String) {
                    holder.setText(R.id.text, item)
                }

            }

        arcView.bindViewPager(viewPager2)




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