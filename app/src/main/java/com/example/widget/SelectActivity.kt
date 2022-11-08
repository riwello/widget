package com.example.widget

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.example.widget.activity.AnimationActivity
import com.example.widget.activity.GeometryActivity
import com.example.widget.activity.MainActivity

class SelectActivity : AppCompatActivity() {

    val mAdapter by lazy {
        object : BaseQuickAdapter<Item, BaseViewHolder>(R.layout.item_activity) {
            override fun convert(holder: BaseViewHolder, item: Item) {
                holder.setText(R.id.tv_name, item.name)
            }
        }.apply {
            setOnItemClickListener { adapter, view, position ->
                startActivity(Intent(this@SelectActivity, getItem(position).clazz))
            }
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select)
        val recyclerview = findViewById<RecyclerView>(R.id.recyclerview)
        recyclerview.apply {
            adapter = mAdapter

        }
        mAdapter.setNewInstance(
            mutableListOf(
                Item("等级椭圆", MainActivity::class.java),
                Item("奖池动画", AnimationActivity::class.java),
                Item("几何图形按钮", GeometryActivity::class.java),
            )
        )
    }

    class Item(
    val name: String,
    val clazz: Class<*>
    )
}