package com.kasolution.aiohunterresources.UI.User.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.util.Base64
import androidx.recyclerview.widget.RecyclerView
import com.kasolution.aiohunterresources.R
import com.kasolution.aiohunterresources.UI.FichasTecnicas.view.model.menuOption
import com.kasolution.aiohunterresources.UI.User.model.user
import com.kasolution.aiohunterresources.databinding.ItemListUserBinding
import com.kasolution.aiohunterresources.core.PopupMenuHelper
import java.util.Locale
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec


class UserAdapter(
    private val listaRecibida: ArrayList<user>,
    private val onClickListener: (user, Int, Int) -> Unit,
) : RecyclerView.Adapter<UserAdapter.ViewHolder>() {
    private var copylist: ArrayList<user> = ArrayList()
    var mod = 0
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater =
            LayoutInflater.from(parent.context).inflate(R.layout.item_list_user, parent, false)
        return ViewHolder(layoutInflater)

    }

    init {
        copylist.addAll(listaRecibida)
    }

    override fun getItemCount(): Int {
        if (listaRecibida.size > copylist.size) {
            copylist.clear()
            copylist.addAll(listaRecibida)
        }
        return listaRecibida.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = listaRecibida[position]
        holder.render(item, onClickListener)
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val binding = ItemListUserBinding.bind(view)

        fun render(
            lista: user,
            onClickListener: (user, Int, Int) -> Unit
        ) {
            //itemView.setOnClickListener { OnClickListener(lista) }
            binding.tvNombre.text = lista.name + " " + lista.lastName
            binding.tvTipo.text = lista.tipo
            binding.tvUser.text = lista.user

            binding.ivOption.setOnClickListener() {
                PopupMenuHelper.configureAndShowPopupMenu(
                    itemView.context,
                    it,
                    MenuList(),
                    object : PopupMenuHelper.PopupMenuItemClickListener {
                        override fun onMenuItemClicked(item: menuOption) {
                            when (item.texto) {
                                "Resetear" -> onClickListener(lista, 2, bindingAdapterPosition)
                                "Editar" -> onClickListener(lista, 3, bindingAdapterPosition)
                                "Eliminar" -> onClickListener(lista, 4, bindingAdapterPosition)
                            }
                        }
                    })
            }
        }
    }

    private fun MenuList(): ArrayList<menuOption> {
        val lista: ArrayList<menuOption> = ArrayList()
        lista.add(menuOption(R.drawable.ic_reset, "Resetear"))
        lista.add(menuOption(R.drawable.ic_edit, "Editar"))
        lista.add(menuOption(R.drawable.ic_delete, "Eliminar"))

        return lista
    }

    // Función para filtrar la lista
    fun filter(text: String) {

        listaRecibida.clear() // Limpiar la lista filtrada

        if (text.isEmpty()) {
            listaRecibida.addAll(copylist) // Si el texto de búsqueda está vacío, mostrar la lista original
        } else {
            val filterPattern = text.toUpperCase(Locale.getDefault()).trim()
            for (item in copylist) {
                if (item.user.toUpperCase(Locale.getDefault()).contains(filterPattern)) {
                    listaRecibida.add(item) // Agregar elementos que coinciden con el patrón de búsqueda
                }
            }
        }
        notifyDataSetChanged() // Notificar al adaptador que los datos han cambiado
    }

    fun encrypt(text: String, key: String): String {
        val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
        cipher.init(Cipher.ENCRYPT_MODE, SecretKeySpec(key.toByteArray(), "AES"))
        val encryptedBytes = cipher.doFinal(text.toByteArray())
        return Base64.encodeToString(encryptedBytes, Base64.DEFAULT)
    }

    fun decrypt(encryptedText: String, key: String): String {
        val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
        cipher.init(Cipher.DECRYPT_MODE, SecretKeySpec(key.toByteArray(), "AES"))
        val encryptedBytes = Base64.decode(encryptedText, Base64.DEFAULT)
        val decryptedBytes = cipher.doFinal(encryptedBytes)
        return String(decryptedBytes)
    }
}