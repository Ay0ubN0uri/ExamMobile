package com.nouri.exam.utils

import com.android.volley.NetworkResponse
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.HttpHeaderParser
import java.io.ByteArrayOutputStream
import java.io.DataOutputStream
import java.io.IOException
class MultipartRequest(
    url: String,
    private val byteArray: ByteArray,
//    private val drawable: Drawable,
//    private val params: Map<String, String>?,
//    private val headers: Map<String, String>?,
    private val listener: Response.Listener<String>,
    errorListener: Response.ErrorListener
) : Request<String>(Method.POST, url, errorListener) {

    private val boundary = "----" + System.currentTimeMillis()

    override fun getBodyContentType(): String {
        return "multipart/form-data; boundary=$boundary"
    }

    @Throws(IOException::class)
    private fun buildMultipartEntity(): ByteArray {
        val outputStream = ByteArrayOutputStream()
        val dataOutputStream = DataOutputStream(outputStream)

//        // Add parameters
//        for ((key, value) in params) {
//            dataOutputStream.writeBytes("--$boundary\r\n")
//            dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"$key\"\r\n\r\n")
//            dataOutputStream.writeBytes(value + "\r\n")
//        }

        // Add image
        dataOutputStream.writeBytes("--$boundary\r\n")
        dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"image\"; filename=\"image.jpg\"\r\n")
        dataOutputStream.writeBytes("Content-Type: image/jpeg\r\n\r\n")

        // using a drawable
//        val bitmap = (drawable as BitmapDrawable).bitmap
//        val byteArrayOutputStream = ByteArrayOutputStream()
//        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
//        val bitmapData = byteArrayOutputStream.toByteArray()
//        dataOutputStream.write(bitmapData)

        // using a byte array
        dataOutputStream.write(byteArray)

        dataOutputStream.writeBytes("\r\n")
        dataOutputStream.writeBytes("--$boundary--\r\n")

        return outputStream.toByteArray()
    }

    override fun getBody(): ByteArray {
        return try {
            buildMultipartEntity()
        } catch (e: IOException) {
            // handle the exception
            byteArrayOf()
        }
    }

    override fun parseNetworkResponse(response: NetworkResponse): Response<String> {
        val parsed: String = String(response.data, charset(HttpHeaderParser.parseCharset(response.headers)))
        return Response.success(parsed, HttpHeaderParser.parseCacheHeaders(response))
    }

    override fun deliverResponse(response: String) {
        listener.onResponse(response)
    }
}