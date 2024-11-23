package com.kasolution.aiohunterresources.UI.dashboard.view.Fragment

import android.content.Intent
import android.os.Bundle
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
import com.kasolution.aiohunterresources.UI.Settings.SettingsActivity
import com.kasolution.aiohunterresources.UI.dashboard.view.Adapter.adapterListMenu
import com.kasolution.aiohunterresources.UI.dashboard.view.Model.itemGrid
import com.kasolution.aiohunterresources.databinding.FragmentMenuBinding

class FragmentMenu : Fragment() {
    private lateinit var binding: FragmentMenuBinding
    private lateinit var Lista: ArrayList<itemGrid>
    private var key: String? = null
    private lateinit var glmanager: GridLayoutManager
    private lateinit var adapterlistMenu: adapterListMenu
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Lista = llenarDatosMenu()
        init()
    }

    fun llenarDatosMenu():ArrayList<itemGrid>{
        val arrayList:ArrayList<itemGrid> = ArrayList()
        arrayList.add(itemGrid(R.drawable.ic_gastos,"Caja chica"))
        arrayList.add(itemGrid(R.drawable.ic_conexion,"Fichas técnicas"))
        arrayList.add(itemGrid(R.drawable.ic_equipos,"Equipos"))
        arrayList.add(itemGrid(R.drawable.ic_document,"Documentos"))
        arrayList.add(itemGrid(R.drawable.ic_database,"Datos"))
        arrayList.add(itemGrid(R.drawable.ajustes,"Ajustes"))


        return arrayList
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMenuBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun init() {
        if (Lista!=null){
            val columnWidthDp = 400
            val columns = resources.displayMetrics.widthPixels / columnWidthDp
            glmanager = GridLayoutManager(context, columns)
            adapterlistMenu = adapterListMenu(
                listaRecibida = Lista,
                OnClickListener = { itemGrid -> onItemSelected(itemGrid) })
            binding.rvMenu.layoutManager = glmanager
            binding.rvMenu.adapter = adapterlistMenu
        }
    }

    private fun onItemSelected(itemGrid: itemGrid) {

        when (itemGrid.name){
            "Caja chica"->{
                val Intent=Intent(requireContext(), CajaChica::class.java)
                startActivity(Intent)
            }
            "Fichas técnicas"->{
                val intent=Intent(requireContext(), FichasTecnicas::class.java)
                startActivity(intent)
            }
            "Equipos"->{
                val intent=Intent(requireContext(), ControlEquipos::class.java)
                startActivity(intent)
            }
            "Documentos"->{
                Toast.makeText(requireContext(),itemGrid.name,Toast.LENGTH_SHORT).show()
            }
            "Datos"->{
                Toast.makeText(requireContext(),itemGrid.name,Toast.LENGTH_SHORT).show()
            }
            "Ajustes"->{
                val intent=Intent(requireContext(), SettingsActivity::class.java)
                startActivity(intent)
            }
        }
    }
}