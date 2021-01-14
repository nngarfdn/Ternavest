package com.example.ternavest.ui.peternak.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.example.ternavest.R
import com.example.ternavest.adaper.pager.PeternakPagerAdapter
import com.iammert.library.readablebottombar.ReadableBottomBar
import kotlinx.android.synthetic.main.activity_peternak.*


class PeternakActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_peternak)

        val pagerAdapter = PeternakPagerAdapter(supportFragmentManager)
        val viewPager = findViewById<ViewPager>(R.id.view_pager_main)
        viewPager.adapter = pagerAdapter

        bn_main.setOnItemSelectListener(object : ReadableBottomBar.ItemSelectListener {
            override fun onItemSelected(index: Int) {
                viewPager.currentItem = index
            }
        })

        viewPager.addOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageSelected(position: Int) {
                bn_main.selectItem(position)
            }
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
            override fun onPageScrollStateChanged(state: Int) {}
        })
    }
}