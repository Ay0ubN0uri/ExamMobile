package com.nouri.exam.data.entities

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.LocalDate

@Parcelize
data class Person(
    val id: Int,
    val name: String,
    val birthday: LocalDate,
    val gender: Gender,
    val image: ByteArray,
    val url: String
) : Parcelable
