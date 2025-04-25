package com.kasolution.aiohunterresources.UI.Settings.view


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.kasolution.aiohunterresources.R
import com.kasolution.aiohunterresources.UI.Settings.view.fragment.AboutFragment
import com.kasolution.aiohunterresources.UI.Settings.view.fragment.ContactFragment
import com.kasolution.aiohunterresources.UI.Settings.view.fragment.ajustesFragment
import com.kasolution.aiohunterresources.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        val botonId = intent.getIntExtra("opcion", 0)

        val fragmento=when(botonId){
            1->ajustesFragment()
            2->ContactFragment()
            else->AboutFragment()
        }


        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.flContenedor, fragmento)
        fragmentTransaction.commit()


        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val resultIntent = Intent()
                resultIntent.putExtra("ACTUALIZAR_MENU", true) // Enviar señal de actualización
                setResult(Activity.RESULT_OK, resultIntent)
                finish() // Cierra la actividad
            }
        })
    }


    override fun onBackPressed() {
        // Aquí puedes manejar la lógica para ver si hay un fragmento en la pila
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
        } else {
            super.onBackPressed() // Cierra la actividad si no hay más fragmentos
        }
    }
//    override fun onBackPressed() {
//        val resultIntent = Intent()
//        resultIntent.putExtra("ACTUALIZAR_MENU", true) // Avisamos que hay cambios
//        setResult(Activity.RESULT_OK, resultIntent)
//        super.onBackPressed()
//    }


}