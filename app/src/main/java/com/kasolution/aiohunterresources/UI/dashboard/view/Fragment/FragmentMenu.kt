package com.kasolution.aiohunterresources.UI.dashboard.view.Fragment

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.kasolution.aiohunterresources.R
import com.kasolution.aiohunterresources.UI.CajaChica.view.CajaChica
import com.kasolution.aiohunterresources.UI.ControlEquipos.view.ControlEquipos
import com.kasolution.aiohunterresources.UI.FichasTecnicas.view.FichasTecnicas
import com.kasolution.aiohunterresources.UI.Settings.view.SettingsActivity
import com.kasolution.aiohunterresources.UI.User.UserActivity
import com.kasolution.aiohunterresources.UI.dashboard.view.Adapter.adapterListMenu
import com.kasolution.aiohunterresources.UI.dashboard.view.Model.itemGrid
import com.kasolution.aiohunterresources.databinding.FragmentMenuBinding

class FragmentMenu : Fragment() {
    private lateinit var binding: FragmentMenuBinding
    private lateinit var preferencesCajaChica: SharedPreferences
    private lateinit var preferencesFichasTecnicas: SharedPreferences
    private lateinit var preferencesControlEquipos: SharedPreferences
    private lateinit var preferencesDocumentos: SharedPreferences
    private lateinit var preferencesAccess: SharedPreferences
    private lateinit var preferencesUser: SharedPreferences
    private lateinit var Lista: ArrayList<itemGrid>
    private var user: String? = null
    private var tipoUser: String? = null

    private lateinit var glmanager: GridLayoutManager
    private lateinit var adapterlistMenu: adapterListMenu
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        user = arguments?.getString("user")
        tipoUser = arguments?.getString("tipoUser")

        preferencesCajaChica =
            requireContext().getSharedPreferences("valuesCajaChica", Context.MODE_PRIVATE)
        preferencesFichasTecnicas =
            requireContext().getSharedPreferences("valueFichasTecnicas", Context.MODE_PRIVATE)
        preferencesControlEquipos =
            requireContext().getSharedPreferences("valueControlEquipos", Context.MODE_PRIVATE)
        preferencesDocumentos =
            requireContext().getSharedPreferences("valueDocumentos", Context.MODE_PRIVATE)
        preferencesAccess =
            requireContext().getSharedPreferences("valuesAccess", Context.MODE_PRIVATE)
        preferencesUser = requireContext().getSharedPreferences("valueUser", Context.MODE_PRIVATE)
        Lista = llenarDatosMenu()
        init()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentMenuBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun init() {
        if (Lista.isNotEmpty()) {
            val columnWidthDp = 300
            val columns = resources.displayMetrics.widthPixels / columnWidthDp
            glmanager = GridLayoutManager(context, columns)
            adapterlistMenu = adapterListMenu(
                listaRecibida = Lista,
                OnClickListener = { itemGrid -> onItemSelected(itemGrid) })
            binding.rvMenu.layoutManager = glmanager
            binding.rvMenu.adapter = adapterlistMenu
        }else
            binding.tvTitleMenu.text = "No hay opciones disponibles"
    }

    private fun onItemSelected(itemGrid: itemGrid) {

        when (itemGrid.name) {
            "Caja chica" -> {
                val monto = preferencesCajaChica.getString("MONTOCAJACHICA", null)
                if (monto.isNullOrEmpty()) {
                    Toast.makeText(requireContext(), "No tiene configurado el monto, puede agregarlo en ajustes", Toast.LENGTH_SHORT).show()
                    return
                }else{
                    val Intent = Intent(requireContext(), CajaChica::class.java)
                    startActivity(Intent)
                }
            }

            "Fichas técnicas" -> {
                val intent = Intent(requireContext(), FichasTecnicas::class.java)
                startActivity(intent)
            }

            "Equipos" -> {
                val intent = Intent(requireContext(), ControlEquipos::class.java)
                startActivity(intent)
            }

            "Documentos" -> {
                Toast.makeText(requireContext(), itemGrid.name, Toast.LENGTH_SHORT).show()
            }

            "Usuarios" -> {
                val intent = Intent(requireContext(), UserActivity::class.java)
                startActivity(intent)
            }

            "Ajustes" -> {
                val intent = Intent(requireContext(), SettingsActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun llenarDatosMenu(): ArrayList<itemGrid> {
        val arrayList: ArrayList<itemGrid> = ArrayList()
        arrayList.clear()
        //preferencias de caja chica
        val urlCc = preferencesCajaChica.getString("URL_SCRIPT", null)
        val idfileCc = preferencesCajaChica.getString("IDFILE", null)
        val idSheetLiquidacionCc = preferencesCajaChica.getString("IDSHEETLIQUIDACION", null)

        //preferencias de fichas tecnicas
        val urlFichas = preferencesFichasTecnicas.getString("URL_SCRIPT_FICHAS", null)
        val idSheetFichas = preferencesFichasTecnicas.getString("IDSHEET_FICHAS", null)

        //preferencias de control de equipos
        val urlControlEquipos =
            preferencesControlEquipos.getString("URL_SCRIPT_CONTROL_EQUIPOS", null)
        val idSheetControlEquipos =
            preferencesControlEquipos.getString("IDSHEET_CONTROL_EQUIPOS", null)

        //preferencias de documentos
        val urlDocumentos = preferencesDocumentos.getString("URL_SCRIPT_DOCUMENTOS", null)
        val idSheetDocumentos = preferencesDocumentos.getString("IDSHEET_DOCUMENTOS", null)

        //preferencias de usuario
        val idScriptUser = preferencesAccess.getString("IDSCRIPTACCESS", null)
        val idSheetUser = preferencesAccess.getString("IDSHEETACCESS", null)

        if (!urlCc.isNullOrEmpty() && !idfileCc.isNullOrEmpty()) {
            arrayList.add(itemGrid(R.drawable.ic_gastos, "Caja chica"))
        }
        if (!urlFichas.isNullOrEmpty() && !idSheetFichas.isNullOrEmpty())
            arrayList.add(itemGrid(R.drawable.fichas_tecnicas, "Fichas técnicas"))
        if (!urlControlEquipos.isNullOrEmpty() && !idSheetControlEquipos.isNullOrEmpty())
            arrayList.add(itemGrid(R.drawable.ic_equipos, "Equipos"))
        if (!urlDocumentos.isNullOrEmpty() && !idSheetDocumentos.isNullOrEmpty())
            arrayList.add(itemGrid(R.drawable.ic_document, "Documentos"))
        if (!idScriptUser.isNullOrEmpty() && !idSheetUser.isNullOrEmpty())
            if (tipoUser == "Administrador" || tipoUser == "Developer")
                arrayList.add(itemGrid(R.drawable.user_icon, "Usuarios"))
        return arrayList
    }
}