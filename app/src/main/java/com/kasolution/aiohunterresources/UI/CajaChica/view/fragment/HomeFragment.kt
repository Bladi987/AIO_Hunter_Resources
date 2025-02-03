package com.kasolution.aiohunterresources.UI.CajaChica.view.fragment

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
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
import android.view.animation.LinearInterpolator
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.kasolution.aiohunterresources.R
import com.kasolution.aiohunterresources.UI.CajaChica.view.adapter.RecentAdapter
import com.kasolution.aiohunterresources.UI.CajaChica.view.model.recent
import com.kasolution.aiohunterresources.UI.CajaChica.viewModel.LiquidacionViewModel
import com.kasolution.aiohunterresources.core.DialogUtils
import com.kasolution.aiohunterresources.core.dataConexion.urlId
import com.kasolution.aiohunterresources.databinding.FragmentHomeBinding


class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var preferencesCajaChica: SharedPreferences
    private val liquidacionViewModel: LiquidacionViewModel by viewModels()
    private var urlId: urlId? = null
    var resumengastos = 0.00
    var saldoDisponible = 0.00
    var montoCajaChica = 0.00
    var observador = false
    var rotateAnim: ObjectAnimator? = null
    private lateinit var adapter: RecentAdapter
    private lateinit var lmanager: LinearLayoutManager
    val dataList = mutableListOf<DataItem>()
    var currentIndex = 0 // Índice para llevar el seguimiento del dato actual

    // Handler para ejecutar la animación de regreso después de un tiempo sin interacción
    val handler = Handler(Looper.getMainLooper())
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
        recuperarSaldo()
        init()

        animarProgresoYTexto()
        liquidacionViewModel.getResumenGastos.observe(viewLifecycleOwner) { result ->
            result?.let { respuesta ->
                if (respuesta.isSuccess) {
                    val resultResumen = respuesta.getOrNull()
                    resultResumen?.let { resumen ->
                        if (observador) {
                            resumengastos = resumen.toDouble()
                            val saldo = montoCajaChica - resumengastos
                            if (saldo != saldoDisponible) {
                                val editor = preferencesCajaChica.edit()
                                editor.putString("SALDODISPONIBLE", (saldo).toString())
                                editor.apply()
                                recuperarSaldo()
                                animarProgresoYTexto()
                            }
                            observador = false
                        }
                    }
                } else {
                    val exception = respuesta.exceptionOrNull()
                    exception?.let { ex ->
                        showMessageError(ex.message.toString())
                    }
                }
            }


        }
        liquidacionViewModel.isloading.observe(viewLifecycleOwner) {
            if (it) {
//                binding.pbloading.visibility = View.VISIBLE
                if (rotateAnim == null) {
                    rotateAnim =
                        ObjectAnimator.ofFloat(binding.progressCircleView, "rotation", 0f, 360f)
                    rotateAnim?.duration = 500  // Duración de cada ciclo (2 segundos)
                    rotateAnim?.repeatCount = ObjectAnimator.INFINITE  // Repetir indefinidamente
                    rotateAnim?.repeatMode =
                        ObjectAnimator.RESTART  // Reiniciar al finalizar un ciclo
                    rotateAnim?.interpolator = LinearInterpolator()  // Hacerlo suave y constante
                }
                // Iniciar la animación si no ha sido iniciada
                rotateAnim?.start()

                binding.btnSync.isEnabled = false
                binding.imgSync.visibility = View.GONE
            } else {
                rotateAnim?.cancel()  // Esto detiene la animación

                // Restablecer la imagen a su posición original (0 grados)
                binding.progressCircleView.rotation = 0f
                binding.btnSync.isEnabled = true
                binding.imgSync.visibility = View.VISIBLE
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
            fragmentTransaction.addToBackStack("H") // Para agregar el fragmento a la pila de retroceso
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
            observador = true
            liquidacionViewModel.getResumenGastos(urlId!!)
        }
        binding.llDatosSaldo.setOnClickListener() {
            // Detener cualquier tarea previa en el handler (si existiera)
            handler.removeCallbacksAndMessages(null)

            // Iniciar la animación y el cambio de datos
            startDataChangeAnimation()

        }
    }

    private fun init() {
        lmanager = LinearLayoutManager(context)
        adapter = RecentAdapter(
            listaRecibida = getRecentList(),
            onClickListener = { recent, action, position ->
                onItemSelected(
                    recent,
                    action,
                    position
                )
            })
        binding.rvRecent.layoutManager = lmanager
        binding.rvRecent.adapter = adapter
    }

    private fun onItemSelected(recent: recent, action: Int, position: Int) {

    }

    // Función para iniciar la animación y el cambio de datos
    fun startDataChangeAnimation() {
        // Crear la animación para girar alrededor del eje X
        val rotateView1 = ObjectAnimator.ofFloat(binding.progressCircleView, "rotationX", 0f, 360f)
        val rotateView2 = ObjectAnimator.ofFloat(binding.llDatosSaldo, "rotationX", 0f, 360f)
        rotateView1.duration = 500  // Duración de la animación (en milisegundos)
        rotateView2.duration = 500

        // Crear un AnimatorSet para ejecutar ambas animaciones al mismo tiempo
        val animatorSet = AnimatorSet()
        animatorSet.playTogether(rotateView1, rotateView2)

        // Agregar un Listener para cambiar los datos al finalizar la animación
        animatorSet.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(p0: Animator) {

            }

            override fun onAnimationEnd(p0: Animator) {
                // Avanzar al siguiente dato en la lista
                currentIndex = (currentIndex + 1) % dataList.size // Ciclo a través de los datos
                Log.i("BladiDev", "indice $currentIndex")
                // Cambiar el valor de los datos cuando termine la animación
                binding.tvSaldo.text = "S/.${"%.2f".format(dataList[currentIndex].monto)}"
                binding.tvsaldocontable.text = dataList[currentIndex].descripcion
                binding.progressCircleView.setProgress(dataList[currentIndex].porcentaje)
                scheduleReturnToDataA()
            }

            override fun onAnimationCancel(p0: Animator) {

            }

            override fun onAnimationRepeat(p0: Animator) {

            }

        })

        // Iniciar la animación
        animatorSet.start()
    }

    // Función para manejar el efecto de regresar a Data A después de un tiempo sin interacción
    fun scheduleReturnToDataA() {
        // Si ya estamos en Data A, no ejecutamos la animación de regreso
        if (currentIndex == 0) {
            Log.i("BladiDev", "el indice antes de regresar es: $currentIndex")
            return
        }

        // Programar que después de 5 segundos (sin interacción), regrese a Data A
        handler.postDelayed({
            // Animación de regreso a data A
            val rotateBackView1 =
                ObjectAnimator.ofFloat(binding.progressCircleView, "rotationX", 360f, 0f)
            val rotateBackView2 =
                ObjectAnimator.ofFloat(binding.llDatosSaldo, "rotationX", 360f, 0f)
            rotateBackView1.duration = 500
            rotateBackView2.duration = 500

            val animatorSetBack = AnimatorSet()
            animatorSetBack.playTogether(rotateBackView1, rotateBackView2)

            animatorSetBack.addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(p0: Animator) {
                }

                override fun onAnimationEnd(p0: Animator) {
                    // Cambiar el valor del TextView a Data A

                    binding.tvSaldo.text = "S/.${"%.2f".format(dataList[0].monto)}"
                    binding.tvsaldocontable.text = dataList[0].descripcion
                    binding.progressCircleView.setProgress(dataList[0].porcentaje)
                    currentIndex = 0 // Restablecer el índice a 0
                }

                override fun onAnimationCancel(p0: Animator) {

                }

                override fun onAnimationRepeat(p0: Animator) {

                }

            })

            // Iniciar la animación de regreso
            animatorSetBack.start()
        }, 5000) // Tiempo de 5 segundos para iniciar la animación de regreso
    }

    private fun getRecentList(): List<recent> {
        val recentListJson = preferencesCajaChica.getString("RECENT_DATA", "").toString()
        if (recentListJson != "") {
            binding.llNoData.visibility = View.GONE
            binding.rvRecent.visibility = View.VISIBLE
            return recentListJson.let {
                Gson().fromJson(it, Array<String>::class.java)
                    .map { json -> Gson().fromJson(json, recent::class.java) }.toList()
            } ?: emptyList()
        } else {
            binding.llNoData.visibility = View.VISIBLE
            binding.rvRecent.visibility = View.GONE
            return emptyList()
        }
    }

    // Método para calcular el porcentaje restante
    private fun calcularPorcentajeRestante(): Float {
        return if (montoCajaChica > 0) {
            // Calculamos el porcentaje del saldo restante
            val saldoRestante = saldoDisponible
            ((saldoRestante / montoCajaChica) * 100).toFloat()
        } else 0f
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
            //binding.progressCircleView.setColor(Color.parseColor("#87CEEB"))
            binding.progressCircleView.setProgress(animatedValue)

            // Actualizamos el texto del saldo restante (en el centro del círculo)
            val saldoRestante = montoCajaChica * (animatedValue / 100f)
            binding.tvSaldo.text = "S/.${"%.2f".format(saldoRestante)}"
        }
        animator.start() // Iniciamos la animación
        dataList[0].porcentaje = porcentajeRestante
        dataList[1].porcentaje = (100 - porcentajeRestante)
        binding.tvsaldocontable.text = dataList[0].descripcion
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
        dataList.add(DataItem(saldoDisponible, "Saldo disponible"))
        dataList.add(DataItem((montoCajaChica - saldoDisponible), "Total gastos"))
    }

    data class DataItem(val monto: Double, val descripcion: String, var porcentaje: Float = 0f)

    private fun showMessageError(error: String) {
        DialogUtils.dialogMessageResponseError(
            requireContext(),
            icon = R.drawable.emoji_surprise,
            message = "Ups... Ocurrio un error, Vuelva a intentarlo en unos instantes",
            codigo = "Codigo: $error",
        )
    }
}