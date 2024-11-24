package com.ecoactivity.app

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.navigation.ui.setupActionBarWithNavController
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.ecoactivity.app.databinding.ActivityMainBinding
import com.ecoactivity.app.ui.LoginFragment
import com.ecoactivity.app.ui.NovoFragment
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflar o layout principal
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializar FirebaseAuth
        auth = FirebaseAuth.getInstance()

        // Configurar a Toolbar
        setSupportActionBar(binding.appBarMain.toolbar)

        // Configurar o DrawerLayout com o NavigationView
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        navController = findNavController(R.id.nav_host_fragment_content_main)

        // Configurar o AppBarConfiguration com os destinos principais (fragments do menu)
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

        // Vincular a Toolbar com o NavController
        setupActionBarWithNavController(navController, appBarConfiguration)

        // Configurar o NavigationView com o NavController
        navView.setupWithNavController(navController)

        // Verificar autenticação e mostrar LoginFragment se necessário
        checkAuthenticationState()
    }

    // Verifica se o usuário está autenticado e exibe o LoginFragment se necessário
    private fun checkAuthenticationState() {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            // Mostra o LoginFragment no contêiner de autenticação
            showAuthFragment(LoginFragment())
        }
    }

    // Exibe um fragmento de autenticação no contêiner dedicado
    fun showAuthFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.auth_fragment_container, fragment)
        transaction.commit()
        findViewById<View>(R.id.auth_fragment_container).visibility = View.VISIBLE
    }

    // Remove o fragmento de autenticação e oculta o contêiner
    fun hideAuthFragment() {
        val fragment = supportFragmentManager.findFragmentById(R.id.auth_fragment_container)
        if (fragment != null) {
            supportFragmentManager.beginTransaction().remove(fragment).commit()
        }
        findViewById<View>(R.id.auth_fragment_container).visibility = View.GONE
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflar o menu da Toolbar
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_add -> { // Tratando o clique no botão "+"
                openAddDeviceModal()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    /**
     * Abre o modal do NovoFragment.
     */
    private fun openAddDeviceModal() {
        val dialog: DialogFragment = NovoFragment()
        dialog.show(supportFragmentManager, "NovoFragment")
    }

    override fun onSupportNavigateUp(): Boolean {
        // Permite que o botão de navegação funcione corretamente
        return NavigationUI.navigateUp(navController, appBarConfiguration) || super.onSupportNavigateUp()
    }

    fun logoutUser() {
        auth.signOut()
        showAuthFragment(LoginFragment()) // Exibe login após o logout
    }
}
