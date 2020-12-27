package com.example.ternavest.ui.peternak

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.ternavest.R
import com.example.ternavest.ui.peternak.kelola.KelolaFragment
import com.example.ternavest.ui.peternak.peminat.PeminatFragment
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
                    1 ->loadFragment(PeminatFragment())
                    2 -> loadFragment(KelolaFragment())
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
}