package com.example.myapplication.data.remote

import android.content.Context
import android.net.Uri
import com.example.myapplication.BuildConfig
import java.io.IOException
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

class CloudinaryUploader(
    private val context: Context,
    private val client: OkHttpClient = OkHttpClient.Builder()
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()
) {

    suspend fun upload(uri: Uri): Result<String> = withContext(Dispatchers.IO) {
        val cloudName = BuildConfig.CLOUDINARY_CLOUD_NAME.takeIf { it.isNotBlank() }
            ?: return@withContext Result.failure(IllegalStateException("Configura cloudinary.cloud_name en local.properties"))
        val uploadPreset = BuildConfig.CLOUDINARY_UPLOAD_PRESET.takeIf { it.isNotBlank() }
            ?: return@withContext Result.failure(IllegalStateException("Configura cloudinary.upload_preset en local.properties"))

        runCatching {
            val resolver = context.contentResolver
            val mimeType = resolver.getType(uri)?.ifBlank { null } ?: "image/*"
            val fileName = uri.lastPathSegment ?: "upload-${System.currentTimeMillis()}"
            val bytes = resolver.openInputStream(uri)?.use { it.readBytes() }
                ?: throw IOException("No pudimos leer la imagen seleccionada")

            val body = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", fileName, bytes.toRequestBody(mimeType.toMediaType()))
                .addFormDataPart("upload_preset", uploadPreset)
                .build()

            val request = Request.Builder()
                .url("https://api.cloudinary.com/v1_1/$cloudName/image/upload")
                .post(body)
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    throw IOException("Error subiendo imagen (${response.code})")
                }
                val payload = response.body?.string().orEmpty()
                val secureUrl = JSONObject(payload).optString("secure_url")
                if (secureUrl.isBlank()) {
                    throw IOException("Cloudinary no retornó URL pública")
                }
                secureUrl
            }
        }
    }
}
