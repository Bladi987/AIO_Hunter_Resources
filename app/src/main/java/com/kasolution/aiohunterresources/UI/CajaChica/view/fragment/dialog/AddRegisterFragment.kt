package com.kasolution.aiohunterresources.UI.CajaChica.view.fragment.dialog

import android.app.DatePickerDialog
import android.content.Context
import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import androidx.fragment.app.DialogFragment
import com.kasolution.aiohunterresources.R
import com.kasolution.aiohunterresources.UI.CajaChica.interfaces.DialogListener
import com.kasolution.aiohunterresources.UI.CajaChica.view.model.register
import com.kasolution.aiohunterresources.databinding.FragmentAddRegisterBinding
import java.util.Calendar
import java.util.Locale


class AddRegisterFragment : DialogFragment() {
    private lateinit var binding: FragmentAddRegisterBinding
    var listener: DialogListener? = null
    private lateinit var preferencesCajaChica: SharedPreferences
    var sustento = false
    var tipoGasto = 0
    var ultimaUbicacion=""
    private var register: register? = null
    var idRegister = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentAddRegisterBinding.inflate(inflater, container, false)
        // Carga la animación desde el archivo XML
        val anim = AnimationUtils.loadAnimation(requireContext(), R.anim.bounce3)
        // Aplica la animación a la vista del fragmento
        binding.root.startAnimation(anim)
        val view = binding.root
        return view

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Configurar la ventana del diálogo como transparente
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        arguments?.let {
            if (it.getSerializable("register") != null) {
                register = it.getSerializable("register")!! as register
            }
        }
        preferencesCajaChica = requireContext().getSharedPreferences("valuesCajaChica", Context.MODE_PRIVATE)
        recuperarPreferencias()
        calcularTamano()

