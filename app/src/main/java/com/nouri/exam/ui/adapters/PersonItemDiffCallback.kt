package com.nouri.exam.ui.adapters

import androidx.recyclerview.widget.DiffUtil
import com.nouri.exam.data.entities.Person

class PersonItemDiffCallback : DiffUtil.ItemCallback<Person>() {
    override fun areItemsTheSame(oldItem: Person, newItem: Person): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Person, newItem: Person): Boolean {
        return oldItem == newItem
    }
}