package com.kasolution.aiohunterresources.UI.FichasTecnicas.view.adapter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.kasolution.aiohunterresources.R
import com.kasolution.aiohunterresources.UI.FichasTecnicas.view.model.Brand
import com.kasolution.aiohunterresources.databinding.ItemBrandBinding
import com.kasolution.aiohunterresources.core.CustomPicasso

import com.squareup.picasso.Callback
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.net.URL
import java.util.Locale


class BrandAdapter(
    private val listaRecibida: ArrayList<Brand>,
    private val OnClickListener: (Brand) -> Unit,
    private val context: Context
) : RecyclerView.Adapter<BrandAdapter.ViewHolder>() {
    private var copylist: ArrayList<Brand> = ArrayList()
    var mod=0
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater =
            LayoutInflater.from(parent.context).inflate(R.layout.item_brand, parent, false)
        return ViewHolder(layoutInflater)

    }
    init {
        copylist.addAll(listaRecibida)
    }
    override fun getItemCount(): Int {
            if (listaRecibida.size>copylist.size){
                copylist.clear()
                copylist.addAll(listaRecibida)
            }
        return listaRecibida.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = listaRecibida[position]
        holder.render(item, OnClickListener)
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = ItemBrandBinding.bind(view)

        fun render(
            lista: Brand,
            OnClickListener: (Brand) -> Unit
        ) {
            itemView.setOnClickListener { OnClickListener(lista) }
            binding.shimerEffect.startShimmer()
            // Verificamos si la imagen está en la caché
            val cachedImage = loadImageFromCache(context, "https://drive.google.com/uc?export=view&id=${lista.icon}")
            if (cachedImage != null) {
                binding.imgIcon.setImageBitmap(cachedImage)
                binding.shimerEffect.stopShimmer()
                binding.shimmerBrandElement.isVisible = false
                binding.viewBrand.isVisible = true
                binding.lblBrand.isVisible = true
            } else {
                val customPicasso = CustomPicasso.getInstance(itemView.context)
                customPicasso.load("https://drive.google.com/uc?export=view&id=${lista.icon}")
//                    .resize(100, 100) // Tamaño de la imagen ajustado
//                    .centerCrop() // Ajusta el recorte de la imagen
                    .into(binding.imgIcon,
                    object : Callback {
                        override fun onSuccess() {
                            // Descargamos la imagen en segundo plano y actualizamos la UI cuando termine
                            saveImageToCache(context, "https://drive.google.com/uc?export=view&id=${lista.icon}") { filePath ->
                                // Este bloque se ejecutará cuando la imagen esté guardada
                                // Puedes hacer lo que necesites con la ruta de la imagen guardada
                                Log.d("BrandAdapter", "Imagen guardada en caché en: $filePath")
                            }
                            binding.shimerEffect.stopShimmer()
                            binding.shimmerBrandElement.isVisible=false
                            binding.viewBrand.isVisible=true
                            binding.lblBrand.isVisible=true
                            //binding.imgIcon.animate().alpha(1f).setDuration(800)
                        }

                        override fun onError(e: Exception?) {
                            Log.e("BrandAdapter", "Error al cargar imagen", e)
                        }

                    })
            }

            binding.lblBrand.text = lista.brand
        }
    }
    // Función para filtrar la lista
    fun filter(text: String) {

        listaRecibida.clear() // Limpiar la lista filtrada

        if (text.isEmpty()) {
            listaRecibida.addAll(copylist) // Si el texto de búsqueda está vacío, mostrar la lista original
        } else {
            val filterPattern = text.toUpperCase(Locale.getDefault()).trim()
            for (item in copylist) {
                if (item.brand.toUpperCase(Locale.getDefault()).contains(filterPattern)) {
                    listaRecibida.add(item) // Agregar elementos que coinciden con el patrón de búsqueda
                }
            }
        }
        notifyDataSetChanged() // Notificar al adaptador que los datos han cambiado
    }
    fun limpiar(){
        listaRecibida.clear()
    }

    fun saveImageToCache(context: Context, imageUrl: String, onImageSaved: (String) -> Unit) {
        // Realizamos las operaciones en segundo plano usando corutinas
        val fileName = imageUrl.split("/").last()
        val cacheDir = context.cacheDir
        val file = File(cacheDir, fileName)

        if (file.exists()) {
            // Si la imagen ya está en caché, simplemente llamamos al callback con la ruta
            onImageSaved(file.absolutePath)
            return
        }

        // Usamos la corutina para realizar la descarga y guardar la imagen en caché sin bloquear el hilo principal
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val url = URL(imageUrl)
                val inputStream: InputStream = url.openStream() // Descargar la imagen
                val bitmap: Bitmap = BitmapFactory.decodeStream(inputStream)

                // Guardar la imagen en el directorio de caché
                val outputStream = FileOutputStream(file)
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                outputStream.flush()
                outputStream.close()

                // Llamamos al callback con la ruta del archivo donde se guardó la imagen
                withContext(Dispatchers.Main) {
                    onImageSaved(file.absolutePath)
                }

                Log.d("BrandAdapter", "Imagen guardada en caché: ${file.absolutePath}")
            } catch (e: Exception) {
                Log.e("BrandAdapter", "Error al guardar la imagen en caché", e)
            }
        }
    }



    fun loadImageFromCache(context: Context, imageUrl: String): Bitmap? {
        val fileName = imageUrl.split("/").last()
        val cacheDir = context.cacheDir
        val file = File(cacheDir, fileName)

        return if (file.exists()) {
            BitmapFactory.decodeFile(file.absolutePath)
        } else {
            null
        }
    }
}