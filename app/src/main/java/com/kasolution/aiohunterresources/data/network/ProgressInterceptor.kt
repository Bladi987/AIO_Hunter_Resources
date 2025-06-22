package com.kasolution.aiohunterresources.data.network
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.ResponseBody
import okhttp3.MediaType
import okio.Buffer
import okio.BufferedSource
import okio.ForwardingSource
import okio.Source
import okio.buffer
import java.io.IOException

class ProgressInterceptor(private val progressListener: ProgressListener) : Interceptor {

    interface ProgressListener {
        fun onProgress(progress: Int, bytesDownloaded: Long, totalBytes: Long, estimatedTimeRemaining: Long)
    }

    private var startTime: Long = 0
    private var bytesDownloaded: Long = 0
    private var totalBytes: Long = 0

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalResponse = chain.proceed(chain.request())
        totalBytes = originalResponse.body?.contentLength() ?: 0
        startTime = System.currentTimeMillis()
        return originalResponse.newBuilder()
            .body(progressResponseBody(originalResponse.body!!, progressListener))
            .build()
    }

    private fun progressResponseBody(
        responseBody: ResponseBody,
        progressListener: ProgressListener
    ): ResponseBody {
        return object : ResponseBody() {
            private var bufferedSource: BufferedSource? = null

            override fun contentType(): MediaType? {
                return responseBody.contentType()
            }

            override fun contentLength(): Long {
                return totalBytes
            }

            override fun source(): BufferedSource {
                if (bufferedSource == null) {
                    bufferedSource = source(responseBody.source()).buffer()
                }
                return bufferedSource!!
            }

            private fun source(source: Source): Source {
                return object : ForwardingSource(source) {
                    private var totalBytesRead = 0L

                    @Throws(IOException::class)
                    override fun read(sink: Buffer, byteCount: Long): Long {
                        val bytesRead = super.read(sink, byteCount)
                        totalBytesRead += if (bytesRead != -1L) bytesRead else 0
                        bytesDownloaded += if (bytesRead != -1L) bytesRead else 0

                        // Calcular el progreso y notificar al listener
                        val progress = ((totalBytesRead.toFloat() / totalBytes.toFloat()) * 100).toInt()
                        val elapsedTime = System.currentTimeMillis() - startTime
                        val remainingTime = if (totalBytesRead > 0) {
                            ((totalBytes - bytesDownloaded) * elapsedTime) / totalBytesRead
                        } else {
                            0
                        }
                        progressListener.onProgress(progress, bytesDownloaded, totalBytes, remainingTime)

                        return bytesRead
                    }
                }
            }
        }
    }
}