        if (register == null) {
            //es registro nuevo
            binding.tvFecha.text = obtenerFecha()
            binding.txtLugar.setText(ultimaUbicacion)
            binding.tvtitle.text = "Nuevo Registro"
            binding.imgSSustento.setColorFilter(
                ContextCompat.getColor(requireContext(), R.color.boton_verde),
                PorterDuff.Mode.SRC_IN
            )
            binding.tvSSustento.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.boton_verde
                )
            )
        } else {
            //es modificacion de un registro
            if (register!!.nroDoc.isNotEmpty()) {
                //se validara si el registro es con sustento o sin sustento (con sustento traera un nro de documento caso contrario no)
                sustento = true
                binding.imgCSustento.setColorFilter(
                    ContextCompat.getColor(requireContext(), R.color.boton_verde),
                    PorterDuff.Mode.SRC_IN
                )
                binding.tvCSustento.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.boton_verde
                    )
                )
                binding.imgSSustento.setColorFilter(
                    ContextCompat.getColor(requireContext(), R.color.TintImageEnable),
                    PorterDuff.Mode.SRC_IN
                )
                binding.tvSSustento.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.TintImageEnable
                    )
                )
                binding.vwDecorationC.visibility = View.VISIBLE
                binding.vwDecorationS.visibility = View.INVISIBLE
                binding.lysustento.visibility = View.VISIBLE
            } else {
                //no tiene sustento
                sustento = false
                binding.imgCSustento.setColorFilter(
                    ContextCompat.getColor(requireContext(), R.color.TintImageEnable),
                    PorterDuff.Mode.SRC_IN
                )
                binding.tvCSustento.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.TintImageEnable
                    )
                )

                binding.imgSSustento.setColorFilter(
                    ContextCompat.getColor(requireContext(), R.color.boton_verde),
                    PorterDuff.Mode.SRC_IN
                )
                binding.tvSSustento.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.boton_verde
                    )
                )
                binding.vwDecorationC.visibility = View.INVISIBLE
                binding.vwDecorationS.visibility = View.VISIBLE
                binding.lysustento.visibility = View.GONE
            }
            //se seleccionara el boton correspondiente

            if (register!!.c_movilidad.isNotEmpty() || register!!.s_movilidad.isNotEmpty()) {
                ImageViewCompat.setImageTintList(
                    binding.btnmovilidad,
                    ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.boton_verde))
                )
                tipoGasto = 1
            } else if (register!!.c_alimentacion.isNotEmpty() || register!!.s_alimentacion.isNotEmpty()) {
                ImageViewCompat.setImageTintList(
                    binding.btnalimentacion,
                    ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.boton_verde))
                )
                tipoGasto = 2
            } else if (register!!.c_alojamiento.isNotEmpty() || register!!.s_alojamiento.isNotEmpty()) {
                ImageViewCompat.setImageTintList(
                    binding.btnalojamiento,
                    ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.boton_verde))
                )
                tipoGasto = 3
            } else {
                ImageViewCompat.setImageTintList(
                    binding.btnotros,
                    ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.boton_verde))
                )
                tipoGasto = 4
            }

            //se seleccionara el radiobutton correspondiente
            if (register!!.tipoDoc == "FC") binding.rbfactura.isChecked = true
            else if (register!!.tipoDoc == "BV") binding.rbboleta.isChecked = true
            else binding.rbticket.isChecked = true
            if (register!!.nroDoc.isNotEmpty()) {
                var valor = register!!.nroDoc.split("-")
                binding.txtserie.setText(valor[0].trim())
                binding.txtcorrelativo.setText(valor[1].trim())
            }
            //guardamos el id del registro seleccionado para actualizarlo
            idRegister = register!!.id
            binding.tvFecha.text = register!!.fecha
            binding.txtLugar.setText(register!!.ciudad)
            binding.tvtitle.text = "Editar Registro"
            //se distribuye los datos en los distinto imputs segun al tipo de gasto
            when (tipoGasto) {
                1 -> {
                    binding.lyRuta.visibility = View.VISIBLE
                    binding.txtDescripcion.visibility = View.GONE
                    binding.txtorigen.setText(register!!.descripcion.split("-")[0])
                    binding.txtdestino.setText(register!!.descripcion.split("-")[1])
                }

                2, 3, 4 -> {
                    binding.lyRuta.visibility = View.GONE
                    binding.txtDescripcion.visibility = View.VISIBLE
                    binding.txtproveedor.setText(register!!.proveedor)
                    binding.txtDescripcion.setText(register!!.descripcion)
                }
            }
            //buscamos quien tiene el monto y lo recuperamos
            var montoRecuperado = when {
                register!!.c_movilidad.isNotEmpty() -> register!!.c_movilidad
                register!!.c_alimentacion.isNotEmpty() -> register!!.c_alimentacion
                register!!.c_alojamiento.isNotEmpty() -> register!!.c_alojamiento
                register!!.c_otros.isNotEmpty() -> register!!.c_otros
                register!!.s_movilidad.isNotEmpty() -> register!!.s_movilidad
                register!!.s_alimentacion.isNotEmpty() -> register!!.s_alimentacion
                register!!.s_alojamiento.isNotEmpty() -> register!!.s_alojamiento
                register!!.s_otros.isNotEmpty() -> register!!.s_otros
                else -> ""
            }
            //le damos formato al monto que recuperamos y lo colocamos en su respectivo input
            if (montoRecuperado.startsWith("S")) binding.txtMonto.setText(montoRecuperado.split("/")[1].trim())
            else binding.txtMonto.setText(String.format(Locale.getDefault(), "%.2f", formatearMonto(montoRecuperado).toFloat()).trim())

        }

        binding.cvcsustento.setOnClickListener() {
            sustento = true
            binding.imgCSustento.setColorFilter(
                ContextCompat.getColor(requireContext(), R.color.boton_verde),
                PorterDuff.Mode.SRC_IN
            )
            binding.tvCSustento.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.boton_verde
                )
            )
            binding.imgSSustento.setColorFilter(
                ContextCompat.getColor(requireContext(), R.color.TintImageEnable),
                PorterDuff.Mode.SRC_IN
            )
            binding.tvSSustento.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.TintImageEnable
                )
            )
            binding.vwDecorationC.visibility = View.VISIBLE
            binding.vwDecorationS.visibility = View.INVISIBLE
            binding.lysustento.visibility = View.VISIBLE
        }
        binding.cvssustento.setOnClickListener() {
            sustento = false
            binding.imgSSustento.setColorFilter(
                ContextCompat.getColor(requireContext(), R.color.boton_verde),
                PorterDuff.Mode.SRC_IN
            )
            binding.tvSSustento.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.boton_verde
                )
            )

            binding.imgCSustento.setColorFilter(
                ContextCompat.getColor(requireContext(), R.color.TintImageEnable),
                PorterDuff.Mode.SRC_IN
            )
            binding.tvCSustento.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.TintImageEnable
                )
            )
            binding.vwDecorationC.visibility = View.INVISIBLE
            binding.vwDecorationS.visibility = View.VISIBLE
            binding.lysustento.visibility = View.GONE
        }

        binding.btnmovilidad.setOnClickListener() {
            selectedButton(binding.btnmovilidad)
            binding.lyRuta.visibility = View.VISIBLE
            binding.txtDescripcion.visibility = View.GONE
            tipoGasto = 1
        }
        binding.btnalimentacion.setOnClickListener() {
            selectedButton(binding.btnalimentacion)
            binding.lyRuta.visibility = View.GONE
            binding.txtDescripcion.visibility = View.VISIBLE
            tipoGasto = 2
        }
        binding.btnalojamiento.setOnClickListener() {
            selectedButton(binding.btnalojamiento)
            binding.lyRuta.visibility = View.GONE
            binding.txtDescripcion.visibility = View.VISIBLE
            tipoGasto = 3
        }
        binding.btnotros.setOnClickListener() {
            selectedButton(binding.btnotros)
            binding.lyRuta.visibility = View.GONE
            binding.txtDescripcion.visibility = View.VISIBLE
            tipoGasto = 4
        }

        binding.btnFecha.setOnClickListener() {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)
            val datePickerDialog =
                DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
                    val formattedDate = String.format(
                        Locale.getDefault(),
                        "%02d/%02d/%04d",
                        selectedDay,
                        selectedMonth + 1,
                        selectedYear
                    )
                    binding.tvFecha.text = formattedDate
                }, year, month, day)

            datePickerDialog.show()
        }
        binding.btnAgregar.setOnClickListener() {
            var tipoDoc = ""
            var textosVacios = true
            val mensajeError = "Campo requerido"
            var nroDocumento = ""
            var descripcion = ""
            if (sustento) {
                if (tipoGasto == 1) {
                    textosVacios = when {
                        validatetvVacio(binding.txtLugar, mensajeError) -> true
                        validatetvVacio(binding.txtserie, mensajeError) -> true
                        validatetvVacio(binding.txtcorrelativo, mensajeError) -> true
                        validatetvVacio(binding.txtproveedor, mensajeError) -> true
                        validatetvVacio(binding.txtorigen, mensajeError) -> true
                        validatetvVacio(binding.txtdestino, mensajeError) -> true
                        validatetvVacio(binding.txtMonto, mensajeError) -> true
                        else -> false
                    }
                } else {
                    textosVacios = when {
                        validatetvVacio(binding.txtLugar, mensajeError) -> true
                        validatetvVacio(binding.txtserie, mensajeError) -> true
                        validatetvVacio(binding.txtcorrelativo, mensajeError) -> true
                        validatetvVacio(binding.txtproveedor, mensajeError) -> true
                        validatetvVacio(binding.txtDescripcion, mensajeError) -> true
                        validatetvVacio(binding.txtMonto, mensajeError) -> true
                        else -> false
                    }
                }
            } else {
                if (tipoGasto == 1) {
                    textosVacios = when {
                        validatetvVacio(binding.txtLugar, mensajeError) -> true
                        validatetvVacio(binding.txtorigen, mensajeError) -> true
                        validatetvVacio(binding.txtdestino, mensajeError) -> true
                        validatetvVacio(binding.txtMonto, mensajeError) -> true
                        else -> false
                    }
                } else {
                    textosVacios = when {
                        validatetvVacio(binding.txtLugar, mensajeError) -> true
                        validatetvVacio(binding.txtDescripcion, mensajeError) -> true
                        validatetvVacio(binding.txtMonto, mensajeError) -> true
                        else -> false
                    }
                }
            }

            if (!textosVacios) {
                when (binding.rgGrupo.checkedRadioButtonId) {
                    binding.rbfactura.id -> tipoDoc = "FC"
                    binding.rbboleta.id -> tipoDoc = "BV"
                    binding.rbticket.id -> tipoDoc = "TK"
                    else -> null// Ningún RadioButton está seleccionado
                }
                if (binding.txtserie.text.toString().isNotEmpty()) nroDocumento =
                    "${
                        binding.txtserie.text.toString().trim()
                    } - ${binding.txtcorrelativo.text.toString().trim()}"

                if (tipoGasto == 1) descripcion = binding.txtorigen.text.toString()
                    .trim() + " - " + binding.txtdestino.text.toString().trim()
                else descripcion = binding.txtDescripcion.text.toString().trim()

                //se valida si el proceso sera una registro nuevo o una actualizacion de un registro
                if (idRegister.isEmpty()) {
                    //se realizara un registro nuevo
                    if (!sustento) tipoDoc = ""
                    listener?.onDataCollected(
                        register(
                            "",
                            binding.tvFecha.text.toString(),
                            binding.txtLugar.text.toString(),
                            tipoDoc,
                            nroDocumento,
                            binding.txtproveedor.text.toString().trim(),
                            descripcion,
                            identificarGasto(
                                sustento,
                                tipoGasto,
                                1,
                                formatearMonto(binding.txtMonto.text.toString().trim())
                            ),
                            identificarGasto(
                                sustento,
                                tipoGasto,
                                2,
                                formatearMonto(binding.txtMonto.text.toString().trim())
                            ),
                            identificarGasto(
                                sustento,
                                tipoGasto,
                                3,
                                formatearMonto(binding.txtMonto.text.toString().trim())
                            ),
                            identificarGasto(
                                sustento,
                                tipoGasto,
                                4,
                                formatearMonto(binding.txtMonto.text.toString().trim())
                            ),
                            identificarGasto(
                                sustento,
                                tipoGasto,
                                5,
                                formatearMonto(binding.txtMonto.text.toString().trim())
                            ),
                            identificarGasto(
                                sustento,
                                tipoGasto,
                                6,
                                formatearMonto(binding.txtMonto.text.toString().trim())
                            ),
                            identificarGasto(
                                sustento,
                                tipoGasto,
                                7,
                                formatearMonto(binding.txtMonto.text.toString().trim())
                            ),
                            identificarGasto(
                                sustento,
                                tipoGasto,
                                8,
                                formatearMonto(binding.txtMonto.text.toString().trim())
                            )
                        )
                    )
                    idRegister = ""
                    actualizarUltimaUbicacion(binding.txtLugar.text.toString())
                } else {
                    if (!sustento) tipoDoc = ""
                    //se realizara una actualizacion de registro
                    listener?.onDataCollectedUpdate(
                        register(
                            idRegister,
                            binding.tvFecha.text.toString(),
                            binding.txtLugar.text.toString(),
                            tipoDoc,
                            nroDocumento,
                            binding.txtproveedor.text.toString().trim(),
                            descripcion,
                            identificarGasto(
                                sustento,
                                tipoGasto,
                                1,
                                formatearMonto(binding.txtMonto.text.toString().trim())
                            ),
                            identificarGasto(
                                sustento,
                                tipoGasto,
                                2,
                                formatearMonto(binding.txtMonto.text.toString().trim())
                            ),
                            identificarGasto(
                                sustento,
                                tipoGasto,
                                3,
                                formatearMonto(binding.txtMonto.text.toString().trim())
                            ),
                            identificarGasto(
                                sustento,
                                tipoGasto,
                                4,
                                formatearMonto(binding.txtMonto.text.toString().trim())
                            ),
                            identificarGasto(
                                sustento,
                                tipoGasto,
                                5,
                                formatearMonto(binding.txtMonto.text.toString().trim())
                            ),
                            identificarGasto(
                                sustento,
                                tipoGasto,
                                6,
                                formatearMonto(binding.txtMonto.text.toString().trim())
                            ),
                            identificarGasto(
                                sustento,
                                tipoGasto,
                                7,
                                formatearMonto(binding.txtMonto.text.toString().trim())
                            ),
                            identificarGasto(
                                sustento,
                                tipoGasto,
                                8,
                                formatearMonto(binding.txtMonto.text.toString().trim())
                            )
                        )
                    )
                }
                dismiss()
            }
        }
        binding.btnCancelar.setOnClickListener(){
            dismiss()
        }
    }

    private fun actualizarUltimaUbicacion(ultimoLugar: String) {
        val editor = preferencesCajaChica.edit()
        editor.putString("ULTIMA_UBICACION", ultimoLugar)
        editor.apply()
    }

    private fun recuperarPreferencias() {
        ultimaUbicacion = preferencesCajaChica.getString("ULTIMA_UBICACION", "").toString()
    }

    private fun identificarGasto(
        b_CS: Boolean,
        b_tipoGasto: Int,
        calltype: Int,
        monto: String
    ): String {
        var retorno = ""
        when (calltype) {
            1 -> if (b_CS && b_tipoGasto == 1) retorno = monto
            2 -> if (b_CS && b_tipoGasto == 2) retorno = monto
            3 -> if (b_CS && b_tipoGasto == 3) retorno = monto
            4 -> if (b_CS && b_tipoGasto == 4) retorno = monto
            5 -> if (!b_CS && b_tipoGasto == 1) retorno = monto
            6 -> if (!b_CS && b_tipoGasto == 2) retorno = monto
            7 -> if (!b_CS && b_tipoGasto == 3) retorno = monto
            8 -> if (!b_CS && b_tipoGasto == 4) retorno = monto
            else -> retorno = monto
        }
        //return retorno.replace(".", ",")
        return retorno
    }

    private fun formatearMonto(monto: String): String {
        var Monto = ""
        if (monto != "") {
            Monto = monto.replace("S/.", "S/ ")
            Monto = Monto.replace(",", ".")
        }
        return Monto
    }
