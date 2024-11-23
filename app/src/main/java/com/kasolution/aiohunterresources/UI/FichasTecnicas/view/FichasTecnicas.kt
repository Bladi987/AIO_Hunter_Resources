package com.kasolution.aiohunterresources.UI.FichasTecnicas.view

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.kasolution.aiohunterresources.R
import com.kasolution.aiohunterresources.UI.FichasTecnicas.view.fragment.BrandFragment
import com.kasolution.aiohunterresources.databinding.ActivityFichasTecnicasBinding

class FichasTecnicas : AppCompatActivity() {
    private lateinit var binding: ActivityFichasTecnicasBinding
    private lateinit var preferencesAccess: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        binding=ActivityFichasTecnicasBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        preferencesAccess = getSharedPreferences("preferencias", Context.MODE_PRIVATE)

        supportFragmentManager.addOnBackStackChangedListener {
            updateCurrentFragment()
        }

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.contenedor, BrandFragment())
        fragmentTransaction.commit()

//        if (recuperarPreferenciasUser()) {
//
//        } else {
//            var i = Intent(this, AccessActivity::class.java)
//            startActivity(i)
//        }
    }
    private fun recuperarPreferenciasUser(): Boolean {
        var id = preferencesAccess.getString("ID", "")
        var name = preferencesAccess.getString("NAME", "")
        var lastName = preferencesAccess.getString("LASTNAME", "")
        var tipo = preferencesAccess.getString("TIPO", "")
        return !(id.isNullOrEmpty() || name.isNullOrEmpty() || lastName.isNullOrEmpty() || tipo.isNullOrEmpty())
    }

    private fun updateCurrentFragment() {
        val currentFragment = getCurrentFragment()
//        if (currentFragment is BrandFragment) {
//            Log.i(Tag, "estas en el fragmento marca")
//        } else if (currentFragment is BrandDetailsFragment) {
//            Log.i(Tag, "estas en el fragmento detalle")
//        } else {
//            Log.i(Tag, "estas en el fragmento showFragment")
//        }
    }

    private fun getCurrentFragment(): Fragment? {
        return supportFragmentManager.findFragmentById(R.id.contenedor)
    }
    override fun onBackPressed() {
        // Aquí puedes manejar la lógica para ver si hay un fragmento en la pila
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
        } else {
            super.onBackPressed() // Cierra la actividad si no hay más fragmentos
        }
    }
}