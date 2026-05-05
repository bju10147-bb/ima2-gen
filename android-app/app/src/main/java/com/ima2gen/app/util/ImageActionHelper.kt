package com.ima2gen.app.util

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.OutputStream

object ImageActionHelper {

    suspend fun downloadImage(context: Context, imageUrl: String) {
        withContext(Dispatchers.IO) {
            try {
                val bitmap = fetchBitmap(context, imageUrl) ?: return@withContext
                val filename = "ima2gen_${System.currentTimeMillis()}.png"
                
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        put(MediaStore.MediaColumns.RELATIVE_PATH, "Pictures/ima2gen")
                    }
                }

                val uri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                if (uri != null) {
                    val outputStream: OutputStream? = context.contentResolver.openOutputStream(uri)
                    if (outputStream != null) {
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                        outputStream.close()
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "이미지가 갤러리에 저장되었습니다.", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "저장 실패: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    suspend fun shareImage(context: Context, imageUrl: String) {
        withContext(Dispatchers.IO) {
            try {
                val bitmap = fetchBitmap(context, imageUrl) ?: return@withContext
                
                // Save to cache for sharing
                val cachePath = context.cacheDir.resolve("shared_images").apply { mkdirs() }
                val file = cachePath.resolve("shared_image.png")
                file.outputStream().use { 
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
                }
                
                val uri = androidx.core.content.FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    file
                )

                val intent = Intent(Intent.ACTION_SEND).apply {
                    type = "image/png"
                    putExtra(Intent.EXTRA_STREAM, uri)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                
                withContext(Dispatchers.Main) {
                    context.startActivity(Intent.createChooser(intent, "이미지 공유"))
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "공유 실패: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    suspend fun saveToProjectFolder(context: Context, folderUri: String, bitmap: Bitmap): Uri? {
        return withContext(Dispatchers.IO) {
            try {
                val directoryUri = Uri.parse(folderUri)
                val documentUri = androidx.documentfile.provider.DocumentFile.fromTreeUri(context, directoryUri)
                val filename = "gen_${System.currentTimeMillis()}.png"
                
                val file = documentUri?.createFile("image/png", filename)
                file?.uri?.let { uri ->
                    context.contentResolver.openOutputStream(uri)?.use { 
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
                    }
                    uri
                }
            } catch (e: Exception) {
                null
            }
        }
    }

    private suspend fun fetchBitmap(context: Context, imageUrl: String): Bitmap? {
        if (imageUrl.startsWith("data:")) {
            return try {
                val base64Data = imageUrl.substringAfter("base64,")
                val decodedBytes = android.util.Base64.decode(base64Data, android.util.Base64.DEFAULT)
                android.graphics.BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
            } catch (e: Exception) {
                null
            }
        }

        val loader = ImageLoader(context)
        val request = ImageRequest.Builder(context)
            .data(imageUrl)
            .allowHardware(false) // Required for getting bitmap
            .build()
        
        val result = loader.execute(request)
        return if (result is SuccessResult) {
            (result.drawable as BitmapDrawable).bitmap
        } else {
            null
        }
    }
}
