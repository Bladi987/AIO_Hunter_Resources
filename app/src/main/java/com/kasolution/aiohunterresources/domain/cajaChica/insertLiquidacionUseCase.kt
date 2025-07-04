package com.kasolution.aiohunterresources.domain.cajaChica

import android.os.Environment
import android.util.Log
import com.kasolution.aiohunterresources.UI.CajaChica.view.model.liquidacion
import com.kasolution.aiohunterresources.core.dataConexion.urlId
import com.kasolution.aiohunterresources.data.RepositoryCajaChica
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL


class insertLiquidacionUseCase() {
    private val repository = RepositoryCajaChica()
    suspend operator fun invoke(
        urlid: urlId,
        liquidacion: liquidacion,
        adicional: ArrayList<Int>
    ): Result<liquidacion> {
        lateinit var dataliquidacion: liquidacion
        val responseResult = repository.insertLiquidacion(urlid, liquidacion, adicional)
        return when (responseResult.isSuccess) {
            true -> {
                val response = responseResult.getOrNull()?.asJsonObject
                val data = response?.getAsJsonArray("Respuesta")
                if (data != null) {
                    val id = data[0].asString
                    val fecha = data[1].asString
                    val archivo = data[2].asString
                    val concepto = data[3].asString
                    val monto = data[4].asString
                    val estado = data[5].asString
                    val downloadLink = data[6].asString

                    if (downloadLink != "false") {
                        //descargamos el excel
                        dataliquidacion = liquidacion(id, fecha, archivo, concepto, monto, estado,downloadLink,true)
                        //getFileSheet(downloadLink, concepto)
                    }else{
                        dataliquidacion = liquidacion(id, fecha, archivo, concepto, monto, estado,downloadLink,false)
                    }
                } else {
                    dataliquidacion = liquidacion("", "", "", "", "", "","",false)
                }
                Result.success(dataliquidacion)
            }

            false -> {
                // Si la llamada al repositorio falló, capturamos el mensaje de error
                val errorMessage = responseResult.exceptionOrNull()?.message ?: "Error desconocido"
                Log.e("BladiDev", "Error al obtener los datos: $errorMessage")
                Result.failure(Exception(errorMessage))
            }
        }
    }
}
//suspend fun getFileSheet(url: String, titulo: String): String {
//    return withContext(Dispatchers.IO) {
//        try {
//            // Abrir la conexión HTTP para obtener el archivo
//            val connection = URL(url).openConnection() as HttpURLConnection
//            connection.connectTimeout = 20000 // Timeout de 20 segundos
//            connection.readTimeout = 20000 // Timeout de lectura de 20 segundos
//
//            val contentLength = connection.contentLength
//            if (contentLength <= 10 * 1024 * 1024) { // Verificar tamaño máximo (10MB)
//
//                // Obtener la ruta de la carpeta Downloads
//                val downloadFolder =
//                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
//
//                // Verificar si la carpeta existe, si no, crearla
//                if (!downloadFolder.exists()) {
//                    downloadFolder.mkdirs()
//                }
//
//                // Crear el archivo dentro de la carpeta Downloads
//                val file = File(
//                    downloadFolder,
//                    "$titulo.xlsx"
//                ) // Puedes personalizar el nombre y la extensión
//                val outputStream = FileOutputStream(file)
//
//                // Descargar el archivo usando InputStream
//                connection.inputStream.use { inputStream ->
//                    inputStream.copyTo(outputStream)
//                }
//
//                "Archivo descargado correctamente en: ${file.absolutePath}"
//            } else {
//                "El archivo excede el tamaño permitido (10MB)."
//            }
//        } catch (e: Exception) {
//            Log.e("FileService", "Error al descargar archivo desde URL: ${e.message}")
//            "Ocurrió un error al intentar descargar el archivo desde la URL: ${e.message}"
//        }
//    }
//}


