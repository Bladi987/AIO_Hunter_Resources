package com.kasolution.aiohunterresources.UI.FichasTecnicas.view.adapter

import android.content.Context
import android.graphics.Color
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.PopupWindow
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.kasolution.aiohunterresources.R
import com.kasolution.aiohunterresources.UI.FichasTecnicas.view.model.VehicleModel
import com.kasolution.aiohunterresources.UI.FichasTecnicas.view.model.menuOption
import com.kasolution.aiohunterresources.core.CustomPicasso
import com.kasolution.aiohunterresources.core.PopupMenuHelper
import com.kasolution.aiohunterresources.databinding.ItemShowModelBinding
import com.squareup.picasso.Callback


class ShowModelAdapter(
    private val listaRecibida: ArrayList<VehicleModel>,
    private val OnClickListener: (VehicleModel, Int, Int) -> Unit,
    private val tipoUsuario:String
) : RecyclerView.Adapter<ShowModelAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater =
            LayoutInflater.from(parent.context).inflate(R.layout.item_show_model, parent, false)
        return ViewHolder(layoutInflater)
    }

    override fun getItemCount(): Int {
        return listaRecibida.size
    }
    fun limpiar(){
        listaRecibida.clear()
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = listaRecibida[position]
        holder.render(item, OnClickListener)
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = ItemShowModelBinding.bind(view)


        fun render(
            lista: VehicleModel,
            OnClickListener: (VehicleModel, Int, Int) -> Unit
        ) {
            if (tipoUsuario == "Invitado" || tipoUsuario=="Colaborador") binding.ivOpciones.isEnabled = false
//            itemView.setOnClickListener { OnClickListener(lista) }
            val customPicasso = CustomPicasso.getInstance(itemView.context)
            binding.itemViewShimmer.startShimmer()
            //customPicasso.load(lista.imagen).placeholder(R.drawable.collection_tour).into(
            customPicasso.load("https://drive.google.com/uc?export=view&id=${lista.imagen}").into(binding.imgPicture,
                object : Callback {
                    override fun onSuccess() {
                        //binding.imgIcon.animate().alpha(1f).setDuration(300)
                        binding.itemViewShimmer.stopShimmer()
                        binding.itemViewModel.isVisible = true
                        binding.itemViewShimmer.isVisible = false
                        binding.imgPicture.animate().alpha(1f).setDuration(800)
                    }

                    override fun onError(e: Exception?) {

                    }

                })
            binding.lblComentarios.setOnClickListener() {
                if (binding.llComentarios.isVisible) {
                    binding.llComentarios.visibility = View.GONE
                    binding.lblComentarios.text = "Mostrar Comentarios >>>"
                } else {
                    binding.llComentarios.visibility = View.VISIBLE
                    binding.lblComentarios.text = "Ocultar Comentarios >>>"
                }
            }
            binding.lblAuthor.text = extraerIniciales(lista.autor)
            binding.imgPicture.setOnClickListener() { OnClickListener(lista, 1, position) }
            binding.tvComent.text = lista.comentarios
            when (lista.estado) {
                "Revision" -> {
                    binding.tvEtiquetaEstado.text = lista.estado
                    binding.tvEtiquetaEstado.isVisible = true
                    binding.tvEtiquetaEstado.setBackgroundColor(Color.parseColor("#D6CD27"))
                }

                "Observado" -> {
                    binding.tvEtiquetaEstado.text = lista.estado
                    binding.tvEtiquetaEstado.isVisible = true
                    binding.tvEtiquetaEstado.setBackgroundColor(Color.parseColor("#DF1818"))
                }
            }
            binding.frameLayout3.setOnClickListener(){
                mostrarGlobo(binding.frameLayout3,lista.autor)
            }
            binding.ivOpciones.setOnClickListener() {
                PopupMenuHelper.configureAndShowPopupMenu(
                    binding.ivOpciones.context,
                    it,
                    MenuList(tipoUsuario),
                    object : PopupMenuHelper.PopupMenuItemClickListener {
                        override fun onMenuItemClicked(item: menuOption) {
                            when (item.texto) {
                                "Editar" -> OnClickListener(lista, 2,position)
                                "Observar" -> OnClickListener(lista, 3,position)
                                "Eliminar" -> OnClickListener(lista, 4,position)
                            }
                        }

                    })


//                val popupMenu = PopupMenu(binding.ivOpciones.context, binding.ivOpciones)
//                popupMenu.inflate(R.menu.pop_menu_model)
//                popupMenu.setOnMenuItemClickListener {
//                    when (it.itemId) {
//                        R.id.menu_item_Modificar -> {
//                            OnClickListener(lista,1,position)
//                            true
//                        }
//
//                        R.id.menu_item_Observado -> {
//                            OnClickListener(lista,2,position)
//                            true
//                        }
//
//                        R.id.menu_item_eliminar -> {
//                            OnClickListener(lista,3,position)
//                            true
//                        }
//                        else -> true
//                    }
//                }
//
//                try {
//                    val popup = PopupMenu::class.java.getDeclaredField("mPopup")
//                    popup.isAccessible = true
//                    val menu = popup.get(popupMenu)
//                    menu.javaClass.getDeclaredMethod("setForceShowIcon", Boolean::class.java)
//                        .invoke(menu, true)
//                } catch (e: Exception) {
//                    e.printStackTrace()
//                } finally {
//                    popupMenu.gravity = Gravity.END
//                    popupMenu.show()
//                }
            }

        }

        private fun MenuList(tipo: String): ArrayList<menuOption> {
            val lista: ArrayList<menuOption> = ArrayList()
            when (tipo) {
                "Administrador" -> {
                    lista.add(menuOption(R.drawable.ic_edit, "Editar"))
                    lista.add(menuOption(R.drawable.ic_flag, "Observar"))
                    lista.add(menuOption(R.drawable.ic_delete, "Eliminar"))
                }

                "Developer" -> {
                    lista.add(menuOption(R.drawable.ic_edit, "Editar"))
                    lista.add(menuOption(R.drawable.ic_flag, "Observar"))
                    lista.add(menuOption(R.drawable.ic_delete, "Eliminar"))
                }
            }
            return lista
        }
    }

    private fun mostrarGlobo(anchorView: View,autor:String) {
        val inflater = anchorView.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.layout_chat_balloon, null)

        // Cambia el nombre de usuario según tu lógica
        view.findViewById<TextView>(R.id.text_user_name).text = autor

        val popupWindow = PopupWindow(
            view,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        // Cierra el globo de chat cuando se hace clic en cualquier parte fuera de él
        popupWindow.isOutsideTouchable = true
        popupWindow.isFocusable = true

        // Cargar animaciones
        val slideInAnimation = AnimationUtils.loadAnimation(anchorView.context, R.anim.slide_in)
        val slideOutAnimation = AnimationUtils.loadAnimation(anchorView.context, R.anim.slide_out)

        // Asignar animaciones a la ventana emergente
        popupWindow.animationStyle = android.R.style.Animation_Dialog
        view.startAnimation(slideInAnimation)

        popupWindow.showAsDropDown(anchorView)
        // Programa una tarea para que el globo desaparezca después de 3 segundos
        Handler().postDelayed({
            view.startAnimation(slideOutAnimation)
            popupWindow.dismiss()
        }, 3000)
    }

    private fun extraerIniciales(author: String): String {
        var nameinitial = ""
        var part = author.split(" ")
        if (part?.size!! > 1) nameinitial = part[0][0].toString() + part[1][0].toString()
        else nameinitial = part[0][0].toString() + part[0][1].toString()
        return nameinitial
    }
}