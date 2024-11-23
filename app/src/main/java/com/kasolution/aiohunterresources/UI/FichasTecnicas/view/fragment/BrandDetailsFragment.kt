package com.kasolution.aiohunterresources.UI.FichasTecnicas.view.fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.kasolution.aiohunterresources.R
import com.kasolution.aiohunterresources.UI.FichasTecnicas.view.adapter.ModelAdapter
import com.kasolution.aiohunterresources.UI.FichasTecnicas.view.fragment.dialog.AddModelFragment
import com.kasolution.aiohunterresources.UI.FichasTecnicas.view.model.Brand
import com.kasolution.aiohunterresources.UI.FichasTecnicas.view.model.VehicleModel
import com.kasolution.aiohunterresources.UI.FichasTecnicas.viewModel.ModelViewModel
import com.kasolution.aiohunterresources.core.CustomPicasso
import com.kasolution.aiohunterresources.core.DialogProgress
import com.kasolution.aiohunterresources.core.DialogUtils
import com.kasolution.aiohunterresources.core.dataConexion.urlId
import com.kasolution.aiohunterresources.databinding.FragmentBrandDetailsBinding
import com.kasolution.recursoshunter.UI.view.Home.Interfaces.DialogListener
import com.squareup.picasso.Callback
import java.io.Serializable
import java.lang.Exception

class BrandDetailsFragment : Fragment(), DialogListener {
    private var _binding: FragmentBrandDetailsBinding? = null
    private val binding get() = _binding!!
    private val ModelViewModel: ModelViewModel by viewModels()
    private lateinit var lmanager: LinearLayoutManager
    private lateinit var adapter: ModelAdapter
    private lateinit var lista: ArrayList<VehicleModel>
    private var marcas: Brand? = null
    var modeloSeleccionado = ""
    private var tecnico = ""
    private var tipo = ""
    private var itemPosition = -1
    private var urlId: urlId?=null
    private lateinit var preferencesFichasTecnicas: SharedPreferences
    private lateinit var preferencesUser: SharedPreferences
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        preferencesFichasTecnicas =
            requireContext().getSharedPreferences("valueFichasTecnicas", Context.MODE_PRIVATE)
        preferencesUser = requireContext().getSharedPreferences("valueUser", Context.MODE_PRIVATE)
        lista = ArrayList()
        initUI()
        recuperarPreferencias()
        ClasificarAcceso(tipo)
        configSwipe()

        arguments?.let {
            marcas = it.getSerializable("lista")!! as Brand
            ModelViewModel.onCreate(urlId!!, marcas!!.brand, "Publicado")
        }

        binding.rvListaModel.addItemDecoration(
            DividerItemDecoration(
                requireContext(),
                lmanager.orientation
            )
        )


        binding.tvTitle.text = "Lista de modelos ${marcas!!.brand.toString().lowercase()}"
        cargarIcon()
        ModelViewModel.vehicleModel.observe(viewLifecycleOwner, Observer { listaModel ->
            val listModelFilter = listaModel.distinctBy { it.modelo }
            lista.addAll(listModelFilter)
            adapter.notifyDataSetChanged()
        })
        ModelViewModel.isloading.observe(viewLifecycleOwner, Observer {
            if (it) DialogProgress.show(requireContext(), "Cargando...")
            else DialogProgress.dismiss()
        })
        ModelViewModel.insertarModel.observe(viewLifecycleOwner, Observer { Model ->
            lista.add(Model)
            adapter.notifyItemInserted(lista.size - 1)
            lmanager.scrollToPositionWithOffset(lista.size - 1, 10)
            Toast.makeText(requireContext(),"Registro exitoso, pero estará pendiente de aprobación antes de ser visible.",
                Toast.LENGTH_LONG).show()
        })
        ModelViewModel.updateModel.observe(viewLifecycleOwner, Observer { vehiculo ->
            if (vehiculo.estado!="Publicado"){
                lista.removeAt(itemPosition)
                adapter.notifyItemRemoved(itemPosition)
            }else{
                lista[itemPosition] = vehiculo
                adapter.notifyItemChanged(itemPosition)
            }
            Toast.makeText(requireContext(),"Registro actualizado, pero estará pendiente de aprobación antes de ser visible.",
                Toast.LENGTH_LONG).show()
        })
        ModelViewModel.deleteModel.observe(viewLifecycleOwner, Observer {
            lista.removeAt(itemPosition)
            adapter.notifyItemRemoved(itemPosition)
        })
        binding.fbAddModel.setOnClickListener() {
//            dialogAddModel()
            abrirDialog()
        }
        binding.btnback.setOnClickListener() {
            requireActivity().supportFragmentManager.popBackStack()
        }

