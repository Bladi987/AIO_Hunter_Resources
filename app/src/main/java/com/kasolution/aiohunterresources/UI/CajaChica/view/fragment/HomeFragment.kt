package com.kasolution.aiohunterresources.UI.CajaChica.view.fragment

import android.animation.ValueAnimator
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.kasolution.aiohunterresources.R
import com.kasolution.aiohunterresources.UI.CajaChica.viewModel.LiquidacionViewModel
import com.kasolution.aiohunterresources.UI.FichasTecnicas.view.fragment.CheckFragment
import com.kasolution.aiohunterresources.core.DialogProgress
import com.kasolution.aiohunterresources.core.dataConexion.urlId
import com.kasolution.aiohunterresources.databinding.FragmentHomeBinding
import java.util.Locale


class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var preferencesCajaChica: SharedPreferences
    private val liquidacionViewModel: LiquidacionViewModel by viewModels()
    private var urlId: urlId? = null
    var resumengastos = 0.00
    var saldoDisponible = 0.00
    var montoCajaChica = 0.00
    var observador=false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        preferencesCajaChica =
            requireContext().getSharedPreferences("valuesCajaChica", Context.MODE_PRIVATE)
        init()
        animarProgresoYTexto()
        liquidacionViewModel.getResumenGastos.observe(viewLifecycleOwner) { resumen ->
            if (observador){
                resumengastos = resumen.toDouble()
                val saldo = montoCajaChica - resumengastos
                if (saldo != saldoDisponible) {
                    //binding.tvSaldo.text = String.format(Locale.getDefault(), "S/. %.2f", montoCajaChica-resumengastos)
                    val editor = preferencesCajaChica.edit()
                    editor.putString("SALDODISPONIBLE", (saldo).toString())
                    editor.apply()
                    recuperarSaldo()
                    animarProgresoYTexto()
                }
                observador=false
            }
        }
        binding.btnRegistrar.setOnClickListener() {
            val fragment = RegisterFragment()
            val fragmentManager = requireActivity().supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.contenedorCajaChica, fragment)
            fragmentTransaction.addToBackStack(null) // Para agregar el fragmento a la pila de retroceso
            fragmentTransaction.commit()
        }
        binding.btnArchivos.setOnClickListener() {
            val fragment = FileFragment()
            val fragmentManager = requireActivity().supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.contenedorCajaChica, fragment)
            fragmentTransaction.addToBackStack(null) // Para agregar el fragmento a la pila de retroceso
            fragmentTransaction.commit()
        }
        binding.btnLiquidacion.setOnClickListener() {
            val fragment = LiquidacionFragment()
            val fragmentManager = requireActivity().supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.contenedorCajaChica, fragment)
            fragmentTransaction.addToBackStack(null) // Para agregar el fragmento a la pila de retroceso
            fragmentTransaction.commit()
        }
        binding.btnSync.setOnClickListener() {
            observador=true
            liquidacionViewModel.getResumenGastos(urlId!!)
        }
    }

    private fun init() {
        //cargar saldo contable
        recuperarSaldo()
    }

    // Método para calcular el porcentaje restante
    private fun calcularPorcentajeRestante(): Float {
        return if (montoCajaChica > 0) {
            // Calculamos el porcentaje del saldo restante
            val saldoRestante = saldoDisponible
            ((saldoRestante / montoCajaChica) * 100).toFloat()
        } else {
            0f
        }
    }


    // Método para animar tanto el progreso circular como el texto
    private fun animarProgresoYTexto() {
        // Calcular el porcentaje de saldo restante
        val porcentajeRestante = calcularPorcentajeRestante()

        // Creamos el ValueAnimator para animar tanto el progreso como el texto
        val animator = ValueAnimator.ofFloat(100f, porcentajeRestante)
        animator.duration = 2000 // Duración de la animación en milisegundos
        animator.interpolator = DecelerateInterpolator() // Interpolador para suavizar la animación

        animator.addUpdateListener { animation ->
            val animatedValue = animation.animatedValue as Float

            // Actualizamos el progreso circular
            binding.progressCircleView.setProgress(animatedValue)

            // Actualizamos el texto del saldo restante (en el centro del círculo)
            val saldoRestante = montoCajaChica * (animatedValue / 100f)
            binding.tvSaldo.text = "S/.${"%.2f".format(saldoRestante)}"
        }
        animator.start() // Iniciamos la animación
    }

    private fun recuperarSaldo() {
        urlId = urlId(
            preferencesCajaChica.getString("URL_SCRIPT", "").toString(),
            preferencesCajaChica.getString("IDFILE", "").toString(),
            "",
            ""
        )
        montoCajaChica = preferencesCajaChica.getString("MONTOCAJACHICA", "")!!.toDouble()
        saldoDisponible = preferencesCajaChica.getString(
            "SALDODISPONIBLE",
            preferencesCajaChica.getString("MONTOCAJACHICA", "")
        )!!.toDouble()
        Log.i("BladiDev", "saldo disponible en home es $saldoDisponible")
    }
}