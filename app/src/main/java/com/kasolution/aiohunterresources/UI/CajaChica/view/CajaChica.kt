package com.kasolution.aiohunterresources.UI.CajaChica.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.kasolution.aiohunterresources.R
import com.kasolution.aiohunterresources.UI.CajaChica.view.fragment.HomeFragment
import com.kasolution.aiohunterresources.databinding.ActivityCajaChicaBinding

class CajaChica : AppCompatActivity() {
    private lateinit var binding: ActivityCajaChicaBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityCajaChicaBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.contenedorCajaChica, HomeFragment())
        fragmentTransaction.commit()
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