package com.nouri.exam.data.entities

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Service(
    val id:Int,
    val nom:String
): Parcelable {
    override fun toString(): String {
        return this.nom
    }
}
