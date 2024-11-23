package com.kasolution.aiohunterresources.UI.ControlEquipos.view.fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.kasolution.aiohunterresources.R
import com.kasolution.aiohunterresources.UI.CajaChica.view.adapter.RegisterAdapter
import com.kasolution.aiohunterresources.UI.CajaChica.view.model.register
import com.kasolution.aiohunterresources.UI.CajaChica.viewModel.RegisterViewModel
import com.kasolution.aiohunterresources.core.DialogProgress
import com.kasolution.aiohunterresources.databinding.FragmentEquipmentListBinding
import java.util.ArrayList

class EquipmentListFragment : Fragment() {
    private lateinit var binding: FragmentEquipmentListBinding
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private val registerViewModel: RegisterViewModel by viewModels()
    private lateinit var adapter: RegisterAdapter
    private lateinit var lmanager: LinearLayoutManager
    private lateinit var listRegister: ArrayList<register>
    private lateinit var preferencesValueConexion: SharedPreferences
    private lateinit var urlScript: String
    private lateinit var idSheet: String
    private lateinit var sheetName: String
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentEquipmentListBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        inicializarButtomSheet()
        listRegister = ArrayList()
        preferencesValueConexion =
            requireContext().getSharedPreferences("valuesCajaChica", Context.MODE_PRIVATE)
        init()
        recuperarPreferencias()
        //registerViewModel.onCreate("$urlScript->$idSheet->$sheetName")
        registerViewModel.getRegister.observe(viewLifecycleOwner, Observer { listaRegistros ->
            adapter.limpiar()
            listRegister.addAll(listaRegistros)
            adapter.notifyDataSetChanged()

//            binding.RVRegistros.scrollToPosition(0)
//            listRegister.addAll(listaRegistros)
//            adapter.submitList(listaRegistros)
        })
        registerViewModel.isloading.observe(viewLifecycleOwner, Observer {
            if (it) DialogProgress.show(requireContext(), "Recuperando...")
            else DialogProgress.dismiss()
        })

        binding.btnAdd.setOnClickListener(){
            Toast.makeText(requireContext(),"Funcion aun no implementada",Toast.LENGTH_SHORT).show()
        }


    }
    private fun init() {
        lmanager = LinearLayoutManager(context)
        adapter = RegisterAdapter(
            listaRecibida = listRegister,
            onclickListener = { itemRegister -> onItemSelected(itemRegister) },
            OnClickUpdate = { itemRegister, position -> onItemUpdate(itemRegister, position) },
            OnClickDelete = { itemRegister, position -> onItemDelete(itemRegister, position) })
        binding.recyclerView .layoutManager = lmanager
        binding.recyclerView.adapter = adapter
    }
    private fun recuperarPreferencias() {
        urlScript = preferencesValueConexion.getString("URL_SCRIPT", "").toString()
        idSheet = preferencesValueConexion.getString("IDSHEET", "").toString()
        sheetName = preferencesValueConexion.getString("SHEETNAME", "").toString()
        val Sheets = preferencesValueConexion.getString("LIST_SHEET", null)
    }

    private fun onItemDelete(itemRegister: register, position: Int) {


    }

    private fun onItemUpdate(itemRegister: register, position: Int) {


    }

    private fun onItemSelected(itemRegister: register) {


    }

    private fun inicializarButtomSheet() {
        bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheet).apply {
            peekHeight = 150
            state = BottomSheetBehavior.STATE_COLLAPSED

            // Agrega el BottomSheetCallback
            addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    // Manejar cambios de estado si es necesario
                    when (newState) {
                        BottomSheetBehavior.STATE_COLLAPSED -> {
                            // Cuando el BottomSheet está colapsado
                            binding.llheadButtomSheet.visibility = View.VISIBLE // Muestra el botón
                            binding.llResumenButtomSheet.visibility=View.GONE
                            // Otras acciones, como cambiar el color de fondo o la opacidad de otros elementos
                        }
                        BottomSheetBehavior.STATE_EXPANDED -> {
                            // Cuando el BottomSheet está expandido
                            binding.llheadButtomSheet.visibility = View.GONE // Oculta el botón
                            binding.llResumenButtomSheet.visibility=View.VISIBLE
                            // Otras acciones, como deshabilitar ciertos botones
                        }
                        BottomSheetBehavior.STATE_HIDDEN -> {
                            // Cuando el BottomSheet está oculto
                            // Puedes hacer otras acciones aquí, si lo deseas
                        }
                    }

                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                    binding.llheadButtomSheet.alpha = 1 - slideOffset
                    binding.llResumenButtomSheet.alpha = slideOffset
                }

            })
        }
    }

}