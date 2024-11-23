package com.kasolution.aiohunterresources.UI.FichasTecnicas.view.fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.kasolution.aiohunterresources.UI.FichasTecnicas.view.adapter.ApproveAdapter
import com.kasolution.aiohunterresources.UI.FichasTecnicas.view.fragment.dialog.ImageViewFragment
import com.kasolution.aiohunterresources.UI.FichasTecnicas.view.model.Brand
import com.kasolution.aiohunterresources.UI.FichasTecnicas.view.model.VehicleModel
import com.kasolution.aiohunterresources.UI.FichasTecnicas.viewModel.ShowModelViewModel
import com.kasolution.aiohunterresources.core.DialogProgress
import com.kasolution.aiohunterresources.core.dataConexion.urlId
import com.kasolution.aiohunterresources.databinding.FragmentCheckBinding

class CheckFragment : Fragment() {
    private var _binding: FragmentCheckBinding? = null
    private val binding get() = _binding!!
    private val ShowModelViewModel: ShowModelViewModel by viewModels()
    private lateinit var lmanager: LinearLayoutManager
    private lateinit var adapter: ApproveAdapter
    private lateinit var lista: ArrayList<VehicleModel>
    private var itemPosition = -1
    private var urlId: urlId? = null
    private lateinit var preferencesFichasTecnicas: SharedPreferences
    private lateinit var preferencesUser: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCheckBinding.inflate(layoutInflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        preferencesFichasTecnicas =
            requireContext().getSharedPreferences("valueFichasTecnicas", Context.MODE_PRIVATE)
        preferencesUser = requireContext().getSharedPreferences("valueUser", Context.MODE_PRIVATE)
        lista = ArrayList()
        initUI()
        recuperarPreferencias()
        configSwipe()


        ShowModelViewModel.onCreate(urlId!!, "Publicado", 3, "Revision")
        ShowModelViewModel.showVehicleModel.observe(viewLifecycleOwner, Observer { listaModel ->
            lista.addAll(listaModel)
            adapter.notifyDataSetChanged()
        })
        ShowModelViewModel.updateModel.observe(viewLifecycleOwner, Observer { vehiculo ->
            lista.removeAt(itemPosition)
            adapter.notifyItemRemoved(itemPosition)
        })
        ShowModelViewModel.deleteModel.observe(viewLifecycleOwner, Observer {
            lista.removeAt(itemPosition)
            adapter.notifyItemRemoved(itemPosition)
        })
        ShowModelViewModel.isloading.observe(viewLifecycleOwner, Observer {
            if (it) DialogProgress.show(requireContext(), "Cargando...")
            else DialogProgress.dismiss()
//            binding.pbloading.isVisible = it
        })
        binding.imgBottonBack.setOnClickListener() {
            requireActivity().supportFragmentManager.popBackStack()
        }

    }

    private fun recuperarPreferencias() {
        val url=preferencesFichasTecnicas.getString("URL_SCRIPT_FICHAS", "")
        val idSheet=preferencesFichasTecnicas.getString("IDSHEET_FICHAS", "")
        urlId= urlId(url!!,"",idSheet!!,"")
    }

    private fun initUI() {
        lmanager = LinearLayoutManager(context)
        adapter = ApproveAdapter(
            listaRecibida = lista,
            OnClickListener = { modelos, action, position ->
                onItemClick(modelos, action, position)
            })
        binding.RvItem.layoutManager = lmanager
        binding.RvItem.adapter = adapter
    }

    private fun onItemClick(modelos: VehicleModel, action: Int, position: Int) {
        itemPosition = position
        if (action == 1) {
            //Modificar Registro
            ShowModelViewModel.updateModel(
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
                getUserAdmin(),
                "Publicado"
            )
        } else if (action == 2) {
            //eliminar Registro
            ShowModelViewModel.deleteModel(urlId!!, modelos.id)
        } else {
            //mostrar imagen
            val dialogFragment = ImageViewFragment()
            dialogFragment.isCancelable = true
            //dialogFragment.getlistamarcas(marcas!!) //desabilitado temporal
            val args = Bundle().apply {
                putString("imagelink", modelos.imagen)
            }
            dialogFragment.arguments = args
            dialogFragment.show(childFragmentManager, "AddImageResourceDialogFragment")
        }
    }
    private fun getUserAdmin(): String {
        val name = preferencesUser.getString("NAME", "").toString()
        val lastName = preferencesUser.getString("LASTNAME", "").toString()

        return "$name $lastName"
    }
    private fun configSwipe() {
        binding.swipeRefresh.setOnRefreshListener {
            binding.swipeRefresh.isRefreshing = false
            ShowModelViewModel.onRefresh(urlId!!, "Publicado", 3, "Revision")
        }
    }
}