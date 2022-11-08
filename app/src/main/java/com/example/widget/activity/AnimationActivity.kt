package com.example.widget.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.example.widget.R
import com.example.widget.databinding.ActivityAnimationBinding

class AnimationActivity: AppCompatActivity() {

    val viewBind by lazy { ActivityAnimationBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(viewBind.root)
        viewBind.lotteryDrawView.apply {

            setOnClickListener {
//                reset()
            }
        }
        viewBind.pool.addChildViews(
            arrayListOf(
            createChildView(),
            createChildView(),
            createChildView(),
            createChildView(),
            createChildView(),
            createChildView(),
        )
        )
        viewBind.btnRoll.setOnClickListener {
            viewBind.lotteryDrawView.startRoll()
        }


    }

    private fun createChildView(): ViewGroup {
       return LayoutInflater.from(this).inflate(R.layout.item_child,viewBind.lotteryDrawView,false) as ViewGroup

    }
}