package edu.vt.cs.cs5254.gallery

import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import edu.vt.cs.cs5254.gallery.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_main)
            if (savedInstanceState == null) {
                supportFragmentManager
                    .beginTransaction()
                    .add(R.id.fragment_container, PhotoGalleryFragment.newInstance())
                    .commit()
            }
        }
}