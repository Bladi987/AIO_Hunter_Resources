package com.kasolution.aiohunterresources.UI.CajaChica.view.fragment

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.kasolution.aiohunterresources.R
import com.kasolution.aiohunterresources.UI.CajaChica.view.adapter.LiquidacionAdapter
import com.kasolution.aiohunterresources.UI.CajaChica.view.model.liquidacion
import com.kasolution.aiohunterresources.UI.CajaChica.view.model.recent
import com.kasolution.aiohunterresources.UI.CajaChica.viewModel.LiquidacionViewModel
import com.kasolution.aiohunterresources.core.DialogProgress
import com.kasolution.aiohunterresources.core.DialogUtils
import com.kasolution.aiohunterresources.core.ToastUtils
import com.kasolution.aiohunterresources.core.dataConexion.urlId
import com.kasolution.aiohunterresources.databinding.FragmentLiquidacionBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class LiquidacionFragment : Fragment() {
    private lateinit var binding: FragmentLiquidacionBinding
    private val LiquidacionViewModel: LiquidacionViewModel by viewModels()
    private lateinit var lmanager: LinearLayoutManager
    private lateinit var adapter: LiquidacionAdapter
    private lateinit var lista: ArrayList<liquidacion>
    private lateinit var itemLiquidacion: liquidacion
    private var itemPosition = -1
    private var urlId: urlId? = null
    var saldoCajaChica = 0.00
    private var messageLoading = "Recuperando..."
    private lateinit var preferencesCajaChica: SharedPreferences
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLiquidacionBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        preferencesCajaChica =
            requireContext().getSharedPreferences("valuesCajaChica", Context.MODE_PRIVATE)
        lista = ArrayList()
        init()
        recuperarPreferencias()
        configSwipe()


        binding.btnback.setOnClickListener() {
            requireActivity().supportFragmentManager.popBackStack()
        }
        LiquidacionViewModel.getLiquidacion(urlId!!)
        LiquidacionViewModel.isloading.observe(viewLifecycleOwner, Observer {
            adapter.limpiarSeleccion()
            if (it) DialogProgress.show(requireContext(), "Recuperando...")
            else {
                DialogProgress.dismiss()
                if (lista.isEmpty()) {
                    binding.lottieAnimationView.setAnimation(R.raw.no_data_found)
                    binding.lottieAnimationView.playAnimation()
                    binding.llNoData.visibility = View.VISIBLE
                    binding.swipeRefresh.visibility = View.GONE
                } else {
                    binding.llNoData.visibility = View.GONE
                    binding.swipeRefresh.visibility = View.VISIBLE
                }
            }


        })
        LiquidacionViewModel.getLiquidacion.observe(viewLifecycleOwner) { result ->
            result?.let { respuesta ->
                if (respuesta.isSuccess) {
                    adapter.limpiar()
                    val listaLiquidacion = respuesta.getOrNull()
                    listaLiquidacion?.let { listaLiquidacion ->
                        lista.addAll(listaLiquidacion)
                        adapter.notifyDataSetChanged()
                    }
                } else {
                    val exception = respuesta.exceptionOrNull()
                    exception?.let { ex ->
                        showMessageError(ex.message.toString())
                    }
                }
            }

        }
        LiquidacionViewModel.updateLiquidacion.observe(viewLifecycleOwner) { result ->
            result?.let { respuesta ->
                if (respuesta.isSuccess) {
                    val liquidacion = respuesta.getOrNull()
                    liquidacion?.let {
                        lista[itemPosition] = it
                        adapter.notifyItemChanged(itemPosition)
                        ToastUtils.MensajeToast(
                            requireContext(),
                            mensaje = "Deposito registrado",
                            tipo = 0
                        )
                        abonarPagoACajaChica(it.monto)
                        saveRecent(
                            recent(
                                icon = R.drawable.liquidacion,
                                titulo = "Liquidacion -> pago confimado",
                                detalle = "se confirmo el pago de la liquidacion de: ${it.concepto}",
                                fecha = obtenerFechaActual()
                            )
                        )
                    }
                } else {
                    val exception = respuesta.exceptionOrNull()
                    exception?.let { ex ->
                        showMessageError(ex.message.toString())
                    }
                }
            }
        }
        LiquidacionViewModel.deleteLiquidacion.observe(viewLifecycleOwner) { result ->
            result?.let { respuesta ->
                if (respuesta.isSuccess) {
                    val liquidacion = respuesta.getOrNull()
                    liquidacion?.let {
                        val nombreitem = lista[itemPosition].concepto
                        lista.removeAt(itemPosition)
                        adapter.notifyItemRemoved(itemPosition)
                        saveRecent(
                            recent(
                                icon = R.drawable.liquidacion,
                                titulo = "Liquidacion -> Eliminado",
                                detalle = "Se elimino la liquidacion $nombreitem",
                                fecha = obtenerFechaActual()
                            )
                        )
                    }
                } else {
                    val exception = respuesta.exceptionOrNull()
                    exception?.let { ex ->
                        showMessageError(ex.message.toString())
                    }
                }
            }

        }
        LiquidacionViewModel.exception.observe(viewLifecycleOwner) { error ->
            showMessageError(error)
        }
        binding.btnConfirmPay.setOnClickListener() {
            DialogUtils.dialogQuestion(
                requireContext(),
                titulo = "Aviso",
                mensage = "Desea confirmar el pago de la liquidacion de ${itemLiquidacion.concepto} por el monto de ${itemLiquidacion.monto}?",
                positiveButtontext = "Aceptar",
                negativeButtontext = "Cancelar", onPositiveClick = {
                    LiquidacionViewModel.updateLiquidacion(urlId!!, itemLiquidacion)
                }
            )
        }
        binding.btnDelete.setOnClickListener() {
            DialogUtils.dialogQuestion(
                requireContext(),
                titulo = "Aviso",
                mensage = "Anular la liquidacion de ${itemLiquidacion.concepto}, esto eliminara esta liquidacion de la base de datos, y podra agregar nuevamente.\n\n¿Desea continuar?",
                positiveButtontext = "Aceptar",
                negativeButtontext = "Cancelar", onPositiveClick = {
                    LiquidacionViewModel.deleteLiquidacion(urlId!!, itemLiquidacion)
                }
            )
        }
        binding.btnActualizar.setOnClickListener() {
            binding.llNoData.visibility = View.GONE
            LiquidacionViewModel.onRefresh(urlId!!)
        }
    }

    private fun abonarPagoACajaChica(monto: String) {
        var montorecibido = monto
        montorecibido = montorecibido.replace("S/ ", "")
        saldoCajaChica += montorecibido.toDouble()
        val editor = preferencesCajaChica.edit()
        editor.putString("SALDODISPONIBLE", saldoCajaChica.toString())
        editor.apply()
        recuperarPreferencias()
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
                    LiquidacionViewModel.onRefresh(urlId!!)
                })
        }
    }

    private fun recuperarPreferencias() {
        val idScript = preferencesCajaChica.getString("URL_SCRIPT", "").toString()
        val idSheet = preferencesCajaChica.getString("IDSHEETLIQUIDACION", "").toString()
        saldoCajaChica = preferencesCajaChica.getString("SALDODISPONIBLE", "0.00")!!.toDouble()
        urlId = urlId(
            idScript = idScript,
            "",
            idSheet = idSheet,
            ""
        )
    }

    private fun init() {
        lmanager = LinearLayoutManager(requireContext())
        adapter = LiquidacionAdapter(
            listaRecibida = lista,
            onClickListener = { liquidacion, estado, position ->
                onItemClicListener(liquidacion, estado, position)
            },
            onClickDeselect = { MostrarActionIcon(false) })
        binding.recyclerview1.layoutManager = lmanager
        binding.recyclerview1.adapter = adapter
    }

    private fun MostrarActionIcon(mostrar: Boolean) {
        if (mostrar) binding.llActions.visibility =
            View.VISIBLE else binding.llActions.visibility = View.INVISIBLE
    }

    private fun onItemClicListener(liquidacion: liquidacion, estado: Int, position: Int) {
        when (estado) {
            1 -> binding.btnConfirmPay.visibility = View.VISIBLE
            2 -> binding.btnConfirmPay.visibility = View.INVISIBLE
        }
        MostrarActionIcon(true)
        itemPosition = position
        itemLiquidacion = liquidacion
    }

    private fun saveRecent(recent: recent) {
        val editor = preferencesCajaChica.edit()

        val recentListJson = preferencesCajaChica.getString("RECENT_DATA", null)

        val recentList = mutableListOf<String>()

        // Convertir la lista actual de JSON a objetos Recent (si existe)
        recentListJson?.let {
            recentList.addAll(Gson().fromJson(it, Array<String>::class.java).toList())
        }

        // Agregar el nuevo objeto a la lista y limitar a 10 elementos
        recentList.add(0, Gson().toJson(recent))
        if (recentList.size > 10) {
            recentList.removeAt(recentList.size - 1)
        }

        // Convertir la lista actualizada a JSON y guardarla
        editor.putString("RECENT_DATA", Gson().toJson(recentList))
        editor.apply()
    }

    private fun obtenerFechaActual(): String {
        val fechaActual = Calendar.getInstance()  // Obtiene la fecha actual del sistema
        val formato =
            SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())  // Define el formato deseado
        return formato.format(fechaActual.time)  // Formatea la fecha y la devuelve como cadena
    }

    private fun showMessageError(error: String) {
        DialogUtils.dialogMessageResponseError(
            requireContext(),
            icon = R.drawable.emoji_surprise,
            message = "Ups... Ocurrio un error, Vuelva a intentarlo en unos instantes",
            codigo = "Codigo: $error",
        )
    }
}