package com.kasolution.aiohunterresources.UI.dashboard.view

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.AnimationUtils
import androidx.activity.result.contract.ActivityResultContracts
import com.kasolution.aiohunterresources.R
import com.kasolution.aiohunterresources.UI.Access.AccessActivity
import com.kasolution.aiohunterresources.UI.Settings.view.SettingsActivity
import com.kasolution.aiohunterresources.UI.dashboard.view.Fragment.FragmentMenu
import com.kasolution.aiohunterresources.core.DialogUtils
import com.kasolution.aiohunterresources.databinding.ActivityDashboardBinding

class Dashboard : AppCompatActivity() {
    private lateinit var binding:ActivityDashboardBinding
    private lateinit var preferencesUser: SharedPreferences
    private lateinit var preferencesCajaChica: SharedPreferences
    private lateinit var preferencesFichasTecnicas: SharedPreferences
    private lateinit var preferencesControlEquipos: SharedPreferences
    private lateinit var preferencesDocumentos: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        binding=ActivityDashboardBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        recuperarPreferences()
        initComponent()
        mostrarMenu()

        binding.btnExit.setOnClickListener {
            //dialogExit()
            DialogUtils.dialogQuestion(
                this,
                titulo = "Salir",
                mensage = "Desea salir de la apliacion y eliminar sus datos?",
                positiveButtontext = "Si",
                negativeButtontext = "No",
                onPositiveClick = {
//                    preferencesUser.edit().clear().apply()
                    val editor = preferencesUser.edit()
                    editor.remove("NAME")
                    editor.remove("LASTNAME")
                    editor.remove("TIPO")
                    editor.apply()

                    this.finish()
                    val i = Intent(this, AccessActivity::class.java)
                    startActivity(i)
                })
        }
        binding.btnSettings.setOnClickListener {
          iniciarActivitySettings(1)
        }
        binding.btnContact.setOnClickListener {
           iniciarActivitySettings(2)
        }
        binding.btnAbout.setOnClickListener {
            iniciarActivitySettings(3)
        }
    }
    private fun iniciarActivitySettings(botonId: Int) {
        val i = Intent(this, SettingsActivity::class.java)
        i.putExtra("opcion", botonId)
        settingsLauncher.launch(i)
    }

    private fun initComponent() {
        //iniciamos el menu de opciones
        val bundle = Bundle()
        bundle.putString("user",binding.tvUser.text.toString())
        bundle.putString("tipoUser",binding.tvTipoUser.text.toString())
        val fragment = FragmentMenu()
        fragment.arguments = bundle

        supportFragmentManager.beginTransaction()
        .replace(R.id.flcontainerMenu, fragment)
        .commit()
    }

    private fun mostrarMenu() {
        val animation = AnimationUtils.loadAnimation(this, R.anim.menu_input)
        binding.flcontainerMenu.startAnimation(animation)
    }
    private fun recuperarPreferences(){
        //iniciamos el preferences
        preferencesUser = this.getSharedPreferences("valueUser", Context.MODE_PRIVATE)
        preferencesCajaChica = this.getSharedPreferences("valuesCajaChica", Context.MODE_PRIVATE)
        preferencesFichasTecnicas = this.getSharedPreferences("valueFichasTecnicas", Context.MODE_PRIVATE)
        preferencesControlEquipos = this.getSharedPreferences("valueControlEquipos", Context.MODE_PRIVATE)
        preferencesDocumentos = this.getSharedPreferences("valueDocumentos", Context.MODE_PRIVATE)

        binding.tvUser.text=preferencesUser.getString("NAME","")+" "+preferencesUser.getString("LASTNAME","")
        binding.tvTipoUser.text=preferencesUser.getString("TIPO","")
    }
    private val settingsLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val actualizar = result.data?.getBooleanExtra("ACTUALIZAR_MENU", false) ?: false
                if (actualizar) {
                    initComponent()
                }
            }
        }

}