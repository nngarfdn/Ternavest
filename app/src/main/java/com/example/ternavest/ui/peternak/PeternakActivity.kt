package com.example.ternavest.ui.peternak

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.ternavest.MainActivity
import com.example.ternavest.R
import com.example.ternavest.ui.peternak.kelola.proyek.KelolaFragment
import com.example.ternavest.ui.both.portfolio.PortfolioFragment
import com.example.ternavest.ui.both.profile.ProfileFragment
import com.iammert.library.readablebottombar.ReadableBottomBar
import kotlinx.android.synthetic.main.activity_peternak.*


class PeternakActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_peternak)
        loadFragment(KelolaFragment())


        bn_main.setOnItemSelectListener( object : ReadableBottomBar.ItemSelectListener{
            override fun onItemSelected(index: Int) {
                when (index){
                    0 -> loadFragment(KelolaFragment())
                    1 ->loadFragment(PortfolioFragment())
                    2 -> loadFragment(ProfileFragment())
                }
            }
        })
    }

    private fun loadFragment(fragment: Fragment?): Boolean {
        if (fragment != null) {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.fl_container, fragment)
                    .addToBackStack(null)
                    .commit()
            return true
        }
        return false
    }

    override fun onBackPressed() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }


}