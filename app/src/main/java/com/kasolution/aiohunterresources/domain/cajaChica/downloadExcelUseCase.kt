package com.kasolution.aiohunterresources.domain.cajaChica

import android.os.Environment
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL


class downloadExcelUseCase {
    suspend fun getFileSheet(url: String, titulo: String): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                // Abrir la conexión HTTP para obtener el archivo
                val connection = URL(url).openConnection() as HttpURLConnection
                connection.connectTimeout = 20000 // Timeout de 20 segundos
                connection.readTimeout = 20000 // Timeout de lectura de 20 segundos

                val contentLength = connection.contentLength
                if (contentLength <= 10 * 1024 * 1024) { // Verificar tamaño máximo (10MB)

                    // Obtener la ruta de la carpeta Downloads
                    val downloadFolder =
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)

                    // Verificar si la carpeta existe, si no, crearla
                    if (!downloadFolder.exists()) {
                        downloadFolder.mkdirs()
                    }

                    // Crear el archivo dentro de la carpeta Downloads
                    val file = File(
                        downloadFolder,
                        "$titulo.xlsx"
                    ) // Puedes personalizar el nombre y la extensión
                    val outputStream = FileOutputStream(file)

                    // Descargar el archivo usando InputStream
                    connection.inputStream.use { inputStream ->
                        inputStream.copyTo(outputStream)
                    }

                    // Si todo salió bien, devolvemos el resultado de éxito con el mensaje
                    Result.success(file.absolutePath)
                } else {
                    // Si el archivo es demasiado grande, devolvemos el error correspondiente
                    Result.failure(Exception("El archivo excede el tamaño permitido (10MB)."))
                }
            } catch (e: Exception) {
                Log.e("FileService", "Error al descargar archivo desde URL: ${e.message}")
                // En caso de error, devolvemos el resultado con la excepción
                Result.failure(Exception("Ocurrió un error al intentar descargar el archivo desde la URL: ${e.message}"))
            }
        }
    }
}