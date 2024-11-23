package com.kasolution.aiohunterresources.core

import android.util.Log
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitHelper {
    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(20, TimeUnit.SECONDS) // Tiempo máximo de espera para establecer la conexión
        .readTimeout(20, TimeUnit.SECONDS) // Tiempo máximo de espera para la lectura de datos
        .writeTimeout(20, TimeUnit.SECONDS) // Tiempo máximo de espera para la escritura de datos
        .build()
    fun getRetrofit(url: String): Retrofit {
        return Retrofit.Builder()
            .baseUrl(url)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    fun <T> handleRetrofitCall(call: Call<T>, callback: Callback<T>) {
        call.enqueue(object : Callback<T> {
            override fun onResponse(call: Call<T>, response: Response<T>) {
                if (response.isSuccessful) {
                    callback.onResponse(call, response)
                } else {
                    // Manejar la respuesta fallida aquí
                    // Por ejemplo, puedes notificar al usuario sobre el problema
                }
            }

            override fun onFailure(call: Call<T>, t: Throwable) {
                // Manejar la excepción aquí
                // Por ejemplo, puedes mostrar un mensaje de error o realizar alguna acción específica
                Log.e("BladiDev","Ocurrio un error ${t.toString()}")
            }
        })
    }
}