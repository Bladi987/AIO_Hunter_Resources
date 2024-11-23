package com.kasolution.aiohunterresources.UI.Settings

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.kasolution.aiohunterresources.core.DialogUtils
import com.kasolution.aiohunterresources.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding
    private lateinit var preferencesCajaChica: SharedPreferences
    private lateinit var preferencesFichasTecnicas: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        preferencesCajaChica = getSharedPreferences("valuesCajaChica", Context.MODE_PRIVATE)
        preferencesFichasTecnicas = getSharedPreferences("valueFichasTecnicas", Context.MODE_PRIVATE)
        recuperarPreferencias()

        binding.itemUrlCajaChica.setOnClickListener {
            openInputDialog("Ingrese id del Script",preferencesCajaChica,"URL_SCRIPT",binding.tvURL)
        }
        binding.itemFileCajaChica.setOnClickListener {
            openInputDialog("Ingrese id del archivo",preferencesCajaChica,"IDFILE",binding.tvFileDrive)
        }
        binding.itemLiquidacionCajaChica.setOnClickListener {
            openInputDialog("Ingrese id del sheet",preferencesCajaChica,"IDSHEETLIQUIDACION",binding.tvSheetLiquidacion)
        }
        binding.itemCajaChica.setOnClickListener {
            openInputDialog("Ingrese Monto Asignado",preferencesCajaChica,"MONTOCAJACHICA",binding.tvMonto)
        }
        binding.itemUrlFichasTecnicas.setOnClickListener {
            openInputDialog("Ingrese id del Script",preferencesFichasTecnicas,"URL_SCRIPT_FICHAS",binding.tvURLFichas)
        }
        binding.itemSheetFichasTecnicas.setOnClickListener {
            openInputDialog("Ingrese id del sheet",preferencesFichasTecnicas,"IDSHEET_FICHAS",binding.tvsheetFichas)
        }
    }

    private fun recuperarPreferencias() {
        binding.tvURL.text = preferencesCajaChica.getString("URL_SCRIPT", "")
        binding.tvFileDrive.text = preferencesCajaChica.getString("IDFILE", "")
        binding.tvSheetLiquidacion.text = preferencesCajaChica.getString("IDSHEETLIQUIDACION", "")
        binding.tvMonto.text = preferencesCajaChica.getString("MONTOCAJACHICA", "")

        binding.tvURLFichas.text = preferencesFichasTecnicas.getString("URL_SCRIPT_FICHAS", "")
        binding.tvsheetFichas.text = preferencesFichasTecnicas.getString("IDSHEET_FICHAS", "")
    }


    private fun openInputDialog(titulo: String,namepreferences:SharedPreferences,keypreferences:String,textview: TextView) {
        DialogUtils.dialogInput(this,titulo){input->
            if (input.isNotEmpty()){
                val editor = namepreferences.edit()
                editor.putString(keypreferences, input)
                editor.apply()
                textview.text = input
                if(titulo!="Ingrese Monto Asignado"){
                    Toast.makeText(this, "Codigo Ingresado correctamente", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}