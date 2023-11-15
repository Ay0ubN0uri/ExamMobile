package com.nouri.exam.ui.adapters

import android.util.Log
import androidx.databinding.BindingAdapter
import androidx.navigation.findNavController
import com.google.android.material.card.MaterialCardView
import com.nouri.exam.data.entities.Person
import com.nouri.exam.ui.fragments.person.PersonFragmentDirections

@BindingAdapter("onPersonClickListener")
fun MaterialCardView.onPersonClickListener(person: Person) {
    this.setOnClickListener {
        Log.i("info", "onPersonClickListener: $person")
        val action =PersonFragmentDirections.actionNavPersonToPersonUpdateFragment(person)
        this.findNavController().navigate(action)
    }
}


