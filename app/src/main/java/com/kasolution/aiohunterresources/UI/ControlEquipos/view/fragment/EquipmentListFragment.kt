package com.kasolution.aiohunterresources.UI.ControlEquipos.view.fragment


import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.TooltipCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.kasolution.aiohunterresources.R
import com.kasolution.aiohunterresources.UI.ControlEquipos.view.adapter.equiposAdapter
import com.kasolution.aiohunterresources.UI.ControlEquipos.view.adapter.resumenAdapter
import com.kasolution.aiohunterresources.UI.ControlEquipos.view.model.equipos
import com.kasolution.aiohunterresources.UI.ControlEquipos.view.model.itemResumen
import com.kasolution.aiohunterresources.UI.ControlEquipos.viewModel.equiposViewModel
import com.kasolution.aiohunterresources.core.DialogProgress
import com.kasolution.aiohunterresources.core.DialogUtils
import com.kasolution.aiohunterresources.core.dataConexion.urlId
import com.kasolution.aiohunterresources.databinding.FragmentEquipmentListBinding

import kotlin.collections.ArrayList

class EquipmentListFragment : Fragment() {
    private lateinit var binding: FragmentEquipmentListBinding
    private val equiposViewModel: equiposViewModel by viewModels()
    private lateinit var adapter: equiposAdapter
    private lateinit var adapterdisponibles: resumenAdapter
    private lateinit var adapternodisponibles: resumenAdapter
    private lateinit var adapteraveriados: resumenAdapter
    private lateinit var lmanager: LinearLayoutManager
    private lateinit var listEquipos: ArrayList<equipos>
    private lateinit var listDisponibles: ArrayList<itemResumen>
    private lateinit var listNoDisponibles: ArrayList<itemResumen>
    private lateinit var listAveriados: ArrayList<itemResumen>
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private var urlId: urlId? = null
    private var messageLoading = "Recuperando..."
    private var nameTecnico: String? = null
    private var heigthResumen = 0
    private var heigthButtonResumen = 0
    private var itemSelected: equipos? = null
    private var positionSelected = -1
    private lateinit var preferencesControlEquipos: SharedPreferences
    private lateinit var preferencesUser: SharedPreferences

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
        preferencesControlEquipos =
            requireContext().getSharedPreferences("valueControlEquipos", Context.MODE_PRIVATE)
        preferencesUser = requireContext().getSharedPreferences("valueUser", Context.MODE_PRIVATE)
        listEquipos = ArrayList()
        listDisponibles = ArrayList()
        listNoDisponibles = ArrayList()
        listAveriados = ArrayList()

        initRecycler()
        cargarResumen()
        recuperarPreferencias()
        configSwipe()
        calcularTamanoShowButtonResumen()


        equiposViewModel.getEquipos(urlId!!, nameTecnico!!)
        equiposViewModel.listEquipos.observe(viewLifecycleOwner, Observer { result ->
            result?.let { respuesta ->
                if (respuesta.isSuccess) {
                    val data = respuesta.getOrNull()
                    data?.let { listaEquipos ->
                        analizarData(listaEquipos)
                        adapter.limpiar()
                        listEquipos.addAll(listaEquipos)
                        adapter.notifyDataSetChanged()
                    }
                } else {
                    val exception = respuesta.exceptionOrNull()
                    exception?.let { ex ->
                        showMessageError(ex.message.toString())
                    }
                }
            }
        })


        equiposViewModel.updateEquipos.observe(viewLifecycleOwner, Observer { result ->
            result?.let { respuesta ->
                if (respuesta.isSuccess) {
                    val data = respuesta.getOrNull()
                    data?.let { equipoModificado ->
                        when (equipoModificado.estado) {
                            "Instalado" -> {
                                listEquipos.removeAt(positionSelected)
                                adapter.notifyItemRemoved(positionSelected)
                            }

                            "No disponible" -> {
                                listEquipos[positionSelected] = equipoModificado
                                adapter.notifyDataSetChanged()
                            }

                            "Dañado" -> {
                                listEquipos[positionSelected] = equipoModificado
                                adapter.notifyDataSetChanged()
                            }

                            "Cambio" -> {
                                listEquipos[positionSelected] = equipoModificado
                                adapter.notifyDataSetChanged()
                            }
                        }
                    }
                } else {
                    val exception = respuesta.exceptionOrNull()
                    exception?.let { ex ->
                        showMessageError(ex.message.toString())
                    }
                }
            }
            //ocultar menu de accion
            binding.llActions.visibility = View.GONE
            binding.llMostrarBoton.visibility = View.VISIBLE
            bottomSheetBehavior.isDraggable = true
            analizarData(listEquipos)
        })

        equiposViewModel.isloading.observe(viewLifecycleOwner, Observer { cargando ->
            adapter.limpiarSeleccion()
            if (cargando) DialogProgress.show(requireContext(), messageLoading)
            else {
                DialogProgress.dismiss()
                if (listEquipos.isEmpty()) {
                    binding.lottieAnimationView.setAnimation(R.raw.no_data_found)
                    binding.lottieAnimationView.playAnimation()
                    binding.llNoData.visibility = View.VISIBLE
                    binding.swipeRefresh.visibility = View.GONE
                } else {
                    binding.llNoData.visibility = View.GONE
                    binding.swipeRefresh.visibility = View.VISIBLE
                }
            }
            MostrarActionIcon(false)
        })
        equiposViewModel.exception.observe(viewLifecycleOwner) { error ->
            showMessageError(error)
        }

