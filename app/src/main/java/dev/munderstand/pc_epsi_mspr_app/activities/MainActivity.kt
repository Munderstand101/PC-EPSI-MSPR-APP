package dev.munderstand.pc_epsi_mspr_app.activities

import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.tabs.TabLayout
import dev.munderstand.pc_epsi_mspr_app.R
import dev.munderstand.pc_epsi_mspr_app.activities.common.BaseActivity
import dev.munderstand.pc_epsi_mspr_app.fragments.*

class MainActivity : BaseActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomNavigationView = findViewById(R.id.navigation)
        bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.tabAcceuil -> {
                    replaceFragment(AcceuilFragment())
                    true
                }
                R.id.tabMaps -> {
                    replaceFragment(MapFragment())
                    true
                }
                R.id.tabCamera -> {
                    replaceFragment(PhotoPlanteFragment())// Handle camera placeholder item
                    true
                }
                R.id.tabPlantes -> {
                    replaceFragment(ConversationsFragment())
                    true
                }
                R.id.tabAnnonces -> {
                    replaceFragment(AnnoncesFragment())
                    true
                }
                else -> false
            }
        }

        // Set the default fragment
        replaceFragment(AcceuilFragment())
    }

    fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.content, fragment)
            .commit()
    }
}
