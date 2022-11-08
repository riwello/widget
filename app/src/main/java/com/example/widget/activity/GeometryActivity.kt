package com.example.widget.activity

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.widget.databinding.ActivityGeometryBinding

class GeometryActivity: AppCompatActivity() {
 private   val viewBinding by lazy { ActivityGeometryBinding.inflate(layoutInflater) }

    val tag= "GeometryActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(viewBinding.root)
        var toast:Toast?= null
        viewBinding.ivLeft.setOnClickListener {
            Log.e("GeometryImageView","ivLeft")
            toast?.cancel()
            toast = Toast.makeText(this,"左边",Toast.LENGTH_SHORT)
            toast?.show()
        }
        viewBinding.ivCenter.setOnClickListener {
            Log.e("GeometryImageView","ivCenter")
            toast?.cancel()
            toast = Toast.makeText(this,"中间",Toast.LENGTH_SHORT)
            toast?.show()
        }
        viewBinding.ivRight.setOnClickListener {
            Log.e("GeometryImageView","ivRight")
            toast?.cancel()
            toast = Toast.makeText(this,"右边",Toast.LENGTH_SHORT)
            toast?.show()
        }
    }
}