// src/main/java/com/example/webmo2024app/EssenActivity.kt
package com.example.webmo2024app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.webmo2024app.fragments.AddFoodFragment
import com.example.webmo2024app.fragments.EssenListFragment
import com.example.webmo2024app.fragments.EssensplanFragment
import com.example.webmo2024app.fragments.EssensplanErstellenFragment // Importiere dein neues Fragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator


class EssenActivity : AppCompatActivity() {

    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_essen)

        // Toolbar initialisieren
        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // TabLayout und ViewPager2 initialisieren
        tabLayout = findViewById(R.id.tabLayout)
        viewPager = findViewById(R.id.viewPager)

        // ViewPager2 Adapter setzen
        val adapter = MyFragmentAdapter(this)
        adapter.addFragment(EssenListFragment(), "Essensliste")
        adapter.addFragment(AddFoodFragment(), "Neues Essen hinzufügen")
        adapter.addFragment(EssensplanErstellenFragment(), "Essensplan erstellen") // Neuer Tab hinzugefügt
        adapter.addFragment(EssensplanFragment(), "Essenspläne")

        viewPager.adapter = adapter

        // Verknüpfe TabLayout mit ViewPager2
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = adapter.getPageTitle(position)
        }.attach()
    }

    // Adapter-Klasse für die Tabs
    class MyFragmentAdapter(activity: AppCompatActivity) : FragmentStateAdapter(activity) {
        private val fragmentList: MutableList<Fragment> = ArrayList()
        private val fragmentTitleList: MutableList<String> = ArrayList()

        override fun getItemCount(): Int {
            return fragmentList.size
        }

        override fun createFragment(position: Int): Fragment {
            return fragmentList[position]
        }

        fun addFragment(fragment: Fragment, title: String) {
            fragmentList.add(fragment)
            fragmentTitleList.add(title)
        }

        fun getPageTitle(position: Int): String {
            return fragmentTitleList[position]
        }
    }
}
