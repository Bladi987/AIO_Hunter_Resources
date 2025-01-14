package com.kasolution.aiohunterresources.UI.CajaChica.view.fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.kasolution.aiohunterresources.UI.CajaChica.view.adapter.LiquidacionAdapter
import com.kasolution.aiohunterresources.UI.CajaChica.view.model.liquidacion
import com.kasolution.aiohunterresources.UI.CajaChica.viewModel.LiquidacionViewModel
import com.kasolution.aiohunterresources.core.DialogProgress
import com.kasolution.aiohunterresources.core.DialogUtils
import com.kasolution.aiohunterresources.core.ToastUtils
import com.kasolution.aiohunterresources.core.dataConexion.urlId
import com.kasolution.aiohunterresources.databinding.FragmentLiquidacionBinding

class LiquidacionFragment : Fragment() {
    private lateinit var binding: FragmentLiquidacionBinding
    private val LiquidacionViewModel: LiquidacionViewModel by viewModels()
    private lateinit var lmanager: LinearLayoutManager
    private lateinit var adapter: LiquidacionAdapter
    private lateinit var lista: ArrayList<liquidacion>
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
            if (it) DialogProgress.show(requireContext(), "Recuperando...")
            else DialogProgress.dismiss()
        })
        LiquidacionViewModel.getLiquidacion.observe(viewLifecycleOwner) { listaLiquidacion ->
            adapter.limpiar()
            lista.addAll(listaLiquidacion)
            adapter.notifyDataSetChanged()
        }
        LiquidacionViewModel.updateLiquidacion.observe(viewLifecycleOwner) {
            lista[itemPosition] = it
            adapter.notifyItemChanged(itemPosition)
            ToastUtils.MensajeToast(requireContext(), mensaje = "Deposito registrado", tipo = 0)
            abonarPagoACajaChica(it.monto)
        }
        LiquidacionViewModel.deleteLiquidacion.observe(viewLifecycleOwner) {
            lista.removeAt(itemPosition)
            adapter.notifyItemRemoved(itemPosition)
        }
    }

    private fun abonarPagoACajaChica(monto: String) {
        Log.i("BladiDev", saldoCajaChica.toString())
        Log.i("BladiDev", monto)

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
            onClickListener = { liquidacion, action, position ->
                onItemClicListener(liquidacion, action, position)
            })
        binding.recyclerview1.layoutManager = lmanager
        binding.recyclerview1.adapter = adapter
    }

    private fun onItemClicListener(liquidacion: liquidacion, action: Int, position: Int) {

        itemPosition = position
        if (action == 1) {
            DialogUtils.dialogQuestion(
                requireContext(),
                titulo = "Aviso",
                mensage = "Desea confirmar el pago de la liquidacion de ${liquidacion.concepto} por el monto de ${liquidacion.monto}?",
                positiveButtontext = "Aceptar",
                negativeButtontext = "Cancelar", onPositiveClick = {
                    LiquidacionViewModel.updateLiquidacion(urlId!!, liquidacion)
                }
            )
        } else {
            DialogUtils.dialogQuestion(
                requireContext(),
                titulo = "Aviso",
                mensage = "Anular la liquidacion de ${liquidacion.concepto}, esto eliminara esta liquidacion de la base de datos, y podra agregar nuevamente.\n\n¿Desea continuar?",
                positiveButtontext = "Aceptar",
                negativeButtontext = "Cancelar", onPositiveClick = {
                    LiquidacionViewModel.deleteLiquidacion(urlId!!, liquidacion)
                }
            )
        }
    }
}