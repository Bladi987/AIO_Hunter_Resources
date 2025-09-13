package com.kasolution.aiohunterresources.UI.User.fragment

import com.kasolution.aiohunterresources.R
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ArrayAdapter
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.kasolution.aiohunterresources.UI.User.model.user
import com.kasolution.aiohunterresources.UI.User.viewModel.UserViewModel
import com.kasolution.aiohunterresources.core.dataConexion.urlId
import com.kasolution.aiohunterresources.databinding.FragmentAddUserBinding

class AddUserFragment : DialogFragment() {
    private lateinit var binding: FragmentAddUserBinding
    private val UserViewModel: UserViewModel by activityViewModels()
    private var usuario: user? = null
    private var tipoSeleccionado = ""
    private var user: user? = null
    private var urlId: urlId? = null
    private lateinit var preferencesAccess: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddUserBinding.inflate(layoutInflater, container, false)
        // Carga la animación desde el archivo XML
        val anim = AnimationUtils.loadAnimation(requireContext(), R.anim.bounce3)

        // Aplica la animación a la vista del fragmento
        binding.root.startAnimation(anim)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        preferencesAccess =
            requireContext().getSharedPreferences("valuesAccess", Context.MODE_PRIVATE)
        recuperarPreferencias()
        // Configurar la ventana del diálogo como transparente
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        arguments?.let {
            usuario = it.getSerializable("usuario")!! as user
            binding.etNombre.setText(usuario!!.name)
            binding.etApellidos.setText(usuario!!.lastName)
            binding.etIdentification.setText(usuario!!.identification)
            binding.etUser.setText(usuario!!.user)
            tipoSeleccionado = usuario!!.tipo
        }
        calcularTamano()

        llenarSpinnerCategoria()

        binding.btnCancelar.setOnClickListener() {
            dismiss()
        }
        binding.btnGuardar.setOnClickListener() {
            if (binding.etNombre.text!!.isNotEmpty()) {
                if (binding.etApellidos.text!!.isNotEmpty()) {
                    if (binding.etUser.text!!.isNotEmpty()) {
                        if (tipoSeleccionado.isNotEmpty()) {
                            //Actualizar registro
                            actualizarUsuario(
                                user(
                                    id = usuario!!.id, name = binding.etNombre.text.toString(),
                                    lastName = binding.etApellidos.text.toString(),
                                    identification = binding.etIdentification.text.toString(),
                                    user = binding.etUser.text.toString(),
                                    password = usuario!!.password,
                                    tipo = binding.spTipo.selectedItem.toString(),
                                    ""
                                )
                            )

                            dismiss()
                        } else {
                            //Guardar Registro
                            insertarUsuario(
                                user(
                                    id = "",
                                    name = binding.etNombre.text.toString(),
                                    lastName = binding.etApellidos.text.toString(),
                                    identification = binding.etIdentification.text.toString(),
                                    user = binding.etUser.text.toString(),
                                    password = "",
                                    tipo = binding.spTipo.selectedItem.toString(),
                                    ""
                                )
                            )
                            dismiss()
                        }
                    } else binding.etUser.error = "Campo Requerido"
                } else binding.etApellidos.error = "Campo Requerido"
            } else binding.etNombre.error = "Campo Requerido"

        }

        binding.etNombre.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                if (!s.isNullOrBlank()) {
                    val words = s.split("\\s+".toRegex()) // Divide el texto en palabras
                    val capitalizedWords = words.map { it.capitalize() } // Capitaliza cada palabra
                    val result = capitalizedWords.joinToString(" ") // Une las palabras nuevamente
                    if (result != s.toString()) {
                        binding.etNombre.setText(result) // Actualiza el texto en el EditText
                        binding.etNombre.setSelection(result.length) // Establece el cursor al final del texto
                    }
                }
            }

        })
        binding.etApellidos.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                if (!s.isNullOrBlank()) {
                    val words = s.split("\\s+".toRegex()) // Divide el texto en palabras
                    val capitalizedWords = words.map { it.capitalize() } // Capitaliza cada palabra
                    val result = capitalizedWords.joinToString(" ") // Une las palabras nuevamente
                    if (result != s.toString()) {
                        binding.etApellidos.setText(result) // Actualiza el texto en el EditText
                        binding.etApellidos.setSelection(result.length) // Establece el cursor al final del texto
                    }
                }
                binding.etUser.setText(
                    generarUser(
                        binding.etNombre.text.toString(),
                        binding.etApellidos.text.toString()
                    )
                )
            }

        })
    }

    private fun generarUser(nombre: String, apellidos: String): String {
        var part1 = "@"
        if (nombre.isNotEmpty())
            part1 = nombre[0].toString()
        val part2 = apellidos.split(" ")
        return (part1 + part2[0]).lowercase()
    }

    private fun insertarUsuario(user: user) {
        UserViewModel.insertUser(urlId!!,user)
    }

    private fun actualizarUsuario(user: user) {
        UserViewModel.updateUser(urlId!!,user,"update")
    }

    private fun llenarSpinnerCategoria() {
        var lista = llenarCatUser()
        // Adaptadeara el spinner
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, lista)

        // Establecer el layout para el menú desplegable
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // Vincular el adaptador con el spinner
        binding.spTipo.adapter = adapter
        if (tipoSeleccionado.isNotEmpty()) {
            Log.i("BladiDevUser", tipoSeleccionado)
            //seleccionar un item segun categoria proporcionada
            binding.spTipo.setSelection(adapter.getPosition(tipoSeleccionado))
        }
    }


    fun calcularTamano() {
        val displayMetrics = DisplayMetrics()
        requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
        val screenWidth = displayMetrics.widthPixels

        // Calcular el ancho máximo como el 90% del ancho de la pantalla
        val maxWidth = (screenWidth * 0.9).toInt()
        dialog?.window?.setLayout(maxWidth, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    fun llenarCatUser(): List<String> {
        var lista2 = listOf("Administrador", "Colaborador", "Invitado")
        return lista2
    }

    private fun recuperarPreferencias() {
        urlId = urlId(
            idScript = preferencesAccess.getString("IDSCRIPTACCESS", "").toString(),
            "",
            idSheet = preferencesAccess.getString("IDSHEETACCESS", "").toString(),
            ""
        )
    }
}