        binding.customSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                adapter.filter(s.toString())
            }

            override fun afterTextChanged(p0: Editable?) {
            }

        })
        binding.btnSearch.setOnClickListener() {
            val animInLeft = AnimationUtils.loadAnimation(context, R.anim.slide_in_top)
            val animOutLeft = AnimationUtils.loadAnimation(context, R.anim.slide_out_top)
            val animInRight = AnimationUtils.loadAnimation(context, R.anim.slide_in_bottom)
            val animOutRight = AnimationUtils.loadAnimation(context, R.anim.slide_out_bottom)
            if (binding.customSearch.visibility == View.VISIBLE) {
                binding.customSearch.startAnimation(animOutRight)
                binding.ivSearch.setImageResource(R.drawable.ic_search)
                binding.customSearch.text.clear()
                binding.customSearch.visibility = View.GONE
                binding.tvTitle.visibility = View.VISIBLE
                binding.tvTitle.startAnimation(animInLeft)
            } else {
                binding.tvTitle.startAnimation(animOutLeft)
                binding.tvTitle.visibility = View.GONE
                binding.customSearch.startAnimation(animInRight)
                binding.customSearch.visibility = View.VISIBLE
                binding.ivSearch.setImageResource(R.drawable.close_icon)
            }
        }
    }

    private fun abrirDialog(vehiculo: VehicleModel? = null) {
        val dialogFragment = AddModelFragment()
        dialogFragment.isCancelable = false
        dialogFragment.getlistamarcas(marcas!!) //desabilitado temporal
        dialogFragment.listener = this
        val args = Bundle().apply {
            putSerializable("modelo", vehiculo)
        }
        dialogFragment.arguments = args
        dialogFragment.show(childFragmentManager, "AddImageResourceDialogFragment")
    }

    private fun initUI() {
        lmanager = LinearLayoutManager(context)
        adapter = ModelAdapter(
            listaRecibida = lista,
            onClickListener = { modelos, action, position ->
                onItemClicListener(
                    modelos,
                    action,
                    position
                )
            },
            tipo
        )
        binding.rvListaModel.layoutManager = lmanager
        binding.rvListaModel.adapter = adapter


    }

    private fun onItemClicListener(modelos: VehicleModel, action: Int, position: Int) {
        itemPosition = position
        when (action) {
            1 -> {
                //crear nueva version
                modeloSeleccionado = modelos.modelo
                val ShowdetailsFragment = ShowResourcesFragment()
                val args = Bundle().apply {
                    putSerializable("model", objectSend())
                    putSerializable("brand", marcas)
                }
                ShowdetailsFragment.arguments = args
                val fragmentManager = requireActivity().supportFragmentManager
                val fragmentTransaction = fragmentManager.beginTransaction()
                fragmentTransaction.replace(R.id.contenedor, ShowdetailsFragment)
                fragmentTransaction.addToBackStack(null) // Para agregar el fragmento a la pila de retroceso
                fragmentTransaction.commit()
            }

            2 -> {
                //editar
                abrirDialog(lista[position])
            }

            3 -> {
                //observar -> editar
                DialogUtils.dialogQuestion(
                    requireContext(),
                    titulo = "Advertencia",
                    mensage = "Si es observado ningun usuario podrá consultarlo, Desea Observarlo?",
                    positiveButtontext = "Aceptar",
                    onPositiveClick = {
                        ModelViewModel.updateModel(
                            urlId!!,
                            modelos.id,
                            "oldData",
                            "no data",
                            modelos.marca,
                            modelos.modelo,
                            modelos.imagen,
                            modelos.comentarios,
                            modelos.basica,
                            modelos.extra,
                            modelos.autor,
                            "",
                            "Observado"
                        )
                    }
                )
            }

            4 -> {
                //eliminar
                DialogUtils.dialogQuestion(
                    requireContext(),
                    titulo = "Eliminar",
                    mensage = "Esta a punto de Eliminar el registro de ${modelos.marca} ${modelos.modelo}",
                    positiveButtontext = "Aceptar",
                    onPositiveClick = {
                        ModelViewModel.deleteModel(urlId!!, lista[position].id)
                    })
            }
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBrandDetailsBinding.inflate(layoutInflater, container, false)

        // Configurar el SwipeHelper
//        setupSwipeHelper()
        return binding.root
    }

    fun objectSend(): Serializable? {
        var modelo: VehicleModel? = null
        for (valor in lista) {
            if (valor.modelo == modeloSeleccionado) {
                modelo = valor
                break
            }
        }
        return modelo!!
    }

    private fun cargarIcon() {
        val customPicasso = CustomPicasso.getInstance(requireContext())
        customPicasso.load("https://drive.google.com/uc?export=view&id=${marcas!!.icon}").into(
            binding.imgLogo,
            object : Callback {
                override fun onSuccess() {
                    binding.imgLogo.animate().alpha(1f).setDuration(300)
                }

                override fun onError(e: Exception?) {
                }
            })
    }
    fun recuperarPreferencias(){
        val url=preferencesFichasTecnicas.getString("URL_SCRIPT_FICHAS", "")
        val idSheet=preferencesFichasTecnicas.getString("IDSHEET_FICHAS", "")
        urlId= urlId(url!!,"",idSheet!!,"")
        tipo=preferencesUser.getString("TIPO", "")!!
//        Log.i("tipo",tipo)
    }

    override fun onDataCollected(
        id: String,
        data: String,
        type: String,
        marca: String,
        modelo: String,
        comentarios: String,
        basico: String,
        otros: String,
        tecnico: String,
        aprobado: String,
        estado: String
    ) {
        ModelViewModel.insertModel(
            urlId!!,
            data,
            type,
            marca,
            modelo,
            comentarios,
            basico,
            otros,
            tecnico,
            "",
            "Revision"
        )
    }

    override fun onDataCollectedUpdate(
        id: String,
        data: String,
        type: String,
        marca: String,
        modelo: String,
        linkImage: String,
        comentarios: String,
        basico: String,
        otros: String,
        tecnico: String,
        aprobado: String,
        estado: String
    ) {
        ModelViewModel.updateModel(
            urlId!!,
            id,
            data,
            type,
            marca,
            modelo,
            linkImage,
            comentarios,
            basico,
            otros,
            tecnico,
            "",
            "Revision"
        )
    }

    private fun ClasificarAcceso(tipoUsuario: String) {
        when (tipoUsuario) {
            "Administrador" -> {

            }

            "Coloborador" -> {

            }

            "Invitado" -> {
                binding.fbAddModel.isVisible = false
            }

            "Developer" -> {

            }
        }
    }
    private fun configSwipe() {
        binding.swipeRefresh.setOnRefreshListener {
            binding.swipeRefresh.isRefreshing = false
            adapter.limpiar()
            ModelViewModel.onRefresh(urlId!!, marcas!!.brand, "Publicado")
        }
    }
}