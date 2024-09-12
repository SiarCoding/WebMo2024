// src/main/java/com/example/webmo2024app/EssenActivity.kt
package com.example.webmo2024app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.webmo2024app.fragments.AddFoodFragment
import com.example.webmo2024app.fragments.EssenListFragment
import com.example.webmo2024app.fragments.EssensplanFragment
import com.google.android.material.tabs.TabLayout
import androidx.viewpager.widget.ViewPager

class EssenActivity : AppCompatActivity() {

    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_essen)

        // Toolbar initialisieren
        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // TabLayout und ViewPager initialisieren
        tabLayout = findViewById(R.id.tabLayout)
        viewPager = findViewById(R.id.viewPager)

        // ViewPager Adapter setzen
        val adapter = ViewPagerAdapter(supportFragmentManager)
        adapter.addFragment(EssenListFragment(), "Essensliste")
        adapter.addFragment(AddFoodFragment(), "Neues Essen hinzufügen")
        adapter.addFragment(EssensplanFragment(), "Essenspläne")

        viewPager.adapter = adapter
        tabLayout.setupWithViewPager(viewPager)
    }

    // Adapter-Klasse für die Tabs
    class ViewPagerAdapter(manager: FragmentManager) : FragmentPagerAdapter(manager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
        private val fragmentList: MutableList<Fragment> = ArrayList()
        private val fragmentTitleList: MutableList<String> = ArrayList()

        override fun getItem(position: Int): Fragment {
            return fragmentList[position]
        }

        override fun getCount(): Int {
            return fragmentList.size
        }

        fun addFragment(fragment: Fragment, title: String) {
            fragmentList.add(fragment)
            fragmentTitleList.add(title)
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return fragmentTitleList[position]
        }
    }
}