//    private fun formatearMonto(monto: String): String {
//        return monto
//            .takeIf { it.isNotEmpty() } // Verifica que no esté vacío
//            ?.replace("S/.", "S/ ")
//            ?.replace(",", ".")
//            ?: ""                        // Devuelve cadena vacía si es null o vacío
//    }

    private fun selectedButton(buttonSelected: ImageView) {
        val buttons = listOf(
            binding.btnmovilidad,
            binding.btnalimentacion,
            binding.btnalojamiento,
            binding.btnotros
        )

        buttons.forEach { button ->
            if (button == buttonSelected) {
                button.setColorFilter(
                    ContextCompat.getColor(requireContext(), R.color.boton_verde),
                    PorterDuff.Mode.SRC_IN
                )
            } else {
                button.setColorFilter(
                    ContextCompat.getColor(requireContext(), R.color.darker_grey),
                    PorterDuff.Mode.SRC_IN
                )
            }
        }
    }

    private fun validatetvVacio(textView: TextView, errorMessage: String): Boolean {
        return if (textView.text.toString().isEmpty()) {
            textView.error = errorMessage
            true
        } else false
    }

    private fun obtenerFecha(): String {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = String.format(Locale.getDefault(), "%02d", calendar.get(Calendar.MONTH) + 1)
        val dayOfMonth =
            String.format(Locale.getDefault(), "%02d", calendar.get(Calendar.DAY_OF_MONTH))

        return "$dayOfMonth/$month/$year"
    }

    private fun calcularTamano() {
        val displayMetrics = DisplayMetrics()
        requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
        val screenWidth = displayMetrics.widthPixels
        val screenheigth = displayMetrics.heightPixels
        // Calcular el ancho máximo como el 90% del ancho de la pantalla
        val maxWidth = (screenWidth * 0.9).toInt()
        val maxheight = (screenheigth * 0.9).toInt()
        dialog?.window?.setLayout(maxWidth, ViewGroup.LayoutParams.WRAP_CONTENT)
    }
}