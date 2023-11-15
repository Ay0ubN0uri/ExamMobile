package com.a00n.universityapp.utils

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.TransitionDrawable
import java.io.ByteArrayOutputStream

class Constants {
    companion object {
        private const val URL: String = "http://192.168.43.106:8080/api/v1"
        const val ALL_PERSONS_URL: String = "$URL/persons"
        const val DELETE_PERSON_URL: String = "$URL/persons"
        const val ADD_PERSON_URL: String = "$URL/persons"
        const val UPDATE_PERSON_URL: String = "$URL/persons"

        const val EMPLOYEE_URL: String = "$URL/employes"
        const val SERVICE_URL: String = "$URL/services"

//        const val ALL_FILIERES_URL: String = "$URL/filieres"
//        const val DELETE_FILIERE_URL: String = "$URL/filieres"
//        const val ADD_FILIERE_URL: String = "$URL/filieres"
//        const val UPDATE_FILIERE_URL: String = "$URL/filieres"
//
//
//        const val ALL_STUDENTS_URL: String = "$URL/students"
//        const val DELETE_STUDENT_URL: String = "$URL/students"
//        const val ADD_STUDENT_URL: String = "$URL/students"
//        const val UPDATE_STUDENT_URL: String = "$URL/students"
//        const val ASSIGN_ROLES_URL: String = "$URL/students"

        fun convertDrawableToByteArray(drawable: Drawable?): ByteArray {
//            val bitmap = (drawable as BitmapDrawable).bitmap
            val tmp = when (drawable) {
                is BitmapDrawable -> drawable
                is TransitionDrawable -> drawable.getDrawable(
                    0
                ) as BitmapDrawable
//                is ColorDrawable ->
                else -> null
            }
            return if(tmp == null) {
                ByteArray(0)
            } else {
                val stream = ByteArrayOutputStream()
                val bitmap = tmp.bitmap
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                stream.toByteArray()
            }
        }

        fun convertBitmapToByteArray(bitmap: Bitmap): ByteArray {
            val outputStream = ByteArrayOutputStream()
            bitmap.compress(
                Bitmap.CompressFormat.JPEG,
                100,
                outputStream
            ) // You can change the format and quality as needed
            return outputStream.toByteArray()
        }
    }
}