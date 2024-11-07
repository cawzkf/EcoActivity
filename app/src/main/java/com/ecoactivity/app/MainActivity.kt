package com.ecoactivity.app

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.appcompat.app.AppCompatActivity
import com.ecoactivity.app.R
import com.ecoactivity.app.databinding.ActivityMainBinding
import com.ecoactivity.app.ui.LoginFragment
import com.ecoactivity.app.ui.NovoFragment // Certifique-se de importar seu NovoFragment
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var navController: NavController

    private val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
        val user = firebaseAuth.currentUser

        if (user == null) {
            // Mostra o fragmento de login ou registro
            showAuthFragment(LoginFragment())
        } else {
            // O usuário está autenticado, navegue para a tela inicial
            navController.navigate(R.id.nav_painel)
            findViewById<View>(R.id.fragment_container).visibility = View.GONE // Esconde o container de autenticação
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        auth.addAuthStateListener(authStateListener)

        setSupportActionBar(binding.appBarMain.toolbar)

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView

        navController = findNavController(R.id.nav_host_fragment_content_main)

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_painel,
                R.id.nav_graficos,
                R.id.nav_aparelhos,
                R.id.nav_notificacoes,
                R.id.nav_conta,
                R.id.nav_educacional
            ),
            drawerLayout
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        navView.setNavigationItemSelectedListener { menuItem ->
            if (auth.currentUser == null) {
                Snackbar.make(binding.drawerLayout, "Você precisa estar logado para acessar essa área", Snackbar.LENGTH_SHORT).show()
                return@setNavigationItemSelectedListener false
            }

            // Navegar para o fragmento correspondente ao item clicado
            when (menuItem.itemId) {
                R.id.nav_painel -> {
                    navController.navigate(R.id.nav_painel)
                }
                R.id.nav_graficos -> {
                    navController.navigate(R.id.nav_graficos)
                }
                R.id.nav_aparelhos -> {
                    navController.navigate(R.id.nav_aparelhos)
                }
                R.id.nav_notificacoes -> {
                    navController.navigate(R.id.nav_notificacoes)
                }
                R.id.nav_conta -> {
                    navController.navigate(R.id.nav_conta)
                }
                R.id.nav_educacional -> {
                    navController.navigate(R.id.nav_educacional)
                }
                else -> return@setNavigationItemSelectedListener false
            }
            drawerLayout.closeDrawers()
            true
        }
    }

    fun showAuthFragment(fragment: Fragment) {
        findViewById<View>(R.id.fragment_container).visibility = View.VISIBLE // Torna o container visível
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment) // Mostra o fragmento escolhido (Login ou Register)
            .commit()
        findViewById<View>(R.id.nav_host_fragment_content_main).visibility = View.GONE // Esconde o nav_host_fragment
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }


    private fun showAddDeviceDialog() {
        val dialog = NovoFragment()
        dialog.show(supportFragmentManager, "AddDeviceDialog")
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                showAddDeviceDialog() // Mostra o modal de novo aparelho
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }



    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onDestroy() {
        super.onDestroy()
        auth.removeAuthStateListener(authStateListener)
    }
}
