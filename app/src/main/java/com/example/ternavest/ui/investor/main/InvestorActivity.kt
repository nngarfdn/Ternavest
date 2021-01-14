package com.example.ternavest.ui.investor.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager.widget.ViewPager
import com.example.ternavest.R
import com.example.ternavest.adapter.pager.InvestorPagerAdapter
import com.iammert.library.readablebottombar.ReadableBottomBar
import kotlinx.android.synthetic.main.activity_peternak.*

class InvestorActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_investor)

        val pagerAdapter = InvestorPagerAdapter(supportFragmentManager)
        val viewPager = findViewById<ViewPager>(R.id.view_pager_main)
        viewPager.adapter = pagerAdapter

        bn_main.setOnItemSelectListener(object : ReadableBottomBar.ItemSelectListener {
            override fun onItemSelected(index: Int) {
                viewPager.currentItem = index
            }
        })

        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageSelected(position: Int) {
                bn_main.selectItem(position)
            }
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
            override fun onPageScrollStateChanged(state: Int) {}
        })
    }
}