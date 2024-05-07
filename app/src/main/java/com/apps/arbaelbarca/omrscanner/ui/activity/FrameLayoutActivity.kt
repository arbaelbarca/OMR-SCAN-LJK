package com.apps.arbaelbarca.omrscanner.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.apps.arbaelbarca.omrscanner.R
import com.apps.arbaelbarca.omrscanner.ui.home.HomeFragment

class FrameLayoutActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_frame_layout)

        loadFragment()
    }

    private fun loadFragment() {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.frameLayout, HomeFragment())
            .commit()
    }
}