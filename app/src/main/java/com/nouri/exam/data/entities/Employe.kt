package com.nouri.exam.data.entities

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.LocalDate

@Parcelize
class Employe(
    val id: Int,
    val nom: String,
    val prenom: String,
    val image: ByteArray,
    val url: String,
    val dateNaissance: LocalDate,
    val service: Service?,
    val chef: Employe?,
    val employees: List<Employe>
) : Parcelable {
    constructor(
        nom: String,
        prenom: String,
        image: ByteArray,
        url: String,
        dateNaissance: LocalDate,
        service: Service? = null,
        chef: Employe? = null,
        employees: List<Employe> = emptyList()
    ) : this(
        id = 0,
        nom,
        prenom,
        image,
        url,
        dateNaissance,
        service,
        chef,
        employees = employees
    )

    override fun toString(): String {
        return this.nom
    }
//    override fun toString(): String {
//        return "${this.nom} ${this.prenom}"
//    }
}