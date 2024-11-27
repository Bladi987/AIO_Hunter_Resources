package com.kasolution.aiohunterresources.UI.CajaChica.view.fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.kasolution.aiohunterresources.R
import com.kasolution.aiohunterresources.UI.CajaChica.view.adapter.FileDetailsAdapter
import com.kasolution.aiohunterresources.UI.CajaChica.view.model.file
import com.kasolution.aiohunterresources.UI.CajaChica.view.model.fileDetails
import com.kasolution.aiohunterresources.UI.CajaChica.viewModel.FileDetailsViewModel
import com.kasolution.aiohunterresources.core.DialogProgress
import com.kasolution.aiohunterresources.core.dataConexion.urlId
import com.kasolution.aiohunterresources.databinding.FragmentFileDetailsBinding
import java.io.Serializable
import java.util.ArrayList


class FileDetailsFragment : Fragment() {
    private lateinit var binding: FragmentFileDetailsBinding
    private val fileDetailsViewModel: FileDetailsViewModel by viewModels()
    private lateinit var adapter: FileDetailsAdapter
    private lateinit var lmanager: LinearLayoutManager
    private lateinit var listFileDetails: ArrayList<fileDetails>
    private var files: file? = null
    private var urlId: urlId?=null
    var sheetSelected = ""
    private var listaSheet:ArrayList<fileDetails>?=null
    private lateinit var preferencesCajaChica: SharedPreferences
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentFileDetailsBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        preferencesCajaChica =
            requireContext().getSharedPreferences("valuesCajaChica", Context.MODE_PRIVATE)
        listFileDetails = ArrayList()
        init()
        recuperarDatosRecibidos()
        recuperarPreferencias()
        configSwipe()
        binding.tvTitle.text = files?.nombre
        binding.btnback.setOnClickListener(){
            requireActivity().supportFragmentManager.popBackStack()
        }
        fileDetailsViewModel.onCreate(urlId!!)
        fileDetailsViewModel.FileDetailsModel.observe(viewLifecycleOwner, Observer { listSheet ->
            adapter.limpiar()
            listaSheet=listSheet
            listFileDetails.addAll(listSheet)
            adapter.notifyDataSetChanged()
        })
        fileDetailsViewModel.isloading.observe(viewLifecycleOwner, Observer {
            if (it) DialogProgress.show(requireContext(), "Recuperando...")
            else DialogProgress.dismiss()
        })
    }

    private fun recuperarPreferencias() {
        urlId=urlId(preferencesCajaChica.getString("URL_SCRIPT", "").toString(),"",files!!.id,"")
    }

    private fun recuperarDatosRecibidos() {
        arguments?.let {
            files = it.getSerializable("lista")!! as file
        }
    }
    private fun configSwipe() {
        binding.swipeRefresh.setOnRefreshListener {
            binding.swipeRefresh.isRefreshing = false
            fileDetailsViewModel.onRefresh(urlId!!)
        }
    }
    private fun init() {
        lmanager = LinearLayoutManager(context)
        adapter = FileDetailsAdapter(
            listaRecibida = listFileDetails,
            OnClickListener = { itemFileDetails -> onItemSelected(itemFileDetails) },
            OnClickUpdate = { position -> onItemUpdate(position) },
            OnClickDelete = { position -> onDeleteItem(position) })
        binding.recyclerview1.layoutManager = lmanager
        binding.recyclerview1.adapter = adapter
    }

    private fun onDeleteItem(position: Int) {

    }

    private fun onItemUpdate(position: Int) {

    }

    private fun onItemSelected(itemFileDetails: fileDetails) {
        sheetSelected = itemFileDetails.nombre
        val gson = Gson()
        val json = gson.toJson(listaSheet)
        val editor = preferencesCajaChica.edit()
        editor.putString("IDSHEET", files?.id)
        editor.putString("FILENAME", files?.nombre)
        editor.putString("SHEETNAME", sheetSelected)
        editor.putString("LIST_SHEET",json.toString())
        editor.apply()
        Log.i("BladiDev",json.toString())
        val registerFragment = RegisterFragment()
        val fragmentManager = requireActivity().supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.contenedorCajaChica, registerFragment)
        fragmentTransaction.addToBackStack(null) // Para agregar el fragmento a la pila de retroceso
        fragmentTransaction.commit()

    }
}