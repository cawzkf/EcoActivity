package com.ecoactivity.app

import android.os.Bundle
import android.view.Menu
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.appcompat.app.AppCompatActivity
import com.ecoactivity.app.R
import com.ecoactivity.app.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        binding.appBarMain.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null)
                .setAnchorView(R.id.fab).show()
        }

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_painel,
                R.id.nav_graficos,
                R.id.nav_aparelhos,
                R.id.nav_notificações,
                R.id.nav_conta
            ),
            drawerLayout
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_painel -> {
                    navController.navigate(R.id.nav_painel)
                    drawerLayout.closeDrawers()
                    true
                }
                R.id.nav_graficos -> {
                    navController.navigate(R.id.nav_graficos)
                    drawerLayout.closeDrawers()
                    true
                }
                R.id.nav_aparelhos -> {
                    navController.navigate(R.id.nav_aparelhos)
                    drawerLayout.closeDrawers()
                    true
                }
                R.id.nav_notificações -> {
                    navController.navigate(R.id.nav_notificações)
                    drawerLayout.closeDrawers()
                    true
                }
                R.id.nav_conta -> {
                    navController.navigate(R.id.nav_conta)
                    drawerLayout.closeDrawers()
                    true
                }
                R.id.buttonLogin -> {
                    navController.navigate(R.id.loginFragment)
                    drawerLayout.closeDrawers()
                    true
                }
                else -> false
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}
