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
import androidx.recyclerview.widget.GridLayoutManager
import com.kasolution.aiohunterresources.R
import com.kasolution.aiohunterresources.UI.CajaChica.view.adapter.FileAdapter
import com.kasolution.aiohunterresources.UI.CajaChica.view.model.file
import com.kasolution.aiohunterresources.UI.CajaChica.viewModel.FileViewModel
import com.kasolution.aiohunterresources.core.DialogProgress
import com.kasolution.aiohunterresources.databinding.FragmentFileBinding
import java.io.Serializable
import java.util.ArrayList
import com.google.gson.Gson
import com.kasolution.aiohunterresources.core.dataConexion.urlId


class FileFragment : Fragment() {
    private lateinit var binding: FragmentFileBinding
    private val fileViewModel: FileViewModel by viewModels()
    private lateinit var adapter: FileAdapter
    private lateinit var glmanager: GridLayoutManager
    private lateinit var listFile: ArrayList<file>
    var fileSelected = ""
    private var urlId: urlId? = null
    private lateinit var preferencesCajaChica: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentFileBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        preferencesCajaChica =
            requireContext().getSharedPreferences("valuesCajaChica", Context.MODE_PRIVATE)
        listFile = ArrayList()
        init()
        recuperarPreferencias()
        configSwipe()
        binding.btnback.setOnClickListener() {
            requireActivity().supportFragmentManager.popBackStack()
            //requireActivity().finish()

        }
        fileViewModel.onCreate(urlId!!)
        fileViewModel.FileModel.observe(viewLifecycleOwner, Observer { listaArchivos ->
            adapter.limpiar()
            listFile.addAll(listaArchivos)
            adapter.notifyDataSetChanged()
            //Guardar en preferences la lista de archivos
            guardarListaArchivos(listaArchivos)
        })
        fileViewModel.isloading.observe(viewLifecycleOwner, Observer {
            if (it) DialogProgress.show(requireContext(), "Recuperando...")
            else DialogProgress.dismiss()
        })
    }
    private fun recuperarPreferencias() {
        urlId = urlId(
            preferencesCajaChica.getString("URL_SCRIPT", "").toString(),
            preferencesCajaChica.getString("IDFILE", "").toString(),
            "",
            ""
        )
    }

    private fun guardarListaArchivos(listaArchivos: ArrayList<file>?) {
        val gson = Gson()
        val json = gson.toJson(listaArchivos)
        val editor = preferencesCajaChica.edit()
        editor.putString("LIST_FILE", json).apply()
    }

    private fun configSwipe() {
        binding.swipeRefresh.setOnRefreshListener {
            binding.swipeRefresh.isRefreshing = false
            fileViewModel.onRefresh(urlId!!)
        }
    }

    private fun init() {
        val columnWidthDp = 400
        val columns = resources.displayMetrics.widthPixels / columnWidthDp
        glmanager = GridLayoutManager(context, columns)
        adapter = FileAdapter(
            listaRecibida = listFile,
            OnClickListener = { itemFile -> onItemSelected(itemFile) },
            OnClickUpdate = { itemFile, position -> onItemUpdate(itemFile, position) },
            OnClickDelete = { id, position -> onDeleteItem(id, position) })
        binding.recyclerview1.layoutManager = glmanager
        binding.recyclerview1.adapter = adapter

    }

    private fun onItemSelected(file: file) {
        fileSelected = file.nombre
        val detailsFragment = FileDetailsFragment()
        val args = Bundle().apply {
            putSerializable("lista", objectSend())
        }
        detailsFragment.arguments = args

        val fragmentManager = requireActivity().supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.contenedorCajaChica, detailsFragment)
        fragmentTransaction.addToBackStack(null) // Para agregar el fragmento a la pila de retroceso
        fragmentTransaction.commit()
    }

    private fun onItemUpdate(file: file, position: Int) {
        //dialogFile(itemFile, position)
    }

    private fun onDeleteItem(id: Int, position: Int) {
//        if (manager.elimarArchivo(id.toString()) != -1) {
//            listFile.removeAt(position)
//            adapterFile.notifyItemRemoved(position)
//            Toast.makeText(context, "Registro eliminado correctamente", Toast.LENGTH_SHORT).show()
//        } else
//            Toast.makeText(context, "Ups... Algo salio mal", Toast.LENGTH_SHORT).show()
    }

    fun objectSend(): Serializable? {
        var file: file? = null
        for (valor in listFile) {
            if (valor.nombre == fileSelected) {
                file = valor
                break
            }
        }
        return file!!
    }
}