        binding.btnMostrarResumen.setOnClickListener() {
            if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_COLLAPSED) {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            } else {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            }
        }
        binding.btnActualizar.setOnClickListener() {
            equiposViewModel.onRefresh(urlId!!, nameTecnico!!)
        }
        binding.btnInstall.setOnClickListener() {
            DialogUtils.dialogQuestion(
                requireContext(),
                "Aviso",
                "Desea marcar como instalado?",
                positiveButtontext = "Si",
                negativeButtontext = "no",
                onPositiveClick = {
                    equiposViewModel.updateEquipos(
                        urlId!!,
                        equipos(
                            itemSelected!!.vid,
                            itemSelected!!.marca,
                            itemSelected!!.modelo,
                            itemSelected!!.tecnico,
                            itemSelected!!.fechaEntrega,
                            "Instalado",
                            ""
                        )
                    )
                }
            )
        }
        binding.btnChange.setOnClickListener() {
            DialogUtils.dialogQuestion(
                requireContext(),
                "Aviso",
                "Desea marcar como cambio?",
                positiveButtontext = "Si",
                negativeButtontext = "no",
                onPositiveClick = {
                    equiposViewModel.updateEquipos(
                        urlId!!,
                        equipos(
                            itemSelected!!.vid,
                            itemSelected!!.marca,
                            itemSelected!!.modelo,
                            itemSelected!!.tecnico,
                            itemSelected!!.fechaEntrega,
                            "Cambio",
                            ""
                        )
                    )
                }
            )
        }
        binding.btnDamaged.setOnClickListener() {
            DialogUtils.dialogQuestion(
                requireContext(),
                "Aviso",
                "Desea marcar como dañado?",
                positiveButtontext = "Si",
                negativeButtontext = "no",
                onPositiveClick = {
                    equiposViewModel.updateEquipos(
                        urlId!!,
                        equipos(
                            itemSelected!!.vid,
                            itemSelected!!.marca,
                            itemSelected!!.modelo,
                            itemSelected!!.tecnico,
                            itemSelected!!.fechaEntrega,
                            "Dañado",
                            ""
                        )
                    )
                }
            )
        }
        binding.btnNotAvailable.setOnClickListener() {
            DialogUtils.dialogQuestion(
                requireContext(),
                "Aviso",
                "Desea marcar como no disponible?",
                positiveButtontext = "Si",
                negativeButtontext = "no",
                onPositiveClick = {
                    equiposViewModel.updateEquipos(
                        urlId!!,
                        equipos(
                            itemSelected!!.vid,
                            itemSelected!!.marca,
                            itemSelected!!.modelo,
                            itemSelected!!.tecnico,
                            itemSelected!!.fechaEntrega,
                            "No disponible",
                            ""
                        )
                    )
                }
            )
        }

    }


    private fun analizarData(listaEquipos: ArrayList<equipos>) {
        // Mapas para contar registros por marca y modelo, agrupados por estado
        val disponibles = mutableMapOf<String, Int>()
        val noDisponibles = mutableMapOf<String, Int>()
        val daniados = mutableMapOf<String, Int>()

        // Procesar los datos
        for (equipo in listaEquipos) {
            val key = "${equipo.marca} - ${equipo.modelo}"

            when (equipo.estado) {
                "Disponible" -> disponibles[key] = disponibles.getOrDefault(key, 0) + 1
                "No disponible" -> noDisponibles[key] = noDisponibles.getOrDefault(key, 0) + 1
                "Dañado" -> daniados[key] = daniados.getOrDefault(key, 0) + 1
            }
        }

        // Convertir los mapas en listas de `itemResumen`
        val listaDisponibles = disponibles.map { (key, value) ->
            val (marca, modelo) = key.split(" - ")
            itemResumen(marca, modelo, value.toString())
        }

        val listaNoDisponibles = noDisponibles.map { (key, value) ->
            val (marca, modelo) = key.split(" - ")
            itemResumen(marca, modelo, value.toString())
        }

        val listaDaniados = daniados.map { (key, value) ->
            val (marca, modelo) = key.split(" - ")
            itemResumen(marca, modelo, value.toString())
        }

        // Cargar datos en los RecyclerView
        //cargarResumen(listaDisponibles, listaNoDisponibles, listaDaniados)

        adapterdisponibles.limpiar()
        adapternodisponibles.limpiar()
        adapteraveriados.limpiar()

        listDisponibles.addAll(listaDisponibles)
        listNoDisponibles.addAll(listaNoDisponibles)
        listAveriados.addAll(listaDaniados)
        adapterdisponibles.notifyDataSetChanged()
        adapternodisponibles.notifyDataSetChanged()
        adapteraveriados.notifyDataSetChanged()

        binding.llDisponibles.isVisible = listDisponibles.isNotEmpty()
        binding.llNoDisponible.isVisible = listNoDisponibles.isNotEmpty()
        binding.llAveriados.isVisible = listAveriados.isNotEmpty()
//        calcularTamanoResumenView()
    }


    private fun configSwipe() {
        binding.swipeRefresh.setOnRefreshListener {
            binding.swipeRefresh.isRefreshing = false
            DialogUtils.dialogQuestion(
                requireContext(),
                "Aviso",
                "Desea actualizar la lista?",
                positiveButtontext = "Si",
                negativeButtontext = "no",
                onPositiveClick = {
                    messageLoading = "Recuperando..."
                    equiposViewModel.onRefresh(urlId!!, nameTecnico!!)
                })

        }
    }

    private fun recuperarPreferencias() {
        val idScript =
            preferencesControlEquipos.getString("URL_SCRIPT_CONTROL_EQUIPOS", "").toString()
        val idSheet = preferencesControlEquipos.getString("IDSHEET_CONTROL_EQUIPOS", "").toString()
        nameTecnico = (preferencesUser.getString("NAME", "")
            .toString()) + " " + (preferencesUser.getString("LASTNAME", "").toString())
        urlId = urlId(idScript = idScript, "", idSheet = idSheet, "")
    }

    private fun initRecycler() {
        lmanager = LinearLayoutManager(context)
        adapter = equiposAdapter(
            listaRecibida = listEquipos,
            onClickListener = { equipos, action, position ->
                onItemSelected(
                    equipos,
                    action,
                    position
                )
            },
            onClickDeselect = { MostrarActionIcon(false) })
        binding.recyclerview1.layoutManager = lmanager
        binding.recyclerview1.adapter = adapter
    }

    private fun MostrarActionIcon(visible: Boolean) {

    }

    private fun onItemSelected(equipos: equipos, action: Int, position: Int) {
        itemSelected = equipos
        positionSelected = position
        when (action) {
            0 -> {
                //ocultar menu de accion
                binding.llActions.visibility = View.GONE
                binding.llMostrarBoton.visibility = View.VISIBLE
                bottomSheetBehavior.isDraggable = true
            }

            1 -> {
                //aqui se ejecuta cuando se presiona la vista
                if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED)
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

            }

            2 -> {
                //aqui se ejecuta cuando se presiona la vista larga
                //mostrar botones de accion
                if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED)
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                binding.llActions.visibility = View.VISIBLE
                binding.llMostrarBoton.visibility = View.GONE
                bottomSheetBehavior.isDraggable = false
            }
        }
    }

    private fun showMessageError(error: String) {
        DialogUtils.dialogMessageResponseError(
            requireContext(),
            icon = R.drawable.emoji_surprise,
            message = "Ups... Ocurrio un error, Vuelva a intentarlo en unos instantes",
            codigo = "Codigo: $error",
        )
    }

    private fun inicializarButtomSheet() {
        bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheet).apply {
            peekHeight = heigthButtonResumen
            state = BottomSheetBehavior.STATE_COLLAPSED

//            isDraggable = false // Deshabilita el deslizamiento manual

            // Agrega el BottomSheetCallback
            addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    // Manejar cambios de estado si es necesario
                    when (newState) {
                        BottomSheetBehavior.STATE_COLLAPSED -> {
                            // Cuando el BottomSheet está colapsado
                            binding.btnMostrarResumen.text = "Mostrar resumen"
                            // Otras acciones, como cambiar el color de fondo o la opacidad de otros elementos
                        }

                        BottomSheetBehavior.STATE_EXPANDED -> {
                            // Cuando el BottomSheet está expandido
                            binding.btnMostrarResumen.text = "Ocultar resumen"

                            //asignamos el tamaño al resumen
                            val layoutParams = binding.bottomSheet.layoutParams
                            layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
                            binding.bottomSheet.layoutParams = layoutParams
                        }

                        BottomSheetBehavior.STATE_HIDDEN -> {
                            // Cuando el BottomSheet está oculto
                            // Puedes hacer otras acciones aquí, si lo deseas
                        }
                    }

                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {
//                    binding.llMostrarResumen.alpha = 1 - slideOffset
//                    binding.llActions.alpha = slideOffset
                }

            })
        }
    }


    private fun cargarResumen() {
        adapterdisponibles = resumenAdapter(listDisponibles)
        adapternodisponibles = resumenAdapter(listNoDisponibles)
        adapteraveriados = resumenAdapter(listAveriados)

        binding.rvDisponibles.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.rvDisponibles.adapter = adapterdisponibles
        binding.rvNoDisponibles.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.rvNoDisponibles.adapter = adapternodisponibles
        binding.rvAveriados.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.rvAveriados.adapter = adapteraveriados

    }

//    private fun calcularTamanoResumenView() {
//        binding.bottomSheet.post {
//            heigthResumen = binding.bottomSheet.height  // Alto de la vista
//        }
//    }

    private fun calcularTamanoShowButtonResumen() {
        binding.llMostrarBoton.post {
            heigthButtonResumen = binding.llMostrarBoton.height
            inicializarButtomSheet()
        }
    }